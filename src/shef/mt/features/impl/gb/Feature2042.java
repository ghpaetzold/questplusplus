package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Phrase;
import shef.mt.features.impl.Feature;
import java.util.*;

/**
 * maximum size of the bi-phrases for the target sentence, in terms of number of
 * words
 *
 * @author Catalina Hallett
 *
 */
public class Feature2042 extends Feature {

    public Feature2042() {
        setIndex("2042");
        setDescription("maximum size of the bi-phrases for the target sentence, in terms of number of words");
        this.addResource("moses.xml");
    }

    public void run(Sentence source, Sentence target) {
        TreeSet<Phrase> phrases = source.getPhrases();
        Iterator<Phrase> itPhrase = phrases.iterator();
        Phrase crtPhrase;
        int max = 0;
        while (itPhrase.hasNext()) {
            crtPhrase = itPhrase.next();
            String[] tokens = crtPhrase.getText().split(" ");
            if (max < tokens.length) {
                max = tokens.length;
            }
        }
        setValue(max);
    }
}
