/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * percentage of nouns in the target
 *
 * @author cat
 *
 */
public class DocLevelFeature1090 extends DocLevelFeature {

    public DocLevelFeature1090() {
        setIndex(1090);
        setDescription("percentage of nouns in the target");
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
        float noWords = 0;
        float noNouns = 0;
        for (int i=0;i<target.getSentences().size();i++){
            noWords = target.getSentence(i).getNoTokens();
            noNouns = (Integer) target.getSentence(i).getValue("nouns");
        }
        setValue(noNouns / noWords);
    }
}
