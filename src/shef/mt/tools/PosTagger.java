package shef.mt.tools;

import shef.mt.util.Logger;

/**
 * Base class for part-of-speech tagger wrappers
 *
 * @author Catalina Hallett
 *
 */
public class PosTagger extends Resource {

    String name = "";
    String path = "";
    String lang;
    String[] args = null;
    String input;
    String output;
    static String XPOS = ".XPOS";
    public static String[] NounTags = {"NN", "NNS", "NP", "NPS", "NAM", "NOM", "PER", "NMON", "NMEA"};
    public static String[] VerbTags = {
        "VVD", "VV", "VVZ", "VVN", "VHP", "VB", "VBG", "VBD", "VBN", "VBP", "VBZ",
        "VER:cond", "VER:futu", "VER:impe", "VER:impf", "VER:infi", "VER:pper",
        "VER:ppre", "VER:pres", "VER:simp", "VER:subi", "VER:subp", "VCLIger", "VCLIinf",
        "VCLIfin", "VEadj", "VEfin", "VEger", "VEinf", "VHadj", "VHfin",
        "VHger", "VHinf", "VLadj", "VLfin", "VLger", "VLinf", "VMadj", "VMfin",
        "VMger", "VMinf", "VSadj", "VSfin", "VSger", "VSinf"};

    public static String[] getNounTags() {
        return NounTags;
    }

    public static void setNounTags(String[] nounTags) {
        NounTags = nounTags;
    }

    public static String[] getVerbTags() {
        return VerbTags;
    }

    public static void setVerbTags(String[] verbTags) {
        VerbTags = verbTags;
    }

    public static String[] getAdditionalTags() {
        return AdditionalTags;
    }

    public static void setAdditionalTags(String[] additionalTags) {
        AdditionalTags = additionalTags;
    }

    public static String[] getPronTags() {
        return PronTags;
    }

    public static void setPronTags(String[] pronTags) {
        PronTags = pronTags;
    }
    public static String[] AdditionalTags = {"JJ", "JJR", "JJS", "RB", "RBR", "RBS", "ADJ", "ADV"};
    public static String[] PronTags = {"PP", "PRP", "PPX"};
    static boolean alwaysRun;

    public String run() {
        return "";
    }

    ;



	public PosTagger() {
        super(null);
        Logger.log("***********Initiating PosTagger***************");
    }

    public void setParameters(String lang, String tagName, String exePath, String input, String output) {
        Logger.log("PosTagger parameters: type=" + lang + " name:" + tagName);
        Logger.log("Executable: " + exePath);
        Logger.log("Input: " + input);
        Logger.log("Output: " + output);
        name = tagName;
        path = exePath;
        this.lang = lang;
        this.input = input;
        this.output = output;
    }

    public PosTagger(String lang, String tagName, String exePath, String input, String output, ResourceProcessor posProc) {
        super(posProc);
        name = tagName;
        path = exePath;
        this.lang = lang;
        this.input = input;
        this.output = output;

    }

    public static void ForceRun(boolean value) {
        alwaysRun = value;
    }

    public static boolean isVerb(String tag) {
        // 07/03/2012: Reimplemented by Mariano
        return tag.length() > 0 ? (tag.charAt(0) == 'V') : false;

//                for (String crtTag:VerbTags)
//			if (tag.equals(crtTag))
//				return true;
//		return false;
    }

    public static boolean isNoun(String tag) {
        // 07/03/2012: Reimplemented by Mariano
        return tag.length() > 0 ? (tag.charAt(0) == 'N') : false;
//                for (String crtTag:NounTags)
//			if (tag.equals(crtTag))
//				return true;
//		return false;
    }

    public static boolean isAdditional(String tag) {
        for (String crtTag : AdditionalTags) {
            if (tag.equals(crtTag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPronoun(String tag) {
        for (String crtTag : PronTags) {
            if (tag.equals(crtTag)) {
                return true;
            }
        }
        return false;
    }

    public static String getXPOS() {
        return XPOS;
    }
}
