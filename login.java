package chatapp_withfrontend;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class login {
    public static void main(String[] args) {
        login c = new login();
        c.login_component();
    }
    public void login_component(){
        JFrame frame = new JFrame("Chat Application");

        frame.setSize(500, 500);
        frame.setLayout(null);

        JTextField textField1 = new JTextField();
        JTextField textField2 = new JTextField();

        JLabel uid_label = new JLabel("Enter the phone no");
        uid_label.setBounds(20, 70, 200, 25);
        JLabel upass_label = new JLabel("Enter the Password");
        upass_label.setBounds(20, 100, 200, 25);


        textField1.setBounds(150, 70, 200, 25);
        textField2.setBounds(150, 100, 200, 25);

        JButton button = new JButton("Submit");
        button.setBounds(150, 130, 100, 25);

        button.addActionListener(e -> {
            String uid = textField1.getText();
            String upass = textField2.getText();
            try {
                if(isvalid_username(Integer.parseInt(uid), upass)){
                    frame.dispose();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            new chat_client(Integer.parseInt(uid));
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Invalid Cridintials!");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton register = new JButton("Register");
        register.setBounds(250, 130, 100, 25);
        register.addActionListener(e -> SwingUtilities.invokeLater(chatapp_withfrontend.register::new));


        frame.add(uid_label);
        frame.add(textField1);
        frame.add(upass_label);
        frame.add(textField2);
        frame.add(button);
        frame.add(register);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static boolean isvalid_username(int id, String passkey) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery("Select * from user where uid = '" + id + "' and upass ='"+ passkey + "';");
        return rs.next();
    }
}

