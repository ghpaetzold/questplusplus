package shef.mt.tools.mqm;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/12/13
 */
public class Context {
    private String targetFilePath = null;
    private String sourceFilePath = null;

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void checkContext() {
        assert targetFilePath != null;
        assert sourceFilePath != null;
    }
}
