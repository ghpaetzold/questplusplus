package shef.mt.features.impl.binqe;

import shef.mt.tools.jrouge.ScoreType;
import java.util.Map;
import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.jrouge.RougeN;

public class BinQEFeature1021 extends Feature {

    public BinQEFeature1021() {
        setIndex(1018);
        setDescription("3-gram ROUGE recall score.");
        addResource("rouge-n");
    }

    public void run(Sentence source, Sentence target) {
        //Get ROUGE object:
        RougeN rouge = (RougeN) target.getValue("rouge-n");
        
        //Set ROUGE n-gram size:
        Map<ScoreType, Float> scores = rouge.getROUGEScore(3);
        
        //Set value:
        setValue((float)scores.get(ScoreType.R));
    }
}
