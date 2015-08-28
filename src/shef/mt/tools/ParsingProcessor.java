/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds Stanford POS tags and dependency parses to the processed sentences.
 * @author GustavoH
 */
public class ParsingProcessor extends ResourceProcessor {

    private StanfordCoreNLP pipeline;

    private TokenizerAnnotator tokenizer;
    private POSTaggerAnnotator tagger;
    private ParserAnnotator parser;

    private boolean requiresPOSTags;
    private boolean requiresDepCounts;

    public ParsingProcessor(String lang, String pm, String dm, HashSet<String> requirements) {
        //Store required resources:
        this.requiresPOSTags = requirements.contains("postags");
        this.requiresDepCounts = requirements.contains("depcounts");

        //Create model path objects:
        String posModel = null;
        String depModel = null;

        //Setup model paths:
        if (pm == null) {
            if (lang.equals("english")) {
                posModel = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
            } else if (lang.equals("spanish")) {
                posModel = "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger";
            } else if (lang.equals("chinese")) {
                posModel = "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger";
            } else {
                posModel = "edu/stanford.nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
            }
        } else {
            posModel = pm;
        }
        if (dm == null) {
            if (lang.equals("english")) {
                depModel = "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz";
            } else if (lang.equals("spanish")) {
                depModel = "edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz";
            } else if (lang.equals("chinese")) {
                depModel = "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz";
            } else {
                depModel = "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz";
            }
        } else {
            depModel = dm;
        }

        //Create base properties:
        Properties props = new Properties();
        if (this.requiresDepCounts) {
            props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        } else {
            props.setProperty("annotators", "tokenize, ssplit, pos");
        }

        //Create base pipeline:
        pipeline = new StanfordCoreNLP(props);

        try {
            //Create pipeline object:
            tokenizer = new TokenizerAnnotator(true, TokenizerAnnotator.TokenizerType.Whitespace);

            //Add objects to the pipeline:
            pipeline.addAnnotator(tokenizer);
        } catch (Exception ex) {
            System.out.println("ERROR: Problem while creating Stanford tokenizer.");
        }

        try {
            //Create pipeline object:
            tagger = new POSTaggerAnnotator(posModel, false);

            //Add object to the pipeline:
            pipeline.addAnnotator(tagger);
        } catch (Exception ex) {
            System.out.println("ERROR: Problem while creating Stanford POS tagger. Please review the model paths and check for library availability.");
        }

        //If dependency counts are required:
        if (this.requiresDepCounts) {
            try {
                //Create pipeline object:
                parser = new ParserAnnotator(depModel, false, 300, StringUtils.EMPTY_STRING_ARRAY);

                //Add object to the pipeline:
                pipeline.addAnnotator(parser);
            } catch (Exception ex) {
                System.out.println("ERROR: Problem while creating Stanford dependency parser. Please review the model paths and check for library availability.");
            }
        }
    }

    @Override
    public void processNextSentence(Sentence s) {
        //Create resource objects:
        ArrayList<String> POSData = new ArrayList<>();
        HashMap<Integer, Integer> depData = new HashMap<>();
        
        //Get sentences' tokens:
        String[] sentenceTokens = s.getTokens();

        //Create content object:
        Annotation document = new Annotation(s.getText());

        //Annotate content object:
        pipeline.annotate(document);

        //Initialize index shift:
        int shift = 0;

        //Get sentence fragments:
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
        //Initialize token buffer and index:
        String buffer = "";
        int index = 0;
        for (CoreMap sentence : sentences) {
            //Get tokens from sentence fragment:
            List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);

            //Add tokens to resulting POS tag list:
            for (CoreLabel token : tokens) {
                String[] fragments = new String[]{token.originalText()};
                if (token.originalText().contains(" ")) {
                    fragments = token.originalText().split(" ");
                }
                String pos = token.get(PartOfSpeechAnnotation.class);
                for (String fragment : fragments) {
                    buffer += fragment;
                    if(buffer.trim().equals(sentenceTokens[index])){
                        POSData.add(pos);
                        index += 1;
                        buffer = "";
                    }
                }
            }

            //Check for dependency parsing requirement:
            if (this.requiresDepCounts) {
                //Get dependency relations:
                SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
                List<SemanticGraphEdge> deps = dependencies.edgeListSorted();

                //For each edge, add 1 to occurrences of source and target indexes:
                for (SemanticGraphEdge sge : deps) {
                    int sourceIndex = shift + sge.getSource().index() - 1;
                    int targetIndex = shift + sge.getTarget().index() - 1;
                    if (depData.get(sourceIndex) == null) {
                        depData.put(sourceIndex, 1);
                    } else {
                        depData.put(sourceIndex, depData.get(sourceIndex) + 1);
                    }
                    if (depData.get(targetIndex) == null) {
                        depData.put(targetIndex, 1);
                    } else {
                        depData.put(targetIndex, depData.get(targetIndex) + 1);
                    }

                    //Increase shift:
                    shift += tokens.size();
                }
            }
        }

        //Add resources to sentence:
        s.setValue("postags", POSData);
        s.setValue("depcounts", depData);
    }

    @Override
    public void processNextDocument(Doc source) {
        for (int i=0;i<source.getSentences().size();i++){
            this.processNextSentence(source.getSentence(i));
        }
    }

}
