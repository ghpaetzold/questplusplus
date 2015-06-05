package shef.mt.tools;

import java.util.*;

/**
 *
 * @author Catalina Hallett
 *
 */
public abstract class Resource {

    boolean available;
    HashSet<String> req;
    ResourceProcessor resProc;

    public Resource() {
    }

    public Resource(ResourceProcessor resProc) {
        this.resProc = resProc;
    }

    public boolean isAvailable() {
        return available;
    }

    ;

	public void addRequisite(String r) {
        if (req == null) {
            req = new HashSet<String>();
        }
        req.add(r);
    }

    /**
     * checks whether the Resource should run, i.e. it is required by at least
     * one of the currently registered features
     *
     * @return true if the Resource is required, false otherwise
     */
    public boolean isRequired() {
        if (req == null) {
            return true;
        }
        Iterator<String> it = req.iterator();
        while (it.hasNext()) {
            if (ResourceManager.reqRegistered(it.next())) {
                return true;
            }
        }
        return false;
    }

    public ResourceProcessor getProcessor() {
        return resProc;
    }
}
