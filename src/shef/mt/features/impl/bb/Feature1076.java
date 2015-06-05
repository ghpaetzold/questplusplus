package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between number of punctuation marks between source and
 * target normalised by target length
 *
 * @author Catalina Hallett
 *
 */
public class Feature1076 extends Feature {

    public Feature1076() {
        setIndex(1076);
        setDescription("absolute difference between number of punctuation marks between source and target normalised by target length");
    }

    public void run(Sentence source, Sentence target) {

        float noTokensSource;
        float noTokensTarget;

        noTokensSource = source.getNoTokens();
        noTokensTarget = target.getNoTokens();

        float noPunctS = 0;
        float noPunctT = 0;
        if (source.isSet("noPunct")) {
            noPunctS = (Integer) source.getValue("noPunct");
        }

        if (target.isSet("noPunct")) {
            noPunctT = (Integer) target.getValue("noPunct");
        }

        setValue(Math.abs(noPunctS - noPunctT) / noTokensTarget);



    }
}