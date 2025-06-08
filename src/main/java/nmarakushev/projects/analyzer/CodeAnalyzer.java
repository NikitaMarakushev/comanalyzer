package nmarakushev.projects.analyzer;

import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzer {
    private static final String LINE_BREAK_CHARACTER_REGEX = "\n";
    private static final String EXTRACT_VARIABLES_REGEX = "\\b(?:var|int|String|boolean)\\s+(\\w+)";
    private static final String EXTRACT_METHODS_REGEX = "\\b(?:public|private|protected)\\s+\\w+\\s+(\\w+)\\s*\\(";

    public CodeContext getCodeContext(String fileContent, Comment comment) {
        String nearbyCode = extractNearbyCode(fileContent, comment.lineNumber());
        Set<String> variables = extractVariables(nearbyCode);
        Set<String> methods = extractMethods(nearbyCode);
        return new CodeContext(nearbyCode, variables, methods);
    }

    private String extractNearbyCode(String content, int lineNumber) {
        String[] lines = content.split(LINE_BREAK_CHARACTER_REGEX);
        int start = Math.max(0, lineNumber - 3);
        int end = Math.min(lines.length, lineNumber + 3);
        StringBuilder nearbyCode = new StringBuilder();

        for (int i = start; i < end; i++) {
            nearbyCode.append(lines[i]).append("\n");
        }

        return nearbyCode.toString();
    }

    private Set<String> extractVariables(String code) {
        Set<String> variables = new HashSet<>();
        Pattern varPattern = Pattern.compile(EXTRACT_VARIABLES_REGEX);
        Matcher m = varPattern.matcher(code);
        while (m.find()) {
            variables.add(m.group(1));
        }
        return variables;
    }

    private Set<String> extractMethods(String code) {
        Set<String> methods = new HashSet<>();
        Pattern methodPattern = Pattern.compile(EXTRACT_METHODS_REGEX);
        Matcher m = methodPattern.matcher(code);
        while (m.find()) {
            methods.add(m.group(1));
        }
        return methods;
    }
}