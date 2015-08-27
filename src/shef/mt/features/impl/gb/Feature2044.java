/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * relative frequency in the nbest list
 *
 * @author cat
 *
 */
public class Feature2044 extends Feature {

    public Feature2044() {
        setIndex("2044");
        setDescription("relative frequency in the nbest list");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        Translation best = source.getBest();
        String[] words = best.getText().split(" ");
        int wordCount = 0;
        int freq;
        for (String word : words) {
            freq = source.getNbestWordFreq(word);
            //		System.out.println(word+" "+freq);
            wordCount += freq;
        }
        float relFreq = wordCount / Float.valueOf(source.getTranslationAttribute("noTokens"));
        //should this be normalised by the size of the nbest list?
        float result = relFreq / (Integer) source.getValue("nbestSize");
        //	System.out.println(result);
        setValue(result);
        source.setValue("nbestRelWordFreq", result);
    }
}
