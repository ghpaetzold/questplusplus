/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the depth of the syntactic tree for the source
 * and the depth of the syntactic tree for the target
 *
 * @author Catalina Hallett
 *
 *
 */
public class DocLevelFeature1101 extends DocLevelFeature {

    public DocLevelFeature1101() {
        setIndex(1101);
        setDescription("absolute difference between the depth of the syntactic tree for the source and the depth of the syntactic tree for the target");
        
        //requires named entities
        this.addResource("postags");
        
    }

    public void run(Sentence source, Sentence target) {
        
    }

    @Override
    public void run(Doc source, Doc target) {
        int total = 0;
        for (int i=0;i<source.getSentences().size();i++){
            float dptSource = (float) source.getSentence(i).getValue("depth");
            float dptTarget = (float) target.getSentence(i).getValue("depth");
            total+=(Math.abs(dptSource - dptTarget));
        }
        setValue((float) total/source.getSentences().size());
    }
}
