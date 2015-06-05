/**
 *
 */
package shef.mt.tools;

import java.util.*;

/**
 * @author cat
 *
 */
public class ResourceManager {

    private static HashSet<String> resources = new HashSet<String>();
    private static HashSet<String> reqFeatures = new HashSet<String>();

    public ResourceManager() {
    }

    public static void registerResource(String res) {
        resources.add(res);
    }

    public static boolean isAvailable(String res) {
        return resources.contains(res);
    }

    public static boolean isRegistered(String res) {
        return resources.contains(res);
    }

    public static boolean isRegistered(HashSet<String> res) {
        return resources.containsAll(res);
    }

    public static void printResources() {
        System.out.println(resources);
    }

    public static void registerFeature(String feat) {
        reqFeatures.add(feat);
    }

    public static boolean reqRegistered(String req) {
        return reqFeatures.contains(req);
    }
}
