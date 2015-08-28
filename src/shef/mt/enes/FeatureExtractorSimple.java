package shef.mt.enes;

import java.io.*;
import java.util.HashSet;
import org.apache.commons.cli.*;
import shef.mt.features.util.FeatureManager;
import shef.mt.features.util.Sentence;
import shef.mt.tools.BParserProcessor;
import shef.mt.tools.BlockAlignmentProcessor;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;
import shef.mt.tools.GlobalLexicon;
import shef.mt.tools.LanguageModel;
import shef.mt.tools.Lucene;
import shef.mt.tools.MTOutputProcessor;
import shef.mt.tools.NGramExec;
import shef.mt.tools.NGramProcessor;
import shef.mt.tools.NgramCountProcessor;
import shef.mt.tools.POSProcessor;
import shef.mt.tools.PPLProcessor;
import shef.mt.tools.PosTagger;
import shef.mt.tools.ResourceManager;
import shef.mt.tools.Tokenizer;
import shef.mt.tools.TopicDistributionProcessor;
import shef.mt.tools.Triggers;
import shef.mt.tools.TriggersProcessor;
import shef.mt.tools.mqm.Context;
import shef.mt.tools.mqm.MQMManager;
import shef.mt.util.Logger;
import shef.mt.util.NGramSorter;
import shef.mt.util.PropertiesManager;
import shef.mt.xmlwrap.MOSES_XMLWrapper;

/**
 * FeatureExtractor extracts Glassbox and/or Blackbox features from a pair of
 * source-target input files and a set of additional resources specified as
 * input parameters Usage: FeatureExtractor -input <source><target> -lang
 * <source lang><target lang> -feat [list of features] -mode [gb|bb|all] -gb
 * [list of GB resources] -rebuild -log <br> The valid arguments are:<br> -help
 * : print project help information<br> -input <source file> <target file> <word
 * alignment file>: the input source and target files<br> -lang <source
 * language> <target language> : source and target language<br> -feat : the list
 * of features. By default, all features corresponding to the selected mode will
 * be included<br> -gb [list of files] input files required for computing the
 * glassbox features<br> The arguments sent to the gb option depend on the MT
 * system -mode <GB|BB|ALL><br> -rebuild : run all preprocessing tools<br> -log
 * : enable logging<br> -config <config file> : use the configuration file
 * <config file>
 *
 *
 * @author Catalina Hallett & Mariano Felice<br>
 */
public class FeatureExtractorSimple {

    private static int mtSys;
    private static String workDir;
    private static String wordLattices;

    private static String gizaAlignFile;
    /**
     * path to the input folder
     */
    private static String input;

    /**
     * running mode: bb , gb or all
     */
    private String mod;
    /**
     * path to the output folder
     */
    private static String output;
    private static String sourceFile;
    private static String targetFile;
    private static String sourceLang;
    private static String targetLang;
    private static String features;
    private static String nbestInput;
    private static String onebestPhrases;
    private static String onebestLog;

    private static boolean forceRun = false;
    private static PropertiesManager resourceManager;
    private static FeatureManager featureManager;
    private static int ngramSize = 3;
    private static int IBM = 0;
    private static int MOSES = 1;
    private static String configPath;
    private static String gbXML;

    private static boolean isBaseline = false;

    /**
     * set to 0 if the parameter sent to the -gb option is an xml file, 0
     * otherwise
     */
    private int gbMode;

    /**
     * Initialises the FeatureExtractor from a set of parameters, for example
     * sent as command-line arguments
     *
     * @param args The list of arguments
     *
     */
    public FeatureExtractorSimple(String[] args) {
        workDir = System.getProperty("user.dir");
        new Logger("log.txt");
        parseArguments(args);

        input = workDir + File.separator + resourceManager.getString("input");
        output = workDir + File.separator + resourceManager.getString("output");
        System.out.println("input=" + input + "  output=" + output);

    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        FeatureExtractorSimple fe = new FeatureExtractorSimple(args);

        fe.run();
        long end = System.currentTimeMillis();
        Logger.log("processing completed in " + (end - start) / 1000 + " sec");
        Logger.close();
        System.out.println("Processing completed in " + (end - start) / 1000
                + " seconds.");

    }

    /**
     * Parses the command line arguments and sets the respective fields
     * accordingly. This function sets the input source and target files, the
     * source and target language, the running mode (gb or bb), the additional
     * files required by the GB feature extractor, the rebuild and log options
     *
     * @param args The command line arguments
     */
    public void parseArguments(String[] args) {

        Option help = OptionBuilder.withArgName("help").hasArg()
                .withDescription("print project help information")
                .isRequired(false).create("help");

        Option input = OptionBuilder.withArgName("input").hasArgs(3)
                .isRequired(true).create("input");

        Option lang = OptionBuilder.withArgName("lang").hasArgs(2)
                .isRequired(false).create("lang");

        Option feat = OptionBuilder.withArgName("feat").hasArgs(1)
                .isRequired(false).create("feat");

        Option gb = OptionBuilder.withArgName("gb")
                .withDescription("GlassBox input files").hasOptionalArgs(2)
                .hasArgs(3).create("gb");

        Option mode = OptionBuilder
                .withArgName("mode")
                .withDescription("blackbox features, glassbox features or both")
                .hasArgs(1).isRequired(true).create("mode");

        Option config = OptionBuilder
                .withArgName("config")
                .withDescription("cofiguration file")
                .hasArgs(1).isRequired(false).create("config");

        Option rebuild = new Option("rebuild", "run all preprocessing tools");
        rebuild.setRequired(false);

        //separate 17 BB from 79 BB
        Option baseline = new Option("baseline", "only 17 baseline feature will be calculated");
        baseline.setRequired(false);

        CommandLineParser parser = new PosixParser();
        Options options = new Options();

        options.addOption(help);
        options.addOption(input);
        options.addOption(mode);
        options.addOption(lang);
        options.addOption(feat);
        options.addOption(gb);
        options.addOption(rebuild);
        options.addOption(config);
        options.addOption(baseline);

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("config")) {
                resourceManager = new PropertiesManager(line.getOptionValue("config"));
            } else {
                resourceManager = new PropertiesManager();
            }

            if (line.hasOption("input")) {
                // print the value of block-size
                String[] files = line.getOptionValues("input");
                sourceFile = files[0];
                targetFile = files[1];
            }

            if (line.hasOption("lang")) {
                String[] langs = line.getOptionValues("lang");
                sourceLang = langs[0];
                targetLang = langs[1];
            } else {
                sourceLang = resourceManager.getString("sourceLang.default");
                targetLang = resourceManager.getString("targetLang.default");
            }

            if (line.hasOption("gb")) {
                String[] gbOpt = line.getOptionValues("gb");
                for (String s : gbOpt) {
                    System.out.println(s);
                }
                if (gbOpt.length > 1) {
                    mtSys = MOSES;
                    nbestInput = gbOpt[0];
                    onebestPhrases = gbOpt[1];
                    onebestLog = gbOpt[2];
                    gbMode = 1;
                } else {
                    File f = new File(gbOpt[0]);
                    if (f.isDirectory()) {
                        mtSys = IBM;
                        wordLattices = gbOpt[0];
                        gbMode = 1;
                    } else {
                        gbMode = 0;
                        gbXML = gbOpt[0];
                    }

                }

            }

            if (line.hasOption("mode")) {
                String[] modeOpt = line.getOptionValues("mode");
                setMod(modeOpt[0].trim());
                System.out.println(getMod());
                configPath = resourceManager.getString("featureConfig." + getMod());
                System.out.println("feature config:" + configPath);
                featureManager = new FeatureManager(configPath);
            }

            if (line.hasOption("feat")) {
                // print the value of block-size
                features = line.getOptionValue("feat");
                featureManager.setFeatureList(features);
            } else {
                featureManager.setFeatureList("all");
            }

            if (line.hasOption("rebuild")) {
                forceRun = true;
            }

            if (line.hasOption("baseline")) {
                isBaseline = true;
            }

        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }

    public void runPOSTagger() {
        // required by BB features 65-69, 75-80
        String sourceOutput = runPOS(sourceFile, sourceLang, "source");
        String targetOutput = runPOS(targetFile, targetLang, "target");

    }

    /**
     * runs the part of speech tagger
     *
     * @param file input file
     * @param lang language
     * @param type source or target
     * @return path to the output file of the POS tagger
     */
    public String runPOS(String file, String lang, String type) {
        String posName = resourceManager.getString(lang + ".postagger");
        String langResPath = input + File.separator + lang;
        File f = new File(file);
        String absoluteSourceFilePath = f.getAbsolutePath();
        String fileName = f.getName();
        String relativeFilePath = langResPath + File.separator + fileName
                + ".pos";
        String absoluteOutputFilePath = (new File(relativeFilePath))
                .getAbsolutePath();
        String posSourceTaggerPath = resourceManager.getString(lang
                + ".postagger.exePath");
        String outPath = "";
        try {
            Class c = Class.forName(posName);
            PosTagger tagger = (PosTagger) c.newInstance();
            tagger.setParameters(type, posName, posSourceTaggerPath,
                    absoluteSourceFilePath, absoluteOutputFilePath);
            PosTagger.ForceRun(forceRun);
            outPath = tagger.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returns the path of the output file; this is for convenience only so
        // we do't have to calculate it again
        return outPath;

    }

    private static void loadGiza() {
        String gizaPath = resourceManager.getString("pair." + sourceLang
                + targetLang + ".giza.path");
        System.out.println(gizaPath);
        Giza giza = new Giza(gizaPath);
    }

    private static void loadGlobalLexicon() {
        final String glmodelpath = resourceManager.getString("pair." + sourceLang
                + targetLang + ".glmodel.path");
        final Double minweight = Double.valueOf(
                resourceManager.getString("pair." + sourceLang
                        + targetLang + ".glmodel.minweight"));
        GlobalLexicon globalLexicon = new GlobalLexicon(glmodelpath, minweight);
    }

    /*
     * Computes the perplexity and log probability for the source file Required
     * by features 8-13
     */
    private static void runNGramPPL(String sourceOutput, String targetOutput) {
        // required by BB features 8-13
//        NGramExecIRSTLM nge = new NGramExecIRSTLM(resourceManager.getString("tools.irstlm.path") + File.separator + "bin" + File.separator, forceRun);
        NGramExec nge = new NGramExec(resourceManager.getString("tools.ngram.path"), forceRun);
        System.out.println("runNgramPPL");

        File f = new File(sourceFile);
        f = new File(targetFile);

        nge.runNGramPerplex(sourceFile, sourceOutput,
                resourceManager.getString(sourceLang + ".lm"));
        System.out.println(resourceManager.getString(targetLang + ".lm"));
        nge.runNGramPerplex(targetFile, targetOutput,
                resourceManager.getString(targetLang + ".lm"));
    }

    /**
     * Computes the perplexity and log probability for the POS tagged target
     * file<br> Required by BB features 68-69<br> This function could be merged
     * with
     *
     * @seerunNGramPPL() but I separated them to make the code more readable
     *
     * @param posFile file tagged with parts-of-speech
     */
    private String runNGramPPLPos(String posFile) {
        NGramExec nge = new NGramExec(
                resourceManager.getString("tools.ngram.path"), forceRun);

        File f = new File(posFile);
        String posTargetOutput = input
                + File.separator + targetLang + File.separator + f.getName()
                + resourceManager.getString("tools.ngram.output.ext");
        nge.runNGramPerplex(posFile, posTargetOutput,
                resourceManager.getString(targetLang + ".poslm"));
        return posTargetOutput;
    }

    /**
     * Performs some basic processing of the input source and target files For
     * English, this consists of converting the input to lower case and
     * tokenizing For Arabic, this consists of transliteration and tokenization.
     * Please note that the current tools used for tokenizing Arabic also
     * perform POS tagging and morphological analysis Although we could separate
     * the tokenization process from the more in-depth text analysis performed
     * by these tools, for efficiency reasons this is not desirable The input
     * files are also copied to the /input folder. This is necessary because the
     * MADA analyser produces its output in the same folder as the input file,
     * which may cause problems if the right access rights are not available for
     * that particular folder
     */
    private static void preprocessing() {

        tokenizeFiles();

        FeatureExtractorSimple.produceMissingResources();

    }

    private static void produceMissingResources() {
        //Check if source LM is missing:
        if (resourceManager.getProperty(sourceLang + ".lm") == null) {
            if (resourceManager.getProperty("tools.ngram.path") != null) {
                if (resourceManager.getProperty(sourceLang + ".corpus") != null) {
                    if (resourceManager.getProperty("resourcesPath") != null) {
                        System.out.println("Building LM for the " + sourceLang + " language...");
                        System.out.println("Corpus used: " + resourceManager.getProperty(sourceLang + ".corpus"));
                        String[] args = new String[]{
                            resourceManager.getProperty("tools.ngram.path") + "/" + "ngram-count",
                            "-order",
                            "3",
                            "-text",
                            resourceManager.getProperty(sourceLang + ".corpus"),
                            "-lm",
                            resourceManager.getProperty("resourcesPath")
                            + "/" + sourceLang + "_lm.lm"};
                        try {
                            Process process = Runtime.getRuntime().exec(args);
                            process.waitFor();
                            resourceManager.setProperty(sourceLang + ".lm",
                                    resourceManager.getProperty("resourcesPath")
                                    + "/" + sourceLang + "_lm.lm");
                            System.out.println("LM successfully built! Saved at: " + resourceManager.getProperty("resourcesPath")
                                    + "/" + sourceLang + "_lm.lm");
                        } catch (IOException e) {
                            System.out.println("Error running SRILM");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            System.out.println("Error waiting for SRILM to finish its execution.");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Missing source Language Model and resources path is not defined!");
                    }
                } else {
                    System.out.println("Missing source Language Model and corpus is not available!");
                }
            } else {
                System.out.println("Missing source Language Model and SRILM is not available!");
            }
        }

        //Check if target LM is missing:
        if (resourceManager.getProperty(targetLang + ".lm") == null) {
            if (resourceManager.getProperty("tools.ngram.path") != null) {
                if (resourceManager.getProperty(targetLang + ".corpus") != null) {
                    if (resourceManager.getProperty("resourcesPath") != null) {
                        System.out.println("Building LM for the " + targetLang + " language...");
                        System.out.println("Corpus used: " + resourceManager.getProperty(targetLang + ".corpus"));
                        String[] args = new String[]{
                            resourceManager.getProperty("tools.ngram.path") + "/" + "ngram-count",
                            "-order",
                            "3",
                            "-text",
                            resourceManager.getProperty(targetLang + ".corpus"),
                            "-lm",
                            resourceManager.getProperty("resourcesPath")
                            + "/" + targetLang + "_lm.lm"};
                        try {
                            Process process = Runtime.getRuntime().exec(args);
                            process.waitFor();
                            resourceManager.setProperty(targetLang + ".lm",
                                    resourceManager.getProperty("resourcesPath")
                                    + "/" + targetLang + "_lm.lm");
                            System.out.println("LM successfully built! Saved at: " + resourceManager.getProperty("resourcesPath")
                                    + "/" + targetLang + "_lm.lm");
                        } catch (IOException e) {
                            System.out.println("Error running SRILM");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            System.out.println("Error waiting for SRILM to finish its execution.");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Missing source Language Model and resources path is not defined!");
                    }
                } else {
                    System.out.println("Missing source Language Model and corpus is not available!");
                }
            } else {
                System.out.println("Missing source Language Model and SRILM is not available!");
            }
        }

        //Check if source NGRAM file is missing:
        if (resourceManager.getProperty(sourceLang + ".ngram") == null) {
            if (resourceManager.getProperty("tools.ngram.path") != null) {
                if (resourceManager.getProperty(sourceLang + ".corpus") != null) {
                    if (resourceManager.getProperty("resourcesPath") != null) {
                        System.out.println("Building NGRAM file for the " + sourceLang + " language...");
                        System.out.println("Corpus used: " + resourceManager.getProperty(sourceLang + ".corpus"));
                        String[] args = new String[]{
                            resourceManager.getProperty("tools.ngram.path") + "/" + "ngram-count",
                            "-order",
                            "3",
                            "-text",
                            resourceManager.getProperty(sourceLang + ".corpus"),
                            "-write",
                            resourceManager.getProperty("resourcesPath")
                            + "/" + sourceLang + "_ngram.ngram"};
                        try {
                            Process process = Runtime.getRuntime().exec(args);
                            process.waitFor();

                            String spath = resourceManager.getProperty("resourcesPath") + "/" + sourceLang + "_ngram.ngram";
                            NGramSorter.run(spath, 4, 3, 2, spath);

                            resourceManager.setProperty(sourceLang + ".ngram", spath + ".clean");
                            System.out.println("NGRAM successfully built! Saved at: " + spath + ".clean");
                        } catch (IOException e) {
                            System.out.println("Error running SRILM");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            System.out.println("Error waiting for SRILM to finish its execution.");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Missing source NGRAM file and resources path is not defined!");
                    }
                } else {
                    System.out.println("Missing source NGRAM file and corpus is not available!");
                }
            } else {
                System.out.println("Missing source NGRAM file and SRILM is not available!");
            }
        }

        //Check if target NGRAM file is missing:
        if (resourceManager.getProperty(targetLang + ".ngram") == null) {
            if (resourceManager.getProperty("tools.ngram.path") != null) {
                if (resourceManager.getProperty(targetLang + ".corpus") != null) {
                    if (resourceManager.getProperty("resourcesPath") != null) {
                        System.out.println("Building NGRAM file for the " + targetLang + " language...");
                        System.out.println("Corpus used: " + resourceManager.getProperty(targetLang + ".corpus"));
                        String[] args = new String[]{
                            resourceManager.getProperty("tools.ngram.path") + "/" + "ngram-count",
                            "-order",
                            "3",
                            "-text",
                            resourceManager.getProperty(targetLang + ".corpus"),
                            "-write",
                            resourceManager.getProperty("resourcesPath")
                            + "/" + targetLang + "_ngram.ngram"};
                        try {
                            Process process = Runtime.getRuntime().exec(args);
                            process.waitFor();

                            String spath = resourceManager.getProperty("resourcesPath") + "/" + targetLang + "_ngram.ngram";
                            NGramSorter.run(spath, 4, 3, 2, spath);

                            resourceManager.setProperty(targetLang + ".ngram", spath + ".clean");
                            System.out.println("NGRAM successfully built! Saved at: " + spath + ".clean");
                        } catch (IOException e) {
                            System.out.println("Error running SRILM");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            System.out.println("Error waiting for SRILM to finish its execution.");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Missing source NGRAM file and resources path is not defined!");
                    }
                } else {
                    System.out.println("Missing source NGRAM file and corpus is not available!");
                }
            } else {
                System.out.println("Missing source NGRAM file and SRILM is not available!");
            }
        }

        /*
         Check if has LM for each language
         if does not have, check if there is SRILM path
         if does have, check if there is language.corpus
         if does have, generate LM and place into resourcesPath + "/" + language + _LM.lm
         add language.lm = resourcePath + "/" + language + _LM.lm
         */
    }

    private static LanguageModel processNGrams() {
        // required by BB features 30-44
        NGramProcessor ngp = new NGramProcessor(
                resourceManager.getString(sourceLang + ".ngram"));
        return ngp.run();
    }

    /**
     * constructs the folders required by the application. These are, typically:
     * <br> <ul><li>/input and subfolders <ul> <li>/input/<i>sourceLang</i>,
     * /input/<i>targetLang</i> (for storing the results of processing the input
     * files with various tools, such as pos tagger, transliterator,
     * morphological analyser),<br> <li>/input/systems/<i>systemName</i> (for
     * storing system specific resources - for example, the compiled and
     * processed word lattices in the case of the IBM system </ul> <li> /output
     * (for storing the resulting feature files), </ul>
     */
    public void constructFolders() {

        File f = new File(input);
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("folder created " + f.getPath());
        }

        f = new File(input + File.separator + sourceLang);
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("folder created " + f.getPath());
        }
        f = new File(input + File.separator + targetLang);
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("folder created " + f.getPath());
        }
        f = new File(input + File.separator + targetLang + File.separator
                + "temp");
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("folder created " + f.getPath());
        }
        /*
         f = new File(input + File.separator + "systems");
         if (!f.exists()) {
         f.mkdir();
         System.out.println("folder created " + f.getPath());
         }

         f = new File(input + File.separator + "systems" + File.separator
         + "IBM");
         if (!f.exists()) {
         f.mkdir();
         System.out.println("folder created " + f.getPath());
         }

         f = new File(input + File.separator + "systems" + File.separator
         + "MOSES");
         if (!f.exists()) {
         f.mkdir();
         System.out.println("folder created " + f.getPath());
         }
         */
        String output = resourceManager.getString("output");
        f = new File(output);
        if (!f.exists()) {
            f.mkdirs();
            System.out.println("folder created " + f.getPath());
        }

        if (featureManager.hasFeature("1700")) {
            String lang_resources = workDir + File.separator + "lang_resources";
            f = new File(lang_resources);
            if (!f.exists()) {
                System.out.println("For Lucene features, lang_resources are needed.");
                System.exit(0);
            }
            String source_lang_resources = lang_resources + File.separator + sourceLang;
            f = new File(source_lang_resources);
            if (!f.exists()) {
                System.out.println("For Lucene features, source lang_resources are needed.");
                System.exit(0);
            }
            String source_lucene_path = lang_resources + File.separator + sourceLang + File.separator + "luceneIndex";
            f = new File(source_lucene_path);
            if (!f.exists()) {
                f.mkdir();
                System.out.println("folder created " + f.getPath());
            }
            String source_lucene_corpus = source_lucene_path + File.separator + sourceLang + ".corpus";
            try {
                Runtime.getRuntime().exec("ln -s " + workDir + File.separator + resourceManager.getString(sourceLang + ".corpus") + " " + source_lucene_corpus);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Indexing the target
            String target_lang_resources = lang_resources + File.separator + targetLang;
            f = new File(target_lang_resources);
            if (!f.exists()) {
                System.out.println("For Lucene features, target lang_resources are needed.");
                System.exit(0);
            }
            String target_lucene_path = lang_resources + File.separator + targetLang + File.separator + "luceneIndex";
            f = new File(target_lucene_path);
            if (!f.exists()) {
                f.mkdir();
                System.out.println("folder created " + f.getPath());
            }
            String target_lucene_corpus = target_lucene_path + File.separator + targetLang + ".corpus";
            try {
                Runtime.getRuntime().exec("ln -s " + workDir + File.separator + resourceManager.getString(targetLang + ".corpus") + " " + target_lucene_corpus);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs the Feature Extractor<br> <ul> <li>constructs the required folders
     * <li>runs the pre-processing tools <li>runs the BB features, GB features
     * or both according to the command line parameters </ul>
     */
    public String initialiseGBResources() {
        // transform the m output to xml
        String xmlOut = resourceManager.getString("input") + File.separator
                + "systems" + File.separator;
        File f = new File(sourceFile);
        if (mtSys == MOSES) {
            xmlOut += "moses_" + f.getName() + ".xml";
            System.out.println(xmlOut);
            MOSES_XMLWrapper cmuwrap = new MOSES_XMLWrapper(nbestInput, xmlOut,
                    onebestPhrases, onebestLog);
            cmuwrap.run();

            // now send the xml output from cmuwrap to be processed
        }

        return xmlOut;
    }

    /**
     * runs the BB features
     */
    public void runBB() {
        //Get require resources:
        HashSet<String> resources = featureManager.getRequiredResources();

        File f = new File(sourceFile);
        String sourceFileName = f.getName();
        f = new File(targetFile);
        String targetFileName = f.getName();
        String outputFileName = sourceFileName + "_to_" + targetFileName
                + ".out";

        File file = new File(resourceManager.getString("output"));
        if (!file.exists()) {
            System.err.println("Creating dir: " + resourceManager.getString("output"));
            Logger.log("Creating dir: " + resourceManager.getString("output"));
            file.mkdirs();
        } else {
            Logger.log("output dir exists: " + resourceManager.getString("output"));
        }

        String out = resourceManager.getString("output") + File.separator + outputFileName;
        System.out.println("Output will be: " + out);

        String pplSourcePath = resourceManager.getString("input")
                + File.separator + sourceLang + File.separator + sourceFileName
                + resourceManager.getString("tools.ngram.output.ext");
        String pplTargetPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + targetFileName
                + resourceManager.getString("tools.ngram.output.ext");

        String pplPOSTargetPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + targetFileName + PosTagger.getXPOS()
                + resourceManager.getString("tools.ngram.output.ext");
        runNGramPPL(pplSourcePath, pplTargetPath);

        FileModel fm = new FileModel(sourceFile,
                resourceManager.getString(sourceLang + ".corpus"));

        PPLProcessor pplProcSource = new PPLProcessor(pplSourcePath,
                new String[]{"logprob", "ppl", "ppl1"});
        PPLProcessor pplProcTarget = new PPLProcessor(pplTargetPath,
                new String[]{"logprob", "ppl", "ppl1"});

        String sourcePosOutput = null;
        String targetPosOutput = null;
        PPLProcessor pplPosTarget = null;
        if (!isBaseline) {
            sourcePosOutput = runPOS(sourceFile, sourceLang, "source");
            targetPosOutput = runPOS(targetFile, targetLang, "target");

            String targetPPLPos = runNGramPPLPos(targetPosOutput + PosTagger.getXPOS());
            System.out.println("---------TARGET PPLPOS: " + targetPPLPos);
            pplPosTarget = new PPLProcessor(targetPPLPos,
                    new String[]{"poslogprob", "posppl", "posppl1"});

        }

        loadGiza();
        processNGrams();
        boolean gl = false;
        String temp0 = resourceManager.getString("GL");
        if (null != temp0 && temp0.equals("1")) {
            gl = true;
        }

        if (gl) {
            loadGlobalLexicon();
        }

        // Preparing the indices for IR_similarity_features
        Lucene sourceLuc = null;
        Lucene targetLuc = null;
        if (featureManager.hasFeature("1700")) {
            // The indices reside under lang_resources path
            String lang_resources = workDir + File.separator + "lang_resources";
            // Indices are saved under: luceneIndex folder
            String source_lucene_path = lang_resources + File.separator + sourceLang + File.separator + "luceneIndex";
            // The corpus to index
            String source_lucene_corpus = source_lucene_path + File.separator + sourceLang + ".corpus";
            try {
                sourceLuc = new Lucene(source_lucene_path, source_lucene_corpus, true, true, "Source");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String target_lucene_path = lang_resources + File.separator + targetLang + File.separator + "luceneIndex";
            String target_lucene_corpus = target_lucene_path + File.separator + targetLang + ".corpus";
            try {
                targetLuc = new Lucene(target_lucene_path, target_lucene_corpus, true, true, "Target");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //MQM kicks in
        MQMManager.getInstance().initialize(resourceManager);
        Context context = new Context();
        context.setSourceFilePath(sourceFile);
        context.setTargetFilePath(targetFile);
        MQMManager.getInstance().globalProcessing(context);

        try {
            BufferedReader brSource = new BufferedReader(new FileReader(
                    sourceFile));
            BufferedReader brTarget = new BufferedReader(new FileReader(
                    targetFile));
            BufferedWriter output = new BufferedWriter(new FileWriter(out));
            BufferedReader posSource = null;
            BufferedReader posTarget = null;
            boolean posSourceExists = ResourceManager
                    .isRegistered("sourcePosTagger");
            boolean posTargetExists = ResourceManager
                    .isRegistered("targetPosTagger");
            POSProcessor posSourceProc = null;
            POSProcessor posTargetProc = null;

            //lefterav: Berkeley parser modifications start here
            //Check if user has defined the grammar files for source 
            //and target language
            //   if ( ResourceManager.isRegistered("BParser")){   
            boolean bp = false;
            String temp = resourceManager.getString("BP");
            if (null != temp && temp.equals("1")) {
                bp = true;
            }

            BParserProcessor sourceParserProcessor = null;
            BParserProcessor targetParserProcessor = null;

            if (bp) {
                sourceParserProcessor = new BParserProcessor();
                targetParserProcessor = new BParserProcessor();
                sourceParserProcessor.initialize(sourceFile, resourceManager, sourceLang);
                targetParserProcessor.initialize(targetFile, resourceManager, targetLang);
            }
            // } 

            /**
             * BEGIN: Added by Raphael Rubino for the Topic Model Features
             */
            boolean tm = false;
            String temp1 = resourceManager.getString("TM");
            if (temp1 != null && temp1.equals("1")) {
                tm = true;
            }
            TopicDistributionProcessor sourceTopicDistributionProcessor = null;
            TopicDistributionProcessor targetTopicDistributionProcessor = null;
            if (tm) {
                String sourceTopicDistributionFile = resourceManager.getString(sourceLang + ".topic.distribution");
                String targetTopicDistributionFile = resourceManager.getString(targetLang + ".topic.distribution");
                sourceTopicDistributionProcessor = new TopicDistributionProcessor(sourceTopicDistributionFile, "sourceTopicDistribution");
                targetTopicDistributionProcessor = new TopicDistributionProcessor(targetTopicDistributionFile, "targetTopicDistribution");

            }
            /* END: Added by Raphael Rubino for the Topic Model Features
             */

            if (!isBaseline) {
                if (posSourceExists) {
                    posSourceProc = new POSProcessor(sourcePosOutput);
                    posSource = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePosOutput), "utf-8"));
                }
                if (posTargetExists) {
                    posTargetProc = new POSProcessor(targetPosOutput);
                    posTarget = new BufferedReader(new InputStreamReader(new FileInputStream(targetPosOutput)));
                }
            }
            ResourceManager.printResources();
            Sentence sourceSent;
            Sentence targetSent;
            int sentCount = 0;

            String lineSource = brSource.readLine();
            String lineTarget = brTarget.readLine();

            /**
             * Triggers (by David Langlois)
             */
            boolean tr = false;
            String temp2 = resourceManager.getString("TR");
            if (temp2 != null && temp2.equals("1")) {
                tr = true;
            }

            Triggers itl_target = null;
            Triggers itl_source = null;
            Triggers itl_source_target = null;

            TriggersProcessor itl_target_p = null;
            TriggersProcessor itl_source_p = null;
            TriggersProcessor itl_source_target_p = null;

            if (tr) {
                itl_target
                        = new Triggers(
                                resourceManager.getString("target.intra.triggers.file"),
                                Integer.parseInt(resourceManager.getString("nb.max.triggers.target.intra")),
                                resourceManager.getString("phrase.separator"));
                itl_target_p = new TriggersProcessor(itl_target);

                itl_source
                        = new Triggers(
                                resourceManager.getString("source.intra.triggers.file"),
                                Integer.parseInt(resourceManager.getString("nb.max.triggers.source.intra")),
                                resourceManager.getString("phrase.separator"));
                itl_source_p = new TriggersProcessor(itl_source);

                itl_source_target
                        = new Triggers(
                                resourceManager.getString("source.target.inter.triggers.file"),
                                Integer.parseInt(resourceManager.getString("nb.max.triggers.source.target.inter")),
                                resourceManager.getString("phrase.separator"));
                itl_source_target_p
                        = new TriggersProcessor(itl_source_target);

            }

            NgramCountProcessor[] ngramProcessors = this.getNgramProcessors();
            NgramCountProcessor ngramProcessorSource = ngramProcessors[0];
            NgramCountProcessor ngramProcessorTarget = ngramProcessors[1];

            //BlockAlignmentProcessor blockAlignmentProcessor = this.getBlockAlignmentProcessor();
            /*
             * End modification for Triggers
             */

            //1 - Read in each line from the source and target files.
            //2 - Create a sentence from each.
            //3 - Process each sentence.
            //4 - Run the features on the sentences.
            while ((lineSource != null) && (lineTarget != null)) {

                //Create sentence instances:
                sourceSent = new Sentence(lineSource, sentCount);
                targetSent = new Sentence(lineTarget, sentCount);

                //Calculate ngram values for each sentence:
                sourceSent.computeNGrams(3);
                targetSent.computeNGrams(3);

                ngramProcessorSource.processNextSentence(sourceSent);
                ngramProcessorTarget.processNextSentence(targetSent);

                //blockAlignmentProcessor.processNextSentence(targetSent);

                if (posSourceExists) {
                    posSourceProc.processSentence(sourceSent);
                }
                if (posTargetExists) {
                    posTargetProc.processSentence(targetSent);
                }

                pplProcSource.processNextSentence(sourceSent);
                pplProcTarget.processNextSentence(targetSent);
                if (!isBaseline) {
                    pplPosTarget.processNextSentence(targetSent);
                }

                //lefterav: Parse code here
                if (bp) {
                    sourceParserProcessor.processNextSentence(sourceSent);
                    targetParserProcessor.processNextSentence(targetSent);
                }

                if (tm) {
                    sourceTopicDistributionProcessor.processNextSentence(sourceSent);
                    targetTopicDistributionProcessor.processNextSentence(targetSent);
                }

                if (tr) {
                    itl_source_p.processNextSentence(sourceSent);
                    itl_target_p.processNextSentence(targetSent);
                    itl_source_target_p.processNextParallelSentences(sourceSent, targetSent);
                }

                //MQM kicks in
                MQMManager.getInstance().processNextParallelSentences(sourceSent, targetSent);

                if (featureManager.hasFeature("1700")) {
                    sourceLuc.processNextSentence(sourceSent);
                    targetLuc.processNextSentence(targetSent);
                }

                //Increment sentence counter:
                ++sentCount;

                //Run features on sentence pair:
                output.write(featureManager.runFeatures(sourceSent, targetSent));
                output.newLine();

                //Read next lines:
                lineSource = brSource.readLine();
                lineTarget = brTarget.readLine();
            }

            //Close open streams:
            if (posSource != null) {
                posSource.close();
            }
            if (posTarget != null) {
                posTarget.close();
            }
            brSource.close();
            brTarget.close();
            output.close();
            Logger.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File sourceFile, File destFile)
            throws IOException {
        if (sourceFile.equals(destFile)) {
            System.out.println("source=dest");
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
     * returns the working mode: bb, gb or all
     *
     * @return the working mode
     */
    public String getMod() {
        return mod;
    }

    /**
     * sets the working mode
     *
     * @param mod the working mode. Valid values are bb, gb and all
     */
    public void setMod(String mod) {
        this.mod = mod;
    }

    /**
     * runs the GB features
     */
    public void runGB() {
        MTOutputProcessor mtop = null;

        if (gbMode == 1) {
            gbXML = initialiseGBResources();
        }

        String nbestSentPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + "temp";
        String ngramExecPath = resourceManager.getString("tools.ngram.path");

        mtop = new MTOutputProcessor(gbXML, nbestSentPath, ngramExecPath,
                ngramSize);
//		MorphAnalysisProcessor map = new MorphAnalysisProcessor(madaFile);

        File f = new File(sourceFile);
        String sourceFileName = f.getName();
        f = new File(targetFile);
        String targetFileName = f.getName();

        String outputFileName = sourceFileName + "_to_" + targetFileName
                + ".out";

        String out = resourceManager.getString("output") + File.separator + getMod()
                + outputFileName;
        System.out.println("Output will be: " + out);

        String lineTarget;

        try {
            BufferedReader brSource = new BufferedReader(new FileReader(
                    sourceFile));
            BufferedReader brTarget = new BufferedReader(new FileReader(
                    targetFile));
            BufferedWriter output = new BufferedWriter(new FileWriter(out));

            ResourceManager.printResources();

            Sentence targetSent;
            Sentence sourceSent;
            int sentCount = 0;

            String lineSource;

            while (((lineSource = brSource.readLine()) != null)
                    && ((lineTarget = brTarget.readLine()) != null)) {

                lineSource = lineSource.trim().substring(lineSource.indexOf(" "));
                sourceSent = new Sentence(lineSource,
                        sentCount);
                targetSent = new Sentence(lineTarget, sentCount);

                //map.processNextSentence(sourceSent);
                mtop.processNextSentence(sourceSent);

                ++sentCount;
                output.write(featureManager.runFeatures(sourceSent, targetSent));
                output.write("\r\n");

            }
            brSource.close();
            brTarget.close();
            output.close();
            featureManager.printFeatureIndeces();
            Logger.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     public void run() {
     constructFolders();
     preprocessing();
     runBB();
     }*/

    public void runAll() {
        File f = new File(sourceFile);
        String sourceFileName = f.getName();
        f = new File(targetFile);
        String targetFileName = f.getName();
        String outputFileName = sourceFileName + "_to_" + targetFileName
                + ".out";
        String out = resourceManager.getString("output") + File.separator + outputFileName;
        System.out.println("Output will be: " + out);

        MTOutputProcessor mtop = null;

        if (gbMode == 1) {
            gbXML = initialiseGBResources();
        }

        //Run SRILM on ngram count files:
        LanguageModel[] ngramModels = this.getNGramModels();
        LanguageModel ngramModelSource = ngramModels[0];
        LanguageModel ngramModelTarget = ngramModels[1];

        String nbestSentPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + "temp";
        String ngramExecPath = resourceManager.getString("tools.ngram.path");

        mtop = new MTOutputProcessor(gbXML, nbestSentPath, ngramExecPath,
                ngramSize);

        //wlv.mt.features.coherence.Coherence coh = new wlv.mt.features.coherence.Coherence(
        //		getTargetFile());
        String pplSourcePath = resourceManager.getString("input")
                + File.separator + sourceLang + File.separator + sourceFileName
                + resourceManager.getString("tools.ngram.output.ext");
        String pplTargetPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + targetFileName
                + resourceManager.getString("tools.ngram.output.ext");

        String pplPOSTargetPath = resourceManager.getString("input")
                + File.separator + targetLang + File.separator + targetFileName + PosTagger.getXPOS()
                + resourceManager.getString("tools.ngram.output.ext");
        runNGramPPL(pplSourcePath, pplTargetPath);

        PPLProcessor pplProcSource = new PPLProcessor(pplSourcePath,
                new String[]{"logprob", "ppl", "ppl1"});
        PPLProcessor pplProcTarget = new PPLProcessor(pplTargetPath,
                new String[]{"logprob", "ppl", "ppl1"});

        FileModel fm = new FileModel(sourceFile,
                resourceManager.getString(sourceLang + ".corpus"));
        String sourcePosOutput = runPOS(sourceFile, sourceLang, "source");
        String targetPosOutput = runPOS(targetFile, targetLang, "target");

        String targetPPLPos = runNGramPPLPos(targetPosOutput + PosTagger.getXPOS());
        System.out.println("---------TARGET PPLPOS: " + targetPPLPos);
        PPLProcessor pplPosTarget = new PPLProcessor(targetPPLPos,
                new String[]{"poslogprob", "posppl", "posppl1"});

        loadGiza();
        processNGrams();

        try {
            BufferedReader brSource = new BufferedReader(new FileReader(
                    sourceFileName));
            BufferedReader brTarget = new BufferedReader(new FileReader(
                    targetFileName));
            BufferedWriter output = new BufferedWriter(new FileWriter(out));
            BufferedReader posSource = null;
            BufferedReader posTarget = null;
            boolean posSourceExists = ResourceManager
                    .isRegistered("sourcePosTagger");
            boolean posTargetExists = ResourceManager
                    .isRegistered("targetPosTagger");
            POSProcessor posSourceProc = null;
            POSProcessor posTargetProc = null;
            if (posSourceExists) {
                posSourceProc = new POSProcessor(sourcePosOutput);
                posSource = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePosOutput), "utf-8"));
            }
            if (posTargetExists) {
                posTargetProc = new POSProcessor(targetPosOutput);
                posTarget = new BufferedReader(new InputStreamReader(new FileInputStream(targetPosOutput)));
            }
            ResourceManager.printResources();

            Sentence targetSent;
            // HACK
            Sentence sourceSent;
            int sentCount = 0;
            // HACK
            String lineSource = brSource.readLine();
            String lineTarget = brTarget.readLine();
            // HACK
            int result;

            while ((lineSource != null) && (lineTarget != null)) {

                //the MADA-tokenised files contain start each sentence with the setence ID. We put it there (why?) - no we've got to remove it
                lineSource = lineSource.trim().substring(lineSource.indexOf(" ")).replace("+", "");
                sourceSent = new Sentence(lineSource,
                        sentCount);
                targetSent = new Sentence(lineTarget, sentCount);
                System.out.println("Processing sentence " + sentCount);
                if (posSourceExists) {

                    posSourceProc.processSentence(sourceSent);

                }
                if (posTargetExists) {

                    posTargetProc.processSentence(targetSent);
                }

                sourceSent.computeNGrams(3);
                targetSent.computeNGrams(3);

                pplProcSource.processNextSentence(sourceSent);

                pplProcTarget.processNextSentence(targetSent);

                pplPosTarget.processNextSentence(targetSent);

                sourceSent.setValue("ngramcounts", ngramModelSource);
                targetSent.setValue("ngramcounts", ngramModelTarget);

//				coh.processNextSentence(targetSent);
                mtop.processNextSentence(sourceSent);

                ++sentCount;
                output.write(featureManager.runFeatures(sourceSent, targetSent));
                output.write("\r\n");

                lineSource = brSource.readLine();
                lineTarget = brTarget.readLine();

            }
            //		featureManager.printFeatureIndeces();
            if (posSource != null) {
                posSource.close();
            }
            if (posTarget != null) {
                posTarget.close();
            }

            brSource.close();
            brTarget.close();
            output.close();

            Logger.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        constructFolders();
        preprocessing();
        if (getMod().equals("bb")) {
            runBB();
        } else if (getMod().equals("gb")) {
            runGB();
        } else {
            runAll();
        }
    }

    private LanguageModel[] getNGramModels() {
        //Create ngram file processors:
        NGramProcessor sourceNgp = new NGramProcessor(resourceManager.getString(sourceLang + ".ngram"));
        NGramProcessor targetNgp = new NGramProcessor(resourceManager.getString(targetLang + ".ngram"));

        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private NgramCountProcessor[] getNgramProcessors() {
        //Register resource:
        ResourceManager.registerResource("ngramcount");

        //Get source and target Language Models:
        LanguageModel[] ngramModels = this.getNGramModels();
        LanguageModel ngramModelSource = ngramModels[0];
        LanguageModel ngramModelTarget = ngramModels[1];

        //Create source and target processors:
        NgramCountProcessor sourceProcessor = new NgramCountProcessor(ngramModelSource);
        NgramCountProcessor targetProcessor = new NgramCountProcessor(ngramModelTarget);
        NgramCountProcessor[] result = new NgramCountProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
        return result;
    }

    private BlockAlignmentProcessor getBlockAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("blockalignments");

        //Create source and target processors:
        BlockAlignmentProcessor targetProcessor = new BlockAlignmentProcessor(resourceManager.getProperty("alignments.file"));

        //Return processors:
        return targetProcessor;
    }

    private static void tokenizeSource(File sourcefile) {
        //run tokenizer for source (English)
        System.out.println("running tokenizer");

        String src_abbr = "";
        if (sourceLang.equalsIgnoreCase("english")) {
            src_abbr = "en";
        } else if (sourceLang.equalsIgnoreCase("spanish")) {
            src_abbr = "es";
        } else if (sourceLang.equalsIgnoreCase("french")) {
            src_abbr = "fr";
        } else if (sourceLang.equalsIgnoreCase("german")) {
            src_abbr = "de";
        } else if (targetLang.equalsIgnoreCase("dutch")) {
            src_abbr = "nl";
        } else if (targetLang.equalsIgnoreCase("portuguese")) {
            src_abbr = "pt";
        } else {
            System.out.println("Don't recognise the source language");
        }

        String truecasePath = "";
        if (null != resourceManager.getProperty(sourceLang + ".lowercase")) {
            truecasePath = resourceManager.getProperty(sourceLang + ".lowercase") + " -q ";
        } else {
            truecasePath = resourceManager.getString(sourceLang + ".truecase") + " --model " + resourceManager.getString(sourceLang + ".truecase.model");
        }
        //Tokenizer enTok = new Tokenizer(sourcefile.getPath(), sourcefile.getPath() + ".tok", truecasePath, resourceManager.getString(sourceLang + ".tokenizer"), src_abbr, forceRun);

        //enTok.run();
        //sourceFile = enTok.getTok();
        System.out.println(sourceFile);
    }

    private static void tokenizeTarget(File targetfile) {
        String tgt_abbr = "";
        if (targetLang.equalsIgnoreCase("english")) {
            tgt_abbr = "en";
        } else if (targetLang.equalsIgnoreCase("spanish")) {
            tgt_abbr = "es";
        } else if (targetLang.equalsIgnoreCase("french")) {
            tgt_abbr = "fr";
        } else if (targetLang.equalsIgnoreCase("german")) {
            tgt_abbr = "de";
        } else if (targetLang.equalsIgnoreCase("dutch")) {
            tgt_abbr = "nl";
        } else if (targetLang.equalsIgnoreCase("portuguese")) {
            tgt_abbr = "pt";
        } else {
            System.out.println("Don't recognise the target language");
        }

        String truecasePath = "";
        if (null != resourceManager.getProperty(targetLang + ".lowercase")) {
            truecasePath = resourceManager.getProperty(targetLang + ".lowercase") + " -q ";
        } else {
            truecasePath = resourceManager.getString(targetLang + ".truecase") + " --model " + resourceManager.getString(targetLang + ".truecase.model");
        }
        //Tokenizer esTok = new Tokenizer(targetfile.getPath(), targetfile.getPath() + ".tok", truecasePath, resourceManager.getString(targetLang + ".tokenizer"), tgt_abbr, forceRun);

        //esTok.run();
        //targetFile = esTok.getTok();
        System.out.println(targetFile);
    }

    private static void tokenizeFiles() {
        String sourceInputFolder = input + File.separator + sourceLang;
        String targetInputFolder = input + File.separator + targetLang;
        File origSourceFile = new File(sourceFile);
        File inputSourceFile = new File(sourceInputFolder + File.separator + origSourceFile.getName());

        File origTargetFile = new File(targetFile);
        File inputTargetFile = new File(targetInputFolder + File.separator + origTargetFile.getName());
        try {
            copyFile(origSourceFile, inputSourceFile);
            copyFile(origTargetFile, inputTargetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Discover whether or not to tokenize/truecase the source file:
        String srcPreprocess = resourceManager.getProperty(sourceLang + ".preprocess");

        //Discover whether or not to tokenize/truecase the source file:
        String trgPreprocess = resourceManager.getProperty(targetLang + ".preprocess");

        if ("1".equals(srcPreprocess)) {
            System.out.println("Tokenizing and truecasing source file...");
            tokenizeSource(inputSourceFile);
            System.out.println("Source file tokenized and truecased!");
        }
        if ("1".equals(trgPreprocess)) {
            System.out.println("Tokenizing and truecasing target file...");
            tokenizeTarget(inputTargetFile);
            System.out.println("Target file tokenized and truecased!");
        }
    }
}
