package shef.mt.tools.mqm.core.fluency;

import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceProcessor;
import shef.mt.tools.mqm.Configurable;
import shef.mt.util.PropertiesManager;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/13/13
 */
public class DuplicationProcessor extends ResourceProcessor implements Configurable {
    @Override
    public boolean isConfigured(PropertiesManager propertiesManager) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processNextSentence(Sentence source) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
