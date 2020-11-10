package songs;

import java.io.*;
import java.util.Scanner;

/**
 * Started by M. Moussavi
 * March 2015
 * Completed by: STUDENT(S) NAME
 */

public class WriteRecord {

	ObjectOutputStream objectOut = null;
	MusicRecord record = null;
	Scanner stdin = null;
	Scanner textFileIn = null;

	/**
	 * Creates an blank MusicRecord object
	 */
	public WriteRecord() {
		record = new MusicRecord();
	}

	/**
	 * Initializes the data fields of a record object
	 *
	 * @param year       - year that song was purchased
	 * @param songName   - name of the song
	 * @param singerName - singer's name
	 * @param price      - CD price
	 */
	public void setRecord(int year, String songName, String singerName,
						  double price) {
		record.setSongName(songName);
		record.setSingerName(singerName);
		record.setYear(year);
		record.setPrice(price);
	}

	/**
	 * Opens a file input stream, using the data field textFileIn
	 *
	 * @param textFileName name of text file to open
	 * @throws IOException
	 */
	public void openFileInputStream(String textFileName) throws IOException {

		// TO BE COMPLETED BY THE STUDENTS

		File file = new File(textFileName);

		try {
			FileInputStream text = new FileInputStream(file);
			textFileIn = new Scanner(text);

		} catch (Exception e) {

		}
	}

	/**
	 * Opens an ObjectOutputStream using objectOut data field
	 *
	 * @param objectFileName name of the object file to be created
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void openObjectOutputStream(String objectFileName) throws FileNotFoundException, IOException {

		// TO BE COMPLETED BY THE STUDENTS

		//MusicRecord obj = new MusicRecord();
		try {
			// FileOutputStream fos = new FileOutputStream(objectFileName);
			objectOut = new ObjectOutputStream(new FileOutputStream(objectFileName));

		} catch (IOException ioe) {
			System.out.println(ioe);
		}


//    try// close file
//    {
//    	if(objectOut!= null)
//    		objectOut.close();
//    	} // end try
//    catch(IOException ioException)
//    {
//    	System.err.println("Error closing file.");
//    	System.exit(1);} // end catch


//		FileOutputStream out;
//		out = new FileOutputStream(objectFileName);
//		out.write('H');
//		out.write(69);
//		out.write(76);
//		out.write('L');
//		out.write('O');
//		out.write('!');
//		out.close();


	}

	/**
	 * Reads records from given text file, fills the blank MusicRecord
	 * created by the constructor with the existing data in the text
	 * file and serializes each record object into a binary file
	 *
	 * @throws IOException
	 */
	public void createObjectFile() throws IOException {

		while (textFileIn.hasNext()) // loop until end of text file is reached
		{
			System.out.println("sing");
			int year = Integer.parseInt(textFileIn.nextLine());
			System.out.print(year + "  ");       // echo data read from text file

			String songName = textFileIn.nextLine();
			System.out.print(songName + "  ");  // echo data read from text file

			String singerName = textFileIn.nextLine();
			System.out.print(singerName + "  "); // echo data read from text file

			double price = Double.parseDouble(textFileIn.nextLine());
			System.out.println(price + "  ");    // echo data read from text file

			setRecord(year, songName, singerName, price);
			textFileIn.nextLine();   // read the dashed lines and do nothing

			// THE REST OF THE CODE TO BE COMPLETED BY THE STUDENTS
			objectOut.writeObject(record);
			objectOut.reset();
		}
		// YOUR CODE GOES HERE

		try {
			if (objectOut != null)
				objectOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {

		WriteRecord d = new WriteRecord();

		String textFileName = "/Users/adelure/IdeaProjects/ENSF607/LAB6/src/someSongs.txt"; // Name of a text file that contains
		// song records
		//String textFileName = "/Users/adelure/Documents/UofC/Software Engr/Fall 2020/ENSF 607 - Software Design & Arch I/Assignment/LAB6";

		String objectFileName = "mySongs.ser"; // Name of the binary file to
		// serialize record objects

		d.openFileInputStream(textFileName);   // open the text file to read from

		d.openObjectOutputStream(objectFileName); // open the object file to
		// write music records into it

		d.createObjectFile();   // read records from opened text file, and write
		// them into the object file.
	}
}
