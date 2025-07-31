# callite-report-tool
CalLite Report Tool refactored out of Central Valley Modeling WRIMS

# Report Variance Pass / Fail controls
Support for an optional global variable named "MAX_TOLERANCE" has been added to the scalar values in the inp file.

MAX_TOLERANCE is a fixed percentage difference that is validated for each variable in the report table.

If the variable percentage difference is greater than the MAX_TOLERANCE, a ValidationFailureLog is recorded with the variable name, the time window (water year), the percent difference, and the MAX_TOLERANCE that was used for that variable. 
If the list of ValidationFailureLogs is not empty, the main method will report that values exceed tolerance and exit with a non-zero exit code (2).
A CSV file is generated with the ValidationFailureLogs, which can be used to review the failures.

This allows the wrims-engine build to fail if the report fails validation.

# VARIABLE TOLERANCE CONTROLS
The system-summary-report-tool now supports individual variable tolerances that can be defined in an optional table within the .inp file.
This allows for more granular control over the validation of specific variables in the report, overriding the global MAX_TOLERANCE setting.
Variable tolerances are defined with VARIABLE_NAME, TOLERANCE_TYPE, and TOLERANCE_VALUE.

## Variable Tolerance Table Example
```
VARIABLE_TOLERANCES
VARIABLE_NAME        TOLERANCE_TYPE  TOLERANCE_VALUE
"Trinirty Storage"  	MAX_PERCENT_DIFF	    5.0
"Trinirty Storage"  	MAX_VALUE_DIFF	        1
"X2 Position"           MAX_VALUE_DIFF 	        2.0
"Trinity Export"        MAX_PERCENT_DIFF        1.0
"Trinity Export"        MAX_VALUE_DIFF          1.0
END
```

The VARIABLE_NAME must match with a variable name defined in the PATHNAME_MAPPING table. 
Supported TOLERANCE_TYPE values are `MAX_PERCENT_DIFF` and `MAX_VALUE_DIFF`.
Multiple tolerances can be defined for the same variable, and the system will apply all defined tolerances to the variable when validating the report.
If a VariableTolerance is defined for a variable, the MAX_TOLERANCE global variable will not be applied to that variable.

Variances exceeding the defined tolerances will result in a ValidationFailureLog being recorded, similar to the global MAX_TOLERANCE behavior.

# Future Improvements
Additional tolerance types can be added in the future, such as `MIN_PERCENT_DIFF` or `MIN_VALUE_DIFF`, to allow for more comprehensive validation controls. 

# Developer Notes
The source for the system-summary-report-tool can be linked to the wrims-engine build by adding the path to the parent folder of the system-summary-report-tool to the `librarySourcesDir` variable in the `wrims-engine/gradle.properties` file.

## Example gradle.properties entry

    librarySourcesDir=J:\\Development\\CVM-GIT\\

This will allow you debug directly into the system-summary-report-tool code from the wrims-engine/wrims-comparison-test module.
