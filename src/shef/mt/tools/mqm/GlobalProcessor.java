package shef.mt.tools.mqm;

/**
 * Interface for classes that need to collecte statistcs from all the input corpus before processing sentence by sentence
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public interface GlobalProcessor {
    public void globalProcessing(Context context);
}
