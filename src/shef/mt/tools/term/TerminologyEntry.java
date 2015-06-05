package shef.mt.tools.term;

import shef.mt.features.util.Sentence;

/**
 * an entry for the terminology hashmap; consists of a phrase (e.g. in the source language) and its counterpart (in the target language);
 * in case of monolingual terminologies the counterpart can be left NULL
 */
public class TerminologyEntry {
	private String phrase;
	private String counterpartPhrase;
		
	/**
	 * Entry constructor from a raw line from a terminology file.
	 * A line with two tab-separated phrases means a bilingual entry.
	 */
	public TerminologyEntry(String rawline) {
		String[] phrases = rawline.trim().split("\t");
		
		setPhrase(phrases[0]);
		
		if (phrases.length > 1) {
			setCounterpartPhrase(phrases[1]);
		}
		else {
			setCounterpartPhrase(null);
		}
	}
	
	/**
	 *
	 */
	public static int countPhraseOccurrences(String[] haystackToks, String needle) {
		String haystack = tokensToString(haystackToks);
		
		int result = 0;
		int pos = 0;
		
		while ((pos = haystack.indexOf(needle, pos)) >= 0) {
			result++;
			pos++;
		}
		
		return result;
	}
	
	/**
	 *
	 */
	public int countOccurrences(Sentence sentence) {
		return countPhraseOccurrences(sentence.getTokens(), this.getPhrase());
	}
	
	/**
	 *
	 */
	public int countCounterOccurrences(Sentence sentence) {
		return countPhraseOccurrences(sentence.getTokens(), this.getCounterpartPhrase());
	}
	
	/**
	 *
	 */
	public static String tokensToString(String[] sntTokens, int startIdx) {
		String subSnt = "";
		
		for (int i = startIdx; i < sntTokens.length; i++) {
			if (i > startIdx) {
				subSnt += " ";
			}
			
			subSnt += sntTokens[i];
		}
		
		return subSnt;
	}
	
	/**
	 *
	 */
	public static String tokensToString(String[] sntTokens) {
		return tokensToString(sntTokens, 0);
	}
	
	/**
	 * check if the term entry is actually present in the sentence,
	 * starting at the given position
	 */
	public boolean matchesSentence(String[] sntTokens, int startIdx) {
		String subSnt = TerminologyEntry.tokensToString(sntTokens, startIdx);
		
		return subSnt.startsWith(this.phrase);
	}
	
	/**
	 * return the first word of the phrase, to be used as a key in 
	 * collecting term entries
	 */
	public String getPhraseFirstWord() {
		String[] words = this.phrase.split(" ");
		return words[0];
	}
	
	/**
	 * getters/setters
	 */
	public String getPhrase() {
		return this.phrase;
	}
	
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	
	public String getCounterpartPhrase() {
		return this.counterpartPhrase;
	}
	
	public void setCounterpartPhrase(String counterpartPhrase) {
		this.counterpartPhrase = counterpartPhrase;
	}
	
	/**
	 * Hashing stuff
	 */
	@Override
	public int hashCode() {
		return (this.phrase + this.counterpartPhrase).hashCode();
	}
	
	/**
	 *
	 */
	private boolean stringsEqual(String a, String b) {
		if (a == null) {
			return (b == null);
		}
		else {
			return a.equals(b);
		}
	}
	
	/**
	 *
	 */
	@Override
	public boolean equals(Object e) {
		if (!(e instanceof TerminologyEntry)) {
			return false;
		}
		
		TerminologyEntry ee = (TerminologyEntry)e;
		String eePhr = ee.getPhrase();
		String eeCPhr = ee.getCounterpartPhrase();
		
		return (stringsEqual(eePhr, this.phrase) && stringsEqual(eeCPhr, this.counterpartPhrase));
	}
}
