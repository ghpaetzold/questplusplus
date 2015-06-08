package shef.mt.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import shef.mt.enes.DocLevelFeatureExtractor;

public class DocLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;

    private DocLevelFeatureExtractor dlfe;

    public DocLevelProcessorFactory(DocLevelFeatureExtractor dlfe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.dlfe = dlfe;

        //Get required resources:
        HashSet<String> requirements = dlfe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        ArrayList<ResourceProcessor> sourceProcessors = new ArrayList<>();
        ArrayList<ResourceProcessor> targetProcessors = new ArrayList<>();

        if (requirements.contains("Giza")) {
            //Get alignment processors:
            GizaProcessor gizaProcessor = this.getGizaProcessor();

            //Add them to processor vectors:
            targetProcessors.add(gizaProcessor);
            
        }
        
        if (requirements.contains("postagger")) {
            //Get stopwords processors:
            POSTaggerProcessor[] posTaggerProcessors = this.getPOSTaggerProcessors();
            POSTaggerProcessor posTaggerProcSource = posTaggerProcessors[0];
            POSTaggerProcessor posTaggerProcTarget = posTaggerProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(posTaggerProcSource);
            targetProcessors.add(posTaggerProcTarget);
        }
        
        if (requirements.contains("discrep")) {
            //Get stopwords processors:
            DiscourseRepetition[] discRepProcessors = this.getDiscourseRepetitionProcessors();
            DiscourseRepetition discRepProcSource = discRepProcessors[0];
            DiscourseRepetition discRepProcTarget = discRepProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(discRepProcSource);
            targetProcessors.add(discRepProcTarget);
        }
        
        
        if (requirements.contains("stopwords")) {
            //Get stopwords processors:
            StopWordsProcessor[] stopWordsProcessors = this.getStopWordsProcessors();
            StopWordsProcessor stopWordsProcSource = stopWordsProcessors[0];
            StopWordsProcessor stopWordsProcTarget = stopWordsProcessors[0];

            //Add them to processor vectors:
            sourceProcessors.add(stopWordsProcSource);
            targetProcessors.add(stopWordsProcTarget);
        }

        if (requirements.contains("alignments")) {
            //Get alignment processors:
            AlignmentProcessor alignmentProcessor = this.getAlignmentProcessor();

            //Add them to processor vectors:
            targetProcessors.add(alignmentProcessor);
        }

        if (requirements.contains("punctuation")) {
            //Get punctuation processors:
            PunctuationProcessor punctuationProcessor = this.getPunctuationProcessor();

            //Add them to processor vectors:
            targetProcessors.add(punctuationProcessor);
        }

        if (requirements.contains("ngramcount")) {
            //Run SRILM on ngram count files:
            NgramCountProcessor[] ngramProcessors = this.getNgramProcessors();
            NgramCountProcessor ngramProcessorSource = ngramProcessors[0];
            NgramCountProcessor ngramProcessorTarget = ngramProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(ngramProcessorSource);
            targetProcessors.add(ngramProcessorTarget);
        }

        if (requirements.contains("posngramcount")) {
            //Run SRILM on ngram count files:
            POSNgramCountProcessor ngramProcessorTarget = this.getPOSNgramProcessor();

            //Add them to processor vectors:
            targetProcessors.add(ngramProcessorTarget);
        }

        if (requirements.contains("logprob") || requirements.contains("ppl") || requirements.contains("ppl1")) {
            //Run SRILM on language models:
            PPLProcessor[] pplProcessors = this.getLMProcessors();
            PPLProcessor pplProcSource = pplProcessors[0];
            PPLProcessor pplProcTarget = pplProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(pplProcSource);
            targetProcessors.add(pplProcTarget);
        }
        
        if (requirements.contains("poslogprob")) {
            //Run SRILM on language models:
            PPLProcessor[] pplPosProcessors = this.getLMPosProcessors();
            PPLProcessor pplPosProcSource = pplPosProcessors[0];
            PPLProcessor pplPosProcTarget = pplPosProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(pplPosProcSource);
            targetProcessors.add(pplPosProcTarget);
        }

        if (requirements.contains("postags") || requirements.contains("depcounts")) {
            //Get parsing processors:
            ParsingProcessor[] parsingProcessors = this.getParsingProcessors(requirements);
            ParsingProcessor parsingSource = parsingProcessors[0];
            ParsingProcessor parsingTarget = parsingProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(parsingSource);
            targetProcessors.add(parsingTarget);
        }

        if (requirements.contains("sensecounts")) {
            //Get sense processor:
            SenseProcessor[] senseProcessors = this.getSenseProcessors();
            SenseProcessor senseSource = senseProcessors[0];
            SenseProcessor senseTarget = senseProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(senseSource);
            targetProcessors.add(senseTarget);
        }

        if (requirements.contains("reftranslation")) {
            //Get reference translations processor:
            RefTranslationProcessor refTranslationProcessor = this.getRefTranslationProcessor();

            //Add them to processor vectors:
            targetProcessors.add(refTranslationProcessor);
        }

        if (requirements.contains("wordcounts")) {
            //Get sense processor:
            WordCountProcessor[] countProcessors = this.getWordCountProcessors();
            WordCountProcessor countSource = countProcessors[0];
            WordCountProcessor countTarget = countProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(countSource);
            targetProcessors.add(countTarget);
        }

        if (requirements.contains("translationcounts")) {
            //Get translation counts processor:
            TranslationProbabilityProcessor translationProbProcessor = this.getTranslationProbabilityProcessor();

            //Add them to processor vectors:
            targetProcessors.add(translationProbProcessor);
        }

        //Transform array lists in vectors:
        ResourceProcessor[] sourceProcessorVector = new ResourceProcessor[sourceProcessors.size()];
        ResourceProcessor[] targetProcessorVector = new ResourceProcessor[targetProcessors.size()];
        sourceProcessorVector = (ResourceProcessor[]) sourceProcessors.toArray(sourceProcessorVector);
        targetProcessorVector = (ResourceProcessor[]) targetProcessors.toArray(targetProcessorVector);

        //Return vectors:
        this.resourceProcessors = new ResourceProcessor[][]{sourceProcessorVector, targetProcessorVector};
    }

    private ParsingProcessor[] getParsingProcessors(HashSet<String> requirements) {
        //Register resources:
        if (requirements.contains("postags")) {
            ResourceManager.registerResource("postags");
        }
        if (requirements.contains("depcounts")) {
            ResourceManager.registerResource("depcounts");
        }

        //Get paths to Stanford Parser source language models:
        String POSModel = this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".POSModel");
        String parseModel = this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".parseModel");

        //Create source language ParsingProcessor:
        ParsingProcessor sourceProcessor = new ParsingProcessor(this.dlfe.getSourceLang(), POSModel, parseModel, requirements);

        //Get paths to Stanford Parser target language models:
        POSModel = this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".POSModel");
        parseModel = this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".parseModel");

        //Create target language ParsingProcessor:
        ParsingProcessor targetProcessor = new ParsingProcessor(this.dlfe.getTargetLang(), POSModel, parseModel, requirements);

        //Return processors:
        return new ParsingProcessor[]{sourceProcessor, targetProcessor};
    }

    private SenseProcessor[] getSenseProcessors() {
        //Register resource:
        ResourceManager.registerResource("sensecounts");

        //Get path to Universal Wordnet:
        String wordnetPath = this.dlfe.getResourceManager().getProperty("tools.universalwordnet.path");

        //Create SenseProcessor object:
        SenseProcessor sourceProcessor = new SenseProcessor(wordnetPath, this.dlfe.getSourceLang());
        SenseProcessor targetProcessor = new SenseProcessor(wordnetPath, this.dlfe.getTargetLang());
        SenseProcessor[] result = new SenseProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
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

    private StopWordsProcessor[] getStopWordsProcessors() {
        //Register resource:
        ResourceManager.registerResource("stopwords");

        //Get paths to stop word lists:
        String sourcePath = this.dlfe.getResourceManager().getProperty(this.dlfe.getSourceLang() + ".stopwords");
        String targetPath = this.dlfe.getResourceManager().getProperty(this.dlfe.getTargetLang() + ".stopwords");

        //Generate processors:
        StopWordsProcessor sourceProcessor = new StopWordsProcessor(sourcePath);
        StopWordsProcessor targetProcessor = new StopWordsProcessor(targetPath);
        StopWordsProcessor[] result = new StopWordsProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
        return result;
    }

    private PunctuationProcessor getPunctuationProcessor() {
        //Register resource:
        ResourceManager.registerResource("punctuation");

        //Create punctuation processor:
        PunctuationProcessor processor = new PunctuationProcessor();

        //Return processor:
        return processor;
    }
    private PPLProcessor[] getLMPosProcessors() {
        //Generate output paths:
        String sourceOutput = this.dlfe.getSourceFile() + ".poslm";
        String targetOutput = this.dlfe.getTargetFile() + ".poslm";
        
        File fSource = new File(sourceOutput);
        File fTarget = new File(targetOutput);
        
        //Read language models:
        NGramExec nge = new NGramExec(this.dlfe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang() + ".poslm");
        String targetLM = this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang() + ".poslm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.dlfe.getSourceFile());
        System.out.println(this.dlfe.getTargetFile());
        if (fSource.exists() && fSource.length() != 0) {
            System.out.println(this.dlfe.getSourceFile()+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.dlfe.getSourceFile(), sourceOutput, sourceLM);
        }
        if (fTarget.exists() && fTarget.length() != 0) {
            System.out.println(this.dlfe.getTargetFile()+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.dlfe.getTargetFile(), targetOutput, targetLM);
        }
        
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcSource = new PPLProcessor(sourceOutput,
                new String[]{"logprob", "ppl", "ppl1"});
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"logprob", "ppl", "ppl1"});

        //Return processors:
        return new PPLProcessor[]{pplProcSource, pplProcTarget};
    }
    
    private PPLProcessor[] getLMProcessors() {
        //Generate output paths:
        String sourceOutput = this.dlfe.getSourceFile() + ".ppl";
        String targetOutput = this.dlfe.getTargetFile() + ".ppl";

        File fSource = new File(sourceOutput);
        File fTarget = new File(targetOutput);
        
        //Read language models:
        NGramExec nge = new NGramExec(this.dlfe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang() + ".lm");
        String targetLM = this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang() + ".lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.dlfe.getSourceFile());
        System.out.println(this.dlfe.getTargetFile());
        if (fSource.exists() && fSource.length() != 0) {
            System.out.println(this.dlfe.getSourceFile()+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.dlfe.getSourceFile(), sourceOutput, sourceLM);
        }
        if (fTarget.exists() && fTarget.length() != 0) {
            System.out.println(this.dlfe.getTargetFile()+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.dlfe.getTargetFile(), targetOutput, targetLM);
        }
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplPosProcSource = new PPLProcessor(sourceOutput,
                new String[]{"poslogprob", "posppl", "posppl1"});
        PPLProcessor pplPosProcTarget = new PPLProcessor(targetOutput,
                new String[]{"poslogprob", "posppl", "posppl1"});

        //Return processors:
        return new PPLProcessor[]{pplPosProcSource, pplPosProcTarget};
    }

    private LanguageModel[] getNGramModels() {
        //Create ngram file processors:
        NGramProcessor sourceNgp = new NGramProcessor(this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang() + ".ngram"));
        NGramProcessor targetNgp = new NGramProcessor(this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang() + ".ngram"));

        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private LanguageModel getPOSNGramModel() {
        //Create ngram file processors:
        NGramProcessor targetNgp = new NGramProcessor(this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang() + ".posngram"));

        //Generate resulting handlers:
        LanguageModel result = targetNgp.run();

        //Return handlers:
        return result;
    }

    private AlignmentProcessor getAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("alignments");

        //Get path to alignments file:
        String alignmentsPath = this.dlfe.getResourceManager().getProperty("alignments.file");

        //Return AlignmentProcessor:
        return new AlignmentProcessor(alignmentsPath);
    }

    private RefTranslationProcessor getRefTranslationProcessor() {
        //Register resource:
        ResourceManager.registerResource("reftranslation");

        //Get reference translations path:
        String refTranslationsPath = this.dlfe.getResourceManager().getProperty(dlfe.getTargetLang() + ".refTranslations");

        //Return new reference translation processor:
        return new RefTranslationProcessor(refTranslationsPath);
    }

    /**
     * @return the resourceProcessors
     */
    public ResourceProcessor[][] getResourceProcessors() {
        return resourceProcessors;
    }

    private POSNgramCountProcessor getPOSNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("posngramcount");

        //Get target POS Language Models:
        LanguageModel ngramModelTarget = this.getPOSNGramModel();

        //Create source and target processors:
        POSNgramCountProcessor targetProcessor = new POSNgramCountProcessor(ngramModelTarget);

        //Return processors:
        return targetProcessor;
    }

    private WordCountProcessor[] getWordCountProcessors() {
        //Register resource:
        ResourceManager.registerResource("wordcounts");

        //Create ngram file processors:
        WordCountProcessor sourceProcessor = new WordCountProcessor();
        WordCountProcessor targetProcessor = new WordCountProcessor();

        //Generate resulting processors:
        WordCountProcessor[] result = new WordCountProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
        return result;
    }

    private TranslationProbabilityProcessor getTranslationProbabilityProcessor() {
        //Register resource:
        ResourceManager.registerResource("translationcounts");

        //Get reference translations path:
        String translationProbPath = this.dlfe.getResourceManager().getProperty(dlfe.getSourceLang() + ".translationProbs");

        //Return new reference translation processor:
        return new TranslationProbabilityProcessor(translationProbPath);
    }

    private POSTaggerProcessor[] getPOSTaggerProcessors() {
        ResourceManager.registerResource("postagger");
        String posNameSource = this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang() + ".postagger");
        String posNameTarget = this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang() + ".postagger");
        String outputPathSource = this.dlfe.getResourceManager().getProperty("input") + File.separator + this.dlfe.getSourceLang()+File.separator;
        String outputPathTarget = this.dlfe.getResourceManager().getProperty("input") + File.separator + this.dlfe.getTargetLang()+File.separator;
        File sourceFile = new File(this.dlfe.getSourceFile());
        File targetFile = new File(this.dlfe.getTargetFile());   
        String absoluteSourceFilePath = sourceFile.getAbsolutePath();
        String absoluteTargetFilePath = targetFile.getAbsolutePath();
        String fileNameSource = sourceFile.getName();
        String fileNameTarget = targetFile.getName();
        String outputFileSource = outputPathSource+fileNameSource+".pos";
        String outputFileTarget = outputPathTarget+fileNameTarget+".pos";
        String posSourceTaggerPath = this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang()
                + ".postagger.exePath");
        String posTargetTaggerPath = this.dlfe.getResourceManager().getString(this.dlfe.getTargetLang()
                + ".postagger.exePath");
        String sourceOutput = "";
        String targetOutput = "";
        //run for Target
        try {
            Class c = Class.forName(posNameTarget);
            PosTagger tagger = (PosTagger) c.newInstance();
            tagger.setParameters("target", posNameTarget, posTargetTaggerPath,
                    absoluteTargetFilePath, outputFileTarget);
            PosTagger.ForceRun(false);
            targetOutput = tagger.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //run for Source
        try {
            Class c = Class.forName(posNameSource);
            PosTagger tagger = (PosTagger) c.newInstance();
            tagger.setParameters("source", posNameSource, posSourceTaggerPath,
                    absoluteSourceFilePath, outputFileSource);
            PosTagger.ForceRun(false);
            sourceOutput = tagger.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //Generate POSTagger processors:
        POSTaggerProcessor posTaggerProcTarget = new POSTaggerProcessor(targetOutput);
        POSTaggerProcessor posTaggerProcSource = new POSTaggerProcessor(sourceOutput);


        //Return processors:
        return new POSTaggerProcessor[]{posTaggerProcSource, posTaggerProcTarget};
        //return null;
    }

    private DiscourseRepetition[] getDiscourseRepetitionProcessors() {
        ResourceManager.registerResource("discrep");
        DiscourseRepetition discRepTarget = new DiscourseRepetition();
        DiscourseRepetition discRepSource = new DiscourseRepetition();
        return new DiscourseRepetition[]{discRepSource, discRepTarget};
    }

    private GizaProcessor getGizaProcessor() {
        ResourceManager.registerResource("Giza");
        FileModel fm = new FileModel(this.dlfe.getSourceFile(), this.dlfe.getResourceManager().getString(this.dlfe.getSourceLang() + ".corpus"));
        String gizaPath = this.dlfe.getResourceManager().getString("pair." + this.dlfe.getSourceLang()
                + this.dlfe.getTargetLang() + ".giza.path");
        GizaProcessor gizaProc = new GizaProcessor(gizaPath);
        return gizaProc;
    }
}
