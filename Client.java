import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

class Client extends JFrame implements ActionListener
{
	Container c;
	CardLayout cl;
	Toolkit tk;
	MySocket ms;
	LoginPanel lp;
	ChatPanel cp;
	static String roomlist;
	static String username;
	JMenuBar mb;
	JMenu m;
	JMenuItem m1;
	JFileChooser fc;
	Client()
	{
		c=getContentPane();
		fc=new JFileChooser("C:\\");
		mb=new JMenuBar();
		m=new JMenu("File");
		m1=new JMenuItem("Save");
		m.add(m1);mb.add(m);
		m1.addActionListener(this);
		m1.setEnabled(false);
		setJMenuBar(mb);
		cl=new CardLayout();
		c.setLayout(cl);
		lp=new LoginPanel();
		cp=new ChatPanel();
		c.add(lp,"1");
		c.add(cp,"2");
		tk=Toolkit.getDefaultToolkit();
		//setSize(tk.getScreenSize());
		setSize(900,700);
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent w)
			{
				try
				{
					ms.println("closing");
				}catch(Exception e){}
				System.exit(0);
			}
		});
	}
	public static void main(String args[])
	{
		new Client();
	}
	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource()==m1)
		{
			try
			{
				File f=null;
				
				fc.showSaveDialog(this);
				f=fc.getSelectedFile();
				if(f!=null)
				{
					FileOutputStream fos=new FileOutputStream(f);
					String content=cp.ta1.getText();
					for(int i=0;i<content.length();i++)
					{
						fos.write(content.charAt(i));
					}
					fos.close();
				}
			}catch(Exception e){}
		}
	}
public class ChatPanel extends JPanel implements ActionListener,ListSelectionListener 
{
    JLabel l1;
    JList list1;
    JScrollPane sp_list1;
    JTextArea ta2;
    JScrollPane sp_ta2;
    JButton b1;
    JLabel l2;
    JLabel l3,l4;
    JTextArea ta1;
    JScrollPane sp_ta1;
    JList list2;
    JScrollPane sp_list2;

    public ChatPanel() 
	{
   		ChatPanelLayout customLayout = new ChatPanelLayout();
        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setLayout(customLayout);
        l1 = new JLabel("Content:");
        add(l1);
        ta1 = new JTextArea("");
        sp_ta1 = new JScrollPane(ta1);
        add(sp_ta1);
        list1 = new JList();
        sp_list1 = new JScrollPane(list1);
        add(sp_list1);
        ta2 = new JTextArea("");
        sp_ta2 = new JScrollPane(ta2);
        add(sp_ta2);
        b1 = new JButton("Send");
        add(b1);
        l2 = new JLabel("User List");
        add(l2);
        l3 = new JLabel("Rooms");
        add(l3);
        list2 = new JList();
        sp_list2 = new JScrollPane(list2);
        add(sp_list2);
        l4 = new JLabel("Message");
   		add(l4);
		b1.addActionListener(this);
		list1.addListSelectionListener(this);
		list2.addListSelectionListener(this);
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent ce)
			{
				updateRooms();
				new ChatHandler().start();
				m1.setEnabled(true);
			}
		});
    }
	void updateRooms()
	{
		StringTokenizer st=new StringTokenizer(roomlist,"|");
		Object o[]=new Object[st.countTokens()+1];
		int i=1;
		o[0]="All";
		while(st.hasMoreTokens())
		{
			o[i++]=st.nextToken();
			//System.out.println(o[i-1]);
		}
		list2.setListData(o);
		list2.setSelectedIndex(0);
	}

	public void actionPerformed(ActionEvent a)
	{
		if(a.getSource()==b1)
		{
			try
			{
				String str=ta2.getText();
				String u1=(String)list1.getSelectedValue();
				String r1=(String)list2.getSelectedValue();
				if(u1.equals("All") && r1.equals("All"))
				{
					ms.println(username+" : "+str);
				}
				else if(u1.equals("All"))
				{
					ms.println("1111|"+r1+"|"+username+" : "+str);
					ta1.append(username+" : "+str+"\n");
				}
				else if(!u1.equals("All"))
				{
					ms.println("2222|"+u1+"|"+r1+"|"+username+" : "+str);
					ta1.append(username+" : "+str+"\n");
				}
				ta2.setText("");
			}catch(Exception e){}
		}
	}

	public void valueChanged(ListSelectionEvent l)
	{
		if(l.getSource()==list2)
		{
			String s=(String)list2.getSelectedValue();
			try
			{
				ms.println("0000"+s);
			}catch(Exception e){}
		}
	}
	class ChatHandler extends Thread
	{
		public void run()
		{
			while(true)
			{
				try
				{
					String str=ms.readLine();
					//System.out.println("Data Received");
					if(!str.equals(""))
					{
						String code=str.substring(0,4);
						if(code.equals("0000"))
						{
							String temp=str.substring(4,str.length());
							StringTokenizer users=new StringTokenizer(temp,"|");
							Object o[]=new Object[users.countTokens()];
							int i=0;
							while(users.hasMoreTokens())
							{
								o[i]=users.nextToken();
								i++;
						}
							list1.setListData(o);
							list1.setSelectedIndex(0);
						}
						else
						{
							ta1.append(str+"\n");
						}
					}
				}catch(Exception e){}
			}
		}
	}
}

class ChatPanelLayout implements LayoutManager {

    public ChatPanelLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        Insets insets = parent.getInsets();
        dim.width = 719 + insets.left + insets.right;
        dim.height = 620 + insets.top + insets.bottom;

        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        return dim;
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        Component c;
        c = parent.getComponent(0);
        if (c.isVisible()) {c.setBounds(insets.left+208,insets.top+0,112,24);}
        c = parent.getComponent(1);
        if (c.isVisible()) {c.setBounds(insets.left+8,insets.top+24,544,488);}
        c = parent.getComponent(2);
        if (c.isVisible()) {c.setBounds(insets.left+552,insets.top+24,168,344);}
        c = parent.getComponent(3);
        if (c.isVisible()) {c.setBounds(insets.left+88,insets.top+520,376,56);}
        c = parent.getComponent(4);
        if (c.isVisible()) {c.setBounds(insets.left+472,insets.top+536,72,24);}
        c = parent.getComponent(5);
        if (c.isVisible()) {c.setBounds(insets.left+592,insets.top+0,72,24);}
        c = parent.getComponent(6);
        if (c.isVisible()) {c.setBounds(insets.left+592,insets.top+376,72,24);}
        c = parent.getComponent(7);
        if (c.isVisible()) {c.setBounds(insets.left+552,insets.top+400,168,184);}
        c = parent.getComponent(8);
        if (c.isVisible()) {c.setBounds(insets.left+8,insets.top+536,72,24);}
    }
}

	class LoginPanel extends JPanel implements ActionListener
	{
		GridBagLayout gb;
		GridBagConstraints gbc;
		JLabel l1,l2;
		JTextField t1;
		JPasswordField t2;
		JButton b1,b2;
		
		LoginPanel()
		{
			gb=new GridBagLayout();
			gbc=new GridBagConstraints();
			setLayout(gb);
			l1=new JLabel("User Name:");
			l2=new JLabel("Password:");
			t1=new JTextField(20);
			t2=new JPasswordField(20);
			b1=new JButton("Login");
			b2=new JButton("Cancel");
			addComponent(l1,0,0,1,1);
			addComponent(t1,0,1,1,3);
			addComponent(l2,1,0,1,1);
			addComponent(t2,1,1,1,3);
			addComponent(b1,2,1,1,1);
			addComponent(b2,2,2,1,1);
			b1.addActionListener(this);
			b2.addActionListener(this);
		}
		void addComponent(Component cp,int row,int col,int h,int w)
		{
			gbc.gridy=row;
			gbc.gridx=col;
			gbc.gridheight=h;
			gbc.gridwidth=w;
			add(cp,gbc);
		}
		public void actionPerformed(ActionEvent a)
		{
			if(a.getSource()==b1)
			{
				try
				{
					Socket s=new Socket("127.0.0.1",1234);
					authenticate(s);
				}catch(Exception e){}
			}
			if(a.getSource()==b2)
			{
				System.exit(0);
			}
		}
		void authenticate(Socket s)
		{
			try
			{
				ms=new MySocket(s);
				username=t1.getText();
				ms.println(t1.getText()+"|"+t2.getText());
				Thread.sleep(2000);
				String response=ms.readLine();
				if(response.charAt(0)=='1')
				{
					cl.show(c,"2");
					roomlist=response.substring(2,response.length());
					setTitle(username);
				}
				else
				JOptionPane.showMessageDialog(this,"Invalid User");
			}catch(Exception e){}
		}
	}
}