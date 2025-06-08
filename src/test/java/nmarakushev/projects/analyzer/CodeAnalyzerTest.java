package nmarakushev.projects.analyzer;

import nmarakushev.projects.context.CodeContext;
import nmarakushev.projects.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CodeAnalyzerTest {
    
    private CodeAnalyzer codeAnalyzer;
    
    @BeforeEach
    public void setUp() {
        codeAnalyzer = new CodeAnalyzer();
    }
    
    @Test
    public void testGetCodeContext() {
        // Arrange
        String fileContent = "public class Test {\n" +
                           "    private String name;\n" +
                           "    // test comment\n" +
                           "    public void doSomething() {\n" +
                           "        var result = 42;\n" +
                           "    }\n" +
                           "}";
        Comment comment = new Comment("test comment", Comment.CommentType.SINGLE_LINE, 3);
        
        // Act
        CodeContext context = codeAnalyzer.getCodeContext(fileContent, comment);
        
        // Assert
        assertNotNull(context);
        assertTrue(context.nearbyCode().contains("private String name"));
        assertTrue(context.nearbyCode().contains("public void doSomething()"));
        assertTrue(context.variables().contains("name"));
        assertTrue(context.variables().contains("result"));
        assertTrue(context.methods().contains("doSomething"));
    }
}