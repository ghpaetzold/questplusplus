package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between number of ? in source and target normalized by
 * target length
 *
 * @author Catalina Hallett
 *
 */
public class Feature1071 extends Feature {

    public Feature1071() {
        setIndex(1071);
        setDescription("absolute difference between number of ? in source and target normalized by target length");
    }

    public void run(Sentence source, Sentence target) {
        float sourceCount;
        float targetCount;
        if (!source.isSet("count_?")) {
            sourceCount = source.countChar('?');
        } else {
            sourceCount = (Integer) source.getValue("count_?");
        }

        if (!source.isSet("count_¿")) {
            sourceCount += source.countChar('¿');
        } else {
            sourceCount += (Integer) source.getValue("count_¿");
        }

        if (!target.isSet("count_?")) {
            targetCount = target.countChar('?');
        } else {
            targetCount = (Integer) target.getValue("count_?");
        }

        if (!target.isSet("count_¡")) {
            targetCount = target.countChar('¿');
        } else {
            targetCount = (Integer) target.getValue("count_¿");
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