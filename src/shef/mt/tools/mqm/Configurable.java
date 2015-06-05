package shef.mt.tools.mqm;


import shef.mt.util.PropertiesManager;

/**
 * basic interface for classes that is configurable from the Properties manager
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public interface Configurable {
    public boolean isConfigured(PropertiesManager propertiesManager);
}
