package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * log probability of the target
 *
 *
 */
public class Feature1012 extends Feature {

    public Feature1012() {
        setIndex(1012);
        setDescription("log probability of the target");
        this.addResource("target.lm");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) target.getValue("logprob"));
    }
}
