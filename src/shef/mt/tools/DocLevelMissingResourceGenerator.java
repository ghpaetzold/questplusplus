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
import shef.mt.DocLevelFeatureExtractor;
import shef.mt.util.NGramSorter;

public class DocLevelMissingResourceGenerator {

    private DocLevelFeatureExtractor dlfe;

    public DocLevelMissingResourceGenerator(DocLevelFeatureExtractor dlfe) {
        this.dlfe = dlfe;
    }

    public void produceMissingResources() {
        //Get required resources:
        HashSet<String> required = this.dlfe.getFeatureManager().getRequiredResources();

        //Produce missing alignment file, if necessary:
        this.produceMissingAlignments(required);

        //Produce missing language models, if necessary:
        this.produceMissingLanguageModels(required);

        //Produce missing ngram count files, if necessary:
        this.produceMissingNgramCounts(required);

        //Produce missing pos ngram count files, if necessary:
        this.produceMissingPOSNgramCounts(required);

    }

    private void runFastAlign(String inputPath, String outputPath) {
        //Generate path for fast_align:
        String fast_align = this.dlfe.getResourceManager().getProperty("tools.fast_align.path") + File.separator + "fast_align";

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

            //process.waitFor(30, TimeUnit.SECONDS);
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
        if (required.contains("alignments")) {
            if (this.dlfe.getResourceManager().getProperty("alignments.file") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.fast_align.path") != null) {
                    System.out.println("Producing resource: alignments.file");

                    //Create fast_align input file:
                    String inputPath = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.inp";

                    //Create fast_align output file:
                    String outputPath = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.out";

                    try {
                        BufferedReader sourceBR = new BufferedReader(new FileReader(this.dlfe.getSourceFile()));
                        BufferedReader targetBR = new BufferedReader(new FileReader(this.dlfe.getTargetFile()));

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
                        this.dlfe.getResourceManager().put("alignments.file", outputPath);
                        System.out.println("Resource alignments.file saved at: " + outputPath);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(DocLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DocLevelFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void produceMissingLanguageModels(HashSet<String> required) {
        //Check if source LM is missing:
        if (required.contains("logprob") || required.contains("ppl") || required.contains("ppl1")) {
            if (this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".lm") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".corpus") != null) {
                        if (this.dlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.dlfe.getSourceLang() + ".lm");
                            String outputPath = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.dlfe.getSourceLang() + File.separator + this.dlfe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.dlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.dlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.dlfe.getResourceManager().setProperty(this.dlfe.getSourceLang() + ".lm",
                                        outputPath);
                                System.out.println("Resource " + this.dlfe.getSourceLang() + ".lm " + "saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".lm and SRILM is not available!");
                }
            }

            //Check if target LM is missing:
            if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".lm") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".corpus") != null) {
                        if (this.dlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.dlfe.getTargetLang() + ".lm");
                            String outputPath = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.dlfe.getTargetLang() + File.separator + this.dlfe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.dlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.dlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.dlfe.getResourceManager().setProperty(this.dlfe.getTargetLang() + ".lm",
                                        outputPath);
                                System.out.println("Resource " + this.dlfe.getTargetLang() + ".lm " + "saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".lm and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("ngramcount")) {
            if (this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".ngram") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".corpus") != null) {
                        if (this.dlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.dlfe.getSourceLang() + ".ngram");
                            String rawNgramFile = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.dlfe.getSourceLang() + File.separator + this.dlfe.getSourceLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.dlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.dlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.dlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.dlfe.getResourceManager().setProperty(this.dlfe.getSourceLang() + ".ngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.dlfe.getSourceLang() + ".ngram " + "saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.dlfe.getSourceLang() + ".ngram and SRILM is not available!");
                }
            }

            //Check if target NGRAM file is missing:
            if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".ngram") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".corpus") != null) {
                        if (this.dlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.dlfe.getTargetLang() + ".ngram");
                            String rawNgramFile = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.dlfe.getTargetLang() + File.separator + this.dlfe.getTargetLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.dlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.dlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.dlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.dlfe.getResourceManager().setProperty(this.dlfe.getTargetLang() + ".ngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.dlfe.getTargetLang() + ".ngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".ngram and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingPOSNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("posngramcount")) {
            //Check if target NGRAM file is missing:
            if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".posngram") == null) {
                if (this.dlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".poscorpus") != null) {
                        if (this.dlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.dlfe.getTargetLang() + ".posngram");
                            String rawNgramFile = this.dlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.dlfe.getTargetLang() + File.separator + this.dlfe.getTargetLang() + "_posngram.posngram";
                            String[] args = new String[]{
                                this.dlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.dlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".poscorpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.dlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.dlfe.getResourceManager().setProperty(this.dlfe.getTargetLang() + ".posngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.dlfe.getTargetLang() + ".posngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".posngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".posngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.dlfe.getTargetLang() + ".posngram and SRILM is not available!");
                }
            }
        }
    }
    

    
 }







