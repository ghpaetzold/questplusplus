package shef.mt.tools;

import java.util.ArrayList;
import java.util.HashSet;
import shef.mt.enes.WordLevelFeatureExtractor;

public class WordLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;

    private WordLevelFeatureExtractor wlfe;

    public WordLevelProcessorFactory(WordLevelFeatureExtractor wlfe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.wlfe = wlfe;

        //Get required resources:
        HashSet<String> requirements = wlfe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        ArrayList<ResourceProcessor> sourceProcessors = new ArrayList<ResourceProcessor>();
        ArrayList<ResourceProcessor> targetProcessors = new ArrayList<ResourceProcessor>();

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
            POSNgramCountProcessor[] ngramProcessors = this.getPOSNgramProcessors();
            POSNgramCountProcessor sourceNgramProcessor = ngramProcessors[0];
            POSNgramCountProcessor targetNgramProcessor = ngramProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(sourceNgramProcessor);
            targetProcessors.add(targetNgramProcessor);
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
        String POSModel = this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".POSModel");
        String parseModel = this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".parseModel");

        //Create source language ParsingProcessor:
        ParsingProcessor sourceProcessor = new ParsingProcessor(this.wlfe.getSourceLang(), POSModel, parseModel, requirements);

        //Get paths to Stanford Parser target language models:
        POSModel = this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".POSModel");
        parseModel = this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".parseModel");

        //Create target language ParsingProcessor:
        ParsingProcessor targetProcessor = new ParsingProcessor(this.wlfe.getTargetLang(), POSModel, parseModel, requirements);

        //Return processors:
        return new ParsingProcessor[]{sourceProcessor, targetProcessor};
    }

    private SenseProcessor[] getSenseProcessors() {
        //Register resource:
        ResourceManager.registerResource("sensecounts");

        //Get path to Universal Wordnet:
        String wordnetPath = this.wlfe.getResourceManager().getProperty("tools.universalwordnet.path");

        //Create SenseProcessor object:
        SenseProcessor sourceProcessor = new SenseProcessor(wordnetPath, this.wlfe.getSourceLang());
        SenseProcessor targetProcessor = new SenseProcessor(wordnetPath, this.wlfe.getTargetLang());
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
        String sourcePath = this.wlfe.getResourceManager().getProperty(this.wlfe.getSourceLang() + ".stopwords");
        String targetPath = this.wlfe.getResourceManager().getProperty(this.wlfe.getTargetLang() + ".stopwords");

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
        //Generate output paths:
        String sourceOutput = this.wlfe.getSourceFile() + ".ppl";
        String targetOutput = this.wlfe.getTargetFile() + ".ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.wlfe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.wlfe.getResourceManager().getString(this.wlfe.getSourceLang() + ".lm");
        String targetLM = this.wlfe.getResourceManager().getString(this.wlfe.getTargetLang() + ".lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.wlfe.getSourceFile());
        System.out.println(this.wlfe.getTargetFile());
        nge.runNGramPerplex(this.wlfe.getSourceFile(), sourceOutput, sourceLM);
        nge.runNGramPerplex(this.wlfe.getTargetFile(), targetOutput, targetLM);
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
        NGramProcessor sourceNgp = new NGramProcessor(this.wlfe.getResourceManager().getString(this.wlfe.getSourceLang() + ".ngram"));
        NGramProcessor targetNgp = new NGramProcessor(this.wlfe.getResourceManager().getString(this.wlfe.getTargetLang() + ".ngram"));

        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private LanguageModel[] getPOSNGramModels() {
        //Create ngram file processors:
        NGramProcessor sourceNgp = new NGramProcessor(this.wlfe.getResourceManager().getString(this.wlfe.getSourceLang() + ".posngram"));
        NGramProcessor targetNgp = new NGramProcessor(this.wlfe.getResourceManager().getString(this.wlfe.getTargetLang() + ".posngram"));
        
        //Generate resulting handlers:
        LanguageModel[] result = new LanguageModel[]{sourceNgp.run(), targetNgp.run()};

        //Return handlers:
        return result;
    }

    private AlignmentProcessor getAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("alignments");

        //Get path to alignments file:
        String alignmentsPath = this.wlfe.getResourceManager().getProperty("alignments.file");

        //Return AlignmentProcessor:
        return new AlignmentProcessor(alignmentsPath);
    }

    private RefTranslationProcessor getRefTranslationProcessor() {
        //Register resource:
        ResourceManager.registerResource("reftranslation");

        //Get reference translations path:
        String refTranslationsPath = this.wlfe.getResourceManager().getProperty(wlfe.getTargetLang() + ".refTranslations");

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
        ResourceManager.registerResource("posngramcount");

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
        ResourceManager.registerResource("translationcounts");

        //Get reference translations path:
        String translationProbPath = this.wlfe.getResourceManager().getProperty(wlfe.getSourceLang() + ".translationProbs");

        //Return new reference translation processor:
        return new TranslationProbabilityProcessor(translationProbPath);
    }
}
