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
public class Feature1117 extends Feature {

    public Feature1117() {
        setIndex(1117);
        setDescription("Named Entity feature: difference in number of LOCATION entities in source and target normalised by total number of person entities (max between english and arabic)");
        HashSet res = new HashSet();
        //requires named entities
        res.add("ner");
        setResources(res);
    }

    public void run(Sentence source, Sentence target) {
        int nerSource = (Integer) source.getValue("ner");
        int nerTarget = (Integer) target.getValue("ner");
        int locSource = ((ArrayList<String>) source.getValue("location")).size();
        int locTarget = ((ArrayList<String>) target.getValue("location")).size();
//		System.out.println("FEATURE 1117: "+locSource+" "+locTarget);
        if (nerSource == 0 && nerTarget == 0) {
            setValue(0);
            return;
        }
        setValue((float) Math.abs(locSource - locTarget) / Math.max(nerSource, nerTarget));
    }
}
