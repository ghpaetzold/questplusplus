/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Creates a static Giza object to be used by several features.
 * @author carol
 */
public class GizaProcessor extends ResourceProcessor{
    private String gizaPath;    
    
    public GizaProcessor(String gizaPath){
        this.gizaPath = gizaPath;
        System.out.println(gizaPath);
        Giza giza = new Giza(gizaPath);
    }

    @Override
    public void processNextSentence(Sentence source) {
        
    }

    @Override
    public void processNextDocument(Doc source) {
  
    }
    
}
