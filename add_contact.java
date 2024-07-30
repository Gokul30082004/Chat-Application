package chatapp_withfrontend;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class add_contact {
    private JFrame frame;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button;
    private int uid;
    public add_contact(int uid) {
        // Create the frame
        this.uid = uid;
        frame = new JFrame("Add Contact");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create panel for inputs and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and style text fields
        Font inputFont = new Font("Arial", Font.PLAIN, 16);
        textField1 = new JTextField(10);
        textField1.setFont(inputFont);
        textField1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField2 = new JTextField(10);
        textField2.setFont(inputFont);
        textField2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Create and style button
        button = new JButton("Submit");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection con= DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
                Statement st= null;
                st = con.createStatement();
                st.executeUpdate("INSERT INTO `contacts`(`contact_uid`, `id`, `name`) VALUES ('" + uid + "','" + textField1.getText() + "','" + textField2.getText() +"');");
                frame.dispose();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(frame, "Contact added");
            }
        });

        // Add components to the input panel
        inputPanel.add(new JLabel("Phone no:"));
        inputPanel.add(textField1);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(textField2);
        inputPanel.add(button);

        // Add input panel to the frame
        frame.add(inputPanel, BorderLayout.CENTER);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        // Display the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create an instance of the AttractiveUIExample class
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new add_contact(1);
            }
        });
    }
}
