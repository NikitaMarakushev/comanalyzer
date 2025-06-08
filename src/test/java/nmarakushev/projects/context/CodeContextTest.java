package nmarakushev.projects.context;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class CodeContextTest {
    
    @Test
    public void testConstructor() {
        // Arrange
        String nearbyCode = "public class Test {}";
        Set<String> variables = new HashSet<>();
        variables.add("testVar");
        Set<String> methods = new HashSet<>();
        methods.add("testMethod");
        
        // Act
        CodeContext context = new CodeContext(nearbyCode, variables, methods);
        
        // Assert
        assertEquals(nearbyCode, context.nearbyCode(), "Nearby code should match");
        assertEquals(variables, context.variables(), "Variables should match");
        assertEquals(methods, context.methods(), "Methods should match");
    }
    
    @Test
    public void testEmptyContext() {
        // Act
        CodeContext context = new CodeContext("", new HashSet<>(), new HashSet<>());
        
        // Assert
        assertEquals("", context.nearbyCode(), "Nearby code should be empty");
        assertTrue(context.variables().isEmpty(), "Variables should be empty");
        assertTrue(context.methods().isEmpty(), "Methods should be empty");
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        CodeContext context1 = new CodeContext("test", new HashSet<>(), new HashSet<>());
        CodeContext context2 = new CodeContext("test", new HashSet<>(), new HashSet<>());
        
        // Assert
        assertEquals(context1, context2, "Contexts should be equal");
        assertEquals(context1.hashCode(), context2.hashCode(), "Hash codes should be equal");
    }
    
    @Test
    public void testToString() {
        // Arrange
        CodeContext context = new CodeContext("test", new HashSet<>(), new HashSet<>());
        
        // Act
        String toString = context.toString();
        
        // Assert
        assertTrue(toString.contains("test"), "ToString should contain nearby code");
        assertTrue(toString.contains("variables"), "ToString should contain variables");
        assertTrue(toString.contains("methods"), "ToString should contain methods");
    }
} 