package gov.ca.water.ssrt.test;

import java.io.IOException;

import gov.ca.water.ssrt.Report;

public class TestMain {
	public static void main(String[] args) throws IOException{
		//Report report = new Report("test/calsim_callite_template.inp");
		Report report = new Report("test/2005/calsim_callite_Corroboration_rev1.inp");
		System.out.println(report.getOutputFile());
		//report.getOutputFile();
		System.exit(0);
	}
}
