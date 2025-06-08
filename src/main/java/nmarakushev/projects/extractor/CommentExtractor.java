package nmarakushev.projects.extractor;

import nmarakushev.projects.entity.Comment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CommentExtractor {
    public List<Comment> extractComments(File javaFile) throws IOException {
        List<Comment> comments = new ArrayList<>();
        List<String> lines = Files.readAllLines(javaFile.toPath());

        boolean inMultiLineComment = false;
        StringBuilder multiLineComment = new StringBuilder();
        int multiLineStart = -1;
        int nestedLevel = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (inMultiLineComment) {
                if (line.contains("/*")) {
                    nestedLevel++;
                }
                if (line.contains("*/")) {
                    if (nestedLevel > 0) {
                        nestedLevel--;
                    } else {
                        multiLineComment.append(line.substring(0, line.indexOf("*/")));
                        String commentText = multiLineComment.toString().trim();
                        if (!commentText.isEmpty()) {
                            comments.add(new Comment(
                                    commentText,
                                    Comment.CommentType.MULTI_LINE,
                                    multiLineStart
                            ));
                        }
                        inMultiLineComment = false;
                    }
                } else {
                    multiLineComment.append(line).append("\n");
                }
            } else if (line.startsWith("/*")) {
                inMultiLineComment = true;
                multiLineStart = i + 1;
                multiLineComment = new StringBuilder(line.substring(2));
                if (line.contains("*/")) {
                    String commentText = line.substring(2, line.indexOf("*/")).trim();
                    if (!commentText.isEmpty()) {
                        comments.add(new Comment(
                                commentText,
                                Comment.CommentType.MULTI_LINE,
                                multiLineStart
                        ));
                    }
                    inMultiLineComment = false;
                }
            } else if (line.startsWith("//")) {
                String commentText = line.substring(2).trim();
                if (!commentText.isEmpty()) {
                    comments.add(new Comment(
                            commentText,
                            Comment.CommentType.SINGLE_LINE,
                            i + 1
                    ));
                }
            }
        }

        if (inMultiLineComment) {
            String commentText = multiLineComment.toString().trim();
            if (!commentText.isEmpty()) {
                comments.add(new Comment(
                        commentText,
                        Comment.CommentType.MULTI_LINE,
                        multiLineStart
                ));
            }
        }

        return comments;
    }
}