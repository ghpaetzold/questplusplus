
package shef.mt.features.impl;
import shef.mt.tools.ResourceManager;
import shef.mt.features.util.Doc;

import java.util.*;

/**
 * Variation of the Feature class, which is suited for doc-level features.
 * @author Carol
 */
public abstract class DocLevelFeature extends Feature{

    public abstract void run(Doc source, Doc target);

}
