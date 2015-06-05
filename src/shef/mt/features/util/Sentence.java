package shef.mt.features.util;

import shef.mt.util.Pair;
import java.util.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.process.*;

/**
 * Models a sentence as a span of text containing multiple information added by
 * pre-processing tools <br> It provides direct access to well used features of
 * a sentence, such as its text, tokens, ngrams, phrases, but also allows any
 * tool to add information related to the sentence via the setValue() method.
 *
 * @author Catalina Hallett
 */
public class Sentence {

    private int noWords = -1;
    private String sentence;
    private ArrayList<Pair> tdl;
    private HashMap<String, Object> values;
    private ArrayList<String>[] ngrams;
    private String[] tokens;
    
    private String[] tags;
    
    private int index;
    private TreeSet<Phrase> phrases;
    private TreeSet<Translation> translations;
    private SentenceTranslationModel sentenceTranslationModel;
    private Translation center;
    private ArrayList<PronMorph> morphs;

    public Sentence(String s, int index) {
        sentence = s;
        this.index = index;
        center = null;
        values = new HashMap<String, Object>();
        tokens = sentence.trim().split(" ");
    }
    
    public Sentence(String s, String t, int index) {
        this(s, index);
        tags = t.trim().split(" ");
    }

    public String getText() {
        return sentence;
    }

    public int getIndex() {
        return index;
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    /**
     *
     * @return the grammatical structure of the sentence as set by running the
     * Stanford Lexicalised Parser over the sentence
     */
    public ArrayList<Pair> getDependencies() {
        return tdl;
    }

    public void setDependencies(ArrayList<Pair> tdl) {
        this.tdl = tdl;
    }

    public void addDependency(String key, String value) {
        if (tdl == null) {
            tdl = new ArrayList<Pair>();
        }
        tdl.add(new Pair(key, value));
    }

    public void addDependency(Pair pair) {
        if (tdl == null) {
            tdl = new ArrayList<Pair>();
        }
        tdl.add(pair);
    }

    public void setValue(String key, float value) {
        values.put(key, new Float(value));
    }

    public void setValue(String key, int value) {
        values.put(key, new Integer(value));
    }

    public Object getValue(String key) {
        Object value = values.get(key);
        if (value == null) {
            return Float.NaN;
        }

        return values.get(key);
    }

    public boolean isSet(String key) {
        return (values.get(key) != null);
    }

    /**
     * Returns the token at index index. Indeces start at 0
     *
     * @param index the index of the required token
     * @return the index-th token
     */
    public String getToken(int index) {
        String result = "";
        try {
            result = getTokens()[index];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            for (String tok : getTokens()) {
                System.out.print(tok + " , ");
            }
            System.out.println(getTokens().length + " \t " + index + " \t " + getText());
        }
        return result;
    }

    public int countChar(char c) {
        String charS = Character.toString(c);
        if (c == '?') //we need to escape this to be able to use it in a regular expression
        {
            charS = "\\" + charS;
        }

        String[] splitResult = sentence.split(charS);
        int size = 0;
        if (!sentence.contains(charS)) {
            size = 0;
        } else {
            size = splitResult.length;
        }
        setValue("count_" + c, size);
        return size;
    }

    public void computeNGrams(int ngSize) {
        ngrams = new ArrayList[ngSize];
        String[] tokens = sentence.split(" ");

        for (int size = 0; size < ngSize; ++size) {
            ngrams[size] = new ArrayList<String>();
        }

        String ngram = "";
        String token = "";
        for (int i = 0; i < tokens.length; ++i) {
            token = tokens[i];
            ngram = token;
            ngrams[0].add(token);
            for (int size = 1; size < ngSize; ++size) {
                if (i + size < tokens.length) {
                    ngram += " " + tokens[i + size];
                    ngrams[size].add(ngram);
                }
            }
        }
    }

    public ArrayList<String> getNGrams(int size) {
        return ngrams[size - 1];
    }

    public String[] getTokens() {
        return tokens;
    }

    public String[] getTags() {
        return tags;
    }
    
    public int getNoTokens() {
        return tokens.length;
    }

    public void addTranslation(Translation t) {
        if (translations == null) {
            translations = new TreeSet<Translation>(new TranslationComparator());
        }
        translations.add(t);
    }

    public Translation getBest() {
        if (translations == null) {
            System.out.println("no best in " + getIndex() + " " + getText());
            return null;
        }

        return translations.first();
    }

    public Translation getCenter() {
        if (center == null) {
            findCenter();
        }
        return center;
    }

    public void findCenter() {
        Iterator<Translation> it = translations.iterator();
        int size = translations.size();
        int i = 0;
        while (i < size / 2) {
            center = it.next();
            i++;
        }

    }

    public void addPhrase(Phrase p) {
        if (phrases == null) {
            phrases = new TreeSet<Phrase>(new PhraseComparator());
        }
        phrases.add(p);
    }

    public TreeSet<Phrase> getPhrases() {
        return phrases;
    }

    public void buildSentenceModel() {
        sentenceTranslationModel = new SentenceTranslationModel();
        Iterator<Translation> it = translations.iterator();
        Translation crtTrans;
        String crtText;
        while (it.hasNext()) {
            crtTrans = it.next();
            crtText = crtTrans.getText();
            String[] words = crtText.split(" ");
            int pos = 0;
            HashSet<String> ordered = new HashSet<String>();
            crtTrans.setAttribute("noTokens", words.length + "");
            for (String word : words) {
                sentenceTranslationModel.addWord(word, pos);
                ordered.add(word);
                pos++;
            }
            crtTrans.setAttribute("noUniqueTokens", ordered.size() + "");
        }
    }

    public SentenceTranslationModel getSentenceTranslationModel() {
        if (sentenceTranslationModel == null) {
            buildSentenceModel();
        }
        return sentenceTranslationModel;
    }

    public int getNbestWordFreq(String word) {
        return getSentenceTranslationModel().getWordFreq(word);
    }

    public String getTranslationAttribute(String name) {
        return getBest().getAttribute(name);
    }

    public TreeSet<Translation> getTranslations() {
        return translations;
    }

    public void addWordMorph(PronMorph word) {
        if (morphs == null) {
            morphs = new ArrayList<PronMorph>();
        }
        morphs.add(word);
    }

    public ArrayList<PronMorph> getWordMorphs() {
        return morphs;
    }

    public PronMorph getWord(int index) {
        return morphs.get(index);
    }

    public String findPhrases(int position) {
        //returns the contiguous text of the following phrases: the Phrase the position belongs to, plus one Phrase to the left and right of the main Phrase
        ArrayList<Phrase> pArray = new ArrayList<Phrase>(3);
        //create a mock phrase with start and end indeces equal to position
        Phrase p = new Phrase("", position, position);

        //get the list of Phrases that are larger or equal to p according to PhraseComparator
        NavigableSet<Phrase> tailSet = phrases.tailSet(p, false);

        Iterator<Phrase> it = phrases.iterator();
        boolean found = false;
        Phrase crtPhrase = null;
        while (it.hasNext() && !found) {
            crtPhrase = it.next();
            int start = crtPhrase.getStart();
            int end = crtPhrase.getEnd();
            if (start <= position && end >= position) {
                found = true;
            }
        }


        //the first element in the list is the phrase that contains the position
//                System.out.println("position: "+position);
//                System.out.println(crtPhrase.getStart()+" "+crtPhrase.getEnd()+" "+crtPhrase.getText());
        //get the Phrase immediately lower
        Phrase before = phrases.lower(crtPhrase);
        //get the Phrase immediately higher
        Phrase after = phrases.higher(crtPhrase);
        StringBuffer result = new StringBuffer();
        if (before != null) {
            result.append(before.getText());
            result.append(" ");
        }

        result.append(crtPhrase.getText());

        if (after != null) {
            result.append(" ");
            result.append(after.getText());
            result.append(" ");
        }

        return result.toString();
    }
}
