package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between number of periods in source and target normalised
 * by target length
 *
 * @author cat
 *
 */
public class Feature1063 extends Feature {

    public Feature1063() {
        setIndex(1063);
        setDescription("absolute difference between number of periods in source and target normalised by target length");
    }

    public void run(Sentence source, Sentence target) {
        float sourceCount;
        float targetCount;
        if (!source.isSet("count_.")) {
            sourceCount = source.countChar('.');
        } else {
            sourceCount = (Integer) source.getValue("count_.");
        }

        if (!target.isSet("count_.")) {
            targetCount = target.countChar('.');
        } else {
            targetCount = (Integer) target.getValue("count_.");
        }

        float noTokensSource = 1;
        float noTokensTarget = 1;
        if (source.isSet("noTokens")) {
            noTokensSource = source.getNoTokens();
        }
        if (target.isSet("noTokens")) {
            noTokensTarget = target.getNoTokens();
        }

        setValue(Math.abs(sourceCount - targetCount) / noTokensTarget);
    }
}
