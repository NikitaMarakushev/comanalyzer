package nmarakushev.projects.context;

import java.util.Objects;
import java.util.Set;

public record CodeContext(String nearbyCode, Set<String> variables, Set<String> methods) {
  public CodeContext {
    nearbyCode = nearbyCode == null ? "" : nearbyCode;
    variables = Set.copyOf(Objects.requireNonNull(variables, "Variables set cannot be null"));
    methods = Set.copyOf(Objects.requireNonNull(methods, "Methods set cannot be null"));
  }

  public boolean containsAnyTerm(Set<String> terms) {
    return terms.stream().anyMatch(term -> variables.contains(term) || methods.contains(term));
  }

  @Override
  public String toString() {
    return String.format(
        "CodeContext[nearbyCodeLines=%d, variables=%d, methods=%d]",
        nearbyCode.lines().count(), variables.size(), methods.size());
  }
}
