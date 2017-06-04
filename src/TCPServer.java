import java.io.*;
import java.net.*;

/**
 * TCPServer class.
 * 
 * This class simulates a server on the network. It sends back an acknowledgment
 * message to the console if user on TCPClient has entered the correct password.
 * 
 * @author TCSS 431 Team 10 
 * @version 6/3/2017
 *
 */
public class TCPServer {
	static Socket connection;
	static String clientPassword;
	static String clientMessage;
	static ServerSocket mServerSocket;
	private static final int PASSWORD_CHECK_STATE = 0;
	private static final int MESSAGE_EXCHANGE_STATE = 1;
	private static final int FINISH_STATE = 2;
	private static int state;
	public static void main(String args[]) throws Exception {

		// the port number that the server listening to.
		mServerSocket = new ServerSocket(6789);
		int decryptionKey = 1;
		int passwordTrial = 1;
		boolean session = true;
		state = PASSWORD_CHECK_STATE;
		while (session) {
			// server starts listening for a connection to be made to this
			// socket
			connection = mServerSocket.accept();

			// Gets user input (ideally the correct server password) sent
			// through the connection.
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			// Initializes the stream that will send a message back to TCPClient
			DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());

			while(state == PASSWORD_CHECK_STATE) {				
				// Reads client's password.
				clientPassword = inFromClient.readLine();
				// Decrypts the password sent from TCPClient
				if (!clientPassword.isEmpty()) {
					char[] decChar = clientPassword.toCharArray();
					for (int i = 0; i < decChar.length; i++) {
						decChar[i] -= decryptionKey;
					}
	
					String password = String.valueOf(decChar);
					//Checks for validity and sends and sends an acknowledgment message back to TCPClient.
					if (password.equals("secretpass777")) {
						outToClient.writeBytes("You entered the correct password! Send me your message:\n");
						state = MESSAGE_EXCHANGE_STATE;
					} else {
						passwordTrial ++;
						outToClient.writeBytes("Incorrect password, please try again.\n");
						//state = PASSWORD_VERIFY;
						if (passwordTrial == 3) {
							state += 2;
							session = false;
						}
					}
				}
			}
			while(state==MESSAGE_EXCHANGE_STATE) {
				// Reads client's password.
				clientMessage = inFromClient.readLine();
				if (!clientMessage.isEmpty()) {
					char[] decChar = clientMessage.toCharArray();
					for (int i = 0; i < decChar.length; i++) {
						decChar[i] -= decryptionKey;
					}
					String message = String.valueOf(decChar);
					if(message.equalsIgnoreCase("bye")) {
						session = false;
						state = FINISH_STATE;
					}
					outToClient.writeBytes("Message Received: " + message + "\n");
				}
			}
		}
		mServerSocket.close();
		connection.close();
		System.out.println("Finished");
	}

}