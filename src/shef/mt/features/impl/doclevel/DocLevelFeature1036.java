/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.features.impl.doclevel;


import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.FileModel;
import shef.mt.tools.Giza;

/**
 * Average number of translations per source word in the document (threshold in
 * giza: prob > 0.01) weighted by the inverse frequency of each word in the
 * source corpus
 *
 * @author Carolina Scarton
 *
 */
public class DocLevelFeature1036 extends DocLevelFeature {

    final static Float probThresh = 0.01f;

    public DocLevelFeature1036() {
        setIndex(1036);
        setDescription("average number of translations per source word in the document (threshold in giza: prob > 0.01) weighted by the inverse frequency of each word in the source corpus");
        this.addResource("giza.path");
        
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(Doc source, Doc target) {
        float noTokens=0.0f;
        float probSum = 0;
        float value =0.0f;
        for (int i=0;i<source.getSentences().size();i++){
            noTokens+= source.getSentence(i).getNoTokens();
            String[] tokens = source.getSentence(i).getTokens();
            for (String word : tokens) {
                float freq = FileModel.getFrequency(word);
                float gizaf = (float) Giza.getWordProbabilityCount(word, probThresh);

                if (freq != 0) {
                    probSum += gizaf / freq;
                }
            }
            if (noTokens == 0) {
                value +=0;
            }else{
                value += probSum/noTokens;
            }
        }
        setValue(value / source.getSentences().size());
    }
}
