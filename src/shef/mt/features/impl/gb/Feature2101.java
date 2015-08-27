package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.PronMorph;
import shef.mt.features.util.Phrase;
import shef.mt.features.impl.Feature;
import java.util.*;


/**
 * percentage of incorrectly translated direct object personal pronouns
 *
 * @author Catalina Hallett
 */
public class Feature2101 extends Feature {

    public Feature2101() {
        setIndex(2101);
        setDescription("percentage of incorrectly translated direct object personal pronouns");
        this.addResource("moses.xml");
    }

    /*
     * computes the percentage of incorrectly translated direct object personal pronouns
     */
    public void run(Sentence source, Sentence target) {
        ArrayList<PronMorph> morphs = source.getWordMorphs();
        if (morphs == null || morphs.isEmpty()) {
            setValue(0);
            //                   System.out.println("Feature1077: "+0);
            return;
        }
        TreeSet<Phrase> phrases = source.getPhrases();
        String translationText = source.getBest().getText();

        Iterator<PronMorph> it = morphs.iterator();
        PronMorph wm;
        int incorrect = 0;
        int count = 0;
        String searchSpace = "";
        String pArray;
        while (it.hasNext()) {
            wm = it.next();
            if (wm.isDOPronoun()) {
                count++;
                int pos = wm.getPosition();
                searchSpace = source.findPhrases(pos);
                ArrayList<String> prons = getDOPronouns(searchSpace);
                if (prons.isEmpty()) {
                    incorrect++;
                } else if (!findMatches(wm, prons)) {
                    incorrect++;
                }
            }
        }

        float value = 0;
        if (count != 0) {
            value = (float) incorrect / count;
        }
        //                 System.out.println("Feature 61:"+value);
        setValue(value);
    }


    /*
     * Constructs a list of personal pronouns which are potentially direct objects found in a text span
     * @param text the span of text representing the search space
     * @return an ArrayList of Strings containing all possessive pronouns found in text
     */
    public ArrayList<String> getDOPronouns(String text) {
        ArrayList<String> prons = new ArrayList<String>();
        String[] tokens = text.split(" ");
        for (String tok : tokens) {
            String tokLow = tok.toLowerCase();
            if (tokLow.equals("it") || tokLow.equals("him") || tokLow.equals("her") || tokLow.equals("me") || tokLow.equals("you") || tok.equals("them")) {
                prons.add(tokLow);
            }
        }
        return prons;
    }

    /*
     * Checks whether a PronMorph matches with any pronoun in a list of pronouns in gender, number and person
     * The match is performed using @see Class:PronMorph.match(String pron) function
     * @return true if there was a match, false otherwise
     */
    public boolean findMatches(PronMorph pm, ArrayList<String> prons) {
        for (String pron : prons) {
            if (pm.matches(pron)) {
                return true;
            }
        }
        return false;
    }
}
