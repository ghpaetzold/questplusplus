package shef.mt.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import shef.mt.FeatureExtractor;
import shef.mt.tools.jrouge.ROUGEProcessor;
import shef.mt.tools.mqm.core.fluency.inconsistency.AbbreviationsProcessor;
import shef.mt.tools.mqm.core.fluency.register.VariantsSlangProcessor;
import shef.mt.tools.mqm.resources.AbbreviationDictionary;
import shef.mt.tools.mqm.resources.SlangDictionary;
import shef.mt.tools.tercom.TERProcessor;
import shef.mt.xmlwrap.MOSES_XMLWrapper;

/**
 * Creates processors for sentence-level feature extraction.
 *
 * @author GustavoH
 */
public class SentenceLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;

    private FeatureExtractor fe;

    public SentenceLevelProcessorFactory(FeatureExtractor fe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.fe = fe;

        //Get required resources:
        HashSet<String> requirements = fe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        ArrayList<ResourceProcessor> sourceProcessors = new ArrayList<ResourceProcessor>();
        ArrayList<ResourceProcessor> targetProcessors = new ArrayList<ResourceProcessor>();

        if (requirements.contains("target.mqm.slang")){
            VariantsSlangProcessor variantsSlangProcessor = this.getVariantsSlangProcessor();
            targetProcessors.add(variantsSlangProcessor);
        }
        
        if (requirements.contains("target.mqm.abbreviation")){
            AbbreviationsProcessor abbreviationsProcessor = this.getAbbreviationsProcessor();
            targetProcessors.add(abbreviationsProcessor);
        }
        
        if (requirements.contains("pair.inter.triggers.file") || requirements.contains("source.intra.triggers.file") || requirements.contains("target.intra.triggers.file")) {
            //Get trigger processors:
            TriggersProcessor[] triggerProcessors = this.getTriggersProcessor();
            TriggersProcessor triggerProcSource = triggerProcessors[0];
            TriggersProcessor triggerProcTarget = triggerProcessors[1];
            TriggersProcessor triggerProcPair = triggerProcessors[2];

            //Add them to processor vectors:
            targetProcessors.add(triggerProcTarget);
            sourceProcessors.add(triggerProcSource);
            targetProcessors.add(triggerProcPair);
            sourceProcessors.add(triggerProcPair);
        }

        if (requirements.contains("globallexicon")) {
            //Get alignment processors:
            GlobalLexiconProcessor globalLexiconProcessor = this.getGlobalLexiconProcessor();

            //Add them to processor vectors:
            targetProcessors.add(globalLexiconProcessor);
            sourceProcessors.add(globalLexiconProcessor);
        }

        if (requirements.contains("giza.path")) {
            //Get alignment processors:
            GizaProcessor gizaProcessor = this.getGizaProcessor();

            //Add them to processor vectors:
            targetProcessors.add(gizaProcessor);
            sourceProcessors.add(gizaProcessor);

        }

        if (requirements.contains("source.postagger")) {
            //Get POSTagger processors:
            POSTaggerProcessor posTaggerProc = this.getSourcePOSTaggerProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(posTaggerProc);
        }
        
        if (requirements.contains("target.postagger")) {
            //Get POSTagger processors:
            POSTaggerProcessor posTaggerProc = this.getTargetPOSTaggerProcessor();

            //Add them to processor vectors:
            targetProcessors.add(posTaggerProc);
        }

        if (requirements.contains("source.ngram")) {
            //Run SRILM on ngram count files:
            NgramCountProcessor ngramProcessor = this.getSourceNgramProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(ngramProcessor);
        }
        
        if (requirements.contains("target.ngram")) {
            //Run SRILM on ngram count files:
            NgramCountProcessor ngramProcessor = this.getTargetNgramProcessor();

            //Add them to processor vectors:
            targetProcessors.add(ngramProcessor);
        }

        if (requirements.contains("source.posngram")) {
            //Run SRILM on ngram count files:
            POSNgramCountProcessor sourceNgramProcessor = this.getSourcePOSNgramProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(sourceNgramProcessor);
        }
        
        if (requirements.contains("target.posngram")) {
            //Run SRILM on ngram count files:
            POSNgramCountProcessor targetNgramProcessor = this.getTargetPOSNgramProcessor();

            //Add them to processor vectors:
            targetProcessors.add(targetNgramProcessor);
        }

        if (requirements.contains("source.lm")) {
            //Run SRILM on language models:
            PPLProcessor pplProcSource = this.getSourceLMProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(pplProcSource);
        }
        
        if (requirements.contains("target.lm")) {
            //Run SRILM on language models:
            PPLProcessor pplProcSource = this.getTargetLMProcessor();

            //Add them to processor vectors:
            targetProcessors.add(pplProcSource);
        }

        if (requirements.contains("target.poslm")) {
            //Run SRILM on language models:
            PPLProcessor pplProcTarget = this.getTargetPOSLMProcessor();

            //Add them to processor vectors:
            targetProcessors.add(pplProcTarget);
        }

        if (requirements.contains("source.topic.distribution")) {
            //Get TM processors:
            TopicDistributionProcessor topicDistProcessor = this.getSourceTopicDistributionProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(topicDistProcessor);
        }
        
        if (requirements.contains("target.topic.distribution")) {
            //Get TM processors:
            TopicDistributionProcessor topicDistProcessor = this.getTargetTopicDistributionProcessor();

            //Add them to processor vectors:
            targetProcessors.add(topicDistProcessor);
        }

        if (requirements.contains("source.bparser.grammar")) {
            //Get TM processors:
            BParserProcessor bParserProcessor = this.getSourceBParserProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(bParserProcessor);
        }
        
        if (requirements.contains("target.bparser.grammar")) {
            //Get TM processors:
            BParserProcessor bParserProcessor = this.getTargetBParserProcessor();

            //Add them to processor vectors:
            targetProcessors.add(bParserProcessor);
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

        if (requirements.contains("moses.xml")) {
            //Get MT output processors:
            MTOutputProcessor mtOutputProc = this.getMTOutputProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(mtOutputProc);
        }

        if (requirements.contains("alignments.file")) {
            //Get alignment processors:
            AlignmentProcessor alignmentProcessor = this.getAlignmentProcessor();

            //Add them to processor vectors:
            targetProcessors.add(alignmentProcessor);
        }

        if (requirements.contains("source.stopwords")) {
            //Get stopwords processors:
            StopWordsProcessor stopWordsProcessor = this.getSourceStopWordsProcessor();

            //Add them to processor vectors:
            sourceProcessors.add(stopWordsProcessor);
        }
        
        if (requirements.contains("target.stopwords")) {
            //Get stopwords processors:
            StopWordsProcessor stopWordsProcessor = this.getTargetStopWordsProcessor();

            //Add them to processor vectors:
            targetProcessors.add(stopWordsProcessor);
        }

        if (requirements.contains("source.POSModel") || requirements.contains("source.parseModel")) {
            //Get parsing processors:
            ParsingProcessor parsingProcessor = this.getSourceParsingProcessor(requirements);

            //Add them to processor vectors:
            sourceProcessors.add(parsingProcessor);
        }
        
        if (requirements.contains("target.POSModel") || requirements.contains("target.parseModel")) {
            //Get parsing processors:
            ParsingProcessor parsingProcessor = this.getTargetParsingProcessor(requirements);

            //Add them to processor vectors:
            targetProcessors.add(parsingProcessor);
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

        if (requirements.contains("punctuation")) {
            //Get punctuation processors:
            PunctuationProcessor punctuationProcessor = this.getPunctuationProcessor();

            //Add them to processor vectors:
            targetProcessors.add(punctuationProcessor);
        }

        //Transform array lists in vectors:
        ResourceProcessor[] sourceProcessorVector = new ResourceProcessor[sourceProcessors.size()];
        ResourceProcessor[] targetProcessorVector = new ResourceProcessor[targetProcessors.size()];
        sourceProcessorVector = (ResourceProcessor[]) sourceProcessors.toArray(sourceProcessorVector);
        targetProcessorVector = (ResourceProcessor[]) targetProcessors.toArray(targetProcessorVector);

        //Return vectors:
        this.resourceProcessors = new ResourceProcessor[][]{sourceProcessorVector, targetProcessorVector};
    }

    private PunctuationProcessor getPunctuationProcessor() {
        //Register resource:
        ResourceManager.registerResource("punctuation");

        //Create punctuation processor:
        PunctuationProcessor processor = new PunctuationProcessor();

        //Return processor:
        return processor;
    }

    private StopWordsProcessor getSourceStopWordsProcessor() {
        //Register resource:
        ResourceManager.registerResource("source.stopwords");

        //Get paths to stop word lists:
        String path = this.fe.getResourceManager().getProperty("source.stopwords");

        //Generate processors:
        StopWordsProcessor processor = new StopWordsProcessor(path);

        //Return processors:
        return processor;
    }
    
    private StopWordsProcessor getTargetStopWordsProcessor() {
        //Register resource:
        ResourceManager.registerResource("target.stopwords");

        //Get paths to stop word lists:
        String path = this.fe.getResourceManager().getProperty("target.stopwords");

        //Generate processors:
        StopWordsProcessor processor = new StopWordsProcessor(path);

        //Return processors:
        return processor;
    }

    private ParsingProcessor getSourceParsingProcessor(HashSet<String> requirements) {
        //Register resources:
        if (requirements.contains("source.POSModel")) {
            ResourceManager.registerResource("source.POSModel");
        }
        if (requirements.contains("source.parseModel")) {
            ResourceManager.registerResource("source.parseModel");
        }

        //Get paths to Stanford Parser source language models:
        String POSModel = this.fe.getResourceManager().getProperty(this.fe.getSourceLang() + ".POSModel");
        String parseModel = this.fe.getResourceManager().getProperty(this.fe.getSourceLang() + ".parseModel");

        //Create source language ParsingProcessor:
        ParsingProcessor processor = new ParsingProcessor(this.fe.getSourceLang(), POSModel, parseModel, requirements);
        
        //Return processors:
        return processor;
    }
    
    private ParsingProcessor getTargetParsingProcessor(HashSet<String> requirements) {
        //Register resources:
        if (requirements.contains("target.POSModel")) {
            ResourceManager.registerResource("target.POSModel");
        }
        if (requirements.contains("target.parseModel")) {
            ResourceManager.registerResource("target.parseModel");
        }

        //Get paths to Stanford Parser source language models:
        String POSModel = this.fe.getResourceManager().getProperty(this.fe.getTargetLang() + ".POSModel");
        String parseModel = this.fe.getResourceManager().getProperty(this.fe.getTargetLang() + ".parseModel");

        //Create source language ParsingProcessor:
        ParsingProcessor processor = new ParsingProcessor(this.fe.getTargetLang(), POSModel, parseModel, requirements);
        
        //Return processors:
        return processor;
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

    private AlignmentProcessor getAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("alignments.file");

        //Get path to alignments file:
        String alignmentsPath = this.fe.getResourceManager().getProperty("alignments.file");

        //Return AlignmentProcessor:
        return new AlignmentProcessor(alignmentsPath);
    }

    private NgramCountProcessor getSourceNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("source.ngram");

        //Get source and target Language Models:
        LanguageModel ngramModel = this.getNGramModel(this.fe.getResourceManager().getString("source.ngram"));

        //Create source and target processors:
        NgramCountProcessor sourceProcessor = new NgramCountProcessor(ngramModel);
        NgramCountProcessor result = sourceProcessor;

        //Return processors:
        return result;
    }
    
    private NgramCountProcessor getTargetNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("target.ngram");

        //Get source and target Language Models:
        LanguageModel ngramModel = this.getNGramModel(this.fe.getResourceManager().getString("target.ngram"));

        //Create source and target processors:
        NgramCountProcessor targetProcessor = new NgramCountProcessor(ngramModel);
        NgramCountProcessor result = targetProcessor;

        //Return processors:
        return result;
    }

    private PPLProcessor getSourceLMProcessor() {
        //Register resources:
        ResourceManager.registerResource("source.lm");

        //Generate output paths:
        String sourceOutput = this.fe.getSourceFile() + ".ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.fe.getResourceManager().getString("source.lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.fe.getSourceFile());
        nge.runNGramPerplex(this.fe.getSourceFile(), sourceOutput, sourceLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcSource = new PPLProcessor(sourceOutput,
                new String[]{"logprob", "ppl", "ppl1"});

        //Return processors:
        return pplProcSource;
    }
    
    private PPLProcessor getTargetLMProcessor() {
        //Register resources:
        ResourceManager.registerResource("target.lm");

        //Generate output paths:
        String targetOutput = this.fe.getTargetFile() + ".ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String targetLM = this.fe.getResourceManager().getString("target.lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.fe.getTargetFile());
        nge.runNGramPerplex(this.fe.getTargetFile(), targetOutput, targetLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"logprob", "ppl", "ppl1"});

        //Return processors:
        return pplProcTarget;
    }

    private PPLProcessor getTargetPOSLMProcessor() {
        //Register resources:
        ResourceManager.registerResource("target.poslm");

        //Generate output paths:
        String targetOutput = this.fe.getTargetFile() + ".XPOS.ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String targetLM = this.fe.getResourceManager().getString("target.poslm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(targetOutput);
        nge.runNGramPerplex(this.fe.getTargetFile() + ".pos.XPOS", targetOutput, targetLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"poslogprob", "posppl", "posppl1"});

        //Return processors:
        return pplProcTarget;
    }

    private LanguageModel getNGramModel(String path) {
        //Create ngram file processors:
        NGramProcessor ngp = new NGramProcessor(path);

        //Generate resulting handlers:
        LanguageModel result = ngp.run();

        //Return handlers:
        return result;
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
    
    private POSNgramCountProcessor getSourcePOSNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("source.posngram");

        //Get target POS Language Models:
        LanguageModel POSNgramModel = this.getNGramModel(this.fe.getResourceManager().getString("source.ngram"));

        //Create source and target processors:
        POSNgramCountProcessor sourceProcessor = new POSNgramCountProcessor(POSNgramModel);

        //Return processors:
        return sourceProcessor;
    }

    private POSNgramCountProcessor getTargetPOSNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("target.posngram");

        //Get target POS Language Models:
        LanguageModel POSNgramModel = this.getNGramModel(this.fe.getResourceManager().getString("target.ngram"));

        //Create source and target processors:
        POSNgramCountProcessor targetProcessor = new POSNgramCountProcessor(POSNgramModel);

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
        ResourceManager.registerResource("source.translationProbs");

        //Get reference translations path:
        String translationProbPath = this.fe.getResourceManager().getProperty("source.translationProbs");

        //Return new reference translation processor:
        return new TranslationProbabilityProcessor(translationProbPath);
    }

    private BlockAlignmentProcessor getBlockAlignmentProcessor() {
        //Register resource:
        ResourceManager.registerResource("blockalignments");

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

    private POSTaggerProcessor getSourcePOSTaggerProcessor() {
        ResourceManager.registerResource("source.postagger");
        String posNameSource = "shef.mt.tools.PosTreeTagger";
        String outputPathSource = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getSourceLang() + File.separator;
        File sourceFile = new File(this.fe.getSourceFile());
        String absoluteSourceFilePath = sourceFile.getAbsolutePath();
        String fileNameSource = sourceFile.getName();
        String outputFileSource = outputPathSource + fileNameSource + ".pos";
        String posSourceTaggerPath = this.fe.getResourceManager().getString("source.postagger");
        String sourceOutput = "";

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
        POSTaggerProcessor posTaggerProcSource = new POSTaggerProcessor(sourceOutput);

        //Return processors:
        return posTaggerProcSource;
    }

    private POSTaggerProcessor getTargetPOSTaggerProcessor() {
        ResourceManager.registerResource("target.postagger");
        String posNameTarget = "shef.mt.tools.PosTreeTagger";
        String outputPathTarget = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getTargetLang() + File.separator;
        File targetFile = new File(this.fe.getTargetFile());
        String absoluteTargetFilePath = targetFile.getAbsolutePath();
        String fileNameTarget = targetFile.getName();
        String outputFileTarget = outputPathTarget + fileNameTarget + ".pos";
        String posTargetTaggerPath = this.fe.getResourceManager().getString("target.postagger");
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

        //Generate POSTagger processors:
        POSTaggerProcessor posTaggerProcTarget = new POSTaggerProcessor(targetOutput);

        //Return processors:
        return posTaggerProcTarget;
    }

    private GizaProcessor getGizaProcessor() {
        ResourceManager.registerResource("Giza");
        ResourceManager.registerResource("giza.path");
        FileModel fm = new FileModel(this.fe.getSourceFile(), this.fe.getResourceManager().getString("source.corpus"));
        String gizaPath = this.fe.getResourceManager().getString("giza.path");
        GizaProcessor gizaProc = new GizaProcessor(gizaPath);
        return gizaProc;
    }

    private TopicDistributionProcessor getSourceTopicDistributionProcessor() {
        String topicDistributionFile = this.fe.getResourceManager().getString("source.topic.distribution");

        TopicDistributionProcessor topicDistProc = new TopicDistributionProcessor(topicDistributionFile, "source.topic.distribution");

        //Return processors:
        return topicDistProc;
    }
    
    private TopicDistributionProcessor getTargetTopicDistributionProcessor() {
        String topicDistributionFile = this.fe.getResourceManager().getString("target.topic.distribution");

        TopicDistributionProcessor topicDistProc = new TopicDistributionProcessor(topicDistributionFile, "target.topic.distribution");

        //Return processors:
        return topicDistProc;
    }

    private BParserProcessor getSourceBParserProcessor() {
        BParserProcessor bParserProc = null;

        bParserProc = new BParserProcessor();
        bParserProc.initialize(this.fe.getResourceManager().getString("source.bparser.grammar"), this.fe.getResourceManager(), "source");
        
        //Return processors:
        return bParserProc;
    }
    
    private BParserProcessor getTargetBParserProcessor() {
        BParserProcessor bParserProc = null;

        bParserProc = new BParserProcessor();
        bParserProc.initialize(this.fe.getResourceManager().getString("target.bparser.grammar"), this.fe.getResourceManager(), "target");
        
        //Return processors:
        return bParserProc;
    }

    private GlobalLexiconProcessor getGlobalLexiconProcessor() {
        ResourceManager.registerResource("globallexicon");
        String glmodelpath = this.fe.getResourceManager().getString("pair.glmodel.path");
        final Double minweight = Double.valueOf(
                this.fe.getResourceManager().getString("pair.glmodel.minweight"));
        GlobalLexiconProcessor globalLexicon = new GlobalLexiconProcessor(glmodelpath, minweight);
        return globalLexicon;
    }

    private MTOutputProcessor getMTOutputProcessor() {
        //Create the XML file if not provided:
        String xmlOut = null;
        if (this.fe.getResourceManager().getString("moses.xml") == null) {
            xmlOut = this.fe.getResourceManager().getString("input") + File.separator + "systems" + File.separator;
            File f = new File(this.fe.getSourceFile());
            xmlOut += "moses_" + f.getName() + ".xml";

            String nbestInput = this.fe.getResourceManager().getString("moses.nbestInput");
            String onebestPhrases = this.fe.getResourceManager().getString("moses.onebestPhrases");
            String onebestLog = this.fe.getResourceManager().getString("moses.onebestLog");
            MOSES_XMLWrapper cmuwrap = new MOSES_XMLWrapper(nbestInput, xmlOut, onebestPhrases, onebestLog);
            cmuwrap.run();

            this.fe.getResourceManager().put("moses.xml", xmlOut);
        } else {
            xmlOut = this.fe.getResourceManager().getString("moses.xml");
        }

        //Create MTOutputProcessor:
        String nbestSentPath = this.fe.getResourceManager().getString("input")
                + File.separator + this.fe.getTargetLang() + File.separator + "temp";
        String ngramExecPath = this.fe.getResourceManager().getString("tools.ngram.path");
        int ngramSize = Integer.parseInt(this.fe.getResourceManager().getString("ngramsize"));

        MTOutputProcessor mtop = new MTOutputProcessor(xmlOut, nbestSentPath, ngramExecPath, ngramSize);

        return mtop;
    }

    private TriggersProcessor[] getTriggersProcessor() {
        ResourceManager.registerResource("target.intra.triggers.file");
        ResourceManager.registerResource("source.intra.triggers.file");
        ResourceManager.registerResource("pair.inter.triggers.file");

        Triggers itl_target = null;
        Triggers itl_source = null;
        Triggers itl_source_target = null;

        TriggersProcessor itl_target_p = null;
        TriggersProcessor itl_source_p = null;
        TriggersProcessor itl_source_target_p = null;

        itl_target
                = new Triggers(
                        this.fe.getResourceManager().getString("target.intra.triggers.file"),
                        Integer.parseInt(this.fe.getResourceManager().getString("target.nb.max.intra.triggers")),
                        this.fe.getResourceManager().getString("phrase.separator"));
        itl_target_p = new TriggersProcessor(itl_target);

        itl_source
                = new Triggers(
                        this.fe.getResourceManager().getString("source.intra.triggers.file"),
                        Integer.parseInt(this.fe.getResourceManager().getString("source.nb.max.intra.triggers")),
                        this.fe.getResourceManager().getString("phrase.separator"));
        itl_source_p = new TriggersProcessor(itl_source);

        itl_source_target
                = new Triggers(
                        this.fe.getResourceManager().getString("pair.inter.triggers.file"),
                        Integer.parseInt(this.fe.getResourceManager().getString("pair.nb.max.inter.triggers")),
                        this.fe.getResourceManager().getString("phrase.separator"));
        itl_source_target_p
                = new TriggersProcessor(itl_source_target);

        //Return processors:
        return new TriggersProcessor[]{itl_source_p, itl_target_p, itl_source_target_p};
    }

    private VariantsSlangProcessor getVariantsSlangProcessor() {
        ResourceManager.registerResource("target.mqm.slang");
        SlangDictionary trgSlangDict = new SlangDictionary();
        trgSlangDict.load(this.fe.getResourceManager().getString("target.mqm.slang"));
        VariantsSlangProcessor variantsSlangProcessor = new VariantsSlangProcessor(trgSlangDict);
        return variantsSlangProcessor;
    }

    private AbbreviationsProcessor getAbbreviationsProcessor() {
        ResourceManager.registerResource("target.mqm.abbreviation");
        AbbreviationDictionary abbrevDict = new AbbreviationDictionary();
        abbrevDict.load(this.fe.getResourceManager().getString("target.mqm.abbreviation"));
        Set<String> abbrevs = abbrevDict.getAbbrevSet();
        BufferedReader br = null;
        HashMap<String, String> position2abbrev = new LinkedHashMap<String, String>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(this.fe.getTargetFile())));
            String strLine;
            int lineCount = 0;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                for (String abbrev : abbrevs) {
                    int pos = 0;
                    for (String word : strLine.split("\\s+")) {
                        if (word.equals(abbrev)) {
                            String position = lineCount + "-" + pos;
                            position2abbrev.put(position, abbrev);
                        }
                        pos ++;
                    }
                }
                lineCount ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        AbbreviationsProcessor abbreviationProcessor = new AbbreviationsProcessor(abbrevDict,position2abbrev);
        return abbreviationProcessor;
    }
}
