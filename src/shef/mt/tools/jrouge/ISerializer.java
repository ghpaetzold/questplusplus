package shef.mt.tools.jrouge;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * This interface represents the minimal contract that has to be implemented by
 * any<br>
 * class that has to serialize and/or prepare file to a format acceptable to the
 * <br>
 * original Rouge script. <br>
 * As an example see the {@link RougeSeeFormatSerializer}
 *
 * @author nocgod
 */
public interface ISerializer {

    /**
     * Writes to files the data passed to it
     *
     * @param data The data to write
     * @param output The path to the output folder
     */
    public void serialize(Map<IRougeSummaryModel, Set<IRougeSummaryModel>> data, File output);

    /**
     * Reads the files in the input directory and creates the needed mapping of
     * system to models <br>
     * ready for the rouge implementation to rate/evaluate and/or serialize to
     * be consumed by<br>
     * this class' {@link #serialize(Map, File)} method.
     * <p>
     * The input folder should have a sub-folder called "gold" that will contain
     * the gold standard<br>
     * of the files in the input folder.
     * <p>
     * The gold standard files are matched to the system/peer using the file
     * names, hence a text<br>
     * named A.txt should have gold standard files gold/A.1.txt gold/A.2.txt and
     * so forth.
     *
     * @param inputDirectory The directory that holds the input
     * @return The mapping.
     */
    public Map<IRougeSummaryModel, Set<IRougeSummaryModel>> prepareForRouge(File inputDirectory, File goldStandardDirectory);
}
