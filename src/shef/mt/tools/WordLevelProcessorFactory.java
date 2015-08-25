package shef.mt.tools;

import java.util.ArrayList;
import java.util.HashSet;
import shef.mt.enes.FeatureExtractorInterface;
import shef.mt.tools.jrouge.ROUGEProcessor;
import shef.mt.tools.tercom.TERProcessor;

public class WordLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;

    private FeatureExtractorInterface fe;

    public WordLevelProcessorFactory(FeatureExtractorInterface fe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.fe = fe;

        //Get required resources:
        HashSet<String> requirements = fe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        ArrayList<ResourceProcessor> sourceProcessors = new ArrayList<ResourceProcessor>();
        ArrayList<ResourceProcessor> targetProcessors = new ArrayList<ResourceProcessor>();

        if (requirements.contains("target.stopwords") || requirements.contains("source.stopwords")) {
            //Get stopwords processors:
            StopWordsProcessor[] stopWordsProcessors = this.getStopWordsProcessors();
            StopWordsProcessor stopWordsProcSource = stopWordsProcessors[0];
            StopWordsProcessor stopWordsProcTarget = stopWordsProcessors[0];

            //Add them to processor vectors:
            sourceProcessors.add(stopWordsProcSource);
            targetProcessors.add(stopWordsProcTarget);
        }

        if (requirements.contains("alignments.file")) {
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

        if (requirements.contains("source.ngram") || requirements.contains("target.ngram")) {
            //Run SRILM on ngram count files:
            NgramCountProcessor[] ngramProcessors = this.getNgramProcessors();
            NgramCountProcessor ngramProcessorSource = ngramProcessors[0];
            NgramCountProcessor ngramProcessorTarget = ngramProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(ngramProcessorSource);
            targetProcessors.add(ngramProcessorTarget);
        }

        if (requirements.contains("source.posngram") || requirements.contains("target.posngram")) {
            //Run SRILM on ngram count files:
            POSNgramCountProcessor[] ngramProcessors = this.getPOSNgramProcessors();
            POSNgramCountProcessor sourceNgramProcessor = ngramProcessors[0];
            POSNgramCountProcessor targetNgramProcessor = ngramProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(sourceNgramProcessor);
            targetProcessors.add(targetNgramProcessor);
        }

        if (requirements.contains("source.lm") || requirements.contains("target.lm")) {
            //Run SRILM on language models:
            PPLProcessor[] pplProcessors = this.getLMProcessors();
            PPLProcessor pplProcSource = pplProcessors[0];
            PPLProcessor pplProcTarget = pplProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(pplProcSource);
            targetProcessors.add(pplProcTarget);
        }

        if (requirements.contains("source.POSModel") || requirements.contains("target.POSModel") || requirements.contains("source.parseModel") || requirements.contains("target.parseModel")) {
            //Get parsing processors:
            ParsingProcessor[] parsingProcessors = this.getParsingProcessors(requirements);
            ParsingProcessor parsingSource = parsingProcessors[0];
            ParsingProcessor parsingTarget = parsingProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(parsingSource);
            targetProcessors.add(parsingTarget);
        }

        if (requirements.contains("tools.universalwordnet.path")) {
            //Get sense processor:
            SenseProcessor[] senseProcessors = this.getSenseProcessors();
            SenseProcessor senseSource = senseProcessors[0];
            SenseProcessor senseTarget = senseProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(senseSource);
            targetProcessors.add(senseTarget);
        }

        if (requirements.contains("target.refTranslations")) {
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

        if (requirements.contains("source.translationProbs")) {
            //Get translation counts processor:
            TranslationProbabilityProcessor translationProbProcessor = this.getTranslationProbabilityProcessor();

            //Add them to processor vectors:
            targetProcessors.add(translationProbProcessor);
        }

        if (requirements.contains("alignments.file")) {
            //Get block alignment processor:
            BlockAlignmentProcessor blockAlignmentProcessor = this.getBlockAlignmentProcessor();

            //Add them to processor vectors:
            targetProcessors.add(blockAlignmentProcessor);
        }

        if (requirements.contains("teralignment")) {
            //Get TER alignment processor:
            TERProcessor terAlignmentProcessor = this.getTERProcessor();

            //Add them to processor vectors:
            targetProcessors.add(terAlignmentProcessor);
        }

        if (requirements.contains("rouge-n")) {
            //Get ROUGE processor:
            ROUGEProcessor rougeProcessor = this.getROUGEProcessor();

            //Add them to processor vectors:
            targetProcessors.add(rougeProcessor);
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
        if (requirements.contains("source.POSModel") || requirements.contains("target.POSModel")) {
            ResourceManager.registerResource("source.POSModel");
            ResourceManager.registerResource("target.POSModel");
        }
        if (requirements.contains("source.parseModel") || requirements.contains("target.parseModel")) {
            ResourceManager.registerResource("source.parseModel");
            ResourceManager.registerResource("target.parseModel");
        }

        //Get paths to Stanford Parser source language models:
        String POSModel = this.fe.getResourceManager().getProperty(this.fe.getSourceLang() + ".POSModel");
        String parseModel = this.fe.getResourceManager().getProperty(this.fe.getSourceLang() + ".parseModel");

        //Create source language ParsingProcessor:
        ParsingProcessor sourceProcessor = new ParsingProcessor(this.fe.getSourceLang(), POSModel, parseModel, requirements);

        //Get paths to Stanford Parser target language models:
        POSModel = this.fe.getResourceManager().getProperty(this.fe.getTargetLang() + ".POSModel");
        parseModel = this.fe.getResourceManager().getProperty(this.fe.getTargetLang() + ".parseModel");

        //Create target language ParsingProcessor:
        ParsingProcessor targetProcessor = new ParsingProcessor(this.fe.getTargetLang(), POSModel, parseModel, requirements);

        //Return processors:
        return new ParsingProcessor[]{sourceProcessor, targetProcessor};
    }

    private SenseProcessor[] getSenseProcessors() {
        //Register resource:
        ResourceManager.registerResource("tools.universalwordnet.path");

        //Get path to Universal Wordnet:
        String wordnetPath = this.fe.getResourceManager().getProperty("tools.universalwordnet.path");

        //Create SenseProcessor object:
        SenseProcessor sourceProcessor = new SenseProcessor(wordnetPath, this.fe.getSourceLang());
        SenseProcessor targetProcessor = new SenseProcessor(wordnetPath, this.fe.getTargetLang());
        SenseProcessor[] result = new SenseProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
        return result;
    }

    private NgramCountProcessor[] getNgramProcessors() {
        //Register resource:
        ResourceManager.registerResource("target.ngram");
        ResourceManager.registerResource("source.ngram");

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
        ResourceManager.registerResource("source.stopwords");
        ResourceManager.registerResource("target.stopwords");

        //Get paths to stop word lists:
        String sourcePath = this.fe.getResourceManager().getProperty("source.stopwords");
        String targetPath = this.fe.getResourceManager().getProperty("target.stopwords");

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

    private PPLProcessor[] getLMProcessors() {
        //Register resources:
        ResourceManager.registerResource("source.lm");
        ResourceManager.registerResource("target.lm");
        
        //Generate output paths:
        String sourceOutput = this.fe.getSourceFile() + ".ppl";
        String targetOutput = this.fe.getTargetFile() + ".ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.fe.getResourceManager().getString(this.fe.getSourceLang() + ".lm");
        String targetLM = this.fe.getResourceManager().getString(this.fe.getTargetLang() + ".lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.fe.getSourceFile());
        System.out.println(this.fe.getTargetFile());
        nge.runNGramPerplex(this.fe.getSourceFile(), sourceOutput, sourceLM);
        nge.runNGramPerplex(this.fe.getTargetFile(), targetOutput, targetLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcSource = new PPLProcessor(sourceOutput,
                new String[]{"logprob", "ppl", "ppl1"});
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"logprob", "ppl", "ppl1"});

        //Return processors:
        return new PPLProcessor[]{pplProcSource, pplProcTarget};
    }

    private LanguageModel[] getNGramModels() {
        //Create ngram file processors:
        NGramProcessor sourceNgp = new NGramProcessor(this.fe.getResourceManager().getString("source.ngram"));
        NGramProcessor targetNgp = new NGramProcessor(this.fe.getResourceManager().getString("target.ngram"));

        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private LanguageModel[] getPOSNGramModels() {
        //Create ngram file processors:
        NGramProcessor sourceNgp = new NGramProcessor(this.fe.getResourceManager().getString("source.posngram"));
        NGramProcessor targetNgp = new NGramProcessor(this.fe.getResourceManager().getString("target.posngram"));

        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private AlignmentProcessor getAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("alignments.file");

        //Get path to alignments file:
        String alignmentsPath = this.fe.getResourceManager().getProperty("alignments.file");

        //Return AlignmentProcessor:
        return new AlignmentProcessor(alignmentsPath);
    }

    private RefTranslationProcessor getRefTranslationProcessor() {
        //Register resource:
        ResourceManager.registerResource("target.refTranslations");

        //Get reference translations path:
        String refTranslationsPath = this.fe.getResourceManager().getProperty("target.refTranslations");

        //Return new reference translation processor:
        return new RefTranslationProcessor(refTranslationsPath);
    }

    /**
     * @return the resourceProcessors
     */
    public ResourceProcessor[][] getResourceProcessors() {
        return resourceProcessors;
    }

    private POSNgramCountProcessor[] getPOSNgramProcessors() {
        //Register resource:
        ResourceManager.registerResource("source.posngram");
        ResourceManager.registerResource("target.posngram");

        //Get target POS Language Models:
        LanguageModel[] POSNgramModels = this.getPOSNGramModels();
        LanguageModel ngramModelSource = POSNgramModels[0];
        LanguageModel ngramModelTarget = POSNgramModels[1];

        //Create source and target processors:
        POSNgramCountProcessor sourceProcessor = new POSNgramCountProcessor(ngramModelSource);
        POSNgramCountProcessor targetProcessor = new POSNgramCountProcessor(ngramModelTarget);
        POSNgramCountProcessor[] POSNgramProcessors = new POSNgramCountProcessor[]{sourceProcessor, targetProcessor};

        //Return processors:
        return POSNgramProcessors;
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
        ResourceManager.registerResource("source.translationProbs");

        //Get reference translations path:
        String translationProbPath = this.fe.getResourceManager().getProperty("source.translationProbs");

        //Return new reference translation processor:
        return new TranslationProbabilityProcessor(translationProbPath);
    }

    private BlockAlignmentProcessor getBlockAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("alignments.file");

        //Create source and target processors:
        BlockAlignmentProcessor targetProcessor = new BlockAlignmentProcessor(this.fe.getResourceManager().getProperty("alignments.file"));

        //Return processors:
        return targetProcessor;
    }

    private TERProcessor getTERProcessor() {
        ResourceManager.registerResource("teralignment");
        return new TERProcessor(this.fe.getSourceFile(), this.fe.getTargetFile());
    }

    private ROUGEProcessor getROUGEProcessor() {
        ResourceManager.registerResource("rouge-n");
        return new ROUGEProcessor(this.fe.getSourceFile(), this.fe.getTargetFile());
    }
}
