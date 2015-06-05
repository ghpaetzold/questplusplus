package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;

/**
 * number of mismatched quotation marks
 *
 * @author Catalina Hallett
 *
 */
public class Feature1008 extends Feature {

    public Feature1008() {
        setIndex(1008);
        setDescription("number of mismatched quotation marks");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int doubleQ = 0;
        int singleQ = 0;

        String[] result = target.getText().split("^\"\'");
        for (String crt : result) {
            if (crt.equals("\"")) {
                doubleQ++;
            } else if (crt.equals("\'")) {
                singleQ++;
            }
        }
        if ((doubleQ % 2 == 0) && (singleQ % 2 == 0)) {
            setValue(0);
        } else {
            setValue(1);
        }
    }
}
