/**
 *
 */
package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Translation;
import shef.mt.features.impl.Feature;

import java.util.*;

/**
 * n-best list density (vocabulary size / average sentence length)
 *
 * @author cat
 *
 */
public class Feature2050 extends Feature {

    public Feature2050() {
        setIndex("2050");
        setDescription("n-best list density (vocabulary size / average sentence length)");
        this.addResource("moses.xml");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int vocSize = source.getSentenceTranslationModel().getVocabularySize();
        float transLength = 0;
        float avgValue = 0;
        Iterator<Translation> it = source.getTranslations().iterator();
        while (it.hasNext()) {
            transLength += Float.parseFloat(it.next().getAttribute("noTokens"));
        }
        setValue(vocSize / transLength);
    }
}
