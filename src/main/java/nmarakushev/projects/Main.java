package nmarakushev.projects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import nmarakushev.projects.analyzer.CodeAnalyzer;
import nmarakushev.projects.analyzer.RelevanceAnalyzer;
import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;
import nmarakushev.projects.extractor.CommentExtractor;
import nmarakushev.projects.processor.NLPProcessor;

public class Main {
  private static final double REDUNDANCY_THRESHOLD = 0.7;

  public static void main(String[] args) {
    try {
      Path filePath = getFilePath(args);
      analyzeCommentsInFile(filePath);
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      printUsage();
      System.exit(1);
    }
  }

  private static void analyzeCommentsInFile(Path filePath) throws IOException {
    try (NLPProcessor nlpProcessor = new NLPProcessor()) {
      CommentExtractor extractor = new CommentExtractor();
      CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
      RelevanceAnalyzer relevanceAnalyzer =
          new RelevanceAnalyzer(nlpProcessor, REDUNDANCY_THRESHOLD);

      String fileContent = Files.readString(filePath);
      List<Comment> comments = extractor.extractComments(filePath.toFile());

      comments.forEach(
          comment -> processComment(comment, fileContent, codeAnalyzer, relevanceAnalyzer));
    }
  }

  private static void processComment(
      Comment comment,
      String fileContent,
      CodeAnalyzer codeAnalyzer,
      RelevanceAnalyzer relevanceAnalyzer) {
    CodeContext context = codeAnalyzer.getCodeContext(fileContent, comment);
    double usefulness = relevanceAnalyzer.calculateUsefulness(comment, context);
    boolean isRedundant = relevanceAnalyzer.isRedundant(comment, context);

    // Форматированный вывод
    System.out.printf(
        """
            ========================================
            Line: %d
            Type: %s
            Comment: '%s...'
            Usefulness score: %.2f
            Is redundant: %s
            Context: %s
            %n""",
        comment.lineNumber(),
        comment.type(),
        comment.text().substring(0, Math.min(30, comment.text().length())),
        usefulness,
        isRedundant,
        context);
  }

  private static Path getFilePath(String[] args) {
    if (args.length != 1) {
      throw new IllegalArgumentException("Please provide exactly one file path");
    }

    Path path = Path.of(args[0]);
    if (!Files.exists(path)) {
      throw new IllegalArgumentException("File does not exist: " + path);
    }
    if (!path.toString().endsWith(".java")) {
      throw new IllegalArgumentException("Only .java files are supported");
    }

    return path;
  }

  private static void printUsage() {
    System.out.println(
        """
            Usage:
              java -jar comment-analyzer.jar <path-to-java-file>

            Example:
              java -jar comment-analyzer.jar src/main/java/Example.java
            """);
  }
}
