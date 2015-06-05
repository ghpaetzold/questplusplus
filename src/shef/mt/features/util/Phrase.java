/**
 *
 */
package shef.mt.features.util;

/**
 * Models a translation phrase as a span of text starting and ending at given
 * positions in the input
 *
 * @author Catalina Hallett
 *
 */
public class Phrase {

    private String text;
    private int start;
    private int end;

    public Phrase(String text, String start, String end) {
        this.text = text;
        this.start = Integer.parseInt(start);
        this.end = Integer.parseInt(end);
    }

    public Phrase(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getText() {
        return text;
    }
}
