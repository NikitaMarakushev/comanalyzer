package nmarakushev.projects.analyzer;

import nmarakushev.projects.entity.Comment;
import nmarakushev.projects.processor.NLPProcessor;
import nmarakushev.projects.context.CodeContext;

import java.util.Set;

public class RelevanceAnalyzer {
    private final NLPProcessor nlpProcessor;
    private static final double REDUNDANCY_THRESHOLD = 0.8;

    public RelevanceAnalyzer(NLPProcessor nlpProcessor) {
        this.nlpProcessor = nlpProcessor;
    }

    public double calculateUsefulness(Comment comment, CodeContext codeContext) {
        Set<String> commentTerms = nlpProcessor.extractKeyTerms(comment.text());
        Set<String> codeTerms = codeContext.variables();
        codeTerms.addAll(codeContext.methods());

        long matches = commentTerms.stream()
                .filter(codeTerms::contains)
                .count();

        return (double) matches / Math.max(1, commentTerms.size());
    }

    public boolean isRedundant(Comment comment, CodeContext codeContext) {
        Set<String> commentTerms = nlpProcessor.extractKeyTerms(comment.text());
        Set<String> codeTerms = codeContext.variables();
        codeTerms.addAll(codeContext.methods());

        long matches = commentTerms.stream()
                .filter(codeTerms::contains)
                .count();

        return (double) matches / Math.max(1, commentTerms.size()) >= REDUNDANCY_THRESHOLD;
    }
}