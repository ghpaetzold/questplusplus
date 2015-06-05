package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

import java.util.*;

/**
 * average token length in source
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1006 extends Feature {

    public Feature1006() {
        setIndex(1006);
        setDescription("average source token length");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        String[] tokens = source.getTokens();
        int noChars = 0;
        int noTokens = tokens.length;
        for (String token : tokens) {
            noChars += token.length();
        }
        setValue((float) noChars / noTokens);
    }
}
