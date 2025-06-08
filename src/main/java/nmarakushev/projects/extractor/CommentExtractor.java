package nmarakushev.projects.extractor;

import nmarakushev.projects.entity.Comment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


public class CommentExtractor {
    private static final Pattern SINGLE_LINE_PATTERN = Pattern.compile("//.*");
    private static final Pattern MULTI_LINE_START_PATTERN = Pattern.compile("/\\*.*");
    private static final Pattern MULTI_LINE_END_PATTERN = Pattern.compile(".*\\*/");
    private static final Pattern JAVADOC_START_PATTERN = Pattern.compile("/\\*\\*.*");

    public List<Comment> extractComments(File javaFile) throws IOException {
        Objects.requireNonNull(javaFile, "File cannot be null");

        if (!javaFile.getName().endsWith(".java")) {
            throw new IllegalArgumentException("Only .java files are supported");
        }

        List<String> lines = Files.readAllLines(javaFile.toPath());
        List<Comment> comments = new ArrayList<>();
        MultiLineCommentContext context = new MultiLineCommentContext();

        IntStream.range(0, lines.size())
                .forEach(i -> processLine(lines.get(i).trim(), i + 1, comments, context));

        completeMultiLineCommentIfExists(comments, context);

        return comments;
    }

    private void processLine(String line, int lineNumber,
                             List<Comment> comments, MultiLineCommentContext context) {
        if (context.isActive()) {
            processMultiLineContinuation(line, lineNumber, comments, context);
        } else {
            processNewComment(line, lineNumber, comments, context);
        }
    }

    private void processNewComment(String line, int lineNumber,
                                   List<Comment> comments, MultiLineCommentContext context) {
        if (line.startsWith("//")) {
            addSingleLineComment(line.substring(2), lineNumber, comments);
        } else if (line.startsWith("/*")) {
            context.start(line, lineNumber);
            if (line.contains("*/")) {
                completeMultiLineComment(line, lineNumber, comments, context);
            }
        }
    }

    private void processMultiLineContinuation(String line, int lineNumber,
                                              List<Comment> comments, MultiLineCommentContext context) {
        context.appendLine(line);
        if (line.contains("/*")) {
            context.incrementNesting();
        }
        if (line.contains("*/")) {
            if (context.decrementAndCheckComplete()) {
                completeMultiLineComment(line, lineNumber, comments, context);
            }
        }
    }

    private void completeMultiLineComment(String line, int lineNumber,
                                          List<Comment> comments, MultiLineCommentContext context) {
        String commentText = context.complete(line).trim();
        if (!commentText.isEmpty()) {
            comments.add(new Comment(
                    commentText,
                    determineCommentType(context.originalStart()),
                    context.startLine()
            ));
        }
    }

    private void completeMultiLineCommentIfExists(List<Comment> comments,
                                                  MultiLineCommentContext context) {
        if (context.isActive()) {
            String commentText = context.getCurrentText().trim();
            if (!commentText.isEmpty()) {
                comments.add(new Comment(
                        commentText,
                        determineCommentType(context.originalStart()),
                        context.startLine()
                ));
            }
        }
    }

    private void addSingleLineComment(String text, int lineNumber, List<Comment> comments) {
        String trimmed = text.trim();
        if (!trimmed.isEmpty()) {
            comments.add(new Comment(
                    trimmed,
                    Comment.CommentType.SINGLE_LINE,
                    lineNumber
            ));
        }
    }

    private Comment.CommentType determineCommentType(String startLine) {
        return startLine.startsWith("/**") ? Comment.CommentType.DOCUMENTATION
                : Comment.CommentType.MULTI_LINE;
    }

    private static class MultiLineCommentContext {
        private boolean active;
        private StringBuilder content;
        private int startLine;
        private int nestingLevel;
        private String originalStart;

        boolean isActive() {
            return active;
        }

        void start(String startLine, int lineNumber) {
            this.active = true;
            this.content = new StringBuilder(startLine.substring(2));
            this.startLine = lineNumber;
            this.nestingLevel = 0;
            this.originalStart = startLine;
        }

        void appendLine(String line) {
            content.append("\n").append(line);
        }

        String complete(String endLine) {
            String text = content.substring(0, content.length() - endLine.length()
                    + endLine.indexOf("*/"));
            reset();
            return text;
        }

        String getCurrentText() {
            return content.toString();
        }

        int startLine() {
            return startLine;
        }

        String originalStart() {
            return originalStart;
        }

        void incrementNesting() {
            nestingLevel++;
        }

        boolean decrementAndCheckComplete() {
            nestingLevel--;
            return nestingLevel < 0;
        }

        private void reset() {
            active = false;
            content = null;
            startLine = -1;
            nestingLevel = 0;
            originalStart = null;
        }
    }
}