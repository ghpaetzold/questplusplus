/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * number source tokens that do not contain only a-z (SET TO 0 for Arabic)
 *
 * @author cat
 *
 */
public class Feature1080 extends Feature {

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    public Feature1080() {
        setIndex(1080);
        setDescription("");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noTokens;
        StringTokenizer st = new StringTokenizer(source.getText());
        if (source.isSet("noTokens")) {
            noTokens = source.getNoTokens();
        } else {
            noTokens = st.countTokens();
            source.setValue("noTokens", noTokens);
        }
        String token;
        int count = 0;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (StringOperations.isNoAlpha(token)) {
                count++;
            }

        }
        setValue((float) count / noTokens);
        //setValue(0);
    }
}
