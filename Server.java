import java.sql.*;
import java.net.*;
import java.io.*;
import java.util.*;

class Server
{
	static Connection con;
	static Statement st;
	static ResultSet rs;
	ServerSocket ss;
	static String rooms[];
	static Vector v[];					//Vector(Thread Safe) / ArrayList
	static String roomlist="";
	static Vector connections;
	static Vector connect[];

	Server()throws Exception
	{
		try
		{
			ss=new ServerSocket(1234);
			connections=new Vector();
			System.out.println("Server is running.....");
		}catch(Exception e){}
	}

	void init()throws Exception
	{
		while(true)
		{
			Socket s=ss.accept();
			System.out.println("Connection received..");
			MySocket ms=new MySocket(s);
			String abc=ms.readLine();

			StringTokenizer request=new StringTokenizer(abc,"|");
			String uname=request.nextToken();
			String pass=request.nextToken();
			rs=st.executeQuery("select * from usernames where username='"+uname+"' and password='"+pass+"'");
			try
			{
				if(rs.next())
				{
					ms.println("1"+"|"+roomlist);
					String r=rs.getString(3);
					ResultSet rs1=st.executeQuery("select * from roomnames where roomid='"+r+"'");
					rs1.next();
					r=rs1.getString(2);
					//System.out.println(r);
					new ChatHandler(uname,r,ms).start();
					updateRoom(uname,r,ms);
				}
				else
				{
					ms.println("0");
				}
			}catch(Exception e){}
		}
	}

	void updateRoom(String u, String r, MySocket m)
	{
		//System.out.println("Updating list");
		int i;
		for(i=0;i<rooms.length;i++)
		{
			if(rooms[i].equalsIgnoreCase(r))
			{
				//System.out.println("Match Found");
				break;
			}
		}
		v[i].addElement(u);
		connect[i].addElement(m);
	}

  public static void main(String a[])throws Exception
  {
  	Class.forName("com.mysql.jdbc.Driver");
	con=DriverManager.getConnection("jdbc:mysql://localhost/chatting","root","root");
	st=con.createStatement();
	rs=st.executeQuery("select * from RoomNames");
	int count=0;
	while(rs.next())
	{
		count++;
	}
	//System.out.println(count);
	rooms=new String[count];
	v=new Vector[count];
	connect=new Vector[count];
	ResultSet rs1=st.executeQuery("select * from RoomNames");
	int i=0;
	while(rs1.next())
	{
		rooms[i++]=rs1.getString(2);
		//System.out.println(rooms[i-1]);
	}
	for(i=0;i<rooms.length;i++)
	{
		roomlist=roomlist+"|"+rooms[i];
	}
	for(i=0;i<v.length;i++)
	{
		v[i]=new Vector();
		connect[i]=new Vector();
		//System.out.println(rooms[i]+" created");
	}
	Server ser=new Server();
	ser.init();
  }


  class ChatHandler extends Thread
  {
  	String uname,room;
	MySocket m;
	ChatHandler(String u, String r,MySocket ms)
	{
		uname=u;
		room=r;
		m=ms;
		connections.addElement(ms);
		System.out.println("Total Users : "+connections.size());
	}
	public void run()
	{
		updateAll();
		while(true)
		{
			try
			{
				String str=m.readLine();
				if(!str.equals(""))
				{
					if(str.equals("closing"))
					break;
					String code=str.substring(0,4);
					if(code.equals("0000"))
					{
						sendUserInfo(m,str.substring(4,str.length()));
					}
					else if(code.equals("1111"))
					{
						System.out.println("Multicast");
						multicast(str.substring(4,str.length()),m);
					}
					else if(code.equals("2222"))
					{
						System.out.println("Unicast");
						unicast(str.substring(4,str.length()));
					}
					else
					{
						for(int i=0;i<connections.size();i++)
						{
							MySocket temp=(MySocket)connections.elementAt(i);
							temp.println("From "+room+":"+str);
						}
					}
				}
			}catch(Exception e){}
		}
		try
		{
			connections.remove(m);
			System.out.println("Total Users : "+connections.size());
			m.close();
			for(int i=0;i<connections.size();i++)
			{
				MySocket temp=(MySocket)connections.elementAt(i);
				temp.println(uname+" has logged out");
			}
			updateRoom(uname,room,m);
		}catch(Exception e){}
	}

	void multicast(String text, MySocket m)
	{
		StringTokenizer request=new StringTokenizer(text,"|");
		String r=request.nextToken();
		String message=request.nextToken();
		int i;
		for(i=0;i<rooms.length;i++)
		{
			if(rooms[i].equalsIgnoreCase(r))
			{
				//System.out.println("Match Found");
				break;
			}
		}
		for(int j=0;i<connect[i].size();j++)
		{
			MySocket temp=(MySocket)connect[i].elementAt(j);
			try
			{
				if(temp!=m)
				temp.println("From "+room+":"+message);
			}catch(Exception e){}
		}
	}
	void unicast(String text)
	{
		StringTokenizer request=new StringTokenizer(text,"|");
		String uname=request.nextToken();
		String r=request.nextToken();
		String message=request.nextToken();
		int i,j,k;
		for(i=0;i<rooms.length;i++)
		{
			if(rooms[i].equalsIgnoreCase(r))
			{
				//System.out.println("Match Found");
				break;
			}
		}
		for(j=0;j<v[i].size();j++)
		{
			if(v[i].elementAt(j).equals(uname))
			{
				//System.out.println("Unicast Address Found");
				break;
			}
		}
		try
		{
			MySocket temp=(MySocket)connect[i].elementAt(j);
			temp.println("From "+room+":"+message);
		}catch(Exception e){}
	}
	void sendUserInfo(MySocket m,String r)
	{
		int i,j;
		String userlist="All";
		if(r.equals("All"))
		{
			for(i=0;i<v.length;i++)
			{
				for(j=0;j<v[i].size();j++)
				{
					userlist=userlist+"|"+v[i].elementAt(j);
				}
			}
		}
		else
		{
			for(i=0;i<rooms.length;i++)
			{
				if(rooms[i].equalsIgnoreCase(r))
				{
					break;
				}
			}
			for(j=0;j<v[i].size();j++)
			{
				userlist=userlist+"|"+v[i].elementAt(j);
			}
		}
		try
		{
			m.println("0000|"+userlist);
		}catch(Exception e){}
	}
	void updateAll()
	{
		//System.out.println("Sending Info to All");
		for(int i=0;i<connections.size();i++)
		{
			try
			{
				MySocket temp=(MySocket)connections.elementAt(i);
				//System.out.println("Sent to "+i);
				temp.println(uname+" has logged into Room "+room);
			}catch(Exception e){}
		}
	}
	void updateRoom(String u, String r,MySocket m)
	{
		//System.out.println("Updating list");
		int i;
		for(i=0;i<rooms.length;i++)
		{
			if(rooms[i].equalsIgnoreCase(r))
			{
				//System.out.println("Match Found");
				break;
			}
		}
		v[i].remove(u);
		connect[i].remove(m);
	}
  }
}




