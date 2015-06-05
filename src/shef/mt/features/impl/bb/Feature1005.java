package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;

/**
 * absolute difference between no tokens and source and target normalised by
 * source length
 *
 * @author Catalina Hallett
 *
 */
public class Feature1005 extends Feature {

    public Feature1005() {
        setIndex(1005);
        setDescription("absolute difference between no tokens and source and target normalised by source length");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int sourceTok = source.getNoTokens();
        int targetTok = target.getNoTokens();

        if (sourceTok == 0) {
            setValue(0);
        } else {
            setValue((float) Math.abs(targetTok - sourceTok) / sourceTok);
        }
    }
}
