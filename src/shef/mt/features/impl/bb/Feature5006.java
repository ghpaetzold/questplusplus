/**
 *
 */
package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Giza;
import shef.mt.tools.Giza2;
import shef.mt.features.wce.CountWordOccurences;



/**
 * Number of stopwords between in target sentence
 *
 * @author Luong Ngoc Quang
 *
 *
 */
public class Feature5006 extends Feature {

    //final static Float probThresh = 0.10f;

    public Feature5006() {
        setIndex(5006);
        setDescription("Average number of occurrences of all words in the target sentence");
        //HashSet res = new HashSet<String>();
        //res.add("Giza");
        //setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        //Calculate number of stopwords in source sentence
        String text = target.getText();
        CountWordOccurences wo = new CountWordOccurences(text);
        setValue(wo.calculate());
    }
}

