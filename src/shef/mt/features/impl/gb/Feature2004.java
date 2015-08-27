/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * using n-best for training LM: sentence 1-gram log-probability
 *
 * @author cat
 *
 */
public class Feature2004 extends Feature {

    public Feature2004() {
        setIndex("2004");
        setDescription("using n-best for training LM: sentence 1-gram log-probability");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float value = (Float) source.getValue("1_nb_logprob");
        setValue(value);
    }
}
