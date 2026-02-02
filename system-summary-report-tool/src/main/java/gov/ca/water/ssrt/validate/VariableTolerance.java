package gov.ca.water.ssrt.validate;

public class VariableTolerance {
    String variableName;
    Double toleranceValue;
    ToleranceType toleranceType;

    public VariableTolerance(String variableName, Double toleranceValue, ToleranceType toleranceType) {
        this.variableName = variableName;
        this.toleranceValue = toleranceValue;
        this.toleranceType = toleranceType;
    }

    public String getVariableName() {
        return variableName;
    }

    public Double getToleranceValue() {
        return toleranceValue;
    }

    public ToleranceType getToleranceType() {
        return toleranceType;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setToleranceValue(Double toleranceValue) {
        this.toleranceValue = toleranceValue;
    }

    public void setToleranceType(ToleranceType toleranceType) {
        this.toleranceType = toleranceType;
    }
}
