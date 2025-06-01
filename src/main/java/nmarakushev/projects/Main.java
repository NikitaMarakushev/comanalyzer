package nmarakushev.projects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File javaFile = new File("Example.java");
        String fileContent = Files.readString(javaFile.toPath());

        CommentExtractor extractor = new CommentExtractor();
        List<Comment> comments = extractor.extractComments(javaFile);

        CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
        NLPProcessor nlpProcessor = new NLPProcessor();
        RelevanceAnalyzer relevanceAnalyzer = new RelevanceAnalyzer(nlpProcessor);

        for (Comment comment : comments) {
            CodeContext context = codeAnalyzer.getCodeContext(fileContent, comment);
            double usefulness = relevanceAnalyzer.calculateUsefulness(comment, context);
            boolean isRedundant = relevanceAnalyzer.isRedundant(comment, context);

            System.out.println("Line " + comment.lineNumber() + ";");
            System.out.println("Comment: " + comment.text());
            System.out.println("Usefulness score: " + usefulness);
            System.out.println("Is redundant: " + isRedundant);
        }
    }
}