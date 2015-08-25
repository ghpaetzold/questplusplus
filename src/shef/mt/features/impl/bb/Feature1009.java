package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * source sentence log probability
 *
 * @author Catalina Hallett
 *
 */
public class Feature1009 extends Feature {

    public Feature1009() {
        setIndex(1009);
        setDescription("source sentence log probability");
        this.addResource("source.lm");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        setValue((Float) source.getValue("logprob"));
    }
}
