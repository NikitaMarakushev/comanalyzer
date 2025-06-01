package nmarakushev.projects;

import java.util.Set;

public class CodeContext {
    private final String nearbyCode;
    private final Set<String> variables;
    private final Set<String> methods;

    public CodeContext(String nearbyCode, Set<String> variables, Set<String> methods) {
        this.nearbyCode = nearbyCode;
        this.variables = variables;
        this.methods = methods;
    }

    public String getNearbyCode() { return nearbyCode; }
    public Set<String> getVariables() { return variables; }
    public Set<String> getMethods() { return methods; }
}