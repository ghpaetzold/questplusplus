/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * @author carol
 */
public class GizaProcessor extends ResourceProcessor{
    private String gizaPath;    
    
    public GizaProcessor(String gizaPath){
        this.gizaPath = gizaPath;
    }

    @Override
    public void processNextSentence(Sentence source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processNextDocument(Doc source) {
  
        System.out.println(gizaPath);
        Giza giza = new Giza(gizaPath);
    }
    
}
