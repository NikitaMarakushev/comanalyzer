package nmarakushev.projects.analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;

public class CodeAnalyzer {
  private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\n");
  private static final Pattern VARIABLE_PATTERN =
      Pattern.compile("\\b(?:var|int|String|boolean|final\\s+\\w+)\\s+(\\w+)");
  private static final Pattern METHOD_PATTERN =
      Pattern.compile("\\b(?:public|private|protected|static|final)\\s+[\\w<>]+\\s+(\\w+)\\s*\\(");

  public CodeContext getCodeContext(String fileContent, Comment comment) {
    if (fileContent == null || fileContent.isEmpty()) {
      return new CodeContext("", Set.of(), Set.of());
    }
    String nearbyCode = extractNearbyCode(fileContent, comment.lineNumber());
    Set<String> variables = extractVariables(nearbyCode);
    Set<String> methods = extractMethods(nearbyCode);
    return new CodeContext(nearbyCode, variables, methods);
  }

  private String extractNearbyCode(String content, int lineNumber) {
    String[] lines = LINE_BREAK_PATTERN.split(content);
    int start = Math.max(0, lineNumber - 3);
    int end = Math.min(lines.length, lineNumber + 3);
    return String.join("\n", Arrays.copyOfRange(lines, start, end));
  }

  private Set<String> extractVariables(String code) {
    Set<String> variables = new HashSet<>();
    Matcher m = VARIABLE_PATTERN.matcher(code);
    while (m.find()) {
      variables.add(m.group(1));
    }
    return variables;
  }

  private Set<String> extractMethods(String code) {
    Set<String> methods = new HashSet<>();
    Matcher m = METHOD_PATTERN.matcher(code);
    while (m.find()) {
      methods.add(m.group(1));
    }
    return methods;
  }
}
