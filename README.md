# Chat-Room-System
Java

Chat Room System:
A chat Room System has two java classes one for server side socket connections and another for client side. This Server is multithreaded and keeps database of message if it gets shutdown and displays all the messages after restarting. All the clients are getting registered before starting chat. The System also handles bad request names. When a user sends a message to chat room it sends the message in HTTP format, the server then broadcasts the message to all the online users using GET method. The System also maintains record of time difference between two messages sent by a user and displays that time interval to all the online users. All users gets notified when new user logs on or logs off. The date is in HTTP format and the application is independent of the browser.
Following are the steps to execute programs:

[Users](https://github.com/SarikaRKshatriya/Chat-Room-System/blob/master/Users.png)

[Server](https://github.com/SarikaRKshatriya/Chat-Room-System/blob/master/ChatServer.png)

1.Run chatServer. It display’s name of the application “Chat Room System”, Start button  and Stop button ,one textarea to display chats messages with GET/POST and another to display online users. 
 
2.Click on Start button, it starts the server and allows other clients to send and receive requests to it. The Server window displays message server started with current date. This helps to keep track of messages according to date. 
 
3.Run chatClient. The Client window shows 3 buttons connect, disconnect and send button to connect to the chat room, disconnect the chat room and to send messages respectively. This also asks to enter username. It accepts only alphabets/characters as a username. If any name except characters is entered, it shows error message in textarea and asks to enter valid username containing only characters. 

4. If username is valid then after clicking on “Connect” button. The client gets connected to Chat room System. It display’s connected message in servers and clients both windows. The user’s name is now a title for that client window. The server shows username in online Users textarea and thread number in title.

5.To connect more than one client, in this lab the requirement is of four users then run chatClient to get more users. If four users (A, B,C,D) are registered and connected then they can send messages to chat room. 

The Server window shows:
a. Number of threads running (in title).
b. Number of users online/offline in Online Users textarea.
c. All the message from all the users and connection messages after connection. Also, disconnect messages after disconnecting the client.
d. The messages are in HTTP format.
e. Current date in HTTP date format.
The Client windows shows:
a. Name of the user on title.
b. Time: It displays time difference between two messages sent by the user.
c. Connected/disconnected messages. And displays messages from all users including self.

6.When user wants to exit the chat he/she can click on disconnect button. After disconnecting the particular user server notifies all the other online user about it. All clients get notified and the chat server window shows which user is disconnected/ offline and number of threads running. 

7.To stop the system, user has to click on Stop button to proper shutdown. The server maintains a database of all the messages in a text file with filename chatSystem.txt. If the file doesn’t exist the server creates the file and then saves all the communication on that file. When server again starts, it displays all the previous messages in the textarea.
 
Additional features:
1.Multithreading:  Server is multithreaded. To display number of threads running, the server GUI shows number of threads running in title. If 4 threads are running, it shows that on Server window’s title.   
 
2.Server maintains database of messages: The server maintains a database of all the messages in a text file with filename chatSystem.txt. If the file doesn’t exists the server creates the file and then saves all the communication on that file. When server again starts, it displays all the previous messages in the textarea. 

 


