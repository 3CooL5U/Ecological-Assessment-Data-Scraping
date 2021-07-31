import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/* This program is a variant of SEA.java and has both check() and extract() for SEA forms 
 * 		filled out by Agriculture & Markets (ie the text is blue, selected scores are indicated wit "pts")
 * 
 * notes on use:
 * 		- to switch between check() and extract(): change the headers and the call function in the for loop
 * 		- the console might print out warnings, they can be ignored as long as the program works
 * 
 * Michael Ding, 2021
 */

public class SEABlue{	
	
	//lists for forest impact, agriculture and both
    public static List<String> forestList = new ArrayList<String>();  
    public static List<String> agricultureList = new ArrayList<String>();  
    public static List<String> bothList = new ArrayList<String>();  
	
	public static void main(String args[]) throws IOException {
		//location of folder with assessments
		String dir = "C:/Users/micha/OneDrive/Desktop/New York Invasive Species Research Institute/data/Assessments_forMD/SEA-blue/";
		
		//get array of files
		File file = new File(dir);
        File[] files = file.listFiles();    
                
        //headers for extract()
    	//String[] HEADERS = {"scientificName", "Other scientific names", "stateCommonName", "Hum_health_pos", 
		//"Hum_health_neg", "Ind1_ag_pos", "Ind2_for_pos", "Ind1_ag_neg", "Ind2_for_neg", "Soc_rec_pos", "Soc_rec_neg"}; 
    	
        //headers for check()
    	String[] HEADERS = {"scientificName", "Other scientific names", "stateCommonName", "type", "sign", "word", "line"};
    	
    	//output file
    	FileWriter out = new FileWriter("outputSEABlue.csv");
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS));        
                
        //for each file in the dir
    	for (int i = 0; i < files.length; i++) {
        	System.out.println(files[i].getName());
        	if(!files[i].getName().equals("desktop.ini")) {
	        	//extract(dir + files[i].getName(), printer);
        		check(dir + files[i].getName(), printer);
        	}
        }
    	
    	/*print out the number of each category (forest, agriculture, or both), and list out the names. 
			the prints will not work for check() because that method does not add species to these lists*/
		System.out.println("\n\nSEA Blue");
		System.out.println("forest: " + forestList.size());
		for(int i = 0; i < forestList.size(); i++) System.out.println(forestList.get(i));
		
		System.out.println("\n\n\nagri: " + agricultureList.size());
		for(int i = 0; i < agricultureList.size(); i++) System.out.println(agricultureList.get(i));
		
		System.out.println("\n\n\nboth: " + bothList.size());
		for(int i = 0; i < bothList.size(); i++) System.out.println(bothList.get(i));
	}
	
	public static void check(String filePath, CSVPrinter printer) throws IOException {
		//read pdf
		PDDocument document = PDDocument.load(new File(filePath));
		PDFTextStripper pdfStripper = new PDFTextStripper();
  		String text = pdfStripper.getText(document);
  		
        //getting names with indexof() and substring()
        String scientificName = text.substring(text.indexOf("Scientific Name:") + 16, text.indexOf(" ", text.indexOf(" ", text.indexOf("Scientific Name:") + 18) + 2)).trim();
        scientificName = scientificName.replaceAll("[^a-zA-Z ]", "");
        String otherScientificName = text.substring(text.indexOf("Scientific Name:") + 16, text.indexOf("Common Name:")).trim();
        String commonNames = text.substring(text.indexOf("Common Name:") + 13, text.indexOf("Native Distribution:")).trim();
                
        //list of key words we search with
        String[] agList = {"agricult", "crop", "farm", "horticult", "honey", "cattle", "livestock"};
        String[] forList = {"forest", "timber", "lumber", "silvicult", "tree"};

        //making substrings of the discussion section 
		String posDiscussion = text.substring(
				text.indexOf("Discussion:", text.indexOf("ECONOMIC VALUE OF THE SPECIES:")),
				text.indexOf("Sources of Information:", text.indexOf("ECONOMIC VALUE OF THE SPECIES:")));
		String negDiscussion = text.substring(
				text.indexOf("Discussion:", text.indexOf("Moderate Detriment (impacts minor and long lasting")),
				text.indexOf("Sources of Information:",
						text.indexOf("Moderate Detriment (impacts minor and long lasting")));
             
        //for each key word in our agList array, we use .contains() to find it and pint out the sentence that the word is found in
		for (int i = 0; i < agList.length; i++)
			if (posDiscussion.contains(agList[i])) 
				if (posDiscussion.indexOf(".", posDiscussion.indexOf(agList[i])) == -1)
					printer.printRecord(scientificName, otherScientificName, commonNames, "agr", "pos", agList[i],
							posDiscussion
									.substring(posDiscussion.lastIndexOf(".", posDiscussion.indexOf(agList[i])) + 1));
				else
					printer.printRecord(scientificName, otherScientificName, commonNames, "agr", "pos", agList[i],
							posDiscussion.substring(
									posDiscussion.lastIndexOf(".", posDiscussion.indexOf(agList[i])) + 1,
									posDiscussion.indexOf(".", posDiscussion.indexOf(agList[i]))));

		for (int i = 0; i < agList.length; i++)
			if (negDiscussion.contains(agList[i])) 
				if (negDiscussion.indexOf(".", negDiscussion.indexOf(agList[i])) == -1)
					printer.printRecord(scientificName, otherScientificName, commonNames, "agr", "neg", agList[i],
							negDiscussion
									.substring(negDiscussion.lastIndexOf(".", negDiscussion.indexOf(agList[i])) + 1));
				else
					printer.printRecord(scientificName, otherScientificName, commonNames, "agr", "neg", agList[i],
							negDiscussion.substring(
									negDiscussion.lastIndexOf(".", negDiscussion.indexOf(agList[i])) + 1,
									negDiscussion.indexOf(".", negDiscussion.indexOf(agList[i]))));

		for (int i = 0; i < forList.length; i++)
			if (posDiscussion.contains(forList[i])) 
				if (posDiscussion.indexOf(".", posDiscussion.indexOf(forList[i])) == -1)
					printer.printRecord(scientificName, otherScientificName, commonNames, "forest", "pos", forList[i],
							posDiscussion
									.substring(posDiscussion.lastIndexOf(".", posDiscussion.indexOf(forList[i])) + 1));
				else
					printer.printRecord(scientificName, otherScientificName, commonNames, "forest", "pos", forList[i],
							posDiscussion.substring(
									posDiscussion.lastIndexOf(".", posDiscussion.indexOf(forList[i])) + 1,
									posDiscussion.indexOf(".", posDiscussion.indexOf(forList[i]))));

		for (int i = 0; i < forList.length; i++)
			if (negDiscussion.contains(forList[i])) 
				if (negDiscussion.indexOf(".", negDiscussion.indexOf(forList[i])) == -1)
					printer.printRecord(scientificName, otherScientificName, commonNames, "forest", "neg", forList[i],
							negDiscussion
									.substring(negDiscussion.lastIndexOf(".", negDiscussion.indexOf(forList[i])) + 1));
				else
					printer.printRecord(scientificName, otherScientificName, commonNames, "forest", "neg", forList[i],
							negDiscussion.substring(
									negDiscussion.lastIndexOf(".", negDiscussion.indexOf(forList[i])) + 1,
									negDiscussion.indexOf(".", negDiscussion.indexOf(forList[i]))));
                	    		               
  	    //flush and close
  	    printer.flush();
  	    document.close();
	}
	
	@SuppressWarnings("deprecation")
	public static void extract(String filePath, CSVPrinter printer) throws IOException {
		//read pdf
		PDDocument document = PDDocument.load(new File(filePath));
		PDFTextStripper pdfStripper = new PDFTextStripper();
  		String text = pdfStripper.getText(document);
  		
        //getting names with indexof() and substring()
        String scientificName = text.substring(text.indexOf("Scientific Name:") + 16, text.indexOf(" ", text.indexOf(" ", text.indexOf("Scientific Name:") + 18) + 2)).trim();
        scientificName = scientificName.replaceAll("[^a-zA-Z ]", "");
        String otherScientificName = text.substring(text.indexOf("Scientific Name:") + 16, text.indexOf("Common Name:")).trim();
        String commonNames = text.substring(text.indexOf("Common Name:") + 13, text.indexOf("Native Distribution:")).trim();
  		
        String[] arr = text.split("\\s");   //splits our text String with spaces
        double[] scores = new double[10];  //double array for our scores which will be scaled
        
        //get scores by finding "pts"
        int q = 0;
        for(int i = 0; i < arr.length; i++) 
        	if(arr[i].contains("pts")) {
                scores[q] = Integer.parseInt(arr[i].trim().substring(0, arr[i].trim().length()-3).trim());
        		q++;
        	}
              
        /*we used the scores array to get the scores but the indexes do not align right now with the order we want to print in
    		so we rearange the scores and scale them to be from 0 - 1 */
        scores[0] = scores[0]/15.0; scores[1] = scores[1]/15.0;
        scores[6] = scores[6]/15.0; scores[7] = scores[7]/15.0;
        double pos = scores[3]/70.0; double neg = scores[4]/70.0;
        for(int i = 2; i <= 5; i++) scores[i] = 0;
        
        //list of key words we search with
        String[] agList = {"agricult", "crop", "farm", "horticult", "honey", "cattle", "livestock"};
        String[] forList = {"forest", "timber", "lumber", "silvicult", "tree"};

        //making substrings of the discussion section 
		String posDiscussion = text.substring(
				text.indexOf("Discussion:", text.indexOf("ECONOMIC VALUE OF THE SPECIES:")),
				text.indexOf("Sources of Information:", text.indexOf("ECONOMIC VALUE OF THE SPECIES:")));
		String negDiscussion = text.substring(
				text.indexOf("Discussion:", text.indexOf("Moderate Detriment (impacts minor and long lasting")),
				text.indexOf("Sources of Information:",
						text.indexOf("Moderate Detriment (impacts minor and long lasting")));
              
        boolean ag = false; boolean forest = false;

        //for each key word in our agList array, we use .contains() to find it
        for(int i = 0; i < agList.length; i++) if(posDiscussion.contains(agList[i])) {
        	scores[2] = pos; ag = true;
        }
   
        for(int i = 0; i < agList.length; i++) if(negDiscussion.contains(agList[i])) {
        	scores[4] = neg; ag = true;
        }
                
        for(int i = 0; i < forList.length; i++) if(posDiscussion.contains(forList[i])) {
        	scores[3] = pos; forest = true;     
    	}
    
        for(int i = 0; i < forList.length; i++) if(negDiscussion.contains(forList[i])) {
        	scores[5] = neg; forest = true;
        }
                	    		               
        //adding scientific names to corresponding list
        if(forest && !ag) forestList.add(scientificName);
        if(!forest && ag) agricultureList.add(scientificName);
        if(forest && ag) bothList.add(scientificName);
        
        //print data to csv
  	    printer.printRecord(scientificName, otherScientificName, commonNames, scores[0], scores[1], scores[2], scores[3], scores[4], scores[5], scores[6], scores[7]);
  	    
  	    //flush and close
  	    printer.flush();
  	    document.close();
	}
}

