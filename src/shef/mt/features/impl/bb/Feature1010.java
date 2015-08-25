package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * source sentence perplexity
 *
 * @author Catalina Hallett
 *
 */
public class Feature1010 extends Feature {

    public Feature1010() {
        setIndex(1010);
        setDescription("source sentence perplexity");
        this.addResource("source.lm");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) source.getValue("ppl"));
    }
}
