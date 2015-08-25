package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 *
 *
 * perplexity of the target sentence without end of sentence marker
 */
public class Feature1014 extends Feature {

    public Feature1014() {
        setIndex(1014);
        setDescription("perplexity of the target sentence without end of sentence marker");
        this.addResource("target.lm");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) target.getValue("ppl1"));
    }
}
