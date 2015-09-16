/**
 *
 */
package shef.mt.features.impl.bb;


import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Numerical;



/**
 * Number of stopwords between in target sentence
 *
 * @author Luong Ngoc Quang
 *
 *
 */
public class Feature5005 extends Feature {

    //final static Float probThresh = 0.10f;

    public Feature5005() {
        setIndex(5005);
        setDescription("Ratio of number of numericals in target and source sentence");
        //HashSet res = new HashSet<String>();
        //res.add("giza.path");
        //setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        //Calculate number of stopwords in source sentence
        String text1 = source.getText();
        Numerical numcalculator1 = new Numerical(text1);
        int result1 = numcalculator1.calculate();
        //Number of stopwords in the target sentence
        String text2 = target.getText();
        Numerical numcalculator2 = new Numerical(text2);
        int result2 = numcalculator2.calculate();
        float ratio ;
        if (result1==0) ratio = -1.00f;
        else
        ratio = (float) result2/result1;
        setValue(ratio);
    }
}

