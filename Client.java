import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
import java.util.LinkedList; 
import java.util.Queue; 
public class Client
{ 
	public static void main(String[] args) throws IOException 
	{ 
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			Socket s = new Socket(ip, 5000); 
	
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
			String received;
			String tosend;
			while (true) 
			{ 
				System.out.println(dis.readUTF()); 
				tosend = scn.nextLine(); 
				if(tosend.equals("1"))
				{
					dos.writeUTF(tosend);
					received = dis.readUTF(); 
					System.out.println(received);
				} 
				if(tosend.equals("3")) 
				{ 
					dos.writeUTF(tosend);
					System.out.println("Closing this connection : " + s); 
					s.close(); 
					System.out.println("Connection closed"); 
					break; 
				} 
				if(tosend.equals("2"))
				{
					dos.writeUTF(tosend);
					received = dis.readUTF(); 
					System.out.println(received);
					tosend = scn.nextLine(); 
					dos.writeUTF(tosend); 
					received = dis.readUTF();
					System.out.println(received);
					received = dis.readUTF();
					System.out.println(received);
				} 
			} 
			scn.close(); 
			dis.close(); 
			dos.close(); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
} 
