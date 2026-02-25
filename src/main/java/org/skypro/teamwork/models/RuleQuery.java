package org.skypro.teamwork.models;

import java.util.List;
import java.util.Objects;

public class RuleQuery {
    private String query;
    private List<String> arguments;
    private boolean negate;

    public RuleQuery() {}

    public RuleQuery(String query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleQuery ruleQuery = (RuleQuery) o;
        return negate == ruleQuery.negate &&
                Objects.equals(query, ruleQuery.query) &&
                Objects.equals(arguments, ruleQuery.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, arguments, negate);
    }
}