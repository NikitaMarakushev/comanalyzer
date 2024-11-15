package nmarakushev.projects;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        String pathToSentenceModel = "models\\opennlp-ru-ud-gsd-sentence-1.1-2.4.0.bin";
        String pathToTokensModel = "models\\opennlp-ru-ud-gsd-tokens-1.1-2.4.0.bin";
        System.out.println(System.getProperty("user.dir") + "\n");

        File modelFile = new File(pathToSentenceModel);
        System.out.println(modelFile.lastModified());

        try (InputStream modelIn = new FileInputStream(modelFile)) {
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            String[] sentences = sentenceDetector.sentDetect(" First sentence. Second sentence. ");
            for (String var : sentences) {
                System.out.println(var);
            }
        } catch (Exception e) {
            System.out.println("Модель не открылась \n");
            System.out.println(e.getMessage());
        }
    }
}