/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * Named Entity feature: difference in number of PERSON entities in source and
 * target normalised by total number of person entities (max between english and
 * arabic)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1116 extends Feature {

    public Feature1116() {
        setIndex(1116);
        setDescription("Named Entity feature: difference in number of PERSON entities in source and target normalised by total number of person entities (max between english and arabic)");
        this.addResource("ner");
    }

    public void run(Sentence source, Sentence target) {
        float nerSource = (Integer) source.getValue("ner");
        float nerTarget = (Integer) target.getValue("ner");
        int persSource = ((ArrayList<String>) source.getValue("person")).size();
        int persTarget = ((ArrayList<String>) target.getValue("person")).size();
        if (nerSource == 0 && nerTarget == 0) {
            setValue(0);
            return;
        }
//		System.out.println("FEATURE 1116: "+persSource+" "+persTarget);
        setValue(Math.abs(persSource - persTarget) / Math.max(nerSource, nerTarget));
    }
}
