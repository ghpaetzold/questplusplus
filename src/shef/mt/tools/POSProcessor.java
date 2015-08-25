package shef.mt.tools;

import shef.mt.features.util.Sentence;
import java.io.*;


/**
 * The POSProcessor class analyses a file produced by a pos tagger and sets
 * certain values to a Sentence object These values are, currently, the number
 * of nouns, verbs, pronouns and content words
 *
 */
public class POSProcessor {

    BufferedReader br;
    int sentCount;
//    static String XPOS=".XPOS";
//    BufferedWriter bwXPos;

    /**
     * initialise a POSProcessor from an input file The POSProcessor expect an
     * input file in a fixed format, where each line is of the type:<br> <i>word
     * DT	word</i> (tokens separated by tab) <br>
     *
     *
     * @param input the input file
     *
     */
    public POSProcessor(String input) {
        try {
            System.out.println("INPUT TO POSPROCESSOR:" + input);
            br = new BufferedReader(new FileReader(input));
//			bwXPos = new BufferedWriter(new FileWriter(input+getXPOS()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sentCount = 0;
    }

    /**
     * Reads the pos tags associated to a sentence and counts the number of
     * content words The count for each type of content word is addedd as a
     * value to the sentence
     *
     * @see Sentence.setValue()
     * @param sent the sentence to be analysed
     */
    public void processSentence(Sentence sent) throws Exception {
        int tokCount = sent.getNoTokens();
        String line = br.readLine();
        int contentWords = 0;
        int nounWords = 0;
        int verbWords = 0;
        int pronWords = 0;
        int otherContentWords = 0;
        int count = 0;

        while (line != null && (count < tokCount)) {
            if (!line.trim().isEmpty()) {
                String[] split = line.split("\t");
                String tag = split[1];
                if (tag.contains("SENT")) {
                    tag = tag.split(" ")[0];
                } else if (PosTagger.isNoun(tag)) {
                    nounWords++;
//					System.out.println("is noun");
                } else if (PosTagger.isPronoun(tag)) {
                    pronWords++;
                } else if (PosTagger.isVerb(tag)) {
                    verbWords++;
                    //					System.out.println("is verb");
                } else if (PosTagger.isAdditional(tag)) {
                    otherContentWords++;
                }
                //    	  	bwXPos.write(tag);
                count++;
            }
            line = br.readLine();
        }
        //   bwXPos.newLine();
        contentWords = nounWords + verbWords + otherContentWords;
        sent.setValue("contentWords", contentWords);
        sent.setValue("nouns", nounWords);
        sent.setValue("verbs", verbWords);
        sent.setValue("prons", pronWords);
    }
}
