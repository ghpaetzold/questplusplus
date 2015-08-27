package shef.mt.features.impl.gb;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.PronMorph;
import shef.mt.features.util.Phrase;
import shef.mt.features.impl.Feature;
import java.util.*;


/**
 * percentage of incorrectly translated possessive pronouns
 */
public class Feature2100 extends Feature {

    public Feature2100() {
        setIndex(2100);
        setDescription("percentage of incorrectly translated possessive pronouns");
        this.addResource("moses.xml");
    }

    /*
     * computes the percentage of incorrectly translated possessive pronouns
     */
    public void run(Sentence source, Sentence target) {
//     System.out.println("Feature 60 on sentence "+source.getIndex());
        ArrayList<PronMorph> morphs = source.getWordMorphs();
        if (morphs == null || morphs.isEmpty()) {
            setValue(0);
//                    System.out.println("Feature1076: "+0);
            return;
        }
        //             System.out.println(morphs.size()+" pronouns in sentence "+source.getIndex());

        TreeSet<Phrase> phrases = source.getPhrases();
        String translationText = source.getBest().getText();


        Iterator<PronMorph> it = morphs.iterator();
        PronMorph wm;
        int incorrect = 0;
        String searchSpace = "";
        String pArray;
        int count = 0;
        while (it.hasNext()) {
            wm = it.next();
            if (wm.isPossessive()) {
                count++;
                //                  wm.print();
                int pos = wm.getPosition();
                searchSpace = source.findPhrases(pos);
                ArrayList<String> prons = getPossessivePronouns(searchSpace);
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
        //                   System.out.println("Feature 60: wrong:"+incorrect+" total:"+count+" value="+value);
        setValue(value);
    }


    /*
     * Constructs a list of possessive pronouns found in a text span
     * @param text the span of text representing the search space
     * @return an ArrayList of Strings containing all possessive pronouns found in text
     */
    public ArrayList<String> getPossessivePronouns(String text) {

        ArrayList<String> prons = new ArrayList<String>();
        String[] tokens = text.split(" ");
        for (String tok : tokens) {
            String tokLow = tok.toLowerCase();
            if (tokLow.equals("its") || tokLow.equals("his") || tokLow.equals("her") || tokLow.equals("my") || tokLow.equals("your") || tok.equals("their")) {
                prons.add(tokLow);
            }
        }
        //                      System.out.println(text+" contains "+prons.size()+" possessives");
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
