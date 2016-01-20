package shef.mt.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.FeatureExtractor;
import shef.mt.WordLevelFeatureExtractor;
import shef.mt.util.NGramSorter;

/**
 * Generates resources that are missing from the config file.
 *
 * @author GustavoH
 */
public class MissingResourceGenerator {

    private FeatureExtractor fe;

    public MissingResourceGenerator(FeatureExtractor fe) {
        this.fe = fe;
    }

    public void produceMissingResources() {
        //Get required resources:
        HashSet<String> required = this.fe.getFeatureManager().getRequiredResources();

        //Produce missing alignment file, if necessary:
        //this.produceMissingAlignments(required);

        //Produce missing language models, if necessary:
        this.produceMissingLanguageModels(required);

        //Produce missing ngram count files, if necessary:
        this.produceMissingNgramCounts(required);

        //Produce missing pos ngram count files, if necessary:
        this.produceMissingPOSNgramCounts(required);
    }

    private void runFastAlign(String inputPath, String outputPath) {
        //Generate path for fast_align:
        String fast_align = this.fe.getResourceManager().getProperty("tools.fast_align.path") + File.separator + "fast_align";

        //Create arguments:
        String[] args = new String[]{
            fast_align,
            "-i",
            inputPath,
            "-d",
            "-o",
            "-v"};

        try {
            //Run fast_align:
            Process process = Runtime.getRuntime().exec(args);

            //
            // process.waitFor(30, TimeUnit.SECONDS);
            process.waitFor();
            //Create BufferedReader of fast align's output:
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //Create BufferedWriter to save output:
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));

            //Save output file:
            while (br.ready()) {
                bw.write(br.readLine().trim() + "\n");
            }

            //Close reader and writer:
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void produceMissingAlignments(HashSet<String> required) {
        //Check if alignment file is missing:
        if (required.contains("alignments.file")) {
            if (this.fe.getResourceManager().getProperty("alignments.file") == null) {
                if (this.fe.getResourceManager().getProperty("tools.fast_align.path") != null) {
                    System.out.println("Producing resource: alignments.file");

                    //Create fast_align input file:
                    String inputPath = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.inp";

                    //Create fast_align output file:
                    String outputPath = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.out";

                    try {
                        BufferedReader sourceBR = new BufferedReader(new FileReader(this.fe.getSourceFile()));
                        BufferedReader targetBR = new BufferedReader(new FileReader(this.fe.getTargetFile()));

                        BufferedWriter outputBW = new BufferedWriter(new FileWriter(inputPath));

                        while (sourceBR.ready()) {
                            String sourceSentence = sourceBR.readLine().trim();
                            String targetSentence = targetBR.readLine().trim();

                            outputBW.write(sourceSentence + " ||| " + targetSentence);
                            outputBW.newLine();
                        }

                        sourceBR.close();
                        targetBR.close();
                        outputBW.close();

                        //Run fast align on input file:
                        this.runFastAlign(inputPath, outputPath);

                        //Return resulting processor:
                        this.fe.getResourceManager().put("alignments.file", outputPath);
                        System.out.println("Resource alignments.file saved at: " + outputPath);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(WordLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void produceMissingLanguageModels(HashSet<String> required) {
        //Check if source LM is missing:
        if (required.contains("source.lm")) {
            if (this.fe.getResourceManager().getProperty("source.lm") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("source.corpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: source.lm");
                            String outputPath = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getSourceLang() + File.separator + this.fe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("source.corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.fe.getResourceManager().setProperty("source.lm",
                                        outputPath);
                                System.out.println("Resource source.lm saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource source.lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource source.lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource source.lm and SRILM is not available!");
                }
            }
        }

        if (required.contains("target.lm")) {
            //Check if target LM is missing:
            if (this.fe.getResourceManager().getProperty("target.lm") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("target.corpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: target.lm");
                            String outputPath = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getTargetLang() + File.separator + this.fe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("target.corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.fe.getResourceManager().setProperty("target.lm",
                                        outputPath);
                                System.out.println("Resource " + "target.lm " + "saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource target.lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource target.lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource target.lm and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("source.ngram")) {
            if (this.fe.getResourceManager().getProperty("source.ngram") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("source.corpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + "source.ngram");
                            String rawNgramFile = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getSourceLang() + File.separator + this.fe.getSourceLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("source.corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.fe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.fe.getResourceManager().setProperty("source.ngram", rawNgramFile + ".clean");
                                System.out.println("Resource source.ngram " + "saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource source.ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource source.ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource source.ngram and SRILM is not available!");
                }
            }
        }

        if (required.contains("target.ngram")) {
            //Check if target NGRAM file is missing:
            if (this.fe.getResourceManager().getProperty("target.ngram") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("target.corpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: target.ngram");
                            String rawNgramFile = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getTargetLang() + File.separator + this.fe.getTargetLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("target.corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.fe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.fe.getResourceManager().setProperty("target.ngram", rawNgramFile + ".clean");
                                System.out.println("Resource target.ngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource target.ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource target.ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource target.ngram and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingPOSNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("target.posngram")) {
            //Check if target NGRAM file is missing:
            if (this.fe.getResourceManager().getProperty("target.posngram") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("target.poscorpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: target.posngram");
                            String rawNgramFile = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getTargetLang() + File.separator + this.fe.getTargetLang() + "_posngram.posngram";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("target.poscorpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.fe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.fe.getResourceManager().setProperty("target.posngram", rawNgramFile + ".clean");
                                System.out.println("Resource target.posngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource target.posngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource target.posngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource target.posngram and SRILM is not available!");
                }
            }
        }

        if (required.contains("source.posngram")) {
            //Check if target NGRAM file is missing:
            if (this.fe.getResourceManager().getProperty("source.posngram") == null) {
                if (this.fe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.fe.getResourceManager().getProperty("source.poscorpus") != null) {
                        if (this.fe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: source.posngram");
                            String rawNgramFile = this.fe.getResourceManager().getProperty("resourcesPath") + File.separator + this.fe.getSourceLang() + File.separator + this.fe.getSourceLang() + "_posngram.posngram";
                            String[] args = new String[]{
                                this.fe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.fe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.fe.getResourceManager().getProperty("source.poscorpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.fe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.fe.getResourceManager().setProperty("source.posngram", rawNgramFile + ".clean");
                                System.out.println("Resource source.posngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource source.posngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource source.posngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource source.posngram and SRILM is not available!");
                }
            }
        }
    }
}
