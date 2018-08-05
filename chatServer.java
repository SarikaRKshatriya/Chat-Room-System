
/* This is a server program for chat room system*/
/*import all the required packages*/
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//create a server to access chat messages 
public class chatServer extends JFrame {
	// create a arraylist to maintain number of clients for output stream
	ArrayList clients;
	// create a arraylist for String data to maintain name of the users
	ArrayList<String> username;
	// Create the textarea to display all the input and output messages
	private JTextArea textarea = new JTextArea(5, 20);
	/*
	 * This variable saves filename where all the chat room messages get saved
	 * the chatSystem.txt is a text file to dsave the database of all the input
	 * messages and output messages in HTTP format also saves user connection
	 * and disconnection
	 */
	String filename = "chatSystem.txt";
	/*
	 * the textarea foruser displays the username online/offline and thread
	 * number associated with that user
	 */
	private JTextArea foruser = new JTextArea(3, 10);
	// initialize socket variable to create socket stream
	Socket socket;
	/*
	 * Create the frame and display the frame name as a chat room server This
	 * frame will serve as a Server Window
	 */

	JFrame frame = new JFrame("Chat Room Server");

	/*
	 * this is a class to handle communication among multiple clients one
	 * instance of this thread will run for each client
	 */
	public class multiUsers implements Runnable {
		// create variables for input stream and output stream
		BufferedReader in;
		PrintWriter out;

		/*
		 * get reference to the sockets input and output stream this is a
		 * Constructor
		 **/
		public multiUsers(Socket s, PrintWriter w) throws Exception {
			out = w;
			socket = s;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		// override run method
		@Override
		public void run() {
			/*
			 * line is a string array to take input line and currentuser is a
			 * variable to store name of a client who has sent the message
			 */
			String[] line;
			String currentUser;

			try {
				String msg = "";
				/*
				 * check for the whole message line and split it with : the
				 * first element is username the second is connect/disconnect
				 * message and if the client clicks on connect button it sends
				 * connect message and if client hits disconnect button it sends
				 * disconnect message all the messages are splitted with :,if
				 * the message is to broadcast all current users then :Chat is
				 * used at the end read the inputstream
				 */
				while ((msg = in.readLine()) != null) {
					// split the line with :
					line = msg.split(":");
					// get the name of current client who sent the message
					currentUser = line[1];
					/*
					 * if the request is connect then add the username to
					 * arraylist username and broadcast the message of
					 * connection to all other users.
					 */
					if (line[3].equals("Connect")) {
						textarea.append("\n" + msg);
						
						sendMsg(("GET:" + currentUser + ":" + line[2] + ":" + "Chat"));// broadcast the message to all the current users
						
						username.add(currentUser);// add the current users name to arraylist username
						
						threadNumber();// call the threadnumber to get total online users.
						// get the currentusers number from the list
						int userNumber = username.size();
						// add the threadnumber and user name to foruser
						// textarea to show online users.
						foruser.append("Thread " + userNumber + ":");
						foruser.append(currentUser + " is Online\n");
					}
					/*
					 * if the request is disconnect then delete username from
					 * the arraylist and broadcast the dosconnected message to
					 * all current users.
					 */
					else if (line[3].equals("Disconnect")) {
						textarea.append("\n" + msg);
						// broadcast disconnection message to all current users.
						sendMsg(("GET:" + currentUser + ":has disconnected." + ":" + "Chat"));
						/*
						 * if the client/user is disconnected then remove it
						 * from the arraylist username remove the user name to
						 * set records of current users and this allows the next
						 * user to use same name used by past user who is
						 * disconnected
						 */
						username.remove(currentUser);
						// Call threadnumber method to get total number of
						// threads
						threadNumber();
						// Display a offline message if the user gets
						// disconnected
						foruser.append(currentUser + " is Offline\n");
					}
					// if its not connect/disconnect then its chat/message to
					// send every user
					else {
						/*
						 * display the incoming message from client which is in
						 * HTTP format, here if its message display it to server
						 * window in POST format
						 */
						textarea.append("\n" + msg);
						/*
						 * while broadcasting message display the message as a
						 * GET at Server
						 */
						textarea.append("\n");

						/*
						 * just convert POST method to GET and broadcast the
						 * message to all current users
						 */
						String doGET = msg.replace("POST", "GET");
						sendMsg(doGET);
					}

				}
			} catch (Exception e) {
				System.out.println(e);
			}
		} // run
	}// multiuser

	// create the GUI and display it
	private void createGUI() {

		/*
		 * Create and display the Start button to start the server and display
		 * the content of text file which contains previous chat room database
		 * the stop button is to close the connections
		 */
		JButton start = new JButton("Start");
		JButton stop = new JButton("Stop");
		// provide scrolling capability to textarea
		JScrollPane scrollPane = new JScrollPane(textarea);
		// set both textarea's as not editable
		textarea.setEditable(false);
		foruser.setEditable(false);
		// to display the scrollpane vertically
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// add scrollpane to the frame
		frame.add(scrollPane);
		// close the frame on clicking cross icon on window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*
		 * add listeners set action to start button html#abstractbutton /* when
		 * user clicks on start button then the server starts when it gets
		 * started it displays the current date in the HTTP date format add
		 * listener to start button
		 */
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {

				try {
					/*
					 * create a new filereader with the filename as a chatSystem
					 * as declared before.This file contaions database of all
					 * the messages also a create bufferreader to read content
					 * of the file
					 */
					FileReader reader = new FileReader(filename);
					BufferedReader br = new BufferedReader(reader);
					// read the content of the file and display it on server
					// window
					textarea.read(br, null);
					// close bufferreader
					br.close();
					// focus the textaerea as soon as it loads the previous
					// messages
					textarea.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
				/* create thread instance to make the system multithreaded */
				Thread thread = new Thread(new multiThread());
				// start the thread
				thread.start();
				/* display message at server window */
				textarea.append("Server started...\n");
				/* Display the current date the date is in HTTP format */
				textarea.append("This is chat room messages for the date:\n");
				/*
				 * To print date in the HTTP date format explicitly adding the
				 * time zone to the formatter
				 */
				Instant i = Instant.now();
				String dateFormatted = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(i);
				textarea.append(dateFormatted + "\n");

			}

		});
		/*
		 * Set action to stop button When user clicks on this button then server
		 * stops and all users get notified about it
		 */
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				textarea.append("Sever is stopping...\n");
				sendMsg("Server is Stopping,So all users are disconnected.\n");
				/* create a file with filename chatSystem */
				File f = new File(filename);

				try {
					/*
					 * open filewriter instance to write on a file,it saves all
					 * the server window messages into the file including users
					 * connected,disconnected
					 */
					FileWriter fw = new FileWriter(filename);
					fw.flush();
					// get all the textarea content and copy that to the file
					fw.write(textarea.getText());

					fw.close();// close the filewriter
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {// thread ends in 5 seconds that is 5000 milliseconds
					Thread.sleep(5000);

					System.exit(0);// handle shutdown

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
		// add start button to the frame at the top position
		frame.getContentPane().add(start, BorderLayout.PAGE_START);
		// set tooltip to start button to tell the user to click on the button
		// to get started
		start.setToolTipText("Click this button to start... ");
		// add stop button to the frame at the end
		frame.getContentPane().add(stop, BorderLayout.PAGE_END);
		// set tooltip to start button to tell the user to click on the button
		// to stop
		start.setToolTipText("Click this button to stop... ");
		frame.getContentPane().add(foruser, BorderLayout.LINE_START);
		foruser.append("Online Users:\n");
		// setting frame as visible
		frame.setVisible(true);
		// set frame size
		frame.setSize(600, 600);
		// make the size not resizable to avoid inconsistency among all the
		// frames
		frame.setResizable(false);

	}// createGUI()
		// this class is to allow multithreading

	public class multiThread extends Thread {
		@Override
		public void run() {
			clients = new ArrayList();
			username = new ArrayList();
			try {
				// set up the server socket at port number 7879
				ServerSocket serversocket = new ServerSocket(7879);

				while (true) {
					// Listening for a connection request.
					Socket clientSock = serversocket.accept();
					PrintWriter pw = new PrintWriter(clientSock.getOutputStream());
					// add the client output stream
					clients.add(pw);
					// creating a new thread to process the request from
					// multiple users
					Thread thread = new Thread(new multiUsers(clientSock, pw));
					thread.start(); // starting the thread
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// broadcast message to all users
	public void sendMsg(String message) {
		textarea.append("\n" + message);
		// accessing arraylist client
		Iterator it = clients.iterator();// index just before the first element
											// in arraylist
		// checking the next element availability
		while (it.hasNext()) {
			try {
				PrintWriter output = (PrintWriter) it.next();// moving to next
																// element
				output.println(message);
				output.flush();// flush the outputstream
				textarea.setCaretPosition(textarea.getDocument().getLength());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * this method counts number of threads running and displays them on title
	 * of Server window
	 */
	public void threadNumber() {
		int userSize = username.size();// get the number of current users
		frame.setTitle("Chat Server:" + userSize + " threads are running");

	}

	public static void main(String args[]) {
		// create a new instance of the server class
		chatServer s = new chatServer();
		s.createGUI();// call createGUI() method

	}
}
