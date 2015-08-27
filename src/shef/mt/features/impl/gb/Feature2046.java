/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * averaged target word statistics: relative frequency of the word in the n-best
 * list occurring in the same position as the target word
 *
 * @author cat
 *
 */
public class Feature2046 extends Feature {

    public Feature2046() {
        setIndex("2046");
        setDescription("averaged target word statistics: relative frequency of the word in the n-best list occurring in the same position as the target word");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        String[] words = target.getTokens();
        ArrayList<Integer> positions;
        int pos = 0;
        int countCorrectPos = 0;
        for (String word : words) {
            positions = source.getSentenceTranslationModel().getPositions(word);
            if (positions != null) {
                Iterator<Integer> it = positions.iterator();
                int crtPos;
                while (it.hasNext()) {
                    crtPos = it.next().intValue();
                    if (crtPos == pos) {
                        countCorrectPos++;
                    }
                }
                pos++;
            }
        }
        float value = (float) countCorrectPos / (words.length * (Integer) source.getValue("nbestSize"));
        source.setValue("wordPos", value);
        setValue(value);
    }
}
