
import java.io.*;
import java.net.*;

/**
 * TCPClient class. 
 * 
 * Prompts user to enter the correct server password. The password is sent over the network to the server.
 * The server sends an acknowledgement message if the password was correct, otherwise an invalid password message
 * is displayed to the console.
 * 
 * The correct server password is: secretpass777
 * 
 * @author TCSS 431 Team 10 
 * @version 6/3/2017
 *
 */
public class TCPClient {

	static Socket mSocket;
	static String veriString;
	static String invalidPassword = "Incorrect password, please try again.";
	static String validPassword = "You entered the correct password! Send me your message:";
	private static final int CHECK_PASSWORD_STATE = 0;
	private static final int MESSAGE_EXCHANGE_STATE = 1;
	private static final int FINISH_STATE = 2;
	static int passwordTrial = 1;
	private static int state = CHECK_PASSWORD_STATE;
	public static void main(String argv[]) throws Exception {
		
		// local ip and port number
		mSocket = new Socket("localhost", 6789);

		System.out.print("Welcome to TCP Client, please enter the server's password: ");
		
		/* veriString will contain the message from the server if the password (user input) is incorrect.
		 If the password if correct, TCPServer will display an acknowledgment message to the console.*/
		sendMessageToServer();			
		//We are done,close the socket.
		mSocket.close();

	}

	/**
	 * Sends a message to the server.
	 * 
	 * @return the message returned by the server.
	 * @throws IOException
	 */
	public static void sendMessageToServer() throws IOException {
		String userInput;
		String verificationMessage;
		int encryptionKey = 1;
		// Reader to get input from user at the console.
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// To read response from the server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		// To send user input to the server
		DataOutputStream outToServer = new DataOutputStream(mSocket.getOutputStream());

		// User input is read
		userInput = inFromUser.readLine();
		
		// add 1 to each character to encrypt.
		char[] streamC = userInput.toCharArray();
		for (int i = 0; i < streamC.length; i++) {
			streamC[i] += encryptionKey;
		}
		// Converts encrypted user input from char array to String
		String encSentence = String.valueOf(streamC);

		// Sends user input to the server.
		outToServer.writeBytes(encSentence + '\n');
		
		if (state == CHECK_PASSWORD_STATE) {
			
			// Gets response from the server.
			verificationMessage = inFromServer.readLine();
			if(verificationMessage.equals(invalidPassword)) {	
				passwordTrial++;
				if (passwordTrial > 2) {
					state = FINISH_STATE;
				}
				else {
						System.out.println(verificationMessage + "Last Chance!");
						sendMessageToServer();
					}				
			} else if(verificationMessage.equals(validPassword)) {
				System.out.println(verificationMessage);
				state = MESSAGE_EXCHANGE_STATE;
				sendMessageToServer();
			}
		}
		if (state == MESSAGE_EXCHANGE_STATE) {
			// Gets response from the server.
			String message = inFromServer.readLine();
			System.out.println(message);
			//if the user typed 'bye'
			if(userInput.equalsIgnoreCase("bye")) {
				System.out.println("Good Bye!");
				state = FINISH_STATE;
			}else //continue to receive message
				sendMessageToServer();
		}
	}
}
