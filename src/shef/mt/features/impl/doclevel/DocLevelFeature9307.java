/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;

/**
 * PCFG Parse log likelihood
 *
 * @author Eleftherios Avramidis
 */
public class DocLevelFeature9307 extends DocLevelFeature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public DocLevelFeature9307() {
        setIndex(9303);
        setDescription("Count of possible target PCFG parses (n)");
       // this.addResource("BParser");
         this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Integer) target.getValue("bparser.n")));
    }

    @Override
    public void run(Doc source, Doc target) {
        int n = 0;
        for (int i=0;i<target.getSentences().size();i++){
            n +=(int) target.getSentence(i).getValue("bparser.n");
        }
        
        setValue(new Float((Integer) n));
    }

}
