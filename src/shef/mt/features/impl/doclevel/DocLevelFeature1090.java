/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Percentage of nouns in the target document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1090 extends DocLevelFeature {

    public DocLevelFeature1090() {
        this.setIndex(1090);
        this.setDescription("percentage of nouns in the target document");
        this.addResource("target.postagger");
        
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
