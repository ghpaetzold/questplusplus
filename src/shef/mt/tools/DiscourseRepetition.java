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
    private Integer contentWords=0;
    private Integer lemmas=0;
    private Integer nouns=0;
    private String type;
    
    @Override
    public void processNextSentence(Sentence sentence) {
        int value = 0;
        int value2 = 0;
        int value3 = 0;
        //System.out.println(((String) sentence.getValue("POSinfo")));
        String[] sentence_tags = sentence.getTags();
        //String[] sentence_tags = ((String) sentence.getValue("POSinfo")).split(" ");
        String lemma;
        String tag;
        String word;
        for (String token:sentence_tags){
                lemma = token.split("_")[2].trim();
                tag = token.split("_")[1].trim();
                word = token.split("_")[0].trim();
                         
                if (PosTagger.isNoun(tag)){
                    if (nounRepetition.containsKey(word+"_"+tag)){
                        nouns += 1;
                        value3 += nounRepetition.get(word+"_"+tag);
                    }
                }
                
                //count word repetitions for each sentence
                if (wordRepetition.containsKey(word+"_"+tag)){
                        contentWords += 1;
                        value += wordRepetition.get(word+"_"+tag);
                }
                
                //count lemma repetitions for each sentence
                if (lemma.equals("<unknown>")){
                    lemma = word;
                }
                if (lemmaRepetition.containsKey(lemma+"_"+tag)){
                        lemmas += 1;
                        value2 += lemmaRepetition.get(lemma+"_"+tag);
                }

        }
        
        if (contentWords == 0){
            contentWords = 1;
        }

        if (lemmas == 0){
            lemmas = 1;
        }
        if (nouns == 0){
            nouns = 1;
        }
        
        if (type.equals("target")){
            sentence.setValue("word_repetition_target", new Float((float)value/(float)contentWords));

            sentence.setValue("lemma_repetition_target", new Float((float)value2/(float)lemmas));
            
            sentence.setValue("noun_repetition_target", new Float((float)value3/(float)nouns));
        }
        else{
            sentence.setValue("word_repetition_source", new Float((float)value/(float)contentWords));

            sentence.setValue("lemma_repetition_source", new Float((float)value2/(float)lemmas));
            
            sentence.setValue("noun_repetition_source", new Float((float)value3/(float)nouns));
        }
        
    }
    
        
    public void initialize(PropertiesManager propertiesManager,
			FeatureManager featureManager, String file) {
                String filename = "";
                type = file;
                if (type.equals("target")){
                    filename = propertiesManager.getProperty("tokenized_tagged_target");
                }
                else{
                    filename = propertiesManager.getProperty("tokenized_tagged_source");
                }
		
		FileInputStream fstream;
		
		wordRepetition = new HashMap<String,Integer>(); 
                lemmaRepetition = new HashMap<String,Integer>(); 
                nounRepetition = new HashMap<String,Integer>(); 
		
		try {
                    fstream = new FileInputStream(filename);


                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String strLine;
                    String word;
                    String lemma;
                    String tag;


                    //Read File Line By Line
                    while ((strLine = br.readLine()) != null)   {
                            String[] tok = strLine.split("\t");
                            word = tok[0].trim();
                            lemma = tok[2].trim();
                            tag = tok[1].trim();
                            if ((PosTagger.isNoun(tag)) || (PosTagger.isVerb(tag)) || (tag.equals("ADJ")) || (tag.equals("ADV"))){
                                
                                if (PosTagger.isNoun(tag)){
                                    if (nounRepetition.containsKey(word+"_"+tag)){
                                        nounRepetition.put(word+"_"+tag,nounRepetition.get(word+"_"+tag)+1);
                                    }
                                    else{

                                        nounRepetition.put(word+"_"+tag, 0);
                                    }
                                }
                                
                                //count words repetition
                                if (wordRepetition.containsKey(word+"_"+tag)){
                                    wordRepetition.put(word+"_"+tag,wordRepetition.get(word+"_"+tag)+1);
                                }
                                else{
                                    
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
                                    lemmaRepetition.put(lemma+"_"+tag, 0);
                                }
                            }

                    }

                    //Close the input stream
                    br.close();
                    
                    
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

	}
    
}
