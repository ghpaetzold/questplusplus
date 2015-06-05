/**
 *
 */
package shef.mt.features.util;

import java.util.*;

/**
 * This class encapsulates information about translations in an n-best list
 *
 * @author cat
 *
 */
public class Translation {

    String text;
    int rank;
    float prob;
    private HashMap<String, String> attributes;

    public Translation() {
        attributes = new HashMap<String, String>();
    }

    public float getProb() {
        return prob;
    }

    public void setProb(float prob) {
        this.prob = prob;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public String getText() {
        return text;
    }

    public void countTokens() {
        String[] split = text.split(" ");
        setAttribute("noTokens", split.length + "");
    }
}
