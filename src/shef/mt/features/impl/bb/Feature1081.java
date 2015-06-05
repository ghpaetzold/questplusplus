/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;
import shef.mt.features.impl.Feature;
import java.util.StringTokenizer;


/**
 * percentage of tokens in the target which do not contain only a-z
 *
 * @author cat
 *
 */
public class Feature1081 extends Feature {

    public Feature1081() {
        setIndex(1081);
        setDescription("percentage of tokens in the target which do not contain only a-z");
    }

    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float noTokens;
        StringTokenizer st = new StringTokenizer(target.getText());

        noTokens = target.getNoTokens();
        String token;
        int count = 0;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (StringOperations.isNoAlpha(token)) {
                count++;
            }

        }
        setValue((float) count / noTokens);
    }
}
