// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


public class Client
{
	final static int ServerPort = 1234;
	static boolean authorized = false;
	//static boolean isFound = false;
	static boolean go = false;
	private static ThreadLocal<String> handshake = ThreadLocal.withInitial(() -> "0");
	private static ThreadLocal<String> sender = ThreadLocal.withInitial(() -> "no");
	
	   
	 
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
		
	
		
		/*
		while(!go){
			
			if(handshake.get().equals("0")){
				System.out.println("Press 1 to initiate a conversation");
				String convo = scn.nextLine();
				//dos.writeUTF(convo+"#"+"nothing"+"#"+"nothing");
				if(convo.equals("1")){
					System.out.println("Who do you want to talk too?");
					String theirname = scn.nextLine();

					sendAuthenticate(dis, dos, yourname, theirname, handshake);
					//handshake.set("1");
					
					
				}
			}else if(handshake.get().equals("0") && sender.get().equals("no")){
				readAuthenticate(dis, dos);
				//go = true;
			}
			
		}*/

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

					if(handshake.get().equals("0")){
						System.out.println("Press 1 to initiate a conversation");
						String convo = scn.nextLine();
						//dos.writeUTF(convo+"#"+"nothing"+"#"+"nothing");
						if(convo.equals("1")){
							System.out.println("Who do you want to talk too?");
							String theirname = scn.nextLine();
		
							sendAuthenticate(dis, dos, yourname, theirname, handshake);
							//handshake.set("1");
							
							
						}
					}
					 
					// sendAuthenticate(dis, dos, yourname, theirname);
					
                    /*
					while(!authorized){
						
                        
						try {
							dos.writeUTF("" + yourname + "#" + theirname);
							authorized = true;
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					} */
                    /*
					// read the message to deliver.
					String msg = scn.nextLine();
                    
					//TODO add encryption here
                  
					try {
						// write on the output stream
						dos.writeUTF(msg);
					} catch (IOException e) {
						e.printStackTrace();
					} */
				}
			}
		});

		// readMessage thread
		Thread readMessage = new Thread(new Runnable()
		{
			@Override
			public void run() {

				while (true) {

				  
					//readAuthenticate(dis, dos, yourname, theirname);
					
					
					/*
					while(!authorized){

						try {
							// read the message sent to this client
							String sender = dis.readUTF();
							isFound.set(parseFile("./authorizedUsers.txt", sender));
							if(isFound.get())
								System.out.println(sender + " Is Authorized");
							else
							    System.out.println(sender + " Is not Authorized :(");
							
	
						} catch (IOException e) {

							e.printStackTrace();
						}
					
					} */

					if(handshake.get().equals("0") && sender.get().equals("no")){
						readAuthenticate(dis, dos);}

					/*
					try {
						// read the message sent to this client
						String msg = dis.readUTF();
						System.out.println(msg);
							
					} catch (IOException e) {

							e.printStackTrace();
					}*/
					
				}
			}
		});

		sendMessage.start();
		readMessage.start();

	}

	public static boolean parseFile(String fileName,String searchStr) throws FileNotFoundException{
		Scanner scan = new Scanner(new File(fileName));
		boolean found = false;
		System.out.println("Hello inside parse");
        while(scan.hasNext()){
            String line = scan.nextLine().toString();
            if(line.contains(searchStr)){
				found = true;
			   // System.out.println("" + line + " was found!");
			}
			    
		}
		scan.close();
		return found;
		
	}
	
	public static void readAuthenticate(DataInputStream dis, DataOutputStream dos){

    	    try {
				// read the message sent to this client
				String received = dis.readUTF();
				System.out.println("Hello inside read");
				boolean isFound = false;

				StringTokenizer st = new StringTokenizer(received, "#");
			    String MsgToSend = st.nextToken();
				String step = st.nextToken();
				
				//String confirm = "done";
				isFound = parseFile("./authorizedUsers.txt", MsgToSend);
				
				if(step.equals("step2")){
					if(isFound){
						System.out.println(MsgToSend + " Is Authorized");
						step = "done";
					}else{
						System.out.println(MsgToSend + " Is not Authorized :(");
						step = "done";
					}
				}
				
				
				

			} catch (IOException e) {

				e.printStackTrace();
			}
	}

	public static void sendAuthenticate(DataInputStream dis, DataOutputStream dos, String yourname, 
	                                                     String theirname, ThreadLocal<String> handshake){
			
		    String step = "";
			if(handshake.get().equals("0")){	
				sender.set("yes");
				step = "step1";			
				try {
					dos.writeUTF("" + yourname + "#" + theirname + "#" + step);
					//authorized = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			handshake.set("1");

	}

}
