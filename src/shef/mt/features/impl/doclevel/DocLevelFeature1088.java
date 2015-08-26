/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Percentage of nouns in the source document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1088 extends DocLevelFeature {

    public DocLevelFeature1088() {
        this.setIndex(1088);
        this.setDescription("percentage of nouns in the source document");
        this.addResource("source.postagger");
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
        int noWords = 0;
                
        float noNouns = 0;
        
        for(int i=0;i<source.getSentences().size();i++){
            noNouns+=(Integer) source.getSentence(i).getValue("nouns");
            noWords+=source.getSentence(i).getNoTokens();
        }
        setValue(noNouns / noWords);
    }
}
