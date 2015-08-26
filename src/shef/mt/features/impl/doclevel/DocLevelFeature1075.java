/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;

/**
 * Percentage of punctuation marks in target document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1075 extends DocLevelFeature {

    public DocLevelFeature1075() {
        this.setIndex(1075);
        this.setDescription("percentage of punctuation marks in target document");
    }

    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(Doc source, Doc target) {
        int countT = 0;
        float noTokensTarget = 0;
        for(int i=0;i<target.getSentences().size();i++){
            if (target.getSentence(i).isSet("count_.")) {
                countT += (Integer) target.getSentence(i).getValue("count_.");
            } else {
                countT += target.getSentence(i).countChar('.');
            }
            if (target.getSentence(i).isSet("count_,")) {
                countT += (Integer) target.getSentence(i).getValue("count_,");
            } else {
                countT += target.getSentence(i).countChar(',');
            }
            if (target.getSentence(i).isSet("count_؟")) {
                countT += (Integer) target.getSentence(i).getValue("count_؟");
            } else {
                countT += target.getSentence(i).countChar('؟');
            }
            if (target.getSentence(i).isSet("count_¿")) {
                countT += (Integer) target.getSentence(i).getValue("count_¿");
            } else {
                countT += target.getSentence(i).countChar('¿');
            }
            if (target.getSentence(i).isSet("count_،")) {
                countT += (Integer) target.getSentence(i).getValue("count_،");
            } else {
                countT += target.getSentence(i).countChar('،');
            }
            if (target.getSentence(i).isSet("count_؛")) {
                countT += (Integer) target.getSentence(i).getValue("count_؛");
            } else {
                countT += target.getSentence(i).countChar('؛');
            }
            if (target.getSentence(i).isSet("count_¡")) {
                countT += (Integer) target.getSentence(i).getValue("count_¡");
            } else {
                countT += target.getSentence(i).countChar('¡');
            }
            if (target.getSentence(i).isSet("count_!")) {
                countT += (Integer) target.getSentence(i).getValue("count_!");
            } else {
                countT += target.getSentence(i).countChar('!');
            }
            if (target.getSentence(i).isSet("count_?")) {
                countT += (Integer) target.getSentence(i).getValue("count_?");
            } else {
                countT += target.getSentence(i).countChar('?');
            }
            if (target.getSentence(i).isSet("count_:")) {
                countT += (Integer) target.getSentence(i).getValue("count_:");
            } else {
                countT += target.getSentence(i).countChar(':');
            }
            if (target.getSentence(i).isSet("count_;")) {
                countT += (Integer) target.getSentence(i).getValue("count_;");
            } else {
                countT += target.getSentence(i).countChar(';');
            }


            
            if (target.getSentence(i).isSet("noTokens")) {
                noTokensTarget += target.getSentence(i).getNoTokens();
            }
        }

        target.setValue("noPunct", countT);
        setValue(countT);
        //setValue(countT / noTokensTarget);

        
    }
}
