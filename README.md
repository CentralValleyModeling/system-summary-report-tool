# callite-report-tool
CalLite Report Tool refactored out of Central Valley Modeling WRIMS

# Report Variance Pass / Fail controls
Support for an optional global variable named "MAX_TOLERANCE" has been added to the scalar values in the inp file.

MAX_TOLERANCE is a fixed percentage difference that is validated for each variable in the report table.

If the variable percentage difference is greater than the MAX_TOLERANCE, a ValidationFailureLog is recorded with the variable name, the time window (water year), the percent difference, and the MAX_TOLERANCE that was used for that variable. 
If the list of ValidationFailureLogs is not empty, the main method will report that values exceed tolerance and exit with a non-zero exit code (2).
A CSV file is generated with the ValidationFailureLogs, which can be used to review the failures.

This allows the wrims-engine build to fail if the report fails validation.

# Future Improvements
Additional inputs will be supported to allow for individual variable tolerances and time series difference tolerances. 

# Developer Notes
The source for the system-summary-report-tool can be linked to the wrims-engine build by adding the path the parent folder of the system-summary-report-tool to the `librarySourcesDir` variable in the `wrims-engine/gradle.properties` file.

## Example gradle.properties entry

    librarySourcesDir=J:\\Development\\CVM-GIT\\

This will allow you debug directly into the system-summary-report-tool code from the wrims-engine/wrims-comparison-test module.
