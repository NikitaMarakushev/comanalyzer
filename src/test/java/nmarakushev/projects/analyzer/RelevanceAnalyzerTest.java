package nmarakushev.projects.analyzer;

import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;
import nmarakushev.projects.processor.NLPProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RelevanceAnalyzerTest {
    
    @Mock
    private NLPProcessor nlpProcessor;
    
    private RelevanceAnalyzer relevanceAnalyzer;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        relevanceAnalyzer = new RelevanceAnalyzer(nlpProcessor);
    }
    
    @Test
    public void testCalculateUsefulness() {
        // Arrange
        Comment comment = new Comment("This method calculates the sum", Comment.CommentType.SINGLE_LINE, 1);
        Set<String> commentTerms = new HashSet<>();
        commentTerms.add("method");
        commentTerms.add("calculates");
        commentTerms.add("sum");
        
        Set<String> codeTerms = new HashSet<>();
        codeTerms.add("calculateSum");
        codeTerms.add("sum");
        
        when(nlpProcessor.extractKeyTerms(comment.text())).thenReturn(commentTerms);
        
        CodeContext context = new CodeContext(
            "public int calculateSum(int a, int b) { return a + b; }",
            new HashSet<>(),
            codeTerms
        );
        
        // Act
        double usefulness = relevanceAnalyzer.calculateUsefulness(comment, context);
        
        // Assert
        assertEquals(0.67, usefulness, 0.01);
    }
    
    @Test
    public void testIsRedundant() {
        // Arrange
        Comment comment = new Comment("This method calculates sum", Comment.CommentType.SINGLE_LINE, 1);
        CodeContext context = new CodeContext(
            "public int calculateSum(int a, int b) { return a + b; }",
            new HashSet<>(),
            new HashSet<>()
        );
        
        // Act
        boolean isRedundant = relevanceAnalyzer.isRedundant(comment, context);
        
        // Assert
        assertTrue(isRedundant);
    }
}