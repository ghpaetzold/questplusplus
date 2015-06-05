package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;

import java.util.HashMap;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * Word length ratios.
 * 
 * @author Ergun Bicici
 * 
 */
public class Feature1702 extends Feature {

    public Feature1702() {
        setIndex(1702);
        setDescription("Word length ratios");
    }
    
    public float AverageWordLength(Sentence sent) {
    	/**
    	 * Average length of the words. 
        **/
        String [] tokens = sent.getTokens();
        double sumwordlength = 0.0;
        for (String word: tokens) {
            sumwordlength += word.length();
        }
        return (float) sumwordlength / tokens.length;
    }
     
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
    	source.setValue("avgWordLengthSource", AverageWordLength(source));
    	float avgsourcelength = (float) source.getValue("avgWordLengthSource");
    	setValue((Float) avgsourcelength);
    	target.setValue("avgWordLengthTarget", AverageWordLength(target));
    	float avgtargetlength = (float) target.getValue("avgWordLengthTarget");
    	setValue((Float) avgtargetlength);
    	source.setValue("avgWordLengthRatio", avgsourcelength / avgtargetlength);
    	setValue((Float) source.getValue("avgWordLengthRatio"));
    }
}
