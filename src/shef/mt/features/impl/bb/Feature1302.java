package shef.mt.features.impl.bb;

import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * source sentence intra lingual mutual information
 *
 * @author David Langlois
 *
 */
public class Feature1302 extends Feature {

    public Feature1302() {
        setIndex(1302);
        setDescription("source sentence intra lingual mutual information");
        this.addResource("source.intra.triggers.file");
        //if I add this resource while I declare OK this resource in IntraLingualTriggers. It does not work.
        //I forget until I understand.
        //addResource("IntraLingualTriggers");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        setValue((Float) source.getValue("IntraLingualScore"));
    }
}
