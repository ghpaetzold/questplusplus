package shef.mt.tools.term;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;

import shef.mt.features.util.Sentence;

/**
 * Monolingual phrase terminology.
 * 
 * Represents a terminology of phrases.
 * 
 * To speed up the lookup of phrases in a sentence, it is implemented as a map, where
 * the first word of a phrase points to the list of phrases, starting with it (List<TerminologyEntry>).
 */
public class Terminology {
	private HashMap<String, List<TerminologyEntry>> termWordMap;
	
	/**
	 * create the terminology object and fill it with the entries from the given
	 * file
	 */
	public Terminology(String filename) {
		this.termWordMap = new HashMap<String, List<TerminologyEntry>>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			
			String strLine;
			
			//Read File Line By Line, parsing each Line
			while ((strLine = br.readLine()) != null)   {
				//load one or both phrases into a terminology entry
				TerminologyEntry newEntry = new TerminologyEntry(strLine);
				
				//get the first word of the first phrase to serve as a keyword
				String keyword = newEntry.getPhraseFirstWord();
				
				List<TerminologyEntry> entryList = this.termWordMap.get(keyword);
				
				//update the map with the new entry
				if (entryList == null) {
					entryList = new LinkedList<TerminologyEntry>();
					
					this.termWordMap.put(keyword, entryList);
				}
				
				entryList.add(newEntry);
			}
			
			//Close the input stream
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * returns a map of TerminologyEntries that occur in the given sentence, pointing
	 * to the number of its occurrences in the given sentence
	 */
	public Map<TerminologyEntry, Integer> getTermsInSentence(Sentence sentence) {
		//initialize map of results
		Map<TerminologyEntry, Integer> result = new HashMap<TerminologyEntry, Integer>();
		
		String[] sntTokens = sentence.getTokens();
		
		for (int i = 0; i < sntTokens.length; i++) {
			String currentToken = sntTokens[i];
			
			//find the terms whose first token matches the current sentence token
			List<TerminologyEntry> currentMatches = this.termWordMap.get(currentToken);
			
			if (currentMatches != null) {
				for (TerminologyEntry termEntry:currentMatches) {
					
					//filter the terms that are actually present in the sentence
					if (termEntry.matchesSentence(sntTokens, i)) {
						//increase the count of a term, present in the sentence
						Integer currentCountEntry = result.get(termEntry);
						
						int currValue = (currentCountEntry == null)? 0: currentCountEntry.intValue();
						
						result.put(termEntry, new Integer(currValue + 1));
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 *
	 */
	public int countTermsInSentence(Sentence sentence) {
		int result = 0;
		
		Map<TerminologyEntry, Integer> matches = getTermsInSentence(sentence);
		
		Iterator<Map.Entry<TerminologyEntry, Integer>> it = matches.entrySet().iterator();
		
		while (it.hasNext()) {
			result += it.next().getValue().intValue();
		}
		
		return result;
	}
}
