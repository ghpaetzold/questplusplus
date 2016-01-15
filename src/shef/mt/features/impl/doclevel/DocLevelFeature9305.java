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
public class DocLevelFeature9305 extends DocLevelFeature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public DocLevelFeature9305() {
        setIndex(9305);
        setDescription("Target PCFG average confidence of all possible parses in n-best list");
       // this.addResource("BParser");
         this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Double) target.getValue("bparser.avgConfidence")));
    }

    @Override
    public void run(Doc source, Doc target) {
        double avgConfidence = 0;
        for (int i=0;i<target.getSentences().size();i++){
            avgConfidence +=(double) target.getSentence(i).getValue("bparser.avgConfidence");
        }
        
        setValue(new Float((Double) avgConfidence/target.getSentences().size()));
    }

}
