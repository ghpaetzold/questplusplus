package shef.mt.tools.jrouge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class RougeSeeFormatSerializer implements ISerializer {

    public static Boolean DEBUG = false;

    @Override
    public void serialize(Map<IRougeSummaryModel, Set<IRougeSummaryModel>> systemToModelsMapping, File outputDirectory) {
        if (outputDirectory == null) {
            throw new IllegalArgumentException("Output directory can not be null");
        }

        prepOutputFolders(outputDirectory);

        for (IRougeSummaryModel system : systemToModelsMapping.keySet()) {
            writeToFile(new File(outputDirectory + "/" + "systems", system.getSourceFile().getName() + ".html"), createOutput(system));
        }

        for (Set<IRougeSummaryModel> models : systemToModelsMapping.values()) {
            for (IRougeSummaryModel model : models) {
                writeToFile(new File(outputDirectory + "/" + "models", model.getSourceFile().getName() + ".html"), createOutput(model));
            }

        }

        writeToFile(new File(outputDirectory, "settings.xml"), createSettingXML(systemToModelsMapping));
    }

    @Override
    public Map<IRougeSummaryModel, Set<IRougeSummaryModel>> prepareForRouge(File inputDirectory, File goldStandardDirectory) {
        if (inputDirectory == null) {
            throw new IllegalArgumentException("Input directory can not be null");
        }

        Map<File, List<File>> fileMapping = mapSystemToModelFiles(inputDirectory, goldStandardDirectory);
        Map<IRougeSummaryModel, Set<IRougeSummaryModel>> systemToModelsMapping = createRougeSummaryModels(fileMapping);

        return systemToModelsMapping;
    }

    private void writeToFile(File outputFile, String data) {
        try {
            outputFile.createNewFile();
            FileOutputStream is = new FileOutputStream(outputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            Writer w = new BufferedWriter(osw);
            w.write(data);
            w.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<File, List<File>> mapSystemToModelFiles(File inputDirectory, File goldStandardDirectory) {
        File modelsFolder = goldStandardDirectory;

        if (modelsFolder == null) {
            System.out.println("models folder not found in the location specified: " + inputDirectory);
            return null;
        }
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File arg0) {
                return arg0.isFile();
            }
        };

        List<File> systemFiles = Arrays.asList(inputDirectory.listFiles(fileFilter));
        List<File> modelFiles = Arrays.asList(modelsFolder.listFiles(fileFilter));

        Map<File, List<File>> systemToModelsMapping = new HashMap<File, List<File>>();

        for (File f : systemFiles) {
            if (DEBUG) {
                System.out.println("Matching models for: " + f);
            }
            String systemFileName = FilenameUtils.removeExtension(f.getName());
            if (DEBUG) {
                System.out.println("\tSystem ID: " + systemFileName);
            }

            List<File> matchedModels = new ArrayList<File>();

            for (File model : modelFiles) {
                if (FilenameUtils.removeExtension(model.getName()).equals(systemFileName)) {
                    if (DEBUG) {
                        System.out.println("\t\tModel file: " + model + " Model ID: " + FilenameUtils.removeExtension(model.getName()));
                    }
                    matchedModels.add(model);
                }
            }

            systemToModelsMapping.put(f, matchedModels);
        }

        return systemToModelsMapping;
    }

    private String createSettingXML(Map<IRougeSummaryModel, Set<IRougeSummaryModel>> systemToModelsMapping) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ROUGE_EVAL version=\"1.5.5\">\n");

        int id = 1;

        for (IRougeSummaryModel system : systemToModelsMapping.keySet()) {
            sb.append("<EVAL ID=\"" + id + "\">\n");
            sb.append("<PEER-ROOT>systems</PEER-ROOT>\n");
            sb.append("<MODEL-ROOT>models</MODEL-ROOT>\n");
            sb.append("<INPUT-FORMAT TYPE=\"SEE\"></INPUT-FORMAT>\n");
            int pid = 1;
            sb.append("<PEERS>\n");
            sb.append("<P ID=\"" + pid++ + "\">" + system.getSourceFile().getName() + ".html" + "</P>\n");
            sb.append("</PEERS>\n");

            int mid = 1;
            sb.append("<MODELS>\n");
            for (IRougeSummaryModel model : systemToModelsMapping.get(system)) {
                sb.append("<M ID=\"" + mid++ + "\">" + model.getSourceFile().getName() + ".html" + "</M>\n");
            }
            sb.append("</MODELS>\n");
            sb.append("</EVAL>\n");
            id++;
        }
        sb.append("</ROUGE_EVAL>");

        return sb.toString();
    }

    private String createOutput(IRougeSummaryModel summary) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>" + summary.getSourceFile().getName() + ".html" + "</title>\n");
        sb.append("</head>\n");
        sb.append("<body bgcolor=\"white\">\n");

        int id = 1;
        String title = summary.getTitle();
        sb.append("<a name=\"" + id + "\">[" + id + "]</a> <a href=\"#" + id + "\" id=" + id + ">");
        sb.append(title);
        sb.append("</a>\n");
        id++;
        for (String sentence : summary.getSentences()) {
            String sent = sentence.toString();
            sb.append("<a name=\"" + id + "\">[" + id + "]</a> <a href=\"#" + id + "\" id=" + id + ">");
            sb.append(sent);
            sb.append("</a>\n");
            id++;
        }
        sb.append("</body>\n");
        sb.append("</html>");

        return sb.toString();
    }

    private boolean prepOutputFolders(File output) {
        String s = "y";
        if (output.exists() && output.isDirectory()) {
            System.out.println("WARNING: Output folder exists, proceeding with the process will delete the contents of the folder");
            System.out.print("[y/n]");
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            try {
                s = bufferRead.readLine();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if (s.equals("y")) {
                try {
                    FileUtils.deleteDirectory(output);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                System.out.println("Process canceled.");
                return false;
            }
        }

        output.mkdir();

        File outputSystemFolder = new File(output, "systems");
        File outputModelFolder = new File(output, "models");
        outputModelFolder.mkdir();
        outputSystemFolder.mkdir();
        return true;
    }

    private Map<IRougeSummaryModel, Set<IRougeSummaryModel>> createRougeSummaryModels(Map<File, List<File>> fileMapping) {
        Map<IRougeSummaryModel, Set<IRougeSummaryModel>> mapping = new HashMap<IRougeSummaryModel, Set<IRougeSummaryModel>>();

        Properties props = new Properties();
        props.put("annotators", "tokenize ,ssplit");

        StanfordCoreNLP stanford = new StanfordCoreNLP(props);

        for (File modelFile : fileMapping.keySet()) {
            RougeSummaryModel model = parseText(modelFile, stanford);

            Set<IRougeSummaryModel> listOfSystemFiles = new HashSet<IRougeSummaryModel>();
            for (File systemFile : fileMapping.get(modelFile)) {
                RougeSummaryModel system = parseText(systemFile, stanford);
                listOfSystemFiles.add((IRougeSummaryModel) system);
            }

            mapping.put((IRougeSummaryModel) model, listOfSystemFiles);
        }

        return mapping;
    }

    private RougeSummaryModel parseText(File source, StanfordCoreNLP stanford) {
        String text = readTextFromFile(source);

        RougeSummaryModel summaryModel = new RougeSummaryModel(source);

        Annotation annotation = new Annotation(text);
        stanford.annotate(annotation);
        int id = 0;
        for (CoreMap sentence : annotation.get(SentencesAnnotation.class)) {
            String sent = sentence.toString();
            if (id == 0) {
                summaryModel.setTitle(sent);
            } else {
                summaryModel.addSentence(sent);
            }
            id++;
        }

        return summaryModel;
    }

    private String readTextFromFile(File model) {
        byte[] encoded = null;

        try {
            encoded = Files.readAllBytes(Paths.get(model.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String text = Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString().replaceAll("[\n|\r|\r\n]+", " ").trim();
        text = text.replaceAll("[\\u200C\\u200D\\u066C\\u202B]", "");
        return text;
    }
}
