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
public class Feature1122 extends Feature {

    public Feature1122() {
        setIndex(1122);
        setDescription("Named Entity feature: difference in number of PERSON entities in source and target normalised by total number of person entities (max between english and arabic)");
        this.addResource("ner");
    }

    public void run(Sentence source, Sentence target) {
        float nerSource = (Integer) source.getValue("ner2");
        float nerTarget = (Integer) target.getValue("ner");
        int persSource = ((ArrayList<String>) source.getValue("person2")).size();
        int persTarget = ((ArrayList<String>) source.getValue("person")).size();
        if (nerSource == 0 && nerTarget == 0) {
            setValue(0);
            return;
        }
        setValue(Math.abs(persSource - persTarget) / Math.max(nerSource, nerTarget));
    }
}
