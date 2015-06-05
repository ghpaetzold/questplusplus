package shef.mt.tools.mqm.resources;

import shef.mt.tools.Resource;
import shef.mt.tools.ResourceManager;
import shef.mt.tools.mqm.Configurable;
import shef.mt.tools.mqm.Registrable;
import shef.mt.util.PropertiesManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * monolingual abbreviation dictionary
 * current english resource is from http://public.oed.com/how-to-use-the-oed/abbreviations/
 * TODO: combine with other resources like http://www.indiana.edu/~letrs/help-services/QuickGuides/oed-abbr.html
 *
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/12/13
 */
public class AbbreviationDictionary extends Resource implements Configurable, Registrable {

    private final static Logger LOGGER = Logger.getLogger(AbbreviationDictionary.class.getName());

    private String language;
    private String configKey;
    private HashMap<String, HashSet<String>> abbrev2meaningSets = new LinkedHashMap<String, HashSet<String>>();

    public AbbreviationDictionary(String language) {
        this.language = language;
        this.configKey = "MQM.abbreviation." + this.language;
    }

    @Override
    public boolean isConfigured(PropertiesManager propertiesManager) {
        String config = propertiesManager.getProperty(this.configKey);
        return !(config == null || config.length() == 0);
    }

    @Override
    public void register() {
        ResourceManager.registerResource(this.configKey);
    }

    public void load(PropertiesManager propertiesManager) {
        String config = propertiesManager.getProperty(this.configKey);
        assert (config != null && config.length() > 0);
        this.load(config);
    }

    public void load(String filepath) {
        //read file line by line and put into slang dict
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.length() > 0) {
                   String[] parts = strLine.split("\t");
                   if (parts.length == 2) {
                       String abbrev = parts[0].trim();
                       String meaning = parts[1].trim();
                       LOGGER.finest("parsing abbreviation: [" + abbrev + "] = [" + meaning + "]");
                       List<String> meanings = meaningNormalize(meaning);
                       abbrev2meaningSets.put(abbrev, new HashSet<String>(meanings));
                   } else {
                       LOGGER.severe("Invalid abbreviation line: " + strLine);
                   }
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

    private Pattern frontBraces = Pattern.compile("^\\([^\\）]+\\)\\s+");
    private Pattern endBraces = Pattern.compile("\\s+\\([^\\）]+\\)$");
    private Pattern formChanges = Pattern.compile("(\\S+)\\((\\S+)\\)");

    private List<String> meaningNormalize(String meaning) {
        String[] words = meaning.split(",");
        List<String> meanings = new ArrayList<String>();
        for (String word : words) {
            Matcher m = this.frontBraces.matcher(word);
            if (m.find()) {
                word = m.replaceAll("");
            }
            m = this.endBraces.matcher(word);
            if (m.find()) {
                word = m.replaceAll("");
            }
            m = this.formChanges.matcher(word);
            if (m.find()) {
                String prefix = m.group(1);
                String suffix = m.group(2);
                meanings.add(prefix);
                meanings.add(prefix + suffix);
            } else {
                meanings.add(word);
            }
        }
        return meanings;
    }

    public static void main(String args[]) {
        AbbreviationDictionary dict = new AbbreviationDictionary("english");
        dict.load(args[0]);
    }

    public Set<String> getAbbrevSet() {
        return abbrev2meaningSets.keySet();
    }

    public Set<String> getMeaningSetOfAbbreviation(String abbreviation) {
        if (abbrev2meaningSets.containsKey(abbreviation)) {
            return abbrev2meaningSets.get(abbreviation);
        } else {
            return null;
        }
    }
}
