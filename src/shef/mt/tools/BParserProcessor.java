package shef.mt.tools;

import java.util.Map;
import java.util.Iterator;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.BParser;
import shef.mt.util.PropertiesManager;

/**
 * A processor class for the BParser. It loads the 
 * Parser instances, runs those through the sentences 
 * and provides the features
 * 
 * @author Eleftherios Avramids
 *
 */
public class BParserProcessor extends ResourceProcessor {
	
	BParser parser;
	boolean tokenizer;
        
	
	/***
	 * This function initializes a parser object with the desired grammar
	 * into memeory
	 * @param inputFile text file containing the text to be processed
	 * @param rm contains the parameters set in the configuration file
	 * @param language the name of the language that this processor will be
	 *    responsible for
	 */
	public void initialize(String inputFile, PropertiesManager rm, String language){
		//get the grammar filename from the configuration file
		String grammarFilename = rm.getString(language + ".bparser.grammar");
		String kbest_entry = rm.getString(language + ".bparser.kbest");
		
		int kbest;
		if (kbest_entry == null){
			kbest = 1000;
		} else {
			kbest = Integer.valueOf(kbest_entry);
		}
			
		//initialize the BParser class, which is a wrapper around the native parser
		//a special parameter exists for chinese='true', otherwise this is 'false'
		parser = new BParser(grammarFilename, (language == "chinese"), kbest);
		
		//it has been seen that the English and the Spanish grammar behave better with Moses tokenizer.perl
		//than with the internal berkeley tokenizer 
		tokenizer = false;
		
		//parser initialized, so register the resource, so that the features know it is available
		//ResourceManager.registerResource("BParser");
                ResourceManager.registerResource(language + ".bparser.grammar");
                System.out.println(language + ".bparser.grammar");
	}
	

	@Override
	public void processNextSentence(Sentence s) {
		
		//ask the parser to perform a parse of the sentence
		parser.getParseFeatures(s.getText(), tokenizer);
		
		s.setValue("bparser.parse", parser.getParseTree());
		s.setValue("bparser.loglikelihood", parser.getLoglikelihood());
		s.setValue("bparser.avgConfidence", parser.getAvgConfidence());
		s.setValue("bparser.bestParseConfidence", parser.getBestParseConfidence());
		s.setValue("bparser.n", parser.getParseTreesN());
	}

    @Override
    public void processNextDocument(Doc doc) {
        //ask the parser to perform a parse of the document
        
         
        for (int i=0;i<doc.getSentences().size();i++){
            this.processNextSentence(doc.getSentence(i));
        }

    }
	
	
}
