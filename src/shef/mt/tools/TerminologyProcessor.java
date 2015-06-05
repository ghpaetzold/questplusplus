package shef.mt.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import shef.mt.features.util.FeatureManager;
import shef.mt.features.util.Sentence;
import shef.mt.util.PropertiesManager;
import shef.mt.tools.term.Terminology;
import shef.mt.tools.term.TerminologyEntry;

/**
 * loads monolingual and bilingual terminology, if the corresponding entries in the configuration are set;
 * fills some values to be later used as the following features:
 * 1. number of monolingual terminology phrases occurring in the target sentence
 * 2. number of bilingual terminology phrase pairs where both the source and the target phrase occur in both the source and the target sentence
 * 3. number of bilingual terminology phrase pairs where the source phrase occurs in the source sentence but the target phrase does _not_ occur in the target sentence
 */
//FIXME: This should extend ResourceProcessor; however, since it needs both source and target sentences,
//it currently doesn't. Later, when refactored and run automatically, this might not get executed
public class TerminologyProcessor {
	//monolingual terminology for the target sentence
	private Terminology monoTerms;
	
	//bilingual terminology for the source-target sentence pair
	private Terminology biTerms;

	public TerminologyProcessor() {
		// TODO Auto-generated constructor stub
	}

	public void initialize(PropertiesManager propertiesManager,
			FeatureManager featureManager) {
		//load monolingual terminology, if it is given
		this.monoTerms = null;
		
		String monoTerminologyPath = propertiesManager.getProperty("terminology.monolingual-mwu");
		
		if (monoTerminologyPath != null) {
			this.monoTerms = new Terminology(monoTerminologyPath);
		}
		
		//load bilingual terminology, if it is given
		this.biTerms = null;
		
		String biTerminologyPath = propertiesManager.getProperty("terminology.bilingual-mwu");
		
		if (biTerminologyPath != null) {
			this.biTerms = new Terminology(biTerminologyPath);
		}
	}

	public void processNextSentence(Sentence sourceSentence, Sentence targetSentence) {
		if (this.monoTerms != null) {
			int monoTermCount = this.monoTerms.countTermsInSentence(targetSentence);
			
			targetSentence.setValue("monolingual-terms", monoTermCount);
		}
		
		if (this.biTerms != null) {
			Map<TerminologyEntry, Integer> sourceMatchingTerms = this.biTerms.getTermsInSentence(sourceSentence);
			
			int numTermTranslations = 0;
			int numTermMistranslations = 0;
			
			Iterator<Map.Entry<TerminologyEntry, Integer>> it = sourceMatchingTerms.entrySet().iterator();
			
			while (it.hasNext()) {
				Map.Entry<TerminologyEntry, Integer> nextPair = it.next();
				
				int srcCount = nextPair.getValue().intValue();
				int tgtCount = nextPair.getKey().countCounterOccurrences(targetSentence);
				
				numTermTranslations += Math.min(srcCount, tgtCount);
				numTermMistranslations += Math.max(srcCount - tgtCount, 0);
			}
			
			targetSentence.setValue("bilingual-translations", numTermTranslations);
			targetSentence.setValue("bilingual-mistranslations", numTermMistranslations);
		}
	}
}
