package shef.mt.tools.mqm.resources;

import shef.mt.tools.Resource;
import shef.mt.tools.ResourceManager;
import shef.mt.tools.mqm.Configurable;
import shef.mt.tools.mqm.Registrable;
import shef.mt.util.PropertiesManager;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * monolingual slang dict
 * The current english resource was downloaded from http://www.noslang.com/dictionary/
 * More resources for different languages should follow.
 *
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class SlangDictionary extends Resource implements Configurable, Registrable {

    private String language;
    private String configKey;
    private Set<String> slangDict = new HashSet<String>();

    public SlangDictionary(String language) {
        this.language = language;
        this.configKey = "MQM.slang." + this.language;
    }

    @Override
    public boolean isConfigured(PropertiesManager propertiesManager) {
        String config = propertiesManager.getProperty(this.configKey);
        return !(config == null || config.length() == 0);
    }

    public void load(PropertiesManager propertiesManager) {
        String config = propertiesManager.getProperty(this.configKey);
        assert (config != null && config.length() > 0);
        this.load(config);
    }

    //TODO, currently an web copied text format is used, one line with dict entry followed by slash, one line with explanation, needs better format
    public void load(String filepath) {
        //read file line by line and put into slang dict
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.endsWith("-")) {
                    String entry = strLine.substring(0, strLine.length()-1).trim();
                    slangDict.add(entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void register() {
        ResourceManager.registerResource(this.configKey);
    }

    public static void main(String args[]) {
        SlangDictionary dict = new SlangDictionary("english");
        dict.load(args[0]);
    }

    public boolean isSlang(String word) {
        assert word != null;
        return slangDict.contains(word);
    }
}
