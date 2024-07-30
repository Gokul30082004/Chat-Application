package chatapp_withfrontend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class handleclient implements Runnable{

    public static Map<Integer,handleclient> clients = new HashMap<>();

    Socket socket;
    DataOutputStream op;
    DataInputStream ip;

    int id;

    public handleclient(Socket socket, int id) throws Exception{
        this.socket = socket;
        op = new DataOutputStream(this.socket.getOutputStream());
        ip = new DataInputStream(this.socket.getInputStream());
        this.id = id;
        clients.put(id,this);
    }
//    private void get_prev_msg() throws Exception{
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection con=DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
//        Statement st=con.createStatement();
//        ResultSet rs=st.executeQuery("select * from message where receiver_id = " + id + ";");
//        while(rs.next()){
//            this.op.writeUTF(rs.getString("msg") + " " + Integer.toString(id));
//        }
//        st.executeUpdate("delete from message where receiver_id = " + id + ";");
//    }
    @Override
    public void run(){
//        String msgfromclient;
//        String sender_id;
//        try {
//            this.get_prev_msg();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        while(socket.isConnected()) {
            try {
                String temp = ip.readUTF();
                String msgid[] = temp.split(" ");
                sendgroupmsg(msgid[0], Integer.parseInt(msgid[1]));
            } catch (Exception e) {
            }
        }
    }


    public void sendgroupmsg(String msg, int reciver_id) throws Exception{
        if(clients.containsKey(reciver_id)) {
            clients.get(reciver_id).op.writeUTF(msg + " " + Integer.toString(id));
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://localhost/chat_application","root","");
        Statement st=con.createStatement();
        st.executeUpdate("insert into message values(" + id + ", '" + msg + "', " + reciver_id +");");
    }

}
