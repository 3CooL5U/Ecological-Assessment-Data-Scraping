import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/* This program scrapes the scores out of EA assessments. 
 * 		extract() scrapes the scores along with scientific and common names and prints it out to outputSEA.csv
 * 			the way the selected scores should be indicated should be with an "X"
 *  		
 * notes on use:
 * 		- the console might print out warnings, they can be ignored as long as the program works
 * 		- depending on the number of files, it might take a bit of time to run (you will be able to see progress with the console)
 * 
 * Michael Ding, 2021
 */

public class EA {
	public static void main(String args[]) throws IOException {
		// location of folder with assessments
		String dir = "C:/Users/micha/OneDrive/Desktop/New York Invasive Species Research Institute/data/Assessments_forMD/EA/";

		// get array of files
		File file = new File(dir);
		File[] files = file.listFiles();

		// headers for csv file
		String[] HEADERS = { "scientificName", "Other scientific names", "stateCommonName", "Form type", "1.1 score",
				"1.2 score", "1.3 score", "1.4 score" };

		// output file
		FileWriter out = new FileWriter("outputEA.csv");
		CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS));

		// for each file in the dir
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
			if (!files[i].getName().equals("desktop.ini"))
				extract(dir + files[i].getName(), printer);
		}
	}

	public static void extract(String filePath, CSVPrinter printer) throws IOException {
		// read pdf
		PDDocument document = PDDocument.load(new File(filePath));
		PDFTextStripper pdfStripper = new PDFTextStripper();
		String text = pdfStripper.getText(document);

		// getting names with indexof() and substring()
		String scientificName = text.substring(text.indexOf("Scientific name:") + 16,
				text.indexOf(" ", text.indexOf(" ", text.indexOf("Scientific name:") + 18) + 2)).trim();
		scientificName = scientificName.replaceAll("[^a-zA-Z ]", "");
		String otherScientificName = text
				.substring(text.indexOf("Scientific name:") + 16, text.indexOf("Common names:")).trim();
		String commonNames = text.substring(text.indexOf("Common names:") + 13, text.indexOf("Native distribution:"))
				.trim();

		// get form type
		String formType = text.substring(text.indexOf("NEW YORK") + 8, text.indexOf("Scientific name:")).trim();
		formType = formType.substring(0, formType.length() - 1).trim();

		// double array for our scores which will be scaled from 0-1
		double[] scores = new double[5];

		// whether or not the assessment has a 1.4
		int n = 3;
		if (text.indexOf("1.4. Impact") != -1)
			n++;

		/*
		 * we get the scores for 1.1, 1.2, 1.3 and 1.4 if that exists blanks and "U"'s
		 * are represented as a 0
		 */
		for (int i = 1; i <= n; i++) {
			int loc = text.indexOf("Score", text.indexOf("1." + String.valueOf(i) + ". Impact")) + 5;
			String temp = text.substring(loc, loc + 3).trim().replaceAll("[^0-9]", "");
			if (temp.equals("U"))
				scores[i] = 0;
			else if (temp.equals(""))
				scores[i] = 0;
			else
				scores[i] = Integer.parseInt(temp) / 10.0;
		}

		// print data to csv
		printer.printRecord(scientificName, otherScientificName, commonNames, formType, scores[1], scores[2], scores[3],
				scores[4]);

		// flush and close
		printer.flush();
		document.close();
	}
}
