package nmarakushev.projects.entity;

public record Comment(String text, Comment.CommentType type, int lineNumber) {

    public enum CommentType {
        SINGLE_LINE,
        MULTI_LINE
    }
}
