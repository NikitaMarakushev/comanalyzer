package nmarakushev.projects.entity;

import java.util.Objects;

public record Comment(String text, CommentType type, int lineNumber) {
    public Comment {
        Objects.requireNonNull(text, "Comment text cannot be null");
        Objects.requireNonNull(type, "Comment type cannot be null");

        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must be positive (>= 1)");
        }
    }

    public enum CommentType {
        SINGLE_LINE,

        MULTI_LINE,

        DOCUMENTATION
    }

    public boolean isDocumentation() {
        return type == CommentType.DOCUMENTATION;
    }

    public int lineCount() {
        return type == CommentType.SINGLE_LINE
                ? 1
                : (int) text.lines().count();
    }

    @Override
    public String toString() {
        return String.format(
                "Comment[type=%s, lines=%d, line=%d, text=%s...]",
                type,
                lineCount(),
                lineNumber,
                text.substring(0, Math.min(20, text.length()))
        );
    }
}