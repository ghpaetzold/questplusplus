
package shef.mt.tools;

import shef.mt.features.util.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author David Langlois
 *
 */
public class TriggersProcessor extends ResourceProcessorTwoSentences {
    Triggers ilt;

    public TriggersProcessor(Triggers ilt) {
        this.ilt = ilt;
        ResourceManager.registerResource("IntraLingualTriggers");
    }

    public void processNextSentence(Sentence s) {
        float som = 0;
        int nb = 0;
        int i,j,k;
        HashMap<String,Integer> phrasesSource = new HashMap<String,Integer>();
        

        //building the set of source phrases in source sentence
        for(i=0; i<s.getNoTokens(); i++){
            for(j=1; j<=ilt.lengthMaxSide1; j++){
                String phrase = new String(s.getToken(i));
                for(k=2; k<=j; k++)
                    phrase.concat(ilt.phraseSeparator+s.getToken(i+k-1));
                if ( !phrasesSource.containsKey(phrase) )
                    phrasesSource.put(phrase, 1);
            }
        }
        
        s.setValue("phrases", phrasesSource);
        s.setValue("ilt", this.ilt);   
        
        for( String phraseSource1 : phrasesSource.keySet() ){
            for( String phraseSource2 : phrasesSource.keySet() ){
                if ( !phraseSource1.equals(phraseSource2) ){
                    som += ilt.getScore(phraseSource1, phraseSource2);
                    nb++;
                }
            }
        }
        if ( nb == 0 )
            s.setValue("IntraLingualScore",new Float(0));
        else
            s.setValue("IntraLingualScore",som/nb);
    }

    public void processNextParallelSentences(Sentence s, Sentence t) {
        float som = 0;
        int nb = 0;
        int i,j,k;
        HashMap<String,Integer> phrasesSource = new HashMap<String,Integer>();
        HashMap<String,Integer> phrasesTarget = new HashMap<String,Integer>();

        //building the set of source phrases in source sentence
        for(i=0; i<s.getNoTokens(); i++){
            for(j=1; j<=ilt.lengthMaxSide1; j++){
                String phrase = new String(s.getToken(i));
                //phrase.concat(s.getToken(i));
                for(k=2; k<=j; k++)
                    phrase.concat(ilt.phraseSeparator+s.getToken(i+k-1));
                if ( !phrasesSource.containsKey(phrase) )
                    phrasesSource.put(phrase, 1);
            }
        }
        
        //building the set of target phrases in target sentence
        for(i=0; i<t.getNoTokens(); i++){
            for(j=1; j<=ilt.lengthMaxSide2; j++){
                String phrase = new String(t.getToken(i));
                //phrase.concat(t.getToken(i));
                for(k=2; k<=j; k++)
                    phrase.concat(ilt.phraseSeparator+t.getToken(i+k-1));
                if ( !phrasesTarget.containsKey(phrase) )
                    phrasesTarget.put(phrase, 1);
            }
        }

        for( String phraseSource : phrasesSource.keySet() ){
            for( String phraseTarget : phrasesTarget.keySet() ){
                    som += ilt.getScore(phraseSource, phraseTarget);
                    nb++;
            }
        }
        if ( som == 0 )
            t.setValue("InterLingualSourceTargetScore",new Float(0));
        else
            t.setValue("InterLingualSourceTargetScore",som/nb);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}