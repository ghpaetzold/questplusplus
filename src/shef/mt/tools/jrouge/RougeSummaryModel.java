package shef.mt.tools.jrouge;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * This is a class that should be used straight, or through an adapter
 * ({@link IRougeSummaryModelAdapter}
 * <p>
 * The idea behind this class is to contain all the needed data to evaluate the
 * text using the JRouge<br>
 * implementation of Rouge, or export the data to text files and run on them the
 * original Rouge script
 *
 * @author nocgod
 */
public class RougeSummaryModel implements IRougeSummaryModel {

    private String title;
    private List<String> sentences;
    private File sourceFile;

    /**
     * Constructor for class
     *
     * @param sourceFile The source file the contains the text
     */
    public RougeSummaryModel(File sourceFile) {
        title = "";
        sentences = new Vector<String>();
        this.sourceFile = sourceFile;
    }

    /**
     * {@inheritDoc}
     */
    public File getSourceFile() {
        return sourceFile;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel setSourceFile(File newSourceFile) {
        this.sourceFile = newSourceFile;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel setTitle(String title) {
        this.title = title;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel setSentences(List<String> sentences) {
        this.sentences.clear();

        for (String s : sentences) {
            this.sentences.add(new String(s));
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel addSentence(String sentence) {
        return this.sentences.add(sentence) == false ? null : this;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel addSentenceAt(String sentence, int index) {
        this.sentences.add(index, sentence);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel removeSentence(String sentence) {
        return this.sentences.remove(sentence) == false ? null : this;
    }

    /**
     * {@inheritDoc}
     */
    public RougeSummaryModel removeSentenceAt(int index) {
        this.sentences.remove(index);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getSentenceAt(int index) {
        return this.sentences.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSentences() {
        return sentences;
    }

    /**
     * {@inheritDoc}
     */
    public String asText() {
        StringBuilder sb = new StringBuilder();

        sb.append(title);

        for (String s : sentences) {
            sb.append(s);
        }

        return sb.toString();
    }
}
