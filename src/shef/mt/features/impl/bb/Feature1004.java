package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * no tokens in the target / no tokens in the source
 *
 * @author Catalina Hallett
 *
 */
public class Feature1004 extends Feature {

    public Feature1004() {
        setIndex(1004);
        setDescription("no tokens in the target / no tokens in the source");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub

        int sourceTok = source.getNoTokens();
        int targetTok = target.getNoTokens();
        if (sourceTok == 0) {
            setValue(0);
        } else {
            setValue((float) targetTok / sourceTok);
        }
    }
}
