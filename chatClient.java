
/* This is a client program for chat room system*/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class chatClient extends JFrame {
	// Create the textarea
	JTextArea textarea = new JTextArea();
	String username;
	ArrayList<String> users = new ArrayList();
	Boolean isConnected = false;// to check whether the user is connected or not
	long difference = 0;
	SimpleDateFormat dispTime = new SimpleDateFormat("mm:ss");
	/* create instances for socket,bufferreader,printwriter and date */
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	Date date1, date2;

	/* read data from the servers */
	public class ListenServer implements Runnable {
		@Override
		public void run() {
			/*
			 * line is a string array to take input line and currentuser is a
			 * variable to store name of a client who has sent the message
			 */
			String[] line;
			String msg = "";
			try {
				String time1 = dispTime.format(System.currentTimeMillis());
				// parse the time to the date format
				date1 = dispTime.parse(time1);
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
					String usern = line[1];// get username

					if (line[3].equals("Connect")) {
						/*
						 * if the request is connect then add the username to
						 * arraylist users
						 */
						users.add(usern);
					} else if (line[3].equals("Disconnect")) {
						textarea.append(usern + "  has disconnected.\n");
					}
					// else if (line[3].equals("Chat"))
					else {
						/*
						 * the message coming from server is in HTTP format then
						 * extract the message only and display it on users
						 * window
						 */
						textarea.append(line[1] + line[2] + "\n");
						textarea.setCaretPosition(textarea.getDocument().getLength());
					}
				} // try
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// create the GUI and display it
	private void createGUI() {
		// Create the frame for client window.
		JFrame frame = new JFrame("Client");
		/*
		 * Set the layout as null as using setbounds to set complonents set size
		 * and close the frame when clicked on exit
		 */
		frame.setLayout(null);
		// exit chat rrom system if clicks on close icon
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel userlabel = new JLabel("Enter Username");// label for username
														// input
		JLabel inputlabel = new JLabel("Enter Message");// label for message
		/*
		 * Create the buttons Send button to send messegs connect to connect to
		 * chat room disconnect to logout from the chat room
		 */
		JButton send = new JButton("Send");
		JButton connect = new JButton("Connect");
		JButton disconnect = new JButton("Disconnect");
		// usertext is to input username from user and inputfield is to input
		// messages
		JTextField usertext = new JTextField();
		// set textfield as editable
		usertext.setEditable(true);
		JTextField inputfield = new JTextField();
		// set textfield as editable
		inputfield.setEditable(true);
		userlabel.setBounds(10, 40, 100, 20);
		usertext.setBounds(120, 40, 120, 30);
		inputlabel.setBounds(10, 70, 150, 20);
		inputfield.setBounds(120, 70, 120, 30);
		textarea.setBounds(10, 110, 500, 300);
		// https://www.youtube.com/watch?v=Uo5DY546rKY
		send.setBounds(10, 430, 150, 30);
		connect.setBounds(180, 430, 150, 30);
		disconnect.setBounds(350, 430, 150, 30);
		// provide scrolling capability to textarea
		JScrollPane scrollPane = new JScrollPane(textarea);
		// set textarea as not editable
		textarea.setEditable(false);
		// Create textfield to write message
		// http://cs.lmu.edu/~ray/notes/javanetexamples/
		// exit on close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// put created components in the frame.
		frame.add(userlabel);// add userlabel to the frame
		frame.add(usertext);// add usertext to the frame
		frame.add(inputlabel);// add inputlabel to the frame
		frame.add(inputfield);// add inputfield to the frame
		frame.add(textarea);// add textarea to the frame
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(send);// add send button to the frame
		frame.add(connect);// add connect button to the frame
		frame.add(disconnect);// add disconnect button to the frame
		/*
		 * add listeners set action to start button
		 * https://docs.oracle.com/javase/tutorial/uiswing/components/button.
		 * html#abstractbutton When user clicks on send button after adding
		 * message then client converts that message to HTTP format
		 */
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {

				try {

					/*
					 * user-agent displays name of the browsers,this list shows
					 * this application is independent of a browser. display the
					 * host name in this case host is "localhost"
					 */
					String useragent = " User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
					String host = " Host:" + socket.getInetAddress().getHostName();// get
																					// host
																					// name

					/* get length of the original messsage */
					String ContentLength = " Content-length:" + inputfield.getText().length();
					/*
					 * htm as this is simple chat room system,independent of the
					 * browser the content type is text/plain
					 */
					String contentType = " Conent-Type:text/plain";
					/*
					 * To print current date in the HTTP date format explicitly
					 * adding the time zone to the formatter
					 */
					Instant i = Instant.now();
					String dateFormatted = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(i);
					String currentDate = "Date:" + dateFormatted;
					/*
					 * To get time difference between to messages and converting
					 * it to the string format
					 */
					String time2 = dispTime.format(System.currentTimeMillis());
					date2 = dispTime.parse(time2);
					// get difference between previous message and recent
					// message
					difference = date2.getTime() - date1.getTime();
					int min = (int) ((difference / (1000 * 60)) % 60);// calculate
																		// minute
																		// difference
					int sec = (int) ((difference / 1000) % 60);//// calculate
																//// seconds
																//// difference
					/*
					 * the format of the time is two digit numbers so concevert
					 * minutes and seconds to 2 digits
					 */
					String m = String.format("%02d", min);
					String s = String.format("%02d", sec);
					String time = "(" + m + "." + s + ") - ";
					// minutes and seconds
					date1 = date2;
					// append useragent,host,ContentLength,contentType to a
					String httpmsg = useragent + host + ContentLength + contentType + currentDate;
					// append timedifference to the username
					String timetrack = username + time;
					/*
					 * append all the strings
					 * useragent,host,ContentLength,contentType, timedifference
					 * to the username for HTTP format in the main message
					 * entered server reads this whole message
					 */
					String wholeMsg = "POST:" + timetrack + ":" + inputfield.getText() + ":" + httpmsg + ":Chat";
					out.println(wholeMsg);// send whole message in HTTP format
											// to output stream
					out.flush(); // flushes the buffer
				} catch (Exception e) {
					e.printStackTrace();
				}
				/*
				 * After sending message to output stream clear the textfield
				 * and set focus to inputfield to take messages input
				 */
				inputfield.setText("");
				inputfield.requestFocus();

			}

		});
		/*
		 * add listner to connect button after entering username if user clicks
		 * on connect button then it checks if the username is valid(ie.only
		 * charachters) then its creates a new thread with setting username in
		 * title
		 */
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				if (isConnected == false) {
					// take username
					username = usertext.getText();

					// check if the user name is valid that is contains only
					// letters
					if (username.matches("[a-zA-Z]*")) {
						usertext.setEditable(false);
						try {
							// server is at localhost and port 7879 so use same
							// port number here
							socket = new Socket("localhost", 7879);
							// create inputstream and outputstream
							InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
							in = new BufferedReader(streamreader);
							out = new PrintWriter(socket.getOutputStream());
							out.println("POST:" + username + ":(00.00) has connected.:Connect");
							out.flush();
							isConnected = true; // connection is established so
												// this value is true
							connect.setEnabled(false);// disable the connect
														// button
							frame.setTitle("Thread " + username);
						} // try
						catch (Exception e) {
							e.printStackTrace();
						}
						// create and start a new thread
						Thread thread = new Thread(new ListenServer());
						thread.start();
					} // if
						// if user enters invalid username then give message to
						// enter valid user name
					else {
						textarea.append("Username is not valid.");
						textarea.append("\nPlease enter only Charachters.");
						textarea.append("");

					} // else

				} // if
			}
		});
		/*
		 * add listner to disconnect button this button logs off the user from
		 * chat room
		 */
		disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				String dc = ("POST:" + username + ": has disconnected.:Disconnect");

				try {
					/*
					 * set output stream disable disconnect and send buttons
					 */
					out.println(dc);
					out.flush();
					disconnect.setEnabled(false);
					send.setEnabled(false);
					// socket.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				isConnected = false;
			}
		});
		// setting frame as visible
		frame.setVisible(true);
		frame.setSize(520, 500);
		frame.setResizable(false);

	}

	public static void main(String args[]) {
		// create a new instance of the client class
		chatClient c = new chatClient();
		c.createGUI();// call createGUI() method

	}

}
