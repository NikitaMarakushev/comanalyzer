package nmarakushev.projects;

import java.util.Set;

public record CodeContext(String nearbyCode, Set<String> variables, Set<String> methods) {
}