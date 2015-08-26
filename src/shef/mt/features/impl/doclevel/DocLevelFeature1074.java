/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.Doc;

/**
 * Percentage of punctuation marks in source document
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1074 extends DocLevelFeature {

    public DocLevelFeature1074() {
        this.setIndex(1074);
        this.setDescription("percentage of punctuation marks in source document");
    }

    public void run(Sentence source, Sentence target) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public void run(Doc source, Doc target) {
        int countS = 0;
        int countT = 0;
        float noTokensSource = 0;
        for(int i=0;i<source.getSentences().size();i++){
            if (source.getSentence(i).isSet("count_.")) {
                countS += (Integer) source.getSentence(i).getValue("count_.");
            } else {
                countS += source.getSentence(i).countChar('.');
            }
            if (source.getSentence(i).isSet("count_,")) {
                countS += (Integer) source.getSentence(i).getValue("count_,");
            } else {
                countS += source.getSentence(i).countChar(',');
            }
            if (source.getSentence(i).isSet("count_؟")) {
                countS += (Integer) source.getSentence(i).getValue("count_؟");
            } else {
                countS += source.getSentence(i).countChar('؟');
            }
            if (source.getSentence(i).isSet("count_¿")) {
                countS += (Integer) source.getSentence(i).getValue("count_¿");
            } else {
                countS += source.getSentence(i).countChar('¿');
            }
            if (source.getSentence(i).isSet("count_،")) {
                countS += (Integer) source.getSentence(i).getValue("count_،");
            } else {
                countS += source.getSentence(i).countChar('،');
            }
            if (source.getSentence(i).isSet("count_؛")) {
                countS += (Integer) source.getSentence(i).getValue("count_؛");
            } else {
                countS += source.getSentence(i).countChar('؛');
            }
            if (source.getSentence(i).isSet("count_¡")) {
                countS += (Integer) source.getSentence(i).getValue("count_¡");
            } else {
                countS += source.getSentence(i).countChar('¡');
            }
            if (source.getSentence(i).isSet("count_!")) {
                countS += (Integer) source.getSentence(i).getValue("count_!");
            } else {
                countS += source.getSentence(i).countChar('!');
            }
            if (source.getSentence(i).isSet("count_?")) {
                countS += (Integer) source.getSentence(i).getValue("count_?");
            } else {
                countS += source.getSentence(i).countChar('?');
            }
            if (source.getSentence(i).isSet("count_:")) {
                countS += (Integer) source.getSentence(i).getValue("count_:");
            } else {
                countS += source.getSentence(i).countChar(':');
            }
            if (source.getSentence(i).isSet("count_;")) {
                countS += (Integer) source.getSentence(i).getValue("count_;");
            } else {
                countS += source.getSentence(i).countChar(';');
            }

            if (source.getSentence(i).isSet("noTokens")) {
                noTokensSource += source.getSentence(i).getNoTokens();
            }
        }
        //setValue(countS / noTokensSource);
        setValue(countS);
        source.setValue("noPunct", countS);
        
    }
}