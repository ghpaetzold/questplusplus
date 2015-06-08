package shef.mt.tools.mqm.core.fluency.register;

import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceProcessor;
import shef.mt.tools.mqm.resources.SlangDictionary;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class VariantsSlangProcessor extends ResourceProcessor {
    private SlangDictionary slangDict = null;
    public VariantsSlangProcessor(SlangDictionary slangDict) {
        this.slangDict = slangDict;
    }

    @Override
    public void processNextSentence(Sentence sentence) {
        assert slangDict != null;
        int count = 0;
        for (String word : sentence.getTokens()) {
            if (slangDict.isSlang(word)) {
                count += 1;
            }
        }

        sentence.setValue("slang_words_count", count);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
