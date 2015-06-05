package shef.mt.tools.mqm;

import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceManager;
import shef.mt.tools.ResourceProcessor;
import shef.mt.tools.ResourceProcessorTwoSentences;
import shef.mt.tools.mqm.core.fluency.inconsistency.AbbreviationsProcessor;
import shef.mt.tools.mqm.core.fluency.register.VariantsSlangProcessor;
import shef.mt.tools.mqm.resources.AbbreviationDictionary;
import shef.mt.tools.mqm.resources.SlangDictionary;
import shef.mt.util.PropertiesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class MQMManager {
    private static MQMManager ourInstance = new MQMManager();

    public static MQMManager getInstance() {
        return ourInstance;
    }

    private MQMManager() {
    }

    private boolean isInitialized = false;
    private PropertiesManager propertiesManager = null;
    private List<GlobalProcessor> globalProcessors = new ArrayList<GlobalProcessor>();
    private List<ResourceProcessor> srcResourceProcessors = new ArrayList<ResourceProcessor>();
    private List<ResourceProcessor> trgResourceProcessors = new ArrayList<ResourceProcessor>();
    private List<ResourceProcessorTwoSentences> srcTrgResourceProcessors = new ArrayList<ResourceProcessorTwoSentences>();

    public String getSrcLang() {
        return this.propertiesManager.getString("sourceLang.default");
    }

    public String getTrgLang() {
        return this.propertiesManager.getString("targetLang.default");
    }

    /**
     * Initialize resources and processors that has been config, and register them in Resource Manager
     * @param propertiesManager
     * @return
     */
    public boolean initialize(PropertiesManager propertiesManager) {
        assert propertiesManager != null;
        this.propertiesManager = propertiesManager;
        //this.configurables = new ArrayList<Configurable>();
        //read the config and initialize those resources and processors that has been config
        //TODO; only initialize the resources and processors by reflection from the feature sets, has to change the framework to start
        try {
            String srcLang = this.getSrcLang();
            String trgLang = this.getTrgLang();

            //slang dict only for the target
            SlangDictionary trgSlangDict = new SlangDictionary(trgLang);
            if (trgSlangDict.isConfigured(propertiesManager)) {
                trgSlangDict.load(propertiesManager);
                trgSlangDict.register();
                VariantsSlangProcessor p1 = new VariantsSlangProcessor(trgSlangDict);
                this.trgResourceProcessors.add(p1);
            }

            //abbreviation
            AbbreviationDictionary abbrevDict = new AbbreviationDictionary(trgLang);
            if (abbrevDict.isConfigured(propertiesManager)) {
                abbrevDict.load(propertiesManager);
                abbrevDict.register();
                AbbreviationsProcessor p2 = new AbbreviationsProcessor(abbrevDict);
                this.trgResourceProcessors.add(p2);
            }

            //other features

        } catch (Exception e) {
            isInitialized = false;
            return false;
        }
        isInitialized = true;
        return true;
    }

    public void globalProcessing(Context context) {
        context.checkContext();
        for (GlobalProcessor processor : globalProcessors) {
            processor.globalProcessing(context);
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void processNextParallelSentences(Sentence source,Sentence target) {
        for (ResourceProcessor processor : srcResourceProcessors) {
            processor.processNextSentence(source);
        }
        for (ResourceProcessor processor : trgResourceProcessors) {
            processor.processNextSentence(target);
        }
        for (ResourceProcessorTwoSentences processor : srcTrgResourceProcessors) {
            processor.processNextParallelSentences(source, target);
        }
    }
}
