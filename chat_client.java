package chatapp_withfrontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class chat_client extends JFrame {
    private JPanel mainPanel;
    private JScrollPane buttonScrollPane;
    private JPanel buttonPanel;
    private JButton addcontact;
    private JPanel chatPanel;
    private Map<Integer,JTextArea> chatAreas;
    private JTextField messageInput;

    private static final Color UNSELECTED_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(59, 89, 152); // Dark blue
    private static final Color TEXT_COLOR = Color.BLACK;

    private int currentChatIndex = 1;


    private final int uid;
    handle_clientside handle;
    Socket socket;
    DataInputStream ip;

    public chat_client(int uid) throws Exception{

        SwingUtilities.invokeLater(() -> {
            // Set the look and feel to Nimbus for improved aesthetics
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.uid = uid;
        socket = new Socket("localhost", 9000);
        ip = new DataInputStream(socket.getInputStream());
        handle = new handle_clientside(uid, socket);

        initializeUI();


        get_old_msg();
        getmsg();
    }

    private void get_old_msg() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery("select * from message where receiver_id = " + uid + ";");
        while(rs.next()) {
            Statement ts=con.createStatement();
            ResultSet temp = ts.executeQuery("select * from contacts where id = " + rs.getString("sender_id") + " and contact_uid = "+ uid + ";");
            if(temp.next())
                chatAreas.get(Integer.parseInt(rs.getString("sender_id"))).append(temp.getString("name") + " : " + rs.getString("msg") + "\n");
            else{
                chatAreas.put(Integer.parseInt(rs.getString("sender_id")),new JTextArea());
                chatAreas.get(Integer.parseInt(rs.getString("sender_id"))).append(rs.getString("sender_id") + " : " + rs.getString("msg") + "\n");
                setVisible(true);
            }

        }
        st.executeUpdate("delete from message where receiver_id = " + uid + ";");
    }

    private void initializeUI() throws SQLException, ClassNotFoundException {
        setTitle("Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BUTTON_COLOR);

        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        buttonScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        chatAreas = new HashMap<>();


        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery("select * from contacts where contact_uid = " + uid + ";");
        while(rs.next()){
            JButton button = new JButton(rs.getString("name"));
            button.addActionListener(new ButtonClickListener(Integer.parseInt(rs.getString("id"))));
            button.setFocusPainted(false); // Remove focus border
            button.setForeground(Color.WHITE);
            button.setBackground(BUTTON_COLOR);
            button.setFont(new Font("Arial", Font.BOLD, 14));

            buttonPanel.add(button);

            JTextArea chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatArea.setBackground(UNSELECTED_COLOR);
            chatArea.setForeground(TEXT_COLOR);
            chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
            chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
            chatPanel.add(chatArea);
            chatAreas.put(Integer.parseInt(rs.getString("id")), chatArea);
        }

        JScrollPane chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);

        messageInput = new JTextField();
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        messageInput.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(messageInput, BorderLayout.CENTER);

        addcontact = new JButton("Create Contact");
        addcontact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new add_contact(uid);
            }
        });
        inputPanel.add(addcontact, BorderLayout.EAST);


        mainPanel.add(buttonScrollPane, BorderLayout.WEST);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void sendMessage() throws Exception {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && currentChatIndex != -1) {
            JTextArea currentChat = chatAreas.get(currentChatIndex);
            currentChat.append("You : " + message + "\n");


            // send to the client side
            handle.sendmsg(message, Integer.toString(currentChatIndex));

            messageInput.setText("");
        }
    }


    private void uploadDocument() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Simulate uploading document
            String message = "File uploaded: " + selectedFile.getName();
            if (currentChatIndex != -1) {
                JTextArea currentChat = chatAreas.get(currentChatIndex);
                currentChat.append(message + "\n");
            }
        }
    }

    class ButtonClickListener implements ActionListener {
        private int buttonIndex;

        ButtonClickListener(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public void actionPerformed(ActionEvent e) {
            // Hide all chat areas
            for (Map.Entry<Integer, JTextArea> entry : chatAreas.entrySet()) {
                entry.getValue().setVisible(false);
            }

            // Show the selected chat area
            chatAreas.get(buttonIndex).setVisible(true);
            System.out.println(currentChatIndex);
            currentChatIndex = buttonIndex;
        }
    }

    private void getmsg() {
        new Thread(new Runnable() {
            @Override
            public void run(){
                while (!socket.isClosed()) {
                    try {
                        String id[] = ip.readUTF().split(" ");
//                        putmsg(Integer.parseInt(id[1]), id[0]);
                        System.out.println(id[0] + " " + id[1]);

                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
                        Statement st=con.createStatement();
                        ResultSet rs=st.executeQuery("select * from contacts where id = " + id[1] + " and contact_uid = " + uid + ";");
                        if(rs.next())
                            chatAreas.get(Integer.parseInt(id[1])).append(rs.getString("name") + " : " + id[0] + "\n");
//                        else {
//                            chatAreas.put(Integer.parseInt(id[1]),new JTextArea());
//                            chatAreas.get(Integer.parseInt(id[1])).append(id[1] + " : " + id[0] + "\n");
//                        }
//                        System.out.println(id[1] + "  " + id[0]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            // Set the look and feel to Nimbus for improved aesthetics
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                new chat_client(1); // Adjust the number of chats as needed
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
