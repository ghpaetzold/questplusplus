/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * fertility of the words in the source sentence compared to the n-best list in
 * terms of words (vocabulary size / source sentence length)
 *
 * @author cat
 *
 */
public class Feature2051 extends Feature {

    public Feature2051() {
        setIndex("2051");
        setDescription("fertility of the words in the source sentence compared to the n-best list in terms of words (vocabulary size / source sentence length)");
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
        List list = Arrays.asList(words);

        HashSet uniqueWords = new HashSet(list);
        int vocSize = uniqueWords.size();
        int bestLen = Integer.parseInt(best.getAttribute("noTokens"));
        setValue((float) vocSize / bestLen);

    }
}
