package gov.ca.dwr.callite.validate;

import gov.ca.dsm2.input.parser.InputTable;
import gov.ca.dwr.callite.Utils;
import hec.data.TimeWindow;
import hec.io.TimeSeriesContainer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Validator {
    private HashMap<String, List<VariableTolerance>> variableTolerances;
    private final List<ValidationFailureLog> validationFailureLogs = new ArrayList<>();
    private VariableTolerance globalMaxTolerance = null;
    static final Logger logger = Logger.getLogger("callite.report.validator");
    private static final String VARIABLE_NAME_HEADER = "VARIABLE_NAME";
    private static final String TOLERANCE_TYPE_HEADER = "TOLERANCE_TYPE";
    private static final String TOLERANCE_VALUE_HEADER = "TOLERANCE_VALUE";
    private static final String DIFF_VALUE_HEADER = "DIFF_VALUE";
    private static final String TIME_WINDOW_HEADER = "TIME_WINDOW";
    private static final String GLOBAL_MAX_TOLERANCE_HEADER = "GLOBAL_MAX_TOLERANCE";

    public Validator() {
        this.variableTolerances = new HashMap<>();
    }

    public void setGlobalMaxTolerance(Double globalMaxTolerance) {
        this.globalMaxTolerance = new VariableTolerance(GLOBAL_MAX_TOLERANCE_HEADER, globalMaxTolerance, ToleranceType.MAX_PERCENT_DIFF);
    }

    public void loadVarianceTable(InputTable variableToleranceTable) {
        if (variableToleranceTable == null) {
            return;
        }
        ArrayList<ArrayList<String>> vtValues = variableToleranceTable.getValues();
        variableTolerances = new HashMap<>();
        for (int i = 0; i < vtValues.size(); i++) {
            try {
                parseVariableTolerance(variableToleranceTable, i);
            } catch (Exception e) {
                logger.severe("Error parsing Variable Tolerance: " + vtValues.get(i).toString());
                throw(e);
            }
        }
    }

    private void parseVariableTolerance(InputTable variableToleranceTable, int i) {
        String varName = variableToleranceTable.getValue(i, VARIABLE_NAME_HEADER).replace("\"", "");
        ToleranceType toleranceType = ToleranceType.valueOf(variableToleranceTable
                .getValue(i, TOLERANCE_TYPE_HEADER).toUpperCase());
        Double toleranceValue = Double.parseDouble(variableToleranceTable
                .getValue(i, TOLERANCE_VALUE_HEADER).toUpperCase());

        if(toleranceType == ToleranceType.MAX_PERCENT_DIFF) {
            checkGlobalTolerance(varName, toleranceValue);
        }

        VariableTolerance variableTolerance = new VariableTolerance(varName, toleranceValue, toleranceType);
        if(variableTolerances.containsKey(varName)) {
            variableTolerances.get(varName).add(variableTolerance);
        } else {
            List<VariableTolerance> vtList = new ArrayList<>();
            vtList.add(variableTolerance);
            variableTolerances.put(varName, vtList);
        }
    }

    private void checkGlobalTolerance(String varName, Double toleranceValue) {
        if (globalMaxTolerance != null && toleranceValue > globalMaxTolerance.getToleranceValue()) {
            logger.warning("Variable " + varName + " has a MAX_PERCENT_DIFF tolerance value of " + toleranceValue +
                    " which exceeds the global MAX_TOLERANCE of " + globalMaxTolerance);
        }
    }

    public void loadMaxTolerance(String maxToleranceStr) {
        if (maxToleranceStr != null && !maxToleranceStr.isEmpty()) {
            try {
                setGlobalMaxTolerance(Double.parseDouble(maxToleranceStr));
            } catch (NumberFormatException e) {
                logger.warning("Invalid MAX_TOLERANCE value: " + maxToleranceStr);
                ValidationFailureLog validationFailureLog = new ValidationFailureLog("MAX_TOLERANCE Scalar Value failed to parse from inp file", null,
                        null, null);
                validationFailureLogs.add(validationFailureLog);
            }
        } else {
            logger.warning("No MAX_TOLERANCE value set in input file");
        }
    }

    public void evaluateTolerance(double diff, double pctDiff, String varName, TimeWindow tw) {
        if(variableTolerances.containsKey(varName)) {
            List<VariableTolerance> vtList = variableTolerances.get(varName);
            evaluateVariableTolerances(diff, pctDiff, varName, tw, vtList);
        }
        else {
            evaluateGlobalPercentTolerance(pctDiff, varName, tw);
        }
    }

    public void evaluateVariableTolerances(double diff, double pctDiff, String varName, TimeWindow tw, List<VariableTolerance> vtList) {
        for (VariableTolerance vt : vtList) {
            switch(vt.toleranceType)
            {
                case MAX_VALUE_DIFF:
                    evaluateMaxValueDiff(diff, varName, tw, vt);
                    break;
                case MAX_PERCENT_DIFF:
                    evaluateMaxPercentDiff(pctDiff, varName, tw, vt);
                    break;
                default:
                    logger.warning("Unknown tolerance type for variable: " + varName);
            }
        }
    }

    public void evaluateMaxPercentDiff(double pctDiff, String varName, TimeWindow tw, VariableTolerance vt) {
        logger.fine("Evaluating percent tolerance for variable: " + varName);
        if (Math.abs(pctDiff) > vt.getToleranceValue()) {
            ValidationFailureLog validationFailureLog = new ValidationFailureLog(varName, Utils.formatTimeWindowAsWaterYear(tw),
                    vt, pctDiff);
            validationFailureLogs.add(validationFailureLog);
        }
    }

    public void evaluateMaxValueDiff(double diff, String varName, TimeWindow tw, VariableTolerance vt) {
        logger.fine("Evaluating absolute tolerance for variable: " + varName);
        if (Math.abs(diff) > vt.getToleranceValue()) {
            ValidationFailureLog validationFailureLog = new ValidationFailureLog(varName, Utils.formatTimeWindowAsWaterYear(tw),
                    vt, diff);
            validationFailureLogs.add(validationFailureLog);
        }
    }

    public void evaluateGlobalPercentTolerance(double pctDiff, String varName, TimeWindow tw) {
        if (globalMaxTolerance != null && Math.abs(pctDiff) > globalMaxTolerance.getToleranceValue()) {
            ValidationFailureLog validationFailureLog = new ValidationFailureLog(varName, Utils.formatTimeWindowAsWaterYear(tw),
                    globalMaxTolerance, pctDiff);
            validationFailureLogs.add(validationFailureLog);
        }
    }

    public List<ValidationFailureLog> getValidationFailureLogs() {
        return validationFailureLogs;
    }

    public boolean isValidationFailed() {
        return !validationFailureLogs.isEmpty();
    }

    public void writeFailureLogsToCSV(String delimiter, String outputFile) {
        if (!validationFailureLogs.isEmpty()) {
            String filePath = getValidationFailureFileName(outputFile);

            StringBuilder sb = new StringBuilder();
            sb.append(getValidationFileHeader(delimiter)).append("\n");

            //Add each validation failure log to the CSV
            for (ValidationFailureLog log : validationFailureLogs) {
                sb.append(log.getCsvString(delimiter)).append("\n");
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(sb.toString());
            } catch (IOException e) {
                logger.severe("Error writing validation failure logs to CSV: " + e.getMessage());
            }
        }
        else {
            logger.info("No validation failures to write to CSV.");
        }
    }

    private String getValidationFileHeader(String delimiter) {
        return VARIABLE_NAME_HEADER + delimiter +
                TIME_WINDOW_HEADER + delimiter +
                TOLERANCE_TYPE_HEADER + delimiter +
                TOLERANCE_VALUE_HEADER + delimiter +
                DIFF_VALUE_HEADER;
    }

    public String getValidationFailureFileName(String outputFile) {
        if (outputFile == null || outputFile.isEmpty()) {
            logger.warning("Output file is not set. Cannot generate validation failure log file.");
            return "VALIDATION_FAILURES.csv"; // Default name if output file is not set
        }
        String baseName = outputFile.substring(0, outputFile.lastIndexOf('.'));
        return baseName + "_VALIDATION_FAILURES.csv";
    }

    public void validationComplete(String outputFile) {
        if( isValidationFailed()) {
            logger.fine("Some variables exceeded the tolerance limits.");
            logger.fine("Generating validation failure logs to CSV file.");
            writeFailureLogsToCSV(",", outputFile);
        }
    }

    /**
     * This method assumes that the values are times are in alignment.
     *
     * @param tscAlt
     * @param tscBase
     * @param varName
     * @param tw
     */
    public void evaluateTimeSeriesDiff(TimeSeriesContainer tscAlt, TimeSeriesContainer tscBase, String varName, TimeWindow tw) {
        if(!variableTolerances.containsKey(varName) && globalMaxTolerance == null)
        {
            return;
        }

        if(tscAlt.getStartTime() != tscBase.getStartTime() || tscAlt.getEndTime() != tscBase.getEndTime() ||
                tscAlt.getNumberValues() != tscBase.getNumberValues()) {
            try {
                TimeSeriesContainer cloneBase = (TimeSeriesContainer) tscBase.clone();
                boolean trimSuccess = cloneBase.trimToTime(tscAlt.getStartTime(), tscAlt.getEndTime());
                if (!trimSuccess) {
                    logger.warning("Failed to trim base time series to match alt time series for variable: " + varName);
                    return;
                }
                if( cloneBase.getNumberValues() != tscAlt.getNumberValues()) {
                    logger.warning("Trimmed base time series does not match alt time series in number of values for variable: " + varName);
                    return;
                }
                tscBase = cloneBase;
            } catch (Exception e) {
                logger.severe("Error cloning or trimming base time series for variable: " + varName + " - " + e.getMessage());
                logger.severe("Unable to evaluate time series difference.");
                throw new RuntimeException(e);
            }
        }

        double[] altValues = tscAlt.getValues();
        double[] baseValues = tscBase.getValues();
        double[] valueDiffs = new double[altValues.length];
        double[] pctDiffs = new double[altValues.length];
        for (int i = 0; i < altValues.length; i++) {
            valueDiffs[i] = Math.abs(altValues[i] - baseValues[i]);
            if (baseValues[i] != 0) {
                pctDiffs[i] = Math.abs(100.0 * (altValues[i] - baseValues[i]) / baseValues[i]);
            } else {
                pctDiffs[i] = Double.NEGATIVE_INFINITY;
            }
        }

        OptionalDouble maxDiff = Arrays.stream(valueDiffs).max();
        OptionalDouble maxPercentDiff = Arrays.stream(pctDiffs).max();

        if(maxDiff.isEmpty())
        {
            logger.warning("No valid data found for variable: " + varName);
            return;
        }
        if(maxPercentDiff.isEmpty())
        {
            logger.warning("No valid percent difference data found for variable: " + varName);
            return;
        }
        try{
            if(variableTolerances.containsKey(varName))
            {
                //get the average diff value from absDiffs
                List<VariableTolerance> vtList = variableTolerances.get(varName);
                evaluateVariableTolerances(maxDiff.getAsDouble(), maxPercentDiff.getAsDouble(), varName, tw, vtList);
            }
            else if (globalMaxTolerance != null)
            {
                evaluateGlobalPercentTolerance(maxPercentDiff.getAsDouble(), varName, tw);
            }

        }
        catch (Exception e) {
            logger.severe("Error evaluating time series difference for variable: " + varName + " - " + e.getMessage());
            ValidationFailureLog validationFailureLog = new ValidationFailureLog(varName, Utils.formatTimeWindowAsWaterYear(tw),
                    globalMaxTolerance, maxPercentDiff.getAsDouble());
            validationFailureLogs.add(validationFailureLog);
        }
        logger.info("Completed tolerance validation for time series: " + varName);
    }
}

