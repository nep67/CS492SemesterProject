// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
	final static int ServerPort = 1234;
	static boolean authorized = false;
	

	public static void main(String args[]) throws UnknownHostException, IOException
	{
		Scanner scn = new Scanner(System.in);

		// getting localhost ip
		InetAddress ip = InetAddress.getByName("localhost");

		// establish the connection
		Socket s = new Socket(ip, ServerPort);

		// obtaining input and out streams
		DataInputStream dis = new DataInputStream(s.getInputStream());
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		
		// ask user for name to add to clienthandler array in Server.java
		System.out.println("Enter your name");
		String yourname = scn.nextLine();
					
		try{
			dos.writeUTF(yourname);
        } catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Who do you want to talk too?");
		String theirname = scn.nextLine();
					

		// sendMessage thread
		Thread sendMessage = new Thread(new Runnable()
		{
			
			@Override

			public void run() {
				while (true) {

					
					/*TODO client to client handshake to establish User Authentication and Session Key
					Authenticaion:
					1. Initiating client sends ID and nonce
					2. Receiver Generates session key. Searches known established user ID file for ID and corresponding public key. Encrypt
					   nonce and session key with initiating client's public key and sign with own's private key.
					3. Initiating client validates nonce and applies operation of nonce +1 and encrypts result and session key with receivers
					  public key and signs with private. If the value of nonce isn't equal to original sent value disconnect.
					4. Receiver validates nonce operation. If true initiate session end to end encryption with session key. If not true 
					   disconnect. 
					...


					*/

					
                    
					while(!authorized){
						
                        
						try {
							dos.writeUTF("" + yourname + "#" + theirname);
							authorized = true;
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
                     
					// read the message to deliver.
					String msg = scn.nextLine();
                    
					//TODO add encryption here

					try {
						// write on the output stream
						dos.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		// readMessage thread
		Thread readMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {

				while (true) {

                   
					while(!authorized){

						try {
							// read the message sent to this client
							String sender = dis.readUTF();
							System.out.println(sender + "Is Authorized");
							
	
						} catch (IOException e) {

							e.printStackTrace();
						}
					
					} 

					
					try {
						// read the message sent to this client
						String msg = dis.readUTF();
						System.out.println(msg);
							
					} catch (IOException e) {

							e.printStackTrace();
					}
					
				}
			}
		});

		sendMessage.start();
		readMessage.start();

	}
}
