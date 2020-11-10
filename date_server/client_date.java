package date_server;
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
import java.net.Socket;

	public class client_date {
		private PrintWriter socketOut;
		private Socket palinSocket;
		private BufferedReader stdIn;
		private BufferedReader socketIn;


		/**
		 * client_date constructor
		 * @param serverName
		 * @param portNumber
		 */
		public client_date(String serverName, int portNumber) {
			try {
				palinSocket = new Socket(serverName, portNumber);
				stdIn = new BufferedReader(new InputStreamReader(System.in));
				socketIn = new BufferedReader(new InputStreamReader(
						palinSocket.getInputStream()));
				socketOut = new PrintWriter((palinSocket.getOutputStream()), true);
			} catch (IOException e) {
				System.err.println(e.getStackTrace());
			}
		}

		/**
		 * method that reads the input from the user and communicates
		 * with the server, and also obtains the server response
		 *
		 */

		public void communicate()  {

			String line = "";
			String response = "";
			boolean running = true;
			while (running) {
				try {
					System.out.println("please select an option DATE/TIME: ");
					line = stdIn.readLine();
					if (!line.equals("QUIT")){
						socketOut.println(line);
						response = socketIn.readLine();
						System.out.println(response);	
					}else{
						running = false;
					}
					
				} catch (IOException e) {
					System.out.println("Sending error: " + e.getMessage());
				}
			}
			try {
				stdIn.close();
				socketIn.close();
				socketOut.close();
			} catch (IOException e) {
				System.out.println("Closing error: " + e.getMessage());
			}

		}

		/**
		 * Client_date driver
		 * @param args
		 * @throws IOException
		 */
		public static void main(String[] args) throws IOException  {
			client_date myClient = new client_date("localhost", 9090);
			myClient.communicate();
		}
}
