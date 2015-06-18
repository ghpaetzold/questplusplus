package shef.mt.enes;

import shef.mt.features.util.FeatureManager;
import shef.mt.util.PropertiesManager;

public interface FeatureExtractorInterface {

    public FeatureManager getFeatureManager();

    public PropertiesManager getResourceManager();

    public String getSourceFile();

    public String getTargetFile();

    public String getSourceLang();

    public String getTargetLang();

}
