package gov.ca.dwr.callite;

public class ValidationFailureLog {
    String variableName;
    String timeWindow;
    Double percentDiffTolerance;
    Double percentDiffValue;

    public ValidationFailureLog(String variableName, String timeWindow, Double percentDiffTolerance, Double percentDiffValue) {
        this.variableName = variableName;
        this.timeWindow = timeWindow;
        this.percentDiffTolerance = percentDiffTolerance;
        this.percentDiffValue = percentDiffValue;
    }

    public String getLogString() {
        return String.format("Variable: %s, Time Window: %s, Tolerance: %.2f%%, Value: %.2f",
                             variableName, timeWindow, percentDiffTolerance, percentDiffValue);
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(String timeWindow) {
        this.timeWindow = timeWindow;
    }

    public Double getPercentDiffTolerance() {
        return percentDiffTolerance;
    }

    public void setPercentDiffTolerance(Double percentDiffTolerance) {
        this.percentDiffTolerance = percentDiffTolerance;
    }

    public Double getPercentDiffValue() {
        return percentDiffValue;
    }

    public void setPercentDiffValue(Double percentDiffValue) {
        this.percentDiffValue = percentDiffValue;
    }

    public String getCsvString(String delimiter) {
        return variableName + delimiter +
                timeWindow + delimiter +
                String.format("%.2f", percentDiffTolerance) + delimiter +
                String.format("%.2f", percentDiffValue);
    }
}
