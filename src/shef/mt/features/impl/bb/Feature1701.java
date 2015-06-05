package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;

import java.util.HashMap;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * LIX readability scores.
 *
 * @author Ergun Bicici
 *
 */
public class Feature1701 extends Feature {

    public Feature1701() {
        setIndex(1701);
        setDescription("LIX readability score");
    }
    
    public float LIXScore(Sentence sent) {
    	/**
        LIX indicates the difficulty of reading a text. The larger the more difficult.
        LIX = A / B + (C x 100) / A
        A = number of words
        B = number of periods (defined by period, colon or capital first letter)
        C = Number of long words (more than 6 letters)
        **/
    	HashMap<String, Integer> periods = new HashMap<String, Integer>();
    	periods.put(".",1);
    	periods.put(":", 1);
    	periods.put("!", 1);
    	periods.put("?", 1);
        String [] tokens = sent.getTokens();
        double A = (double) tokens.length;
        double C = 0.0;
        double B = 0.0;
        for (String word: tokens) {
            if (word.length() > 6)
                C += 1.0;
            if (periods.containsKey(word.charAt(0)) || periods.containsKey(word.charAt(word.length()-1)))
                B += 1.0;
        }
        if (B == 0)
            B = 1.0;
        double score = 0.0;
        if (A > 0)
            score = A / B + C * 100.0 / A;
        else
            score = 0.0;
        return (float) score;
    }
     
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
    	source.setValue("LIXscoreSource", LIXScore(source));
    	setValue((Float) source.getValue("LIXscoreSource"));
    	target.setValue("LIXscoreTarget", LIXScore(target));
    	setValue((Float) target.getValue("LIXscoreTarget"));
    }
}
