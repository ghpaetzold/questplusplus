/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds tag-related counts to the sentences processed.
 * @author carol
 */
public class POSTaggerProcessor extends ResourceProcessor {
    private String fileName;

    
    
    public POSTaggerProcessor(String fileName) {
        this.fileName=fileName;
    }

    @Override
    public void processNextSentence(Sentence sent) {
        int tokCount = sent.getNoTokens();
        BufferedReader brPOSTagger;
        
        try {
            brPOSTagger = new BufferedReader(new FileReader(fileName));
            
            
            String line = brPOSTagger.readLine();
            
            int contentWords = 0;
            int nounWords = 0;
            int verbWords = 0;
            int pronWords = 0;
            int otherContentWords = 0;
            int count = 0;

            while (line != null && (count < tokCount)) {
                if (!line.trim().isEmpty()) {
                    String[] split = line.split("\t");
                    String tag = "";
                    if (split.length > 1){
                        tag = split[1];
                    }
                    if (tag.contains("SENT")) {
                        tag = tag.split(" ")[0];
                    } else if (PosTagger.isNoun(tag)) {
                        nounWords++;
    //					System.out.println("is noun");
                    } else if (PosTagger.isPronoun(tag)) {
                        pronWords++;
                    } else if (PosTagger.isVerb(tag)) {
                        verbWords++;
                        //					System.out.println("is verb");
                    } else if (PosTagger.isAdditional(tag)) {
                        otherContentWords++;
                    }
                    //    	  	bwXPos.write(tag);
                    count++;
                }
                line = brPOSTagger.readLine();
            }
            
            
            
            //   bwXPos.newLine();
            contentWords = nounWords + verbWords + otherContentWords;
            sent.setValue("contentWords", contentWords);
            sent.setValue("nouns", nounWords);
            sent.setValue("verbs", verbWords);
            sent.setValue("prons", pronWords);
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        
        } catch (IOException ex) {
                ex.printStackTrace();
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        try {
            BufferedReader brPOSTaggerLemma;
            brPOSTaggerLemma = new BufferedReader(new FileReader(fileName+".XPOS.lemm"));
            String lemma = brPOSTaggerLemma.readLine();
            for(int i=0; i<source.getSentences().size();i++){
                
                this.processNextSentence(source.getSentence(i));
                //include tags for lemmmas
                String[] tags = lemma.trim().split(":::");
                source.getSentence(i).setTags(tags);
                lemma = brPOSTaggerLemma.readLine();
                
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
    }

    /*@Override
    public void processNextSentence(Sentence source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/
    
}
