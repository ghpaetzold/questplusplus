/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import shef.mt.features.util.Doc;
import shef.mt.features.util.FeatureManager;
import shef.mt.features.util.Sentence;
import shef.mt.util.PropertiesManager;


/**
 * This class is part of the implementation of the document-level
 * quality estimation features.
 * Features related to repetition of words and lexical items are 
 * implemented here.
 * 
 * @author carol
 */
public class DiscourseRepetition extends ResourceProcessor {
    private HashMap<String,Integer> wordRepetition;
    private HashMap<String,Integer> lemmaRepetition;
    private HashMap<String,Integer> nounRepetition;
    private Integer contentWords;
    private Integer lemmas;
    private Integer nouns;
    
    
    public DiscourseRepetition(){
        this.wordRepetition = new HashMap<String,Integer>(); 
        this.lemmaRepetition = new HashMap<String,Integer>(); 
        this.nounRepetition = new HashMap<String,Integer>(); 
        this.contentWords=0;
        this.lemmas=0;
        this.nouns=0;
    }
    
    @Override
    public void processNextSentence(Sentence sentence) {
        //System.out.println(((String) sentence.getValue("POSinfo")));
        String[] sentence_tags = sentence.getTags();
        //String[] sentence_tags = ((String) sentence.getValue("POSinfo")).split(" ");
        String lemma;
        String tag;
        String word;

        for (String token:sentence_tags){
            
            if(token.split("_").length==3){
                tag = token.split("_")[1].trim();
                word = token.split("_")[0].trim();
                
                if ((PosTagger.isNoun(tag)) || (PosTagger.isVerb(tag)) || (tag.equals("ADJ")) || (tag.equals("ADV"))){
                    lemma = token.split("_")[2].trim();
                    
                    
                    if (PosTagger.isNoun(tag)){
                        if (nounRepetition.containsKey(word+"_"+tag)){
                            nounRepetition.put(word+"_"+tag,nounRepetition.get(word+"_"+tag)+1);
                        }
                        else{
                            nouns+=1;
                            nounRepetition.put(word+"_"+tag, 0);
                        }
                    }

                    //count words repetition
                    if (wordRepetition.containsKey(word+"_"+tag)){
                        wordRepetition.put(word+"_"+tag,wordRepetition.get(word+"_"+tag)+1);
                    }
                    else{
                        contentWords+=1;
                        wordRepetition.put(word+"_"+tag, 0);
                    }

                    //count lemma repetition
                    if (lemma.equals("<unknown>")){
                        lemma = word;
                    }
                    if (lemmaRepetition.containsKey(lemma+"_"+tag)){
                        lemmaRepetition.put(lemma+"_"+tag,lemmaRepetition.get(lemma+"_"+tag)+1);
                    }
                    else{
                        lemmas+=1;
                        lemmaRepetition.put(lemma+"_"+tag, 0);
                    }
                }
            }
        }
        
        //System.out.println("(((((("+);
        
        if (contentWords == 0){
            contentWords = 1;
        }

        if (lemmas == 0){
            lemmas = 1;
        }
        if (nouns == 0){
            nouns = 1;
        }
                
    }
    
        
    @Override
    public void processNextDocument(Doc doc) {
        
        for (int i=0;i<doc.getSentences().size();i++){
            this.processNextSentence(doc.getSentence(i));
        }
        int sumWordRepetition=0;
        int sumLemmaRepetition=0;
        int sumNounRepetition=0;

        for (int value : wordRepetition.values()){
            sumWordRepetition+=value;
        }
        
        for (int value : lemmaRepetition.values()){
            sumLemmaRepetition+=value;
        }
        
        for (int value : nounRepetition.values()){
            sumNounRepetition+=value;
        }
        
       
        doc.setValue("word_repetition", new Float((float)sumWordRepetition/(float)contentWords));
        doc.setValue("lemma_repetition", new Float((float)sumLemmaRepetition/(float)lemmas));
        doc.setValue("noun_repetition", new Float((float)sumNounRepetition/(float)nouns));
    }    
}
