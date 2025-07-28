package gov.ca.dwr.callite;

public enum ToleranceType {
    MAX_VALUE_DIFF("Absolute Tolerance Value Difference"),
    MAX_PERCENT_DIFF("Percentage Tolerance Difference");

    private final String description;

    ToleranceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
