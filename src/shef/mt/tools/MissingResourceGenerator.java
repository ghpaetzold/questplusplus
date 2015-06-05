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
import shef.mt.enes.WordLevelFeatureExtractor;
import shef.mt.util.NGramSorter;

public class MissingResourceGenerator {

    private WordLevelFeatureExtractor wlfe;

    public MissingResourceGenerator(WordLevelFeatureExtractor wlfe) {
        this.wlfe = wlfe;
    }

    public void produceMissingResources() {
        //Get required resources:
        HashSet<String> required = this.wlfe.getFeatureManager().getRequiredResources();

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
        String fast_align = this.wlfe.getResourceManager().getProperty("tools.fast_align.path") + File.separator + "fast_align";

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

            process.waitFor(30, TimeUnit.SECONDS);

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
            if (this.wlfe.getResourceManager().getProperty("alignments.file") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.fast_align.path") != null) {
                    System.out.println("Producing resource: alignments.file");

                    //Create fast_align input file:
                    String inputPath = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.inp";

                    //Create fast_align output file:
                    String outputPath = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + "source_to_target.out";

                    try {
                        BufferedReader sourceBR = new BufferedReader(new FileReader(this.wlfe.getSourceFile()));
                        BufferedReader targetBR = new BufferedReader(new FileReader(this.wlfe.getTargetFile()));

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
                        this.wlfe.getResourceManager().put("alignments.file", outputPath);
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
        if (required.contains("logprob") || required.contains("ppl") || required.contains("ppl1")) {
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".lm") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".corpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getSourceLang() + ".lm");
                            String outputPath = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getSourceLang() + File.separator + this.wlfe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.wlfe.getResourceManager().setProperty(this.wlfe.getSourceLang() + ".lm",
                                        outputPath);
                                System.out.println("Resource " + this.wlfe.getSourceLang() + ".lm " + "saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".lm and SRILM is not available!");
                }
            }

            //Check if target LM is missing:
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".lm") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".corpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getTargetLang() + ".lm");
                            String outputPath = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getTargetLang() + File.separator + this.wlfe.getSourceLang() + "_lm.lm";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".corpus"),
                                "-lm",
                                outputPath};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();
                                this.wlfe.getResourceManager().setProperty(this.wlfe.getTargetLang() + ".lm",
                                        outputPath);
                                System.out.println("Resource " + this.wlfe.getTargetLang() + ".lm " + "saved at: " + outputPath);
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".lm and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".lm and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".lm and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("ngramcount")) {
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".ngram") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".corpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getSourceLang() + ".ngram");
                            String rawNgramFile = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getSourceLang() + File.separator + this.wlfe.getSourceLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.wlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.wlfe.getResourceManager().setProperty(this.wlfe.getSourceLang() + ".ngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.wlfe.getSourceLang() + ".ngram " + "saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".ngram and SRILM is not available!");
                }
            }

            //Check if target NGRAM file is missing:
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".ngram") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".corpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getTargetLang() + ".ngram");
                            String rawNgramFile = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getTargetLang() + File.separator + this.wlfe.getTargetLang() + "_ngram.ngram";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".corpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.wlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.wlfe.getResourceManager().setProperty(this.wlfe.getTargetLang() + ".ngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.wlfe.getTargetLang() + ".ngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".ngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".ngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".ngram and SRILM is not available!");
                }
            }
        }
    }

    private void produceMissingPOSNgramCounts(HashSet<String> required) {
        //Check if source NGRAM file is missing:
        if (required.contains("posngramcount")) {
            //Check if target NGRAM file is missing:
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".posngram") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".poscorpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getTargetLang() + ".posngram");
                            String rawNgramFile = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getTargetLang() + File.separator + this.wlfe.getTargetLang() + "_posngram.posngram";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".poscorpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.wlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.wlfe.getResourceManager().setProperty(this.wlfe.getTargetLang() + ".posngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.wlfe.getTargetLang() + ".posngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".posngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".posngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getTargetLang() + ".posngram and SRILM is not available!");
                }
            }
            
            //Check if target NGRAM file is missing:
            if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".posngram") == null) {
                if (this.wlfe.getResourceManager().getProperty("tools.ngram.path") != null) {
                    if (this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".poscorpus") != null) {
                        if (this.wlfe.getResourceManager().getProperty("resourcesPath") != null) {
                            System.out.println("Producing resource: " + this.wlfe.getSourceLang() + ".posngram");
                            String rawNgramFile = this.wlfe.getResourceManager().getProperty("resourcesPath") + File.separator + this.wlfe.getSourceLang() + File.separator + this.wlfe.getSourceLang() + "_posngram.posngram";
                            String[] args = new String[]{
                                this.wlfe.getResourceManager().getProperty("tools.ngram.path") + File.separator + "ngram-count",
                                "-order",
                                this.wlfe.getResourceManager().getProperty("ngramsize"),
                                "-text",
                                this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".poscorpus"),
                                "-write",
                                rawNgramFile};
                            try {
                                Process process = Runtime.getRuntime().exec(args);
                                process.waitFor();

                                NGramSorter.run(rawNgramFile, 4, Integer.parseInt(this.wlfe.getResourceManager().getProperty("ngramsize")), 2, rawNgramFile);

                                this.wlfe.getResourceManager().setProperty(this.wlfe.getSourceLang() + ".posngram", rawNgramFile + ".clean");
                                System.out.println("Resource " + this.wlfe.getSourceLang() + ".posngram saved at: " + rawNgramFile + ".clean");
                            } catch (IOException e) {
                                System.out.println("ERROR: Problem while running SRILM.");
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println("ERROR: SRILM could not finish file creation.");
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".posngram and resources path is not defined!");
                        }
                    } else {
                        System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".posngram and corpus is not available!");
                    }
                } else {
                    System.out.println("Missing resource " + this.wlfe.getSourceLang() + ".posngram and SRILM is not available!");
                }
            }
        }
    }
}
