/**
 *
 */
package shef.mt.features.impl.gb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * size of nbest list
 *
 * @author Catalina Hallett
 *
 */
public class Feature2003 extends Feature {

    public Feature2003() {
        setIndex("2003");
        setDescription("size of nbest list");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Integer) source.getValue("nbestSize"));
    }
}
