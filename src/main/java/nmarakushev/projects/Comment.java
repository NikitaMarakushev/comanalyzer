package nmarakushev.projects;

public record Comment(String text, nmarakushev.projects.Comment.CommentType type, int lineNumber) {

    public enum CommentType {
        SINGLE_LINE,
        MULTI_LINE
    }
}
