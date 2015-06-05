/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.util.Set;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * @author Catalina Hallett
 *
 */
public class Feature1201 extends Feature {

    public Feature1201() {
        setIndex("1201");
        setDescription("Number of types");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        Set<String> types = new HashSet<String>();
        for (String token : source.getTokens()) {
            types.add(token.toLowerCase());
        }
        setValue(types.size());
    }
}
