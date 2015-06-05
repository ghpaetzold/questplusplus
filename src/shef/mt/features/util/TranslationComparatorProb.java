/**
 *
 */
package shef.mt.features.util;

import java.util.Comparator;
import shef.mt.features.util.Translation;

/**
 * @author cat
 *
 */
@SuppressWarnings("hiding")
public class TranslationComparatorProb implements Comparator<Translation> {

    public int compare(Translation t1, Translation t2) {
        if (t1.getRank() <= t2.getRank()) {
            return -1;
        }
        return 1;
    }
}
