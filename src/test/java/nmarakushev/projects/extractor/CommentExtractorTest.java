package nmarakushev.projects.extractor;

import nmarakushev.projects.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentExtractorTest {
    
    @TempDir
    File tempDir;
    
    private CommentExtractor commentExtractor;
    private File testFile;
    
    @BeforeEach
    public void setUp() throws IOException {
        commentExtractor = new CommentExtractor();
        testFile = new File(tempDir, "test.java");
    }
    
    @Test
    public void testExtractSingleLineComments() throws IOException {
        // Arrange
        String content = "//Single line comment\n" +
                        "public class Test {\n" +
                        "    //Another comment\n" +
                        "    private String name;\n" +
                        "}";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertEquals(2, comments.size(), "Should extract 2 single-line comments");
        assertEquals("Single line comment", comments.get(0).text());
        assertEquals("Another comment", comments.get(1).text());
        assertEquals(Comment.CommentType.SINGLE_LINE, comments.get(0).type());
    }
    
    @Test
    public void testExtractMultiLineComments() throws IOException {
        // Arrange
        String content = "/* Multi-line\n" +
                        " * comment */\n" +
                        "public class Test {\n" +
                        "    /* Another\n" +
                        "     * multi-line\n" +
                        "     * comment */\n" +
                        "    private String name;\n" +
                        "}";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertEquals(2, comments.size(), "Should extract 2 multi-line comments");
        assertTrue(comments.get(0).text().contains("Multi-line"));
        assertTrue(comments.get(1).text().contains("Another"));
        assertEquals(Comment.CommentType.MULTI_LINE, comments.get(0).type());
    }
    
    @Test
    public void testExtractCommentsInStringLiterals() throws IOException {
        // Arrange
        String content = "String s = \"// This is not a comment\";\n" +
                        "String s2 = \"/* This is also not a comment */\";";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertTrue(comments.isEmpty(), "Should not extract comments from string literals");
    }
    
    @Test
    public void testExtractNestedComments() throws IOException {
        // Arrange
        String content = "/* Outer comment /* Inner comment */ */";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertEquals(1, comments.size(), "Should extract nested comments as one");
        assertTrue(comments.get(0).text().contains("Outer comment"));
        assertTrue(comments.get(0).text().contains("Inner comment"));
    }
    
    @Test
    public void testExtractUnclosedMultiLineComment() throws IOException {
        // Arrange
        String content = "/* Unclosed comment\n" +
                        "public class Test {";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertEquals(1, comments.size(), "Should extract unclosed comment");
        assertTrue(comments.get(0).text().contains("Unclosed comment"));
    }
    
    @Test
    public void testExtractEmptyComments() throws IOException {
        // Arrange
        String content = "//\n" +
                        "/* */\n" +
                        "public class Test {}";
        Files.write(testFile.toPath(), content.getBytes());
        
        // Act
        List<Comment> comments = commentExtractor.extractComments(testFile);
        
        // Assert
        assertTrue(comments.isEmpty(), "Should not extract empty comments");
    }
}