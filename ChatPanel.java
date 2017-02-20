import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import javax.swing.*;

public class ChatPanel extends Applet {
    JLabel l1;
    JTextArea ta1;
    JScrollPane sp_ta1;
    JList list1;
    JScrollPane sp_list1;
    JTextArea ta2;
    JScrollPane sp_ta2;
    JButton b1;
    JLabel l2;
    JLabel l3;
    JList list2;
    JScrollPane sp_list2;
    JLabel l4;

    public void init() {
        ChatPanelLayout customLayout = new ChatPanelLayout();

        setFont(new Font("Helvetica", Font.PLAIN, 12));
        setLayout(customLayout);

        l1 = new JLabel("Content:");
        add(l1);

        ta1 = new JTextArea("");
        sp_ta1 = new JScrollPane(ta1);
        add(sp_ta1);

        DefaultListModel listModel_list1 = new DefaultListModel();
        listModel_list1.addElement("item1");
        listModel_list1.addElement("item2");
        list1 = new JList(listModel_list1);
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
        DefaultListModel listModel_list2 = new DefaultListModel();

        listModel_list2.addElement("item1");
        listModel_list2.addElement("item2");
        list2 = new JList(listModel_list2);
        sp_list2 = new JScrollPane(list2);
        add(sp_list2);

        l4 = new JLabel("Message");
        add(l4);

        setSize(getPreferredSize());

    }

    public static void main(String args[]) {
        ChatPanel applet = new ChatPanel();
        Frame window = new Frame("ChatPanel");

        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        applet.init();
        window.add("Center", applet);
        window.pack();
        window.setVisible(true);
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
        dim.height = 587 + insets.top + insets.bottom;

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
