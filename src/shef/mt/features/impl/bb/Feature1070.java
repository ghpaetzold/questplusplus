package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between number of ? in source and target
 *
 * @author Catalina Hallett
 *
 */
public class Feature1070 extends Feature {

    public Feature1070() {
        setIndex(1070);
        setDescription("absolute difference between number of ? in source and target ");
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

        if (!target.isSet("count_¿")) {
            targetCount = target.countChar('¿');
        } else {
            targetCount = (Integer) target.getValue("count_¿");
        }

        setValue(Math.abs(sourceCount - targetCount));
    }
}
