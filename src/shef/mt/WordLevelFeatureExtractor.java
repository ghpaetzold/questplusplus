package shef.mt;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.WordLevelFeatureManager;
import shef.mt.tools.MissingResourceGenerator;
import shef.mt.tools.ResourceProcessor;
import shef.mt.tools.SentenceLevelProcessorFactory;
import shef.mt.util.PropertiesManager;

/**
 * Main class for the word-level feature extraction pipeline.
 *
 * @author GustavoH
 */
public class WordLevelFeatureExtractor implements FeatureExtractor{

    private String workDir;
    private String input;
    private String output;
    private String features;

    private String sourceFile;
    private String targetFile;
    private String sourceLang;
    private String targetLang;

    private PropertiesManager resourceManager;
    private WordLevelFeatureManager featureManager;
    private String configPath;
    private String mod;

    private StanfordCoreNLP sourcePipe;
    private StanfordCoreNLP targetPipe;

    /**
     * Main class for word-level feature extraction.
     * @param args 
     */
    public WordLevelFeatureExtractor(String[] args) {
        //Parse command line arguments:
        System.out.println("\n********** Parsing arguments **********");
        this.parseArguments(args);

        //Setup main folders:
        System.out.println("\n********** Setting up folders **********");
        workDir = System.getProperty("user.dir");
        input = workDir + File.separator + resourceManager.getString("input");
        output = workDir + File.separator + resourceManager.getString("output");
        System.out.println("Work dir: " + workDir);
        System.out.println("Input folder: " + input);
        System.out.println("Output folder: " + output);
    }

    public static void main(String[] args) {
        //Measure initial time:
        long start = System.currentTimeMillis();

        //Run word-level feature extractor:
        WordLevelFeatureExtractor wfe = new WordLevelFeatureExtractor(args);
        wfe.run();

        //Measure ending time:
        long end = System.currentTimeMillis();
        System.out.println("Processing completed in " + (end - start) / 1000 + " seconds.");
    }

    public void run() {
        //Set output writer for feature values:
        String outputPath = this.output + File.separator + "output.txt";
        BufferedWriter outWriter = null;
        try {
            outWriter = new BufferedWriter(new FileWriter(outputPath));
        } catch (IOException ex) {
            Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Build input and output folders:
        System.out.println("\n********** Creating necessary folders **********");
        this.constructFolders();

        //Lowercase input files:
        //this.preProcess();

        //Produce missing resources:
        System.out.println("\n********** Producing missing resources **********");
        MissingResourceGenerator missingGenerator = new MissingResourceGenerator(this);
        missingGenerator.produceMissingResources();

        //Create processor factory:
        System.out.println("\n********** Creating processors **********");
        SentenceLevelProcessorFactory processorFactory = new SentenceLevelProcessorFactory(this);
        
        //Get required resource processors:
        ResourceProcessor[][] resourceProcessors = processorFactory.getResourceProcessors();
        ResourceProcessor[] resourceProcessorsSource = resourceProcessors[0];
        ResourceProcessor[] resourceProcessorsTarget = resourceProcessors[1];

        //Process sentences and calculate features:
        System.out.println("\n********** Producing output **********");
        try {
            //Get readers of source and target files input:
            BufferedReader sourceBR = new BufferedReader(new FileReader(this.getSourceFile()));
            BufferedReader targetBR = new BufferedReader(new FileReader(this.getTargetFile()));

            //Process each sentence pair:
            int sentenceCounter = 0;
            while (sourceBR.ready() && targetBR.ready()) {
                //Create source and target sentence objects:
                Sentence sourceSentence = new Sentence(sourceBR.readLine().trim(), sentenceCounter);
                Sentence targetSentence = new Sentence(targetBR.readLine().trim(), sentenceCounter);

                //Run processors over source sentence:
                for (ResourceProcessor processor : resourceProcessorsSource) {
                    processor.processNextSentence(sourceSentence);
                }

                //Run processors over target sentence:
                for (ResourceProcessor processor : resourceProcessorsTarget) {
                    processor.processNextSentence(targetSentence);
                }

                //Run features for sentence pair:
                String featureValues = getFeatureManager().runFeatures(sourceSentence, targetSentence).trim();
                outWriter.write(featureValues);
                outWriter.newLine();

                //Increase sentence counter:
                sentenceCounter++;
            }

            //Save output:
            outWriter.close();
            sourceBR.close();
            targetBR.close();
            System.out.println("Features will be saved in the following order:");
            getFeatureManager().printFeatureIndeces();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructs the folders required by word-level quest.
     */
    public void constructFolders() {
        //Create input folders:
        File f = new File(input);
        if (!f.exists()) {
            f.mkdirs();
        }
        System.out.println("Input folder created " + f.getPath());
        
        f = new File(input + File.separator + getSourceLang());
        if (!f.exists()) {
            f.mkdirs();
        }
        System.out.println("Input folder created " + f.getPath());
        
        f = new File(input + File.separator + getTargetLang());
        if (!f.exists()) {
            f.mkdirs();
        }
        System.out.println("Input folder created " + f.getPath());
        
        f = new File(input + File.separator + getTargetLang() + File.separator + "temp");
        if (!f.exists()) {
            f.mkdirs();
        }
        System.out.println("Input folder created " + f.getPath());

        //Create output folders:
        String output = getResourceManager().getString("output");
        f = new File(output);
        if (!f.exists()) {
            f.mkdirs();
        }
        System.out.println("Output folder created " + f.getPath());
    }

    /**
     * Preprocesses the input files.
     */
    private void preProcess() {
        //Create input and output paths:
        String sourceInputFolder = input + File.separator + getSourceLang();
        String targetInputFolder = input + File.separator + getTargetLang();

        File origSourceFile = new File(getSourceFile());
        File inputSourceFile = new File(sourceInputFolder + File.separator + origSourceFile.getName());
        File origTargetFile = new File(getTargetFile());
        File inputTargetFile = new File(targetInputFolder + File.separator + origTargetFile.getName());

        //Create copy of original input files to input folder:
        try {
            System.out.println("Copying source input to: " + inputSourceFile.getPath());
            System.out.println("copying target input to: " + inputTargetFile.getPath());
            this.copyFile(origSourceFile, inputSourceFile);
            this.copyFile(origTargetFile, inputTargetFile);
        } catch (IOException e) {
            return;
        }

        //Lowercase copied input files:
        String sourceOutput = inputSourceFile + ".lower";
        String targetOutput = inputTargetFile + ".lower";

        try {
            BufferedReader sourceBR = new BufferedReader(new FileReader(inputSourceFile));
            BufferedReader targetBR = new BufferedReader(new FileReader(inputTargetFile));

            BufferedWriter sourceBW = new BufferedWriter(new FileWriter(sourceOutput));
            BufferedWriter targetBW = new BufferedWriter(new FileWriter(targetOutput));

            while (sourceBR.ready()) {
                String sourceSentence = sourceBR.readLine().trim();
                String targetSentence = targetBR.readLine().trim();

                sourceBW.write(sourceSentence.toLowerCase());
                targetBW.write(targetSentence.toLowerCase());

                sourceBW.newLine();
                targetBW.newLine();
            }

            sourceBR.close();
            targetBR.close();
            sourceBW.close();
            targetBW.close();

            //Update input paths:
            this.sourceFile = sourceOutput;
            this.targetFile = targetOutput;


        } catch (FileNotFoundException ex) {
            Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Parses the arguments provided in the command line.
     * @param args 
     */
    public void parseArguments(String[] args) {

        Option help = OptionBuilder.withArgName("help").hasArg()
                .withDescription("print project help information")
                .isRequired(false).create("help");

        Option input = OptionBuilder.withArgName("input").hasArgs(3)
                .isRequired(true).create("input");
        
        Option alignments = OptionBuilder.withArgName("alignments").hasArgs(1)
                .withDescription("alignments between source and target input files")
                .isRequired(false).create("alignments");

        Option lang = OptionBuilder.withArgName("lang").hasArgs(2)
                .isRequired(false).create("lang");

        Option feat = OptionBuilder.withArgName("feat").hasArgs(1)
                .isRequired(false).create("feat");

        Option gb = OptionBuilder.withArgName("gb")
                .withDescription("GlassBox input files").hasOptionalArgs(2)
                .hasArgs(3).create("gb");

        Option config = OptionBuilder
                .withArgName("config")
                .withDescription("cofiguration file")
                .hasArgs(1).isRequired(false).create("config");

        Option featureset = OptionBuilder
                .withArgName("featureset")
                .withDescription("feature set cofiguration file")
                .hasArgs(1).isRequired(false).create("featureset");

        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption(help);
        options.addOption(input);
        options.addOption(alignments);
        options.addOption(featureset);
        options.addOption(lang);
        options.addOption(feat);
        options.addOption(gb);
        options.addOption(config);

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("config")) {
                resourceManager = new PropertiesManager(line.getOptionValue("config"));
            } else {
                resourceManager = new PropertiesManager();
            }

            if (line.hasOption("input")) {
                String[] files = line.getOptionValues("input");
                sourceFile = files[0];
                targetFile = files[1];
            }

            if (line.hasOption("lang")) {
                String[] langs = line.getOptionValues("lang");
                sourceLang = langs[0];
                targetLang = langs[1];
            } else {
                sourceLang = getResourceManager().getString("sourceLang.default");
                targetLang = getResourceManager().getString("targetLang.default");
            }

            if (line.hasOption("featureset")) {
                configPath = line.getOptionValue("featureset");
                featureManager = new WordLevelFeatureManager(configPath);
            }
            else{
                configPath = getResourceManager().getString("featureConfig");
                featureManager = new WordLevelFeatureManager(configPath);
            }


            if (line.hasOption("feat")) {
                // print the value of block-size
                features = line.getOptionValue("feat");
                getFeatureManager().setFeatureList(features);
            } else {
                getFeatureManager().setFeatureList("all");
            }
            
            if (line.hasOption("alignments")) {
                String path = line.getOptionValue("alignments");
                this.resourceManager.put("alignments.file", path);
            }

        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    /**
     * Copies a file from a source to a target destination.
     * @param sourceFile Source file to copy.
     * @param destFile Destination in which to copy the source file.
     * @throws IOException 
     */
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (sourceFile.equals(destFile)) {
            return;
        }

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        java.nio.channels.FileChannel source = null;
        java.nio.channels.FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * @return the sourceFile
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * @return the targetFile
     */
    public String getTargetFile() {
        return targetFile;
    }

    /**
     * @return the sourceLang
     */
    public String getSourceLang() {
        return sourceLang;
    }

    /**
     * @return the targetLang
     */
    public String getTargetLang() {
        return targetLang;
    }

    /**
     * @return the resourceManager
     */
    public PropertiesManager getResourceManager() {
        return resourceManager;
    }

    /**
     * @return the featureManager
     */
    public WordLevelFeatureManager getFeatureManager() {
        return featureManager;
    }

    /**
     * @return the mod
     */
    public String getMod() {
        return mod;
    }

    /**
     * @param mod the mod to set
     */
    public void setMod(String mod) {
        this.mod = mod;
    }
}
