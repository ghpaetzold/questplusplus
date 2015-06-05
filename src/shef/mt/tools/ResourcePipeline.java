/**
 *
 */
package shef.mt.tools;

import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * @author Catalina Hallett
 *
 */
public class ResourcePipeline {

    private ArrayList<ResourceProcessor> resources;

    public ResourcePipeline() {
        resources = new ArrayList<ResourceProcessor>();
    }

    public void addResourceProcessor(ResourceProcessor proc) {
        resources.add(proc);
    }

    public void processSentence(Sentence sent) {
        Iterator<ResourceProcessor> it = resources.iterator();
        while (it.hasNext()) {
            it.next().processNextSentence(sent);
        }
    }
}
