package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * coherence
 */
public class Feature1097 extends Feature {

    public Feature1097() {
        setIndex(1097);
        setDescription("coherence");
        addResource("coh");
    }

    public void run(Sentence source, Sentence target) {
        System.out.println(target.getValue("coherence"));
        setValue((Float) target.getValue("coherence"));
    }
}
