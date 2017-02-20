import java.net.*;
import java.io.*;

class MySocket
{
	Socket s;
	BufferedReader br;
	PrintWriter pr;
	
	MySocket(Socket st)throws Exception
	{
		s=st;
		br=new BufferedReader(new InputStreamReader(s.getInputStream()));
		pr=new PrintWriter(s.getOutputStream(),true);
	}
	String  readLine() throws Exception
	{
		 String str=br.readLine();
		 return(str);
	}
	void println(String str) throws Exception
	{
		pr.println(str);
	}
	void close() throws Exception
	{
		s.close();
	}
}
		
		