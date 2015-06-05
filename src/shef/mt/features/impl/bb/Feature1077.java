/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * percentage of numbers in the source
 *
 * @author Catalina Hallett
 *
 */
public class Feature1077 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature1077() {
        setIndex(1077);
        setDescription("percentage of numbers in the source");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noTokens;
        StringTokenizer st = new StringTokenizer(source.getText());

        noTokens = source.getNoTokens();
        String token;
        int count = 0;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (StringOperations.isNumber(token)) {
                count++;
            }
        }
        source.setValue("noNumbers", count);
        setValue((float) count / noTokens);
    }
}
