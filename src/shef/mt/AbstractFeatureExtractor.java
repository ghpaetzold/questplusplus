package shef.mt;

import shef.mt.util.PropertiesManager;
import shef.mt.util.Logger;
import java.io.*;


/**
 * AbstractFeatureExtractor is the base class for any FeatureExtractor. It
 * provides implementations for the methods that create the folder structure of
 * the projec and definitions for methods that have to be implemented b
 * subclasses
 *
 *
 * @author Catalina Hallett<br>
 */
public abstract class AbstractFeatureExtractor {

    private String workDir;
    private static String output;
    private String sourceFile;
    private String targetFile;
    private String sourceLang;
    private String targetLang;
    protected PropertiesManager resourceManager;
    private String config;
    /**
     * path to the input folder
     */
    private String input;
    /**
     * running mode: bb , gb or all
     */
    private String mod;

    /**
     * returns the configuration file
     *
     * @return
     */
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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
     * path to the output folder
     */
    /**
     * Initialises the FeatureExtractor from a set of parameters, for example
     * sent as command-line arguments
     *
     * @param args The list of arguments
     *
     */
    public AbstractFeatureExtractor(String[] args) {
        setWorkDir(System.getProperty("user.dir"));
        new Logger("log.txt");
        parseArguments(args);

        setInput(workDir + File.separator + resourceManager.getString("input"));
        setOutput(output = workDir + File.separator + resourceManager.getString("output"));

    }

    /**
     * returns the current working directory
     *
     * @return
     */
    public String getWorkDir() {
        return workDir;
    }

    /**
     * sets the currenworking direcory
     *
     * @param workDir
     */
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    /**
     * gets the "input" folder path
     *
     * @return
     */
    public String getInput() {
        return input;
    }

    /**
     * sets the input folder path
     *
     * @param input
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * gets the output folder path
     *
     * @return
     */
    public static String getOutput() {
        return output;
    }

    /**
     * sets the output folder path
     *
     * @param output
     */
    public static void setOutput(String output) {
        AbstractFeatureExtractor.output = output;
    }

    /**
     * gets the path to the source input file
     *
     * @return
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * sets the path to the source input file
     *
     * @return
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
     * gets the path to the source target file
     *
     * @return
     */
    public String getTargetFile() {
        return targetFile;
    }

    /**
     * gets the path to the target input file
     *
     * @return
     */
    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    /**
     * retrns the source language
     *
     * @return
     */
    public String getSourceLang() {
        return sourceLang;
    }

    /**
     * sets the source language
     *
     * @param sourceLang
     */
    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    /**
     * returns the target language
     *
     * @return
     */
    public String getTargetLang() {
        return targetLang;
    }

    /**
     * sets the target language
     *
     * @param targetLang
     */
    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    /**
     * returns the ResourceManager associated with this FeatureExtractor
     *
     * @return
     */
    public PropertiesManager getResourceManager() {
        return resourceManager;
    }

    /**
     * sets the ResourceManager associated with this FeatureExtractor
     *
     * @param resourceManager
     */
    public void setResourceManager(PropertiesManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * The implementations of this abstract method should handle the command
     * line arguments and retrieve parameters required by the application Since
     * the command line arguments are application specific, this method doesn't
     * have a default implementation
     *
     * @param args The command line arguments
     */
    public abstract void parseArguments(String[] args);

    /**
     * Performs some basic processing of the input source and target files. The
     * input files are also copied to the /input folder. This is necessary
     * because the some tools (e.g., the MADA morphological analyser) produce
     * their output in the same folder as the input file, which may cause
     * problems if the right access rights are not available for that particular
     * folder
     */
    protected void preprocessing() {
        String sourceInputFolder = input + File.separator + sourceLang;
        String targetInputFolder = input + File.separator + targetLang;
        File origSourceFile = new File(getSourceFile());
        File inputSourceFile = new File(sourceInputFolder + File.separator + origSourceFile.getName());

        System.out.println("source input:" + getSourceFile());
        System.out.println("target input:" + getTargetFile());
        File origTargetFile = new File(getTargetFile());
        File inputTargetFile = new File(targetInputFolder + File.separator + origTargetFile.getName());
        try {
            System.out.println("copying input to " + inputSourceFile.getPath());
            copyFile(origSourceFile, inputSourceFile);
            System.out.println("copying input to " + inputTargetFile.getPath());
            copyFile(origTargetFile, inputTargetFile);
            setSourceFile(inputSourceFile.getPath());
            setTargetFile(inputTargetFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Runs the Feature Extractor<br> <ul> <li>constructs the required folders
     * <li>runs the pre-processing tools <li>runs the BB features, GB features
     * or both according to the command line parameters </ul>
     */
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

    public abstract void runAll();

    /**
     * constructs the folders required by the application. These are, typically:
     * <br> <ul><li>/input and subfolders <ul> <li>/input/<i>sourceLang</i>,
     * /input/<i>targetLang</i> (for storing the results of processing the input
     * files with various tools, such as pos tagger, transliterator,
     * morphological analyser),<br> <li>/input/systems/<i>systemName</i> (for
     * storing system specific resources - for example, the compiled and
     * processed word lattices in the case of the IBM system </ul> <li> /output
     * (for storing the resulting feature files), </ul> Subclasses can override
     * this method to construct, for example, subfolders of the systems folder
     * for each available MT system, or temporary folders
     */
    public void constructFolders() {

        File f = new File(input);
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }


        f = new File(input + File.separator + sourceLang);
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }
        f = new File(input + File.separator + targetLang);
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }
        f = new File(input + File.separator + targetLang + File.separator
                + "temp");
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }

        f = new File(input + File.separator + "systems");
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }

        String output = resourceManager.getString("output");
        f = new File(output);
        if (!f.exists()) {
            f.mkdir();
            System.out.println("folder created " + f.getPath());
        }
    }

    /**
     * runs the BB features
     */
    public abstract void runBB();

    /**
     * runs the GB features
     */
    public abstract void runGB();

    /**
     * makes a copy of one file into another
     *
     * @param sourceFile the source file path
     * @param destFile the destination file path
     * @throws IOException
     */
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
}
