package shef.mt.features.impl.bb;

import shef.mt.features.util.Sentence;
import java.util.regex.Pattern;

import shef.mt.features.impl.Feature;

/**
 * number of mismatched brackets
 *
 * @author cat
 */
public class Feature1007 extends Feature {

    public Feature1007() {
        setIndex(1007);
        setDescription("number of mismatched brackets");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        int sqPlus = 0;
        int roundPlus = 0;
        int curlyPlus = 0;

        String[] result = target.getText().split("^\\[\\]\\(\\)\\{\\}");
        for (String crt : result) {
            if (crt.equals("[")) {
                sqPlus++;
            } else if (crt.equals("]")) {
                sqPlus--;
            } else if (crt.equals("(")) {
                roundPlus++;
            } else if (crt.equals(")")) {
                roundPlus--;
            } else if (crt.equals("{")) {
                curlyPlus++;
            } else if (crt.equals("}")) {
                curlyPlus--;
            }
        }
        setValue(sqPlus + roundPlus + curlyPlus);
    }
}
