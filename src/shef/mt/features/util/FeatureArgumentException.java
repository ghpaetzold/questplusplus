package shef.mt.features.util;

public class FeatureArgumentException extends Exception {

    String message;

    public FeatureArgumentException() {
        super();
        message = "argument parsing exception";
    }

    public FeatureArgumentException(String err) {
        super(err);
        message = err;
    }

    public String getError() {
        return message;
    }
}
