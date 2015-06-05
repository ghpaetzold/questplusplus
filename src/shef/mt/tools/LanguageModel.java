package shef.mt.tools;

import java.util.*;

/**
 * A LanguageModel stores information about the ngram content of language model
 * file It provides access to information such as the frequency of ngrams, and
 * the cut-off points for various ngram frequencies
 *
 * @author cat
 */
public class LanguageModel {

    private int sliceNo = 4;
    private HashMap<String, Integer>[] ngrams;
//	private ArrayList freqs;	//for storing frequencies in a sortable container
    private int[][] cutOffs;	//for storing cut-off frequencies

    /**
     * @return the ngrams
     */
    public HashMap<String, Integer>[] getNgrams() {
        return ngrams;
    }

    /**
     * @param aNgrams the ngrams to set
     */
    public void setNgrams(HashMap<String, Integer>[] aNgrams) {
        ngrams = aNgrams;
    }

    public LanguageModel(int nSize) {
        ngrams = new HashMap[sliceNo];
        for (int i = 0; i < sliceNo; i++) {
            ngrams[i] = new HashMap<String, Integer>();
        }
        //	freqs = new ArrayList();
        cutOffs = new int[nSize][sliceNo];
    }

    public void setCutOffs(int[][] cutoffs) {
        this.cutOffs = cutoffs;
    }

    public void addNGram(String ngram, int freq, int size) {
        getNgrams()[size - 1].put(ngram, new Integer(freq));
        //freqs.add(freq);
    }

    public void addNGram(String ngram, int freq) {
        String[] ngramSplit = ngram.split(" ");
        int size = ngramSplit.length;
        getNgrams()[size - 1].put(ngram, new Integer(freq));
        //freqs.add(freq);
    }

    public int getCutOff(int ngramSize, int pos) {
        return cutOffs[ngramSize - 1][pos - 1];
    }

    public Integer getFreq(String word) {
        String[] split = word.split(" ");
        return getNgrams()[split.length - 1].get(word);
    }

    public Integer getFreq(String ngram, int size) {
        if (getNgrams()[size - 1].get(ngram) == null) {
            return 0;
        }
        return getNgrams()[size - 1].get(ngram);
    }
}
