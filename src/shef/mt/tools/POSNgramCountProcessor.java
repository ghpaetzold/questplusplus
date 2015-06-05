
package shef.mt.tools;

import shef.mt.features.util.Sentence;

public class POSNgramCountProcessor extends ResourceProcessor {

    private LanguageModel lm;

    public POSNgramCountProcessor(LanguageModel lm) {
        this.lm = lm;
    }
    
    @Override
    public void processNextSentence(Sentence s) {
        s.setValue("posngramcount", this.lm);
    }

}
