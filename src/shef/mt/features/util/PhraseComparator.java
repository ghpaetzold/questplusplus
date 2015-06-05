package shef.mt.features.util;

import java.util.Comparator;

import shef.mt.features.impl.Feature;

public class PhraseComparator implements Comparator<Phrase> {

    @Override
    public int compare(Phrase arg0, Phrase arg1) {
        // TODO Auto-generated method stub
        if (arg0.getStart() < arg1.getStart()) {
            return -1;
        }
        if (arg0.getStart() > arg1.getStart()) {
            return 1;
        }
        if (arg0.getEnd() < arg1.getEnd()) {
            return -1;
        }
        if (arg0.getEnd() > arg1.getEnd()) {
            return 1;
        }
        return 0;
    }
}
