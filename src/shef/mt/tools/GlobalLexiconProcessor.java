/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * @author carolis
 */
public class GlobalLexiconProcessor extends ResourceProcessor{
    private String globalLexiconPath;    
    private Double minweight;
    
    public GlobalLexiconProcessor(String globalLexiconPath, Double minweight){
        this.globalLexiconPath = globalLexiconPath;
        this.minweight = minweight;
        System.out.println(globalLexiconPath);
        GlobalLexicon globalLexicon = new GlobalLexicon(this.globalLexiconPath, this.minweight);
    }

    @Override
    public void processNextSentence(Sentence source) {
        
    }

    @Override
    public void processNextDocument(Doc source) {
  
    }
    
}
