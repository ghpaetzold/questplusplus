package shef.mt.features.util;


/*
 * This class models the morphological information attached to a word
 */
public class PronMorph {

    String text;
    int position;
    String pos = null;
    String gen = null;
    String num = null;
    String pers = null;
    String compPos;

    public PronMorph(String text) {
        this.text = text;
    }

    public PronMorph(String text, String compPos) {
        setText(text);
        setCompoundPos(compPos);
    }

    public String getText() {
        return text;
    }

    public String getGen() {
        return gen;
    }

    public String getNum() {
        return num;
    }

    public String getPers() {
        return pers;
    }

    public String getPos() {
        return pos;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public void setPers(String pers) {
        this.pers = pers;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setCompoundPos(String pos) {
        this.compPos = pos;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void print() {
        System.out.println(getText() + "\t" + compPos + "\t" + "type=" + getPos() + "\t" + "pers=" + getPers() + "\tgen=" + getGen() + "\tnum=" + getNum());
    }

    public boolean matches(String pron) {
        if ((isPossessive(pron) && !getPos().equals("POSS_PRON")) || (isPers(pron) && !getPos().equals("PRON"))) {
            //            System.out.println("not same type");
            return false;
        }


        String pers = getPers(pron);
        String gen = getGen(pron);
        String num = getNum(pron);

        if (!pers.equals(getPers())) {
//		System.out.println("pers doesnt match");
            return false;

        }
        if (!num.equals(getNum()) || (getNum().equals("D") && num.equals("S"))) {
//				System.out.println("num doesnt match");
            return false;
        }
        if ((gen == null) || getGen() == null) {
            return true;
        }
        if (!gen.equals(getGen())) {
            //                   		System.out.println("gen doesnt match");
            return false;
        }


        return true;
    }

    public boolean isPers(String pron) {
        String lowPron = pron.toLowerCase();
        return (pron.equals("I") || pron.equals("you") || pron.equals("he")
                || pron.equals("she") || pron.equals("it") || pron.equals("they")
                || pron.equals("me") || pron.equals("her") || pron.equals("him")
                || pron.equals("them"));
    }

    public boolean isPossessive(String pron) {
        return (pron.equals("my") || pron.equals("your") || pron.equals("his") || pron.equals("her")
                || pron.equals("its") || pron.equals("their"));
    }

    public String getPers(String pron) {
        if (pron.equals("I") || pron.equals("me") || pron.equals("my")) {
            return "1";
        }
        if (pron.equals("you") || pron.equals("your")) {
            return "2";
        }
        return "3";
    }

    public String getGen(String pron) {
        if (pron.equals("he") || pron.equals("him") || pron.equals("his")) {
            return "M";
        } else if (pron.equals("she") || pron.equals("her")) {
            return "F";
        }
        return null; //unspecified
    }

    public String getNum(String pron) {
        if (pron.equals("they") || pron.equals("their") || pron.equals("them")) {
            return "P";
        }
        return "S";
    }

    public boolean isPossessive() {
        return (getPos().equals("POSS_PRON"));
    }

    public boolean isDOPronoun() {
        return (getPos().equals("DO"));
    }
}
