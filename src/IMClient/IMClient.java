package IMClient;
/*
IMClient.java - Instant Message client using UDP and TCP communication.

Text-based communication of commands.
*/

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class IMClient {
	// Protocol and system constants
	public static String serverAddress = "localhost";
	public static int TCPServerPort = 1234;					// connection to server
	
	/* 	
	 * This value will need to be unique for each client you are running
	 */
	public static int TCPMessagePort = 1248;				// port for connection between 2 clients
	
	public static String onlineStatus = "100 ONLINE";
	public static String offlineStatus = "101 OFFLINE";

	private BufferedReader reader;							// Used for reading from standard input

	// Client state variables
	private String userId;
	private String status;

	public static void main(String []argv) throws Exception
	{
		IMClient client = new IMClient();
		client.execute();
	}

	public IMClient()
	{
		// Initialize variables
		userId = null;
		status = null;
	}


	public void execute()
	{
		initializeThreads();

		String choice;
		reader = new BufferedReader(new InputStreamReader(System.in));

		printMenu();
		choice = getLine().toUpperCase();

		while (!choice.equals("X"))
		{
			if (choice.equals("Y"))
			{	// Must have accepted an incoming connection
				acceptConnection();
			}
			else if (choice.equals("N"))
			{	// Must have rejected an incoming connection
				rejectConnection();
			}
			else if (choice.equals("R"))		// Register
			{	registerUser();
			}
			else if (choice.equals("L"))		// Login as user id
			{	loginUser();
			}
			else if (choice.equals("A"))		// Add buddy
			{	addBuddy();
			}
			else if (choice.equals("D"))		// Delete buddy
			{	deleteBuddy();
			}
			else if (choice.equals("S"))		// Buddy list status
			{	buddyStatus();
			}
			else if (choice.equals("M"))		// Start messaging with a buddy
			{	buddyMessage();
			}
			else
				System.out.println("Invalid input!");

			printMenu();
			choice = getLine().toUpperCase();
		}
		shutdown();
	}

	private void initializeThreads()
	{

	}

	private void registerUser()
	{	// Register user id
		System.out.print("Enter user id: ");
		userId = getLine();
		System.out.println("Registering user id: "+userId);
		
		String request;
		String response;

		// Send registration message to server
		try {
			Socket clientSocket = new Socket(serverAddress, TCPServerPort);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			request = "REG " + userId;
			outToServer.writeBytes(request +'\n');
			response = inFromServer.readLine();
			System.out.println("FROM SERVER: "+ response);
			clientSocket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set status to online
		status = onlineStatus;
	}

	private void loginUser()
	{	// Login an existing user (no verification required - just set userId to input)
		System.out.print("Enter user id: ");
		userId = getLine();
		System.out.println("User id is set to: " + userId);

		// Set status to online
		if(userId != null)
			status = onlineStatus;
	}

	private void addBuddy()
	{	// Ensure that user is logged in before adding buddy
	    if (userId == null) {
	        System.out.println("You need to login first.");
	        return;
	    }

		// Prompt for buddy id
	    System.out.print("Enter buddy id: ");
	    String buddyId = getLine();
	    System.out.println("Adding buddy: " + buddyId);

	    String request;
	    String response;
	    // Send add buddy message to server
	    try {
	        Socket clientSocket = new Socket(serverAddress, TCPServerPort);
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        request = "ADD " + userId + " " + buddyId;
	        outToServer.writeBytes(request + '\n');
	        response = inFromServer.readLine();
	        System.out.println("FROM SERVER: " + response);
	        clientSocket.close();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void deleteBuddy()
	{	// Delete buddy if have current user id
		System.out.print("Enter buddy id: ");
		String buddyId = getLine();

		if (buddyId == userId) {
			System.out.println("You cannot delete yourself.");
			return;
	
		}

		System.out.println("Deleting buddy: " + buddyId);

		String request;
		String response;

		// Send delete buddy message to server
		try {
			Socket clientSocket = new Socket(serverAddress, TCPServerPort);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			request = "DEL " + userId + " " + buddyId;
			outToServer.writeBytes(request + '\n');
			response = inFromServer.readLine();
			System.out.println("FROM SERVER: " + response);
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buddyStatus()
	{	// Print out buddy status (need to store state in instance variable that received from previous UDP message)
	}

	private void buddyMessage()
	{	// Make connection to a buddy that is online
		// Must verify that they are online and should prompt to see if they accept the connection
	}

	private void shutdown()
	{	// Close down client and all threads
	}

	private void acceptConnection()
	{	// User pressed 'Y' on this side to accept connection from another user
		// Send confirmation to buddy over TCP socket
		// Enter messaging mode
	}

	private void rejectConnection()
	{	// User pressed 'N' on this side to decline connection from another user
		// Send no message over TCP socket then close socket
	}

	private String getLine()
	{	// Read a line from standard input
		String inputLine = null;
		  try{
			  inputLine = reader.readLine();
		  }catch(IOException e){
			 System.out.println(e);
		  }
	 	 return inputLine;
	}

	private void printMenu()
	{	System.out.println("\n\nSelect one of these options: ");
		System.out.println("  R - Register user id");
		System.out.println("  L - Login as user id");
		System.out.println("  A - Add buddy");
		System.out.println("  D - Delete buddy");
		System.out.println("  M - Message buddy");
		System.out.println("  S - Buddy status");
		System.out.println("  X - Exit application");
		System.out.print("Your choice: ");
	}

}

// A record structure to keep track of each individual buddy's status
class BuddyStatusRecord
{	public String IPaddress;
	public String status;
	public String buddyId;
	public String buddyPort;

	public String toString()
	{	return buddyId+"\t"+status+"\t"+IPaddress+"\t"+buddyPort; }

	public boolean isOnline()
	{	return status.indexOf("100") >= 0; }
}

// This class implements the TCP welcome socket for other buddies to connect to.
// I have left it here as an example to show where the prompt to ask for incoming connections could come from.

class TCPMessenger implements Runnable
{
	private IMClient client;
	private ServerSocket welcomeSocket;

	public TCPMessenger(IMClient c)
	{	client = c;
	}

    public void run()
	{
		// This thread starts an infinite loop looking for TCP requests.
		try
		{
			while (true)
			{
		    	// Listen for a TCP connection request.
		    	Socket connection = welcomeSocket.accept();


		    	System.out.print("\nDo you want to accept an incoming connection (y/n)? ");
		    	// Read actually occurs with menu readline
			}
	    }
		catch (Exception e)
		{	System.out.println(e); }
	}
}