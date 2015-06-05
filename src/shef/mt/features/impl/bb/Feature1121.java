/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;
import shef.mt.tools.WordAlign;

/**
 * number of ORGANIZATION entities in source that are aligned to ORGANIZATION
 * entities in target
 *
 * @author Catalina Hallett
 *
 */
public class Feature1121 extends Feature {

    public Feature1121() {
        setIndex(1121);
        setDescription("number of ORGANIZATION entities in source that are aligned to ORGANIZATION entities in target");
        addResource("ner");
    }

    public void run(Sentence source, Sentence target) {
        ArrayList<String> persSource = (ArrayList<String>) source.getValue("organization");
        ArrayList<String> persTarget = (ArrayList<String>) target.getValue("organization");

        int maxValue = Math.max(persSource.size(), persTarget.size());
        if (maxValue == 0) {
            setValue(0);
            return;
        }
        Iterator<String> it = persSource.iterator();
        String sourceWord;
        String targetWord;
        int count = 0;
        while (it.hasNext()) {
            sourceWord = it.next();
            targetWord = WordAlign.getMapping(sourceWord);
            if (targetWord != null && persTarget.contains(targetWord)) {
                count++;
            }
        }


        setValue((float) count / maxValue);
    }
}
