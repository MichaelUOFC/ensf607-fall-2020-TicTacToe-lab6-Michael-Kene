package new_client;

/**
 * Course: ENSF  607
 * Title: Lab6
 * AUthors: Kenechukwu Nwabueze & Michael Adelure
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class server
{
	private Socket aSocket;
	private ServerSocket serverSocket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	public server() {
		try {
			serverSocket = new ServerSocket(8099);
			System.out.println("Server is now running.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String reverse(String s) {

		StringBuilder stringb = new StringBuilder(s);

		stringb.reverse(); // Use the reverse method for StringBuilder object

		return stringb.toString();

	}


	public void isPalindrome() {
		String line = null;
		while (true) {
			try {
				line = socketIn.readLine();
				if (line.equals("QUIT")) {
					line = "Good Bye!";
					socketOut.println(line);
					break;
				}

				// Creating a new string by eliminating non-alphanumeric chars */
				StringBuilder stringb = new StringBuilder();

				// Examine each char in the string to skip alphanumeric char
				for (int i = 0; i < line.length(); i++) {

					if (Character.isLetterOrDigit(line.charAt(i))) {

						stringb.append(line.charAt(i));
					}
				}

				String s1 = stringb.toString();
				String s2 = reverse(s1);

				if (s2.equals(s1)) {
					socketOut.println(line + " is a Palindrome");
				}
				else {
					socketOut.println(line + " is not a Palindrome");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // reading from the client
		}
	}

	/**
	 * Server Driver
	 *
 	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		server myServe = new server();

		// Establishing the connection
		try {
			myServe.aSocket = myServe.serverSocket.accept();
			myServe.socketIn = new BufferedReader(new InputStreamReader(myServe.aSocket.getInputStream()));
			myServe.socketOut = new PrintWriter(myServe.aSocket.getOutputStream(), true);
			myServe.isPalindrome();

			myServe.socketIn.close();
			myServe.socketOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
