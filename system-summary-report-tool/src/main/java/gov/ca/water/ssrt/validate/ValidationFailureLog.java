package gov.ca.water.ssrt.validate;

public class ValidationFailureLog {
    VariableTolerance variableTolerance;
    String variableName;
    String timeWindow;
    Double diffValue;
    String validationSource;

    public ValidationFailureLog(String validationSource, String variableName, String timeWindow, VariableTolerance variableTolerance, Double diffValue) {
        this.validationSource = validationSource;
        this.variableName = variableName;
        this.timeWindow = timeWindow;
        this.diffValue = diffValue;
        this.variableTolerance = variableTolerance;
    }

    public String getLogString() {
        return String.format("Validation Source: %s, Variable: %s, Time Window: %s, Tolerance Type: Tolerance: %.2f%%, Value: %.2f",
                             validationSource, variableName, timeWindow, variableTolerance.getToleranceType(), variableTolerance.getToleranceValue(), diffValue);
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

    public String getValidationSource() {
        return validationSource;
    }

    public Double getDiffTolerance() {
        return variableTolerance.getToleranceValue();
    }

    public Double getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Double diffValue) {
        this.diffValue = diffValue;
    }

    public String getCsvString(String delimiter) {
        return validationSource + delimiter +
                variableName + delimiter +
                timeWindow + delimiter +
                variableTolerance.getToleranceType() + delimiter +
                String.format("%.2f", variableTolerance.getToleranceValue()) + delimiter +
                String.format("%.2f", diffValue);
    }
}
