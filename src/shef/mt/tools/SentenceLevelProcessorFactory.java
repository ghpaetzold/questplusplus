package shef.mt.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import shef.mt.enes.FeatureExtractorInterface;
import shef.mt.tools.jrouge.ROUGEProcessor;
import shef.mt.tools.tercom.TERProcessor;
import shef.mt.xmlwrap.MOSES_XMLWrapper;

public class SentenceLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;

    private FeatureExtractorInterface fe;

    public SentenceLevelProcessorFactory(FeatureExtractorInterface fe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.fe = fe;

        //Get required resources:
        HashSet<String> requirements = fe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        ArrayList<ResourceProcessor> sourceProcessors = new ArrayList<ResourceProcessor>();
        ArrayList<ResourceProcessor> targetProcessors = new ArrayList<ResourceProcessor>();

        if (requirements.contains("globallexicon")) {
            //Get alignment processors:
            GlobalLexiconProcessor globalLexiconProcessor = this.getGlobalLexiconProcessor();

            //Add them to processor vectors:
            targetProcessors.add(globalLexiconProcessor);

        }

        if (requirements.contains("giza.path")) {
            //Get alignment processors:
            GizaProcessor gizaProcessor = this.getGizaProcessor();

            //Add them to processor vectors:
            targetProcessors.add(gizaProcessor);
            sourceProcessors.add(gizaProcessor);

        }

        if (requirements.contains("source.postagger") || requirements.contains("target.postagger")) {
            //Get POSTagger processors:
            POSTaggerProcessor[] posTaggerProcessors = this.getPOSTaggerProcessors();
            POSTaggerProcessor posTaggerProcSource = posTaggerProcessors[0];
            POSTaggerProcessor posTaggerProcTarget = posTaggerProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(posTaggerProcSource);
            targetProcessors.add(posTaggerProcTarget);
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

        if (requirements.contains("target.poslm")) {
            //Run SRILM on language models:
            PPLProcessor pplProcTarget = this.getPOSLMProcessor();

            //Add them to processor vectors:
            targetProcessors.add(pplProcTarget);
        }

        if (requirements.contains("source.topic.distribution") || requirements.contains("target.topic.distribution")) {
            //Get TM processors:
            TopicDistributionProcessor[] topicDistProcessors = this.getTopicDistributionProcessor();
            TopicDistributionProcessor topicDistProcSource = topicDistProcessors[0];
            TopicDistributionProcessor topicDistProcTarget = topicDistProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(topicDistProcSource);
            targetProcessors.add(topicDistProcTarget);
        }

        if (requirements.contains("source.bparser.grammar") || requirements.contains("target.bparser.grammar")) {
            //Get TM processors:
            BParserProcessor[] bParserProcessors = this.getBParserProcessor();
            BParserProcessor bParserProcSource = bParserProcessors[0];
            BParserProcessor bParserProcTarget = bParserProcessors[1];

            //Add them to processor vectors:
            sourceProcessors.add(bParserProcSource);
            targetProcessors.add(bParserProcTarget);
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

        if (requirements.contains("blockalignments")) {
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

        //Transform array lists in vectors:
        ResourceProcessor[] sourceProcessorVector = new ResourceProcessor[sourceProcessors.size()];
        ResourceProcessor[] targetProcessorVector = new ResourceProcessor[targetProcessors.size()];
        sourceProcessorVector = (ResourceProcessor[]) sourceProcessors.toArray(sourceProcessorVector);
        targetProcessorVector = (ResourceProcessor[]) targetProcessors.toArray(targetProcessorVector);

        //Return vectors:
        this.resourceProcessors = new ResourceProcessor[][]{sourceProcessorVector, targetProcessorVector};
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
        String sourceLM = this.fe.getResourceManager().getString("source.lm");
        String targetLM = this.fe.getResourceManager().getString("target.lm");

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

    private PPLProcessor getPOSLMProcessor() {
        //Register resources:
        ResourceManager.registerResource("target.poslm");

        //Generate output paths:
        String targetOutput = this.fe.getTargetFile() + ".ppl";

        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String targetLM = this.fe.getResourceManager().getString("target.poslm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.fe.getTargetFile());
        nge.runNGramPerplex(this.fe.getTargetFile(), targetOutput, targetLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"poslogprob", "posppl", "posppl1"});

        //Return processors:
        return pplProcTarget;
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

    private RefTranslationProcessor getRefTranslationProcessor() {
        //Register resource:
        ResourceManager.registerResource("reftranslation");

        //Get reference translations path:
        String refTranslationsPath = this.fe.getResourceManager().getProperty(fe.getTargetLang() + ".refTranslations");

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
        ResourceManager.registerResource("translationcounts");

        //Get reference translations path:
        String translationProbPath = this.fe.getResourceManager().getProperty(fe.getSourceLang() + ".translationProbs");

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

    private POSTaggerProcessor[] getPOSTaggerProcessors() {
        ResourceManager.registerResource("source.postagger");
        ResourceManager.registerResource("target.postagger");
        String posNameSource = this.fe.getResourceManager().getString("source.postagger");
        String posNameTarget = this.fe.getResourceManager().getString("target.postagger");
        String outputPathSource = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getSourceLang() + File.separator;
        String outputPathTarget = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getTargetLang() + File.separator;
        File sourceFile = new File(this.fe.getSourceFile());
        File targetFile = new File(this.fe.getTargetFile());
        String absoluteSourceFilePath = sourceFile.getAbsolutePath();
        String absoluteTargetFilePath = targetFile.getAbsolutePath();
        String fileNameSource = sourceFile.getName();
        String fileNameTarget = targetFile.getName();
        String outputFileSource = outputPathSource + fileNameSource + ".pos";
        String outputFileTarget = outputPathTarget + fileNameTarget + ".pos";
        String posSourceTaggerPath = this.fe.getResourceManager().getString("source.postagger.exePath");
        String posTargetTaggerPath = this.fe.getResourceManager().getString("target.postagger.exePath");
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

    private GizaProcessor getGizaProcessor() {
        ResourceManager.registerResource("Giza");
        ResourceManager.registerResource("giza.path");
        FileModel fm = new FileModel(this.fe.getSourceFile(), this.fe.getResourceManager().getString("source.corpus"));
        String gizaPath = this.fe.getResourceManager().getString("giza.path");
        GizaProcessor gizaProc = new GizaProcessor(gizaPath);
        return gizaProc;
    }

    private TopicDistributionProcessor[] getTopicDistributionProcessor() {
        String sourceTopicDistributionFile = this.fe.getResourceManager().getString("source.topic.distribution");
        String targetTopicDistributionFile = this.fe.getResourceManager().getString("target.topic.distribution");

        TopicDistributionProcessor topicDistProcSource = new TopicDistributionProcessor(sourceTopicDistributionFile, "source.topic.distribution");
        TopicDistributionProcessor topicDistProcTarget = new TopicDistributionProcessor(targetTopicDistributionFile, "target.topic.distribution");

        //Return processors:
        return new TopicDistributionProcessor[]{topicDistProcSource, topicDistProcTarget};

    }

    private BParserProcessor[] getBParserProcessor() {
        BParserProcessor bParserProcSource = null;
        BParserProcessor bParserProcTarget = null;

        bParserProcSource = new BParserProcessor();
        bParserProcTarget = new BParserProcessor();
        // bParserProcSource.initialize(this.fe.getSourceFile(), this.fe.getResourceManager(), this.fe.getSourceLang());
        // bParserProcTarget.initialize(this.fe.getTargetFile(), this.fe.getResourceManager(), this.fe.getTargetLang());
        bParserProcSource.initialize(this.fe.getResourceManager().getString("source.bparser.grammar"), this.fe.getResourceManager(), "source");
        bParserProcTarget.initialize(this.fe.getResourceManager().getString("target.bparser.grammar"), this.fe.getResourceManager(), "target");
        //Return processors:
        return new BParserProcessor[]{bParserProcSource, bParserProcTarget};

    }
 
    private GlobalLexiconProcessor getGlobalLexiconProcessor(){
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
        }else{
            xmlOut = this.fe.getResourceManager().getString("moses.xml");
        }

        //Create MTOutputProcessor:
        String nbestSentPath = this.fe.getResourceManager().getString("input")
                + File.separator +  this.fe.getTargetLang() + File.separator + "temp";
        String ngramExecPath = this.fe.getResourceManager().getString("tools.ngram.path");
        int ngramSize = Integer.parseInt(this.fe.getResourceManager().getString("ngramsize"));

        MTOutputProcessor mtop = new MTOutputProcessor(xmlOut, nbestSentPath, ngramExecPath, ngramSize);
        
        return mtop;
    }
}
