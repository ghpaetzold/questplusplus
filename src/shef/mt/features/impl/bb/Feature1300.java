/**
 *
 */

package shef.mt.features.impl.bb;

import java.util.HashSet;
import java.lang.Math;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.TopicDistributionProcessor;


/**
 * Feature 1300 is the Kullback-Leibler divergence between a source sentence and a target sentence topic distribution
 *
 * @author Raphael Rubino
 *
 */
public class Feature1300 extends Feature {

    public Feature1300() {
        setIndex("1300");
        setDescription("Kullback-Leibler divergence of source and target topic distributions");
        this.addResource("source.topic.distribution");
        this.addResource("target.topic.distribution");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.impl.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
       // System.out.println(source.getValue("topicDistribution"));
        Float[] sourceTopicVector = (Float[]) source.getValue("topicDistribution");
	Float[] targetTopicVector = (Float[]) target.getValue("topicDistribution");
	Float klDiv = klDivergence( sourceTopicVector, targetTopicVector );
	setValue( klDiv );
    }

    /**
    * @param    sourceVector    Float[] containing the topic distribution of the source sentence
    * @param    targetVector    Float[] containing the topic distribution of the target sentence
    * @return           Float containing the Kullback-Leibler divergence between the source and the target topic distribution
    *
    */
    public Float klDivergence(Float[] sourceVector, Float[] targetVector) {
        Float klDiv = 0.0f;
        for (int i = 0; i < sourceVector.length; ++i) {
                if (sourceVector[i] == 0.0f) { continue; }
                if (targetVector[i] == 0.0f) { continue; }
                Float ratio = new Float( Math.log( sourceVector[i] / targetVector[i] ) );
                klDiv += sourceVector[i] * ratio;
        }
        return klDiv / new Float( Math.log( 2 ) );
    }
}
