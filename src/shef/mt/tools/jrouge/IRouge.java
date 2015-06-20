package shef.mt.tools.jrouge;

import java.util.Map;

/**
 * This interface represents the minimal contract a class should obey in order
 * to be an evaluator
 *
 * @author nocgod
 */
public interface IRouge {

    /**
     * Method runs the evaluation and returns the resulting score
     *
     * @return The score
     */
    Map<ScoreType, Float> getROUGEScore(int n);
}
