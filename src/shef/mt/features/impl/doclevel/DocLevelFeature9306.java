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
public class DocLevelFeature9306 extends DocLevelFeature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public DocLevelFeature9306() {
        setIndex(9306);
        setDescription("Target PCFG confidence of best parse");
      //  this.addResource("BParser");
         this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Double) target.getValue("bparser.bestParseConfidence")));
    }

    @Override
    public void run(Doc source, Doc target) {
        double bestParseConfidence = 0;
        for (int i=0;i<source.getSentences().size();i++){
            bestParseConfidence +=(double) target.getSentence(i).getValue("bparser.bestParseConfidence");
        }
        
        setValue(new Float((Double) bestParseConfidence/target.getSentences().size()));
    }

}
