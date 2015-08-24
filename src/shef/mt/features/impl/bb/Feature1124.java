/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * Named Entity feature: difference in number of LOCATION entities in source and
 * target normalised by total number of person entities (max between english and
 * arabic)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1124 extends Feature {

    public Feature1124() {
        setIndex(1124);
        setDescription("Named Entity feature: difference in number of LOCATION entities in source and target normalised by total number of person entities (max between english and arabic)");
        this.addResource("ner");
    }

    public void run(Sentence source, Sentence target) {
        int nerSource = (Integer) source.getValue("ner2");
        int nerTarget = (Integer) target.getValue("ner");
        int locSource = ((ArrayList<String>) source.getValue("location2")).size();
        int locTarget = ((ArrayList<String>) target.getValue("location")).size();
        if (nerSource == 0 && nerTarget == 0) {
            setValue(0);
            return;
        }
        setValue((float) Math.abs(locSource - locTarget) / Math.max(nerSource, nerTarget));
    }
}
