package shef.mt.features.impl.bb;

import java.util.HashMap;
import java.util.HashSet;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Triggers;

/**
 * source-target sentence inter lingual mutual information
 *
 * @author David Langlois
 *
 */
public class Feature1304 extends Feature {

    public Feature1304() {
        setIndex(1304);
        setDescription("source-target sentence inter lingual mutual information");
        this.addResource("pair.inter.triggers.file");
        //if I add this resource while I declare OK this resource in IntraLingualTriggers. It does not work.
        //I forget until I understand.
        //addResource("IntraLingualTriggers");
    }

    @Override
    public void run(Sentence source, Sentence target) {
        float som = 0;
        int nb = 0;
        HashMap<String,Integer> phrasesTarget = (HashMap<String,Integer>) target.getValue("phrases");
        HashMap<String,Integer> phrasesSource = (HashMap<String,Integer>) source.getValue("phrases");
        Triggers ilt = (Triggers) target.getValue("ilt");
        
        for( String phraseSource : phrasesSource.keySet() ){
            for( String phraseTarget : phrasesTarget.keySet() ){
                    som += ilt.getScore(phraseSource, phraseTarget);
                    nb++;
            }
        }
        if ( som == 0 )
            setValue(new Float(0));
        else
            setValue(som/nb);
   

        //setValue((Float)target.getValue("InterLingualSourceTargetScore"));
    }
}
