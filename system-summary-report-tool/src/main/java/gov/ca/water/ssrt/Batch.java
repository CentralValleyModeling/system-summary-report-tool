package gov.ca.water.ssrt;

public class Batch {

	public static final int EXIT_CODE_VARIABLE_EXCEEDS_TOLERANCE = 2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			System.out.println("Processing Inp file named: \n"+args[0]);
			
			Report report = new Report(args[0]);

			
			String msgs = Utils.getMessages();
			if (msgs.length() > 0) {
				System.out.println(msgs);
			}
			
			System.out.println("Report Complete.\n");
			System.out.println("Validating Results.\n");
			if(report.isValidationFailed())
			{
				System.out.println("Variable difference exceeds tolerance for some variables.");
				System.out.println("Please check the report for details.");
				System.exit(EXIT_CODE_VARIABLE_EXCEEDS_TOLERANCE); // variable tolerance exceeded.
			}

			System.exit(0);

		}
		catch (Exception ex) {
			// ignore
			System.exit(1);
		}

	}
}
