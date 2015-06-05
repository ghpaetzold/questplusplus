package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * ratio of number of tokens in source and target
 *
 * @author Catalina Hallett
 *
 */
public class Feature1003 extends Feature {

    public Feature1003() {
        setIndex(1003);
        setDescription("ratio of number of tokens in source and target");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int sourceTok = source.getNoTokens();
        int targetTok = target.getNoTokens();
        if (targetTok == 0) {
            setValue(0);
        } else {
            setValue((float) sourceTok / targetTok);
        }
    }
}
