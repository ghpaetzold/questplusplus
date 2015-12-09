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
public class DocLevelFeature9303 extends DocLevelFeature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public DocLevelFeature9303() {
        setIndex(9303);
        setDescription("Count of possible source PCFG parses (n)");
       // this.addResource("BParser");
        this.addResource("source.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Integer) source.getValue("bparser.n")));
    }

    @Override
    public void run(Doc source, Doc target) {
        int n = 0;
        for (int i=0;i<source.getSentences().size();i++){
            n +=(int) source.getSentence(i).getValue("bparser.n");
        }
        
        setValue(new Float((Integer) n));
    }

}
