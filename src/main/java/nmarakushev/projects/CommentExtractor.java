package nmarakushev.projects;

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

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (inMultiLineComment) {
                if (line.contains("*/")) {
                    multiLineComment.append(line.substring(0, line.indexOf("*/")));
                    comments.add(new Comment(
                            multiLineComment.toString(),
                            Comment.CommentType.MULTI_LINE,
                            multiLineStart
                    ));
                    inMultiLineComment = false;
                } else {
                    multiLineComment.append(line).append("\n");
                }
            } else if (line.startsWith("/*")) {
                inMultiLineComment = true;
                multiLineStart = i + 1;
                multiLineComment = new StringBuilder(line.substring(2));
            }

            else if (line.startsWith("//")) {
                comments.add(new Comment(
                        line.substring(2),
                        Comment.CommentType.SINGLE_LINE,
                        i + 1
                ));
            }
        }

        return comments;
    }
}