/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.StringOperations;

/**
 * ratio of percentage of tokens a-z in the source and tokens a-z in the target
 * (SET TO 0)
 *
 * @author cat
 *
 */
public class Feature1082 extends Feature {

    public Feature1082() {
        setIndex(1082);
        setDescription("ratio of percentage of tokens a-z in the source and tokens a-z in the target");
    }

    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        float sourcePerc = percentageSentence(source);
        float targetPerc = percentageSentence(target);
        if (targetPerc == 0) {
            setValue(0);
        } else {
            setValue(sourcePerc / targetPerc);
        }
        //setValue(0);
    }

    private float percentageSentence(Sentence sentence) {
        float noTokens;
        StringTokenizer st = new StringTokenizer(sentence.getText());
        noTokens = sentence.getNoTokens();
        String token;
        int count = 0;
        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (StringOperations.isNoAlpha(token)) {
                count++;
            }

        }
        return (float) count / noTokens;

    }
}
