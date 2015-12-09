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
public class DocLevelFeature9304 extends DocLevelFeature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public DocLevelFeature9304() {
        setIndex(9304);
        setDescription("Target PCFG Parse log-likelihood");
       // this.addResource("BParser");
        this.addResource("target.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        setValue(new Float((Double) target.getValue("bparser.loglikelihood")));
    }

    @Override
    public void run(Doc source, Doc target) {
        double likelihood = 0;
        for (int i=0;i<target.getSentences().size();i++){
            likelihood +=(double) target.getSentence(i).getValue("bparser.loglikelihood");
        }
        
        setValue(new Float((Double) likelihood/target.getSentences().size()));
    }

}
