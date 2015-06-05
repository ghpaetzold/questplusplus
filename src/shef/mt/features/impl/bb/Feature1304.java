package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * source-target sentence inter lingual mutual information
 *
 * @author David Langlois
 *
 */
public class Feature1304 extends Feature {

    public Feature1304() {
        setIndex(1304);
        setDescription("source-target sentence inter lingual mutual information");
        //if I add this resource while I declare OK this resource in IntraLingualTriggers. It does not work.
        //I forget until I understand.
        //addResource("IntraLingualTriggers");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Float)target.getValue("InterLingualSourceTargetScore"));
    }
}
