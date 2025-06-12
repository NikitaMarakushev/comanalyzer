package nmarakushev.projects.analyzer;

import java.util.HashSet;
import java.util.Set;
import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;
import nmarakushev.projects.processor.NLPProcessor;

public class RelevanceAnalyzer {
  private final NLPProcessor nlpProcessor;
  private final double redundancyThreshold;

  private static final double REDUNDANCY_THRESHOLD = 0.8;

  public RelevanceAnalyzer(NLPProcessor nlpProcessor) {
    this(nlpProcessor, REDUNDANCY_THRESHOLD);
  }

  public RelevanceAnalyzer(NLPProcessor nlpProcessor, double redundancyThreshold) {
    this.nlpProcessor = nlpProcessor;
    this.redundancyThreshold = redundancyThreshold;
  }

  public double calculateUsefulness(Comment comment, CodeContext codeContext) {
    if (comment == null || codeContext == null) {
      return 0.0;
    }

    Set<String> commentTerms = nlpProcessor.extractKeyTerms(comment.text());
    if (commentTerms.isEmpty()) {
      return 0.0;
    }

    Set<String> codeTerms = combineCodeTerms(codeContext);
    long matches = countTermMatches(commentTerms, codeTerms);
    return (double) matches / commentTerms.size();
  }

  public boolean isRedundant(Comment comment, CodeContext codeContext) {
    return calculateUsefulness(comment, codeContext) >= redundancyThreshold;
  }

  private Set<String> combineCodeTerms(CodeContext codeContext) {
    Set<String> combined = new HashSet<>(codeContext.variables());
    combined.addAll(codeContext.methods());
    return combined;
  }

  private long countTermMatches(Set<String> commentTerms, Set<String> codeTerms) {
    return commentTerms.stream().filter(codeTerms::contains).count();
  }
}
