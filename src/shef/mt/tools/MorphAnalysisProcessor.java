package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.PronMorph;
import java.io.*;
import java.util.*;

/**
 * This is a processor for the output of the MADA morphological analyser for
 * Arabic
 *
 * @author Catalina Hallett
 *
 */
public class MorphAnalysisProcessor extends ResourceProcessor {

    BufferedReader brInput;
    private static String SENT_START = ";;; SENTENCE ";
    private static String SENT_END = "SENTENCE";
    private static String WORD_START = ";;WORD";
    int count = 0;

    public MorphAnalysisProcessor(String inputFile) {
        try {
            System.out.println("input to map: " + inputFile);
            brInput = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processNextSentence(Sentence sent) {
        try {
            int position = -1;
            count = 0;
            String line = brInput.readLine();
            while (line != null && !line.startsWith(SENT_START)) {
                line = brInput.readLine();
            }
//            System.out.println("new sentence:"+line);
            //we've found a new sentence; now let's read the next lines to get the words
            while (line != null && !line.startsWith(SENT_END)) {
                while (line != null && !line.startsWith(WORD_START) && !line.startsWith(SENT_END)) {
                    line = brInput.readLine();
                }


                //this is the line ;;WORD word
                //we get the word from it
                if (line == null) {
                    //                  System.out.println("reached eof");
                    return;

                }
                if (line.startsWith(SENT_END)) {
                    continue;
                }

                position++;
                String[] words = line.split(" ");
                String text = words[1];
                if (line != null) {
                    line = brInput.readLine();	//line containing ;;MADA ....
                    line = brInput.readLine();	//line containing the best word analysis
                    //this is the line we need
                    //we check first if it contains a pronoun, and if so, we process it further
                    if (line.contains("PRON") || line.contains("SUFF_DO")) {
                        //				System.out.println("found pron on line: " + line);
                        String[] features = line.split(" ");
                        //features are: probability diac lex bw gloss pos prc3 prc2 prc1 prc0 per asp vox mod gen num stt cas enc0 rat source stem stemcat
                        //we need bw to extract the pronun
                        String bw = features[3];
                        String pos = features[5].split(":")[1];
                        if (pos.equals("noun") || pos.equals("verb")) {

                            String[] comps = bw.split("/");
                            //bw:+bay~in/NOUN++hum/POSS_PRON_3MP
                            //bw:na/IV1P+fosu/IV+(null)/IVSUFF_MOOD:J+hu/IVSUFF_DO:3MS
                            PronMorph wordMorph;
                            //			System.out.println(bw);
                            for (String comp : comps) {
                                if (comp.contains("SUFF_DO")) {
                                    wordMorph = new PronMorph(text);
                                    wordMorph.setCompoundPos(pos);
                                    int index = comp.indexOf(":");
                                    int endPron = index + 1;
                                    wordMorph.setPos("DO");
                                    wordMorph.setPers(comp.substring(endPron, endPron + 1));
                                    if (comp.length() - endPron >= 3) {
                                        wordMorph.setGen(comp.substring(endPron + 1, endPron + 2));
                                        wordMorph.setNum(comp.substring(endPron + 2, comp.length()));
                                    } else {
                                        wordMorph.setNum(comp.substring(endPron + 1, comp.length()));
                                        //	if (pos.equals("verb"))
                                        //	System.out.println(count);

                                    }
                                    wordMorph.setPosition(position);
                                    sent.addWordMorph(wordMorph);
                                    count++;

                                } else if (comp.contains("PRON_") || comp.contains("POSS_PRON")) {
//						System.out.println(comp);
                                    wordMorph = new PronMorph(text);
                                    wordMorph.setCompoundPos(pos);
                                    int index = comp.indexOf("PRON");
                                    int endPron = index + 4;
                                    wordMorph.setPos(comp.substring(0, endPron));
                                    wordMorph.setPers(comp.substring(endPron + 1, endPron + 2));
                                    if (comp.length() - endPron >= 4) {
                                        wordMorph.setGen(comp.substring(endPron + 2, endPron + 3));
                                        wordMorph.setNum(comp.substring(endPron + 3, comp.length()));
                                    } else {
                                        wordMorph.setNum(comp.substring(endPron + 2, comp.length()));
                                        //	if (pos.equals("verb"))
                                        //	System.out.println(count);

                                    }
                                    //    wordMorph.print();

                                    count++;
                                    wordMorph.setPosition(position);
                                    sent.addWordMorph(wordMorph);
                                }
                            }
                        }
                    }
                    line = brInput.readLine();
                }  //we've found an end of sentence marker or the end of file has been reached
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isVerb(String line) {
        return true;
    }

    public static void main(String[] args) {
        MorphAnalysisProcessor map = new MorphAnalysisProcessor(args[0]);
        int i = 0;
        while (i <= 812) {
            Sentence s = new Sentence("text", i);
            map.processNextSentence(s);
            i++;
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
