/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Percentage of verbs in the source document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1089 extends DocLevelFeature {

    public DocLevelFeature1089() {
        this.setIndex(1089);
        this.setDescription("percentage of verbs in the source document");
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
        float noWords =0;
        float noVerbs =0;
        for(int i=0;i<source.getSentences().size();i++){
            noWords += source.getSentence(i).getNoTokens();
            noVerbs += (Integer) source.getSentence(i).getValue("verbs");
        }
        setValue(noVerbs / noWords);
    }
}
