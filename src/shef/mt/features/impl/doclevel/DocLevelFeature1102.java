/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * number of prepositional phrases in the source
 *
 * @author Catalina Hallett
 *
 *
 */
public class DocLevelFeature1102 extends DocLevelFeature {

    public DocLevelFeature1102() {
        setIndex("1102");
        
        setDescription("number of prepositional phrases in the source");
        //requires named entities
        this.addResource("postags");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        


    }

    @Override
    public void run(Doc source, Doc target) {
        float total=0;
        for (int i=0;i<source.getSentences().size();i++){
            total+=(float) source.getSentence(i).getValue("PP");
        }
        
        setValue(total);
    }
}
