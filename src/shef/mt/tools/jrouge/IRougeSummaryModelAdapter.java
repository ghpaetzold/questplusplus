package shef.mt.tools.jrouge;

/**
 * This interface represents the minimal contract a class must implement so
 * that<br>
 * objects of the class could be used in Rouge evaluations and be used in
 * tools<br>
 * implemented in this project.
 * <p>
 * This interface inherits all the methods from the {@link IRougeSummaryModel}
 * so<br>
 * the adapter must implement all its methods.
 *
 * @author nocgod
 * @param <T> The type of object we are building out adapter from
 */
public interface IRougeSummaryModelAdapter<T> extends IRougeSummaryModel {

    /**
     * Get the adaptee in case it is needed
     *
     * @return The adaptee object
     */
    T getAdaptee();
}
