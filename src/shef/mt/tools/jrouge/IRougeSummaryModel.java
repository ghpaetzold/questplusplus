package shef.mt.tools.jrouge;

import java.io.File;
import java.util.List;

public interface IRougeSummaryModel {

    /**
     * getter for sourceFile
     *
     * @return The source file
     */
    File getSourceFile();

    /**
     * Setter for the sourceFile
     *
     * @param newSourceFile The source file to set
     * @return this object, so the calls could be chained
     */
    RougeSummaryModel setSourceFile(File newSourceFile);

    /**
     * Setter for the Title.
     *
     * @param title The title to set
     * @return this object, so the calls could be chained
     */
    RougeSummaryModel setTitle(String title);

    /**
     * Getter for title field
     *
     * @return The title
     */
    String getTitle();

    /**
     * Setter for the sentences field
     *
     * @param sentences The sentences to set
     * @return this object, so the calls could be chained
     */
    RougeSummaryModel setSentences(List<String> sentences);

    /**
     * Method used to add sentence to the sentence list
     *
     * @param sentence The sentence to add
     * @return this object, so the calls could be chained
     */
    RougeSummaryModel addSentence(String sentence);

    /**
     * Adds a sentence at a specific index in the array
     *
     * @param sentence The sentence to add
     * @param index The index at which to insert the sentence
     * @return this object, so the calls could be chained
     */
    RougeSummaryModel addSentenceAt(String sentence, int index);

    /**
     * Method used to remove a sentence from the sentence list
     *
     * @param sentence The sentence to remove
     * @return this object, so the calls could be chained, null in case failed
     * to remove item
     */
    RougeSummaryModel removeSentence(String sentence);

    /**
     * Method used to remove a sentence at a specific index in the list
     *
     * @param index The index from which to delete
     * @return this object, so the calls could be chained, null in case failed
     * to remove item
     */
    RougeSummaryModel removeSentenceAt(int index);

    /**
     * Method used to retrieve sentences at a specific index
     *
     * @param index The index from which to retrieve the sentence
     * @return The sentence
     */
    String getSentenceAt(int index);

    /**
     * Getter for the sentence field.
     *
     * @return The sentence field
     */
    List<String> getSentences();

    /**
     * Method couples all sentences into a text.
     *
     * @return The sentences as one string (text)
     */
    String asText();
}
