package nmarakushev.projects;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
                String n = "nnn";
            }

            // https://opennlp.apache.org/docs/2.5.0/manual/opennlp.html#tools.namefind.training
            TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec());
            File trainingFile = new File(pathToSentenceModel);
            ObjectStream<String> lineStream =
                    new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainingFile), StandardCharsets.UTF_8);


        } catch (Exception e) {
            System.out.println("Модель не открылась \n");
            System.out.println(e.getMessage());
        }
    }
}