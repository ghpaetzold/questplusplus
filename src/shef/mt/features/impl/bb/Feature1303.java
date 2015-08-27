package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * target sentence intra lingual mutual information
 *
 * @author David Langlois
 *
 */
public class Feature1303 extends Feature {

    public Feature1303() {
        setIndex(1303);
        setDescription("target sentence intra lingual mutual information");
        this.addResource("target.intra.triggers.file");
        //if I add this resource while I declare OK this resource in IntraLingualTriggers. It does not work.
        //I forget until I understand.
        //addResource("IntraLingualTriggers");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Float) target.getValue("IntraLingualScore"));
    }
}
