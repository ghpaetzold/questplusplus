
package shef.mt.tools;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds a tagged language model object to the sentences processed.
 * @author GustavoH
 */
public class POSNgramCountProcessor extends ResourceProcessor {

    private LanguageModel lm;

    public POSNgramCountProcessor(LanguageModel lm) {
        this.lm = lm;
    }
    
    @Override
    public void processNextSentence(Sentence s) {
        s.setValue("posngramcount", this.lm);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
