import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;
import java.util.concurrent.*;
public class Server
{
	public static Dictionary user_details; 
	public static int num_resources;
	public static int[] resources;
	public static int[] resources_available;
	public static Queue<String> que = new LinkedList<>();
	public static void main(String[] args) throws IOException 
	{ 
		//user_details=new Hashtable();
		ServerSocket ss = new ServerSocket(5000); 
		String tosend;
		int count;
		Scanner scn = new Scanner(System.in);
		Semaphore sem = new Semaphore(1); 
		System.out.println("Enter the number of resources:");
		tosend=scn.nextLine();
		num_resources=Integer.parseInt(tosend);
		resources=new int[num_resources];
		resources_available=new int[num_resources];
		for(count=0;count<num_resources;count++)
		{
			System.out.print("Enter the number of instances of resource "+(count+1)+":");
			resources[count]=Integer.parseInt(scn.nextLine());
			resources_available[count]=resources[count];
		}
		while (true) 
		{ 
			Socket s = null; 
			
			try
			{ 
				s = ss.accept(); 
				System.out.println("A new client is connected : " + s); 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				System.out.println("Assigning new thread for this client"); 

				Thread t = new ClientHandler(s, dis, dos,sem); 

				t.start(); 
				
			} 
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			} 
		} 
	} 
} 

class ClientHandler extends Thread 
{ 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 
	Semaphore sem;
	
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Semaphore sem) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
		this.sem = sem;
	} 

	@Override
	public void run() 
	{ 
		String received=""; 
		String toreturn="";
		String tosend="";
		boolean allocated=false;
		boolean error=false;
		String errormsg="";
		Scanner scn = new Scanner(System.in);
		while (true) 
		{ 
			try { 
				dos.writeUTF("Select Option:\n1)Get status of resources\n2)Make a request to allocate resources\n3)Exit");
				received = dis.readUTF();  
				if(received.equals("1"))
				{
					tosend="";
					for(int i=0;i<Server.num_resources;i++)
					{
						tosend=tosend+"Resource "+(i+1)+" : "+Server.resources_available[i]+"\n";
					}
					dos.writeUTF("\nCurrent Resource Status: \n"+tosend);
				}
				else if(received.equals("2"))
				{
					dos.writeUTF("Enter the resources required: ");
					received=this.s+" "+dis.readUTF();
					Server.que.add(received);
					String head=Server.que.peek();
					dos.writeUTF("Waiting in Queue");
					try
					{
						sem.acquire();
						StringTokenizer str1=new StringTokenizer(received," ");
						int i=0;
						String temp=str1.nextToken();
						while(str1.hasMoreTokens())
						{
							Server.resources_available[i]=Server.resources[i]-Integer.parseInt(str1.nextToken());
								if(Server.resources_available[i]<0){
									Server.resources_available[i]=0;
									error=true;
								}
							i++;
						}
						while(i<Server.num_resources){
							Server.resources_available[i]=Server.resources[i];
							i++;
						}
						if(error==true){
								errormsg="(\nAvailable instances are less than requested instances. Allocating all the available instances.\n)";
						}
						dos.writeUTF("Resources allocated"+errormsg);
						allocated=true;
					}
					catch (InterruptedException exc) { 
                    				System.out.println(exc); 
                			} 
				} 
				else if(received.equals("3")) 
				{ 
					for(int i=0;i<Server.num_resources;i++)
					{
						Server.resources_available[i]=Server.resources[i];
					}
					received=Server.que.remove();
					sem.release();
					System.out.println("Client " + this.s + " sends exit..."); 
					System.out.println("Closing this connection."); 
					System.out.println(Server.que.peek());
					this.s.close(); 
					System.out.println("Connection closed"); 
					break; 
				}
				else 
				{
					dos.writeUTF("Invalid Option");
				}
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		}
		try
		{ 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 
