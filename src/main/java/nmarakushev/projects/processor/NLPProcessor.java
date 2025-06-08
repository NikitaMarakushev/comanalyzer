package nmarakushev.projects.processor;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class NLPProcessor {
    private static final String MODEL_PATH = "/models/en-token.bin";
    private static final String IS_STOP_WORD_REGEX = "a|the|is|are|this|that|and|or|but";
    private static final String IS_MEANINGFUL_TOKEN_REGEX = "[a-z]+";

    private final Tokenizer tokenizer;

    public NLPProcessor() throws IOException {
        InputStream modelIn = getClass().getResourceAsStream(MODEL_PATH);
        assert modelIn != null;
        TokenizerModel model = new TokenizerModel(modelIn);
        this.tokenizer = new TokenizerME(model);
    }

    public Set<String> extractKeyTerms(String comment) {
        String[] tokens = tokenizer.tokenize(comment);
        Set<String> keyTerms = new HashSet<>();

        for (String token : tokens) {
            String lowerToken = token.toLowerCase();
            if (!isStopWord(lowerToken) && isMeaningfulToken(lowerToken)) {
                keyTerms.add(lowerToken);
            }
        }

        return keyTerms;
    }

    private boolean isStopWord(String word) {
        return word.matches(IS_STOP_WORD_REGEX);
    }

    private boolean isMeaningfulToken(String word) {
        return word.length() > 3 && word.matches(IS_MEANINGFUL_TOKEN_REGEX);
    }
}