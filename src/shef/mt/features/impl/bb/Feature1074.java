package shef.mt.features.impl.bb;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

/**
 * percentage of punctuation marks in source
 *
 * @author Catalina Hallett
 *
 */
public class Feature1074 extends Feature {

    public Feature1074() {
        setIndex(1074);
        setDescription("percentage of punctuation marks in source");
    }

    public void run(Sentence source, Sentence target) {
        int countS = 0;
        int countT = 0;
        if (source.isSet("count_.")) {
            countS += (Integer) source.getValue("count_.");
        } else {
            countS += source.countChar('.');
        }
        if (source.isSet("count_,")) {
            countS += (Integer) source.getValue("count_,");
        } else {
            countS += source.countChar(',');
        }
        if (source.isSet("count_؟")) {
            countS += (Integer) source.getValue("count_؟");
        } else {
            countS += source.countChar('؟');
        }
        if (source.isSet("count_¿")) {
            countS += (Integer) source.getValue("count_¿");
        } else {
            countS += source.countChar('¿');
        }
        if (source.isSet("count_،")) {
            countS += (Integer) source.getValue("count_،");
        } else {
            countS += source.countChar('،');
        }
        if (source.isSet("count_؛")) {
            countS += (Integer) source.getValue("count_؛");
        } else {
            countS += source.countChar('؛');
        }
        if (source.isSet("count_¡")) {
            countS += (Integer) source.getValue("count_¡");
        } else {
            countS += source.countChar('¡');
        }
        if (source.isSet("count_!")) {
            countS += (Integer) source.getValue("count_!");
        } else {
            countS += source.countChar('!');
        }
        if (source.isSet("count_?")) {
            countS += (Integer) source.getValue("count_?");
        } else {
            countS += source.countChar('?');
        }
        if (source.isSet("count_:")) {
            countS += (Integer) source.getValue("count_:");
        } else {
            countS += source.countChar(':');
        }
        if (source.isSet("count_;")) {
            countS += (Integer) source.getValue("count_;");
        } else {
            countS += source.countChar(';');
        }

        float noTokensSource = 1;
        if (source.isSet("noTokens")) {
            noTokensSource = source.getNoTokens();
        }
        setValue(countS / noTokensSource);

        source.setValue("noPunct", countS);


    }
}
