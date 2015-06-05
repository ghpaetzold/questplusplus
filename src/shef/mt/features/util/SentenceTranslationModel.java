/**
 *
 */
package shef.mt.features.util;

import java.util.*;

/**
 * @author cat This class models the word and phrase content in a the nbest
 * translation list of a sentence
 */
public class SentenceTranslationModel {

    private HashMap<String, ArrayList<Integer>> words;	//each word is mapped to its frequency in the nbest list and a list of positions
    private HashMap<String, Integer> phrases;
    private int wordCount;
    private int nbestSize;

    public SentenceTranslationModel() {
        words = new HashMap<String, ArrayList<Integer>>();
    }

    public void addWord(String word, int pos) {
        if (words.containsKey(word)) {
            ArrayList<Integer> vals = words.get(word);
            int freq = vals.get(0);
            vals.set(0, freq + 1);
            vals.add(pos);
            words.put(word, vals);
        } else {
            ArrayList<Integer> vals = new ArrayList<Integer>();
            vals.add(0);
            vals.add(pos);
            words.put(word, vals);
        }

    }

    public int getWordFreq(String word) {
        if (!words.containsKey(word)) {
            return 0;
        }
        return words.get(word).get(0);
    }

    public int getVocabularySize() {
        return words.size();
    }

    public ArrayList<Integer> getPositions(String word) {
        return words.get(word);
    }
}
