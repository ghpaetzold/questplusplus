/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Named Entity feature: difference in number of ORGANIZATION entities in source
 * and target normalised by total number of person entities (max between english
 * and arabic)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1123 extends Feature {

    public Feature1123() {
        setIndex(1123);
        setDescription("Named Entity feature: difference in number of ORGANIZATION entities in source and target normalised by total number of person entities (max between english and arabic)");
        HashSet res = new HashSet();
        //requires named entities
        res.add("ner");
        setResources(res);
    }

    public void run(Sentence source, Sentence target) {
        int nerSource = (Integer) source.getValue("ner2");
        int nerTarget = (Integer) target.getValue("ner");
        int orgSource = ((ArrayList<String>) source.getValue("organization2")).size();
        int orgTarget = ((ArrayList<String>) target.getValue("organization")).size();
        if (nerSource == 0 && nerTarget == 0) {
            setValue(0);
            return;
        }
        setValue((float) Math.abs(orgSource - orgTarget) / Math.max(nerSource, nerTarget));
    }
}
