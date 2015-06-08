/**
 *
 */
package shef.mt.features.impl.doclevel;

import java.util.HashSet;

import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * absolute difference between the number of CONJP phrases in the source and
 * target normalised by the total number of phrasal tags in the sentence
 *
 * @author Catalina Hallett
 *
 *
 *
 */
public class DocLevelFeature1115 extends DocLevelFeature {

    public DocLevelFeature1115() {
        setIndex(1115);
        setDescription("absolute difference between the number of CONJP phrases in the source and target normalised by the total number of phrasal tags in the sentence");
        this.addResource("postags");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {


    }

    @Override
    public void run(Doc source, Doc target) {
        
        float sourcePP =0;
        float phrasesSource =0;
        float targetPP =0;
        float phrasesTarget =0;
        for(int i=0;i<source.getSentences().size();i++){
            sourcePP+=(float) source.getSentence(i).getValue("CONJP");
            phrasesSource+= (float) source.getSentence(i).getValue("phrase_tags");
            targetPP += (float) target.getSentence(i).getValue("CONJP");
            phrasesTarget += (float) source.getSentence(i).getValue("phrase_tags");
        }
        float sourceNorm = phrasesSource == 0 ? 0 : sourcePP / phrasesSource;
        float targetNorm = phrasesTarget == 0 ? 0 : targetPP / phrasesTarget;
        setValue((float) Math.abs(sourceNorm - targetNorm));
        
        
    }
}
