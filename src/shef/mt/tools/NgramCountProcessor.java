
package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

public class NgramCountProcessor extends ResourceProcessor {

    private LanguageModel lm;

    public NgramCountProcessor(LanguageModel lm) {
        this.lm = lm;
    }
    
    @Override
    public void processNextSentence(Sentence s) {
        s.setValue("ngramcount", this.lm);
        
    }

    @Override
    public void processNextDocument(Doc d) {
        for(int i=0;i<d.getSentences().size();i++){
            this.processNextSentence(d.getSentence(i));
            d.getSentence(i).computeNGrams(3);
        }
    }

}
