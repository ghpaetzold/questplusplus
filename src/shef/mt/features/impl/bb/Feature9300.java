/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * PCFG Parse log likelihood
 *
 * @author Eleftherios Avramidis
 */
public class Feature9300 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature9300() {
        setIndex(9300);
        setDescription("Source PCFG Parse log-likelihood");
       // this.addResource("BParser");
         this.addResource("source.bparser.grammar");
    }

    public void run(Sentence source, Sentence target) {
        //   System.out.println((Float)source.getValue("bparser.loglikelihood"));
        setValue(new Float((Double) source.getValue("bparser.loglikelihood")));
    }

}
