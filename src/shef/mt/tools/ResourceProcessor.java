/**
 *
 */
package shef.mt.tools;

import shef.mt.features.util.Sentence;

/**
 * Abstract class that is the base class for all classes that process the output
 * files of pre-processing tools
 *
 * @author Catalina Hallett
 *
 */
public abstract class ResourceProcessor {

    public abstract void processNextSentence(Sentence source);
}
