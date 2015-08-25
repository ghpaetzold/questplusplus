package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 *
 *
 * perplexity of the target
 */
public class Feature1013 extends Feature {

    public Feature1013() {
        setIndex(1013);
        setDescription("perplexity of the target");
        this.addResource("target.lm");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) target.getValue("ppl"));
    }
}
