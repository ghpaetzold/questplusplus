/**
 *
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.impl.DocLevelFeature;
import java.util.*;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 *
 * percentage of content words in the target
 *
 * @author cat
 *
 */
public class DocLevelFeature1083 extends DocLevelFeature {

    public DocLevelFeature1083() {
        setIndex(1083);
        setDescription("percentage of content words in the target");
        this.addResource("postagger");
        
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run(Doc source, Doc target) {
        float noContent =0;
        float noWords =0;
        for(int i=0;i<target.getSentences().size();i++){
            noWords+= target.getSentence(i).getNoTokens();
            noContent+= (Integer) target.getSentence(i).getValue("contentWords");
        }
        setValue(noContent / noWords);
    }
}
