
package shef.mt.tools;

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

}
