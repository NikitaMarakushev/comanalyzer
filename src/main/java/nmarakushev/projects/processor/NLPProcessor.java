package nmarakushev.projects.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class NLPProcessor implements AutoCloseable {
  private static final String DEFAULT_MODEL_PATH = "/models/en-token.bin";
  private static final Set<String> STOP_WORDS =
      Set.of(
          "a", "an", "the", "is", "are", "this", "that", "and", "or", "but", "of", "to", "in",
          "it");

  private final Tokenizer tokenizer;
  private final PorterStemmer stemmer;
  private final Set<String> codeKeywords;

  public NLPProcessor() throws IOException {
    this(DEFAULT_MODEL_PATH);
  }

  public NLPProcessor(String modelPath) throws IOException {
    this(loadModel(modelPath), loadCodeKeywords());
  }

  public NLPProcessor(TokenizerModel model, Set<String> codeKeywords) {
    this.tokenizer = new TokenizerME(model);
    this.stemmer = new PorterStemmer();
    this.codeKeywords = Collections.unmodifiableSet(new HashSet<>(codeKeywords));
  }

  public Set<String> extractKeyTerms(String comment) {
    if (comment == null || comment.isBlank()) {
      return Collections.emptySet();
    }

    return Arrays.stream(tokenizer.tokenize(comment))
        .map(String::toLowerCase)
        .filter(this::isMeaningfulToken)
        .map(this::normalizeToken)
        .collect(Collectors.toSet());
  }

  @Override
  public void close() {
    // Для будущего расширения, если токенизатор будет требовать очистки
  }

  private static TokenizerModel loadModel(String modelPath) throws IOException {
    try (InputStream modelIn = NLPProcessor.class.getResourceAsStream(modelPath)) {
      if (modelIn == null) {
        throw new IOException("Model file not found: " + modelPath);
      }
      return new TokenizerModel(modelIn);
    }
  }

  private static Set<String> loadCodeKeywords() throws IOException {
    return Set.of(
        "if",
        "else",
        "for",
        "while",
        "return",
        "class",
        "function",
        "var",
        "let",
        "const",
        "new",
        "this");
  }

  private boolean isMeaningfulToken(String token) {
    return token.length() > 2
        && !STOP_WORDS.contains(token)
        && !codeKeywords.contains(token)
        && token.matches("[a-z]+");
  }

  private String normalizeToken(String token) {
    return stemmer.stem(token);
  }
}
