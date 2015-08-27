/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;

import java.util.*;

/**
 * percentage of other hypothesis with and edit distance to the centre <
 * (current hypothesis edit distance to the centre * 0.35)
 *
 * @author Catalina Hallett
 *
 */
public class Feature2054 extends Feature {

    public Feature2054() {
        setIndex("2054");
        setDescription("percentage of other hypothesis with and edit distance to the centre < (current hypothesis edit distance to the centre * 0.35)");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub

        TreeSet<Translation> nbest = source.getTranslations();
        Translation best = source.getBest();
        int size = nbest.size();
        if (size <= 1) {
            setValue(0);
            return;
        }
        Translation center = source.getCenter();

        Iterator<Translation> it = nbest.iterator();
        it.next();
        Translation crtTrans;
        int crtEditDist;
        double value = Integer.parseInt(best.getAttribute("editDist")) * 0.35;
        int count = 0;
        while (it.hasNext()) {
            crtTrans = it.next();
            crtEditDist = Integer.parseInt(crtTrans.getAttribute("editDist"));
            if (crtEditDist < value) {
                count++;
            }
        }
        setValue((float) count / size);


    }
}