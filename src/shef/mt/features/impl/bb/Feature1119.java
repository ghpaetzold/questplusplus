/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;
import shef.mt.tools.WordAlign;

/**
 * number of PERSON entities in source that are aligned to PERSON entities in
 * target
 *
 * @author Catalina Hallett
 *
 */
public class Feature1119 extends Feature {

    public Feature1119() {
        setIndex(1119);
        addResource("ner");
        setDescription("number of PERSON entities in source that are aligned to PERSON entities in target");
    }

    public void run(Sentence source, Sentence target) {
        ArrayList<String> persSource = (ArrayList<String>) source.getValue("person");
        ArrayList<String> persTarget = (ArrayList<String>) target.getValue("person");
//		System.out.println("FEATURE 119: "+persSource.size()+" "+persTarget.size());
//		System.out.println(persSource);
//		System.out.println(persTarget);
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
//			System.out.println(sourceWord+" aligned to "+targetWord);
            if (targetWord != null && persTarget.contains(targetWord.toLowerCase())) {
                count++;
            }
        }
        System.out.println(count);


        setValue((float) count / maxValue);
    }
}
