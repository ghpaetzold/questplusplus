package shef.mt.tools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import shef.mt.features.util.Sentence;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Produces POS tag related information using the Stanford Tagger as opposed to Tree Tagger.
 * @author GustavoH
 */
public class StanfordPOSProcessor {

    private StanfordCoreNLP pipeline;

    private TokenizerAnnotator tokenizer;
    private POSTaggerAnnotator tagger;
    private ParserAnnotator parser;

    private boolean requiresPOSTags;
    private boolean requiresDepCounts;

    public StanfordPOSProcessor(String lang, String pm, HashSet<String> requirements) {
        //Create model path objects:
        String posModel = null;

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

        //Create base properties:
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos");

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
    }

    public void processSentence(Sentence sent) throws Exception {
        int tokCount = sent.getNoTokens();
        int contentWords = 0;
        int nounWords = 0;
        int verbWords = 0;
        int pronWords = 0;
        int otherContentWords = 0;
        int count = 0;

        ArrayList<String> POStags = this.tagSentence(sent);
        for (String tag : POStags) {
            if (tag.contains("SENT")) {
                tag = tag.split(" ")[0];
            } else if (PosTagger.isNoun(tag)) {
                nounWords++;
            } else if (PosTagger.isPronoun(tag)) {
                pronWords++;
            } else if (PosTagger.isVerb(tag)) {
                verbWords++;
            } else if (PosTagger.isAdditional(tag)) {
                otherContentWords++;
            }
            count++;
        }

        contentWords = nounWords + verbWords + otherContentWords;

        sent.setValue("contentWords", contentWords);
        sent.setValue("nouns", nounWords);
        sent.setValue("verbs", verbWords);
        sent.setValue("prons", pronWords);
    }

    public ArrayList<String> tagSentence(Sentence s) {
        //Create resource objects:
        ArrayList<String> POSData = new ArrayList<>();

        //Get sentences' tokens:
        String[] sentenceTokens = s.getTokens();

        //Create content object:
        Annotation document = new Annotation(s.getText());

        //Annotate content object:
        pipeline.annotate(document);

        //Initialize index shift:
        int shift = 0;

        //Get sentence fragments:
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class
        );

        //Initialize token buffer and index:
        String buffer = "";
        int index = 0;
        for (CoreMap sentence : sentences) {
            //Get tokens from sentence fragment:
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            //Add tokens to resulting POS tag list:
            for (CoreLabel token : tokens) {
                String[] fragments = new String[]{token.originalText()};
                if (token.originalText().contains(" ")) {
                    fragments = token.originalText().split(" ");
                }
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                for (String fragment : fragments) {
                    buffer += fragment;
                    if (buffer.trim().equals(sentenceTokens[index])) {
                        POSData.add(pos);
                        index += 1;
                        buffer = "";
                    }
                }
            }
        }

        return POSData;
    }
}
