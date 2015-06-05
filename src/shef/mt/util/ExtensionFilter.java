/**
 *
 */
package shef.mt.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author cat
 *
 */
public class ExtensionFilter implements FilenameFilter {

    String[] extensions;

    public ExtensionFilter(String[] extensions) {
        this.extensions = extensions;
    }

    /* (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name) {
        boolean found = false;
        for (String ext : extensions) {
            found = found || name.endsWith(ext);
        }
        return found;
    }
}
