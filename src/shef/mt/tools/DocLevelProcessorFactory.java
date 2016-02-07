package shef.mt.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import shef.mt.DocLevelFeatureExtractor;
import shef.mt.FeatureExtractor;

public class DocLevelProcessorFactory {

    private ResourceProcessor[][] resourceProcessors;
    
    private ResourceProcessor[][] docResourceProcessors;

    private FeatureExtractor fe;
    
    private HashSet<String> requirements;
    
    private ArrayList<ResourceProcessor> sourceProcessors;
    
    private ArrayList<ResourceProcessor> targetProcessors;
    
    private ArrayList<ResourceProcessor> docSourceProcessors;
    
    private ArrayList<ResourceProcessor> docTargetProcessors;

    public DocLevelProcessorFactory(FeatureExtractor fe) {
        //Setup initial instance of ResourceProcessor matrix:
        this.resourceProcessors = null;

        //Setup feature extractor:
        this.fe = fe;

        //Get required resources:
        requirements = fe.getFeatureManager().getRequiredResources();

        //Allocate source and target processor vectors:
        sourceProcessors = new ArrayList<>();
        targetProcessors = new ArrayList<>();
        
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
            POSNgramCountProcessor ngramProcessorTarget = this.getPOSNgramProcessor();

            //Add them to processor vectors:
            targetProcessors.add(ngramProcessorTarget);
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

    private NgramCountProcessor[] getNgramProcessors() {
        //Register resource:
        ResourceManager.registerResource("source.ngram");
        ResourceManager.registerResource("target.ngram");

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
        ResourceManager.registerResource("target.lm");
        ResourceManager.registerResource("source.lm");
        
        //Generate output paths:
        String sourceOutput = this.fe.getSourceFile() + ".ppl";
        String targetOutput = this.fe.getTargetFile() + ".ppl";

        File fSource = new File(sourceOutput);
        File fTarget = new File(targetOutput);
        
        //Read language models:
        NGramExec nge = new NGramExec(this.fe.getResourceManager().getString("tools.ngram.path"), true);

        //Get paths of LMs:
        String sourceLM = this.fe.getResourceManager().getString("source.lm");
        String targetLM = this.fe.getResourceManager().getString("target.lm");

        //Run LM reader:
        System.out.println("Running SRILM...");
        System.out.println(this.fe.getSourceFile());
        System.out.println(this.fe.getTargetFile());
        if (fSource.exists() && fSource.length() != 0) {
            System.out.println(sourceOutput+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.fe.getSourceFile(), sourceOutput, sourceLM);
        }
        if (fTarget.exists() && fTarget.length() != 0) {
            System.out.println(targetOutput+" exists! SRILIM will not run!");
        }else{
            nge.runNGramPerplex(this.fe.getTargetFile(), targetOutput, targetLM);
        }
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplPosProcSource = new PPLProcessor(sourceOutput,
                new String[]{"logprob", "ppl", "ppl1"});
        PPLProcessor pplPosProcTarget = new PPLProcessor(targetOutput,
                new String[]{"logprob", "ppl", "ppl1"});

        //Return processors:
        return new PPLProcessor[]{pplPosProcSource, pplPosProcTarget};
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

    private LanguageModel getPOSNGramModel() {
        //Create ngram file processors:
        NGramProcessor targetNgp = new NGramProcessor(this.fe.getResourceManager().getString("target.posngram"));

        //Generate resulting handlers:
        LanguageModel result = targetNgp.run();

        //Return handlers:
        return result;
    }

    private POSNgramCountProcessor getPOSNgramProcessor() {
        //Register resource:
        ResourceManager.registerResource("target.posngram");
        ResourceManager.registerResource("source.posngram");

        //Get target POS Language Models:
        LanguageModel ngramModelTarget = this.getPOSNGramModel();

        //Create source and target processors:
        POSNgramCountProcessor targetProcessor = new POSNgramCountProcessor(ngramModelTarget);

        //Return processors:
        return targetProcessor;
    }
   
    private POSTaggerProcessor[] getPOSTaggerProcessors() {
        ResourceManager.registerResource("source.postagger");
        ResourceManager.registerResource("target.postagger");
        String posNameSource = "shef.mt.tools.PosTreeTagger";
        String posNameTarget = "shef.mt.tools.PosTreeTagger";
        String outputPathSource = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getSourceLang()+File.separator;
        String outputPathTarget = this.fe.getResourceManager().getProperty("input") + File.separator + this.fe.getTargetLang()+File.separator;
        File sourceFile = new File(this.fe.getSourceFile());
        File targetFile = new File(this.fe.getTargetFile());   
        String absoluteSourceFilePath = sourceFile.getAbsolutePath();
        String absoluteTargetFilePath = targetFile.getAbsolutePath();
        String fileNameSource = sourceFile.getName();
        String fileNameTarget = targetFile.getName();
        String outputFileSource = outputPathSource+fileNameSource+".pos";
        String outputFileTarget = outputPathTarget+fileNameTarget+".pos";
        String posSourceTaggerPath = this.fe.getResourceManager().getString("source.postagger");
        String posTargetTaggerPath = this.fe.getResourceManager().getString("target.postagger");
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
        ResourceManager.registerResource("giza.path");
        FileModel fm = new FileModel(this.fe.getSourceFile(), this.fe.getResourceManager().getString("source.corpus"));
        String gizaPath = this.fe.getResourceManager().getString("giza.path");
        GizaProcessor gizaProc = new GizaProcessor(gizaPath);
        return gizaProc;
    }
    
    private PPLProcessor getPOSLMProcessor() {
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
        System.out.println(this.fe.getTargetFile());
        nge.runNGramPerplex(this.fe.getTargetFile(), targetOutput, targetLM);
        System.out.println("SRILM finished!");

        //Generate PPL processors:
        PPLProcessor pplProcTarget = new PPLProcessor(targetOutput,
                new String[]{"poslogprob", "posppl", "posppl1"});

        //Return processors:
        return pplProcTarget;
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
    
    
    public void execProcessors(){
        //Allocate source and target processor vectors:
        docSourceProcessors = new ArrayList<>();
        docTargetProcessors = new ArrayList<>();

        if (requirements.contains("giza.path")) {
            //Get alignment processors:
            GizaProcessor gizaProcessor = this.getGizaProcessor();

            //Add them to processor vectors:
            docTargetProcessors.add(gizaProcessor);
            docSourceProcessors.add(gizaProcessor);
            
        }
        
        if (requirements.contains("source.postagger") || requirements.contains("target.postagger")) {
            //Get POSTagger processors:
            POSTaggerProcessor[] posTaggerProcessors = this.getPOSTaggerProcessors();
            POSTaggerProcessor posTaggerProcSource = posTaggerProcessors[0];
            POSTaggerProcessor posTaggerProcTarget = posTaggerProcessors[1];

            //Add them to processor vectors
            docSourceProcessors.add(posTaggerProcSource);
            docTargetProcessors.add(posTaggerProcTarget);
        }
        
        if (requirements.contains("source.lm") || requirements.contains("target.lm")) {
            //Run SRILM on language models:
            PPLProcessor[] pplProcessors = this.getLMProcessors();
            PPLProcessor pplProcSource = pplProcessors[0];
            PPLProcessor pplProcTarget = pplProcessors[1];

            //Add them to processor vectors:
            docSourceProcessors.add(pplProcSource);
            docTargetProcessors.add(pplProcTarget);
        }
        
        if (requirements.contains("target.poslm")) {
            //Run SRILM on language models:
            //Run SRILM on language models:
            PPLProcessor pplProcTarget = this.getPOSLMProcessor();

            //Add them to processor vectors:
            docTargetProcessors.add(pplProcTarget);
        }
        
        if (requirements.contains("discrep")) {
            //Get stopwords processors:
            DiscourseRepetition[] discRepProcessors = this.getDiscourseRepetitionProcessors();
            DiscourseRepetition discRepProcSource = discRepProcessors[0];
            DiscourseRepetition discRepProcTarget = discRepProcessors[1];

            //Add them to processor vectors:
            docSourceProcessors.add(discRepProcSource);
            docTargetProcessors.add(discRepProcTarget);
        }
        
        //Transform array lists in vectors:
        ResourceProcessor[] sourceProcessorVector = new ResourceProcessor[docSourceProcessors.size()];
        ResourceProcessor[] targetProcessorVector = new ResourceProcessor[docTargetProcessors.size()];
        sourceProcessorVector = (ResourceProcessor[]) docSourceProcessors.toArray(sourceProcessorVector);
        targetProcessorVector = (ResourceProcessor[]) docTargetProcessors.toArray(targetProcessorVector);

        //Return vectors:
        this.docResourceProcessors = new ResourceProcessor[][]{sourceProcessorVector, targetProcessorVector};
    }
    
         /**
     * @return the resourceProcessors
     */
    public ResourceProcessor[][] getResourceProcessors() {
        return resourceProcessors;
    }
    
    public ResourceProcessor[][] getDocResourceProcessors() {
        return docResourceProcessors;
    }
}
