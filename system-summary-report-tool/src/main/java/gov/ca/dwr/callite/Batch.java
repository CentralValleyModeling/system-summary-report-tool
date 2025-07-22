package gov.ca.dwr.callite;

import java.io.File;

public class Batch {

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
				System.exit(2); // variable tolerance exceeded.
			}

			System.exit(0);

		}
		catch (Exception ex) {
			// ignore
			System.exit(1);
		}

	}
}
