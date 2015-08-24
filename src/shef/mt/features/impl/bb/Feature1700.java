package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.HashSet;

import shef.mt.features.impl.Feature;

/**
 * Information Retrieval scores and BLEU and F1 similarities over top retrieved instances.
 * 
 * @author Ergun Bicici
 * 
 */
public class Feature1700 extends Feature {

    public Feature1700() {
        setIndex(1700);
        setDescription("Information retrieval scores and BLEU and F1 similarity over top retrieved instances.");
        this.addResource("LuceneIndexSource");
        this.addResource("LuceneIndexTarget");
    }
    
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
    	String key = "";
    	for (int i=0; i < 5; i++) {
    		key = "IRscoreSource" + String.valueOf(i);
    		if (source.isSet(key))
    			setValue((Float) source.getValue(key));
    		else
    			setValue(0);
    		key = "BLEUscoreSource" + String.valueOf(i);
    		if (source.isSet(key))
    			setValue((Float) source.getValue(key));
    		else
    			setValue(0);
    		key = "F1scoreSource" + String.valueOf(i);
    		if (source.isSet(key))
    			setValue((Float) source.getValue(key));
    		else
    			setValue(0);
    		key = "IRscoreTarget" + String.valueOf(i);
    		if (target.isSet(key))
    			setValue((Float) target.getValue(key));
    		else
    			setValue(0);
    		key = "BLEUscoreTarget" + String.valueOf(i);
    		if (target.isSet(key))
    			setValue((Float) target.getValue(key));
    		else
    			setValue(0);
    		key = "F1scoreTarget" + String.valueOf(i);
    		if (target.isSet(key))
    			setValue((Float) target.getValue(key));
    		else
    			setValue(0);
    	}
    }
}
