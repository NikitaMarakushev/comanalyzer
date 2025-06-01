package nmarakushev.projects;

import java.util.Set;

public class RelevanceAnalyzer {
    private final NLPProcessor nlpProcessor;

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
        String commentText = comment.text().toLowerCase();
        String code = codeContext.nearbyCode().toLowerCase();

        return commentText.contains(code) || code.contains(commentText);
    }
}