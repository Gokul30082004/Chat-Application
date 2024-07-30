package chatapp_withfrontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class register {
    private JFrame frame;
    private JPanel panel;
    private JLabel label1, label2, label3;
    private JTextField textField1, textField2, textField3;
    private JButton button;

    public register() {
        frame = new JFrame("Register");
        panel = new JPanel();
        label1 = new JLabel("Phone no:");
        label2 = new JLabel("Name:");
        label3 = new JLabel("Password:");
        textField1 = new JTextField(20);
        textField2 = new JTextField(20);
        textField3 = new JTextField(20);
        button = new JButton("Register");

        // Set fonts and colors
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        label1.setFont(labelFont);
        label2.setFont(labelFont);
        label3.setFont(labelFont);
        button.setFont(labelFont);
        button.setBackground(Color.decode("#4CAF50")); // Green button
        button.setForeground(Color.WHITE); // White text

        // Set panel layout and background color
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.decode("#F0F0F0")); // Light gray background

        // Add components to panel using GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label1, gbc);
        gbc.gridy = 1;
        panel.add(label2, gbc);
        gbc.gridy = 2;
        panel.add(label3, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(textField1, gbc);
        gbc.gridy = 1;
        panel.add(textField2, gbc);
        gbc.gridy = 2;
        panel.add(textField3, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(button, gbc);

        // Add panel to frame
        frame.add(panel);

        // Set frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250); // Set frame size
        frame.setLocationRelativeTo(null); // Center frame
        frame.setVisible(true);

        // Button action listener
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform action here
                try {
                    JOptionPane.showMessageDialog(frame,
                            is_valid(textField1.getText(),textField2.getText(),textField3.getText()),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    private String is_valid(String phone, String name, String passkey) throws Exception{
        if(phone.equals("") || name.equals("") || passkey.equals(""))
            return "Invalid Cridintials";

        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
        Statement st=con.createStatement();
        ResultSet rs = st.executeQuery("select * from user where uid = " + phone + ";");
        if(rs.next())
            return "Phone no Exist";

        st.executeUpdate("INSERT INTO `user`(`uname`, `upass`, `uid`) VALUES ('" + name + "','" + passkey + " ','" + phone + "')");
        return "Account Registered";
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new register();
            }
        });
    }
}
