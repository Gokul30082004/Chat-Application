package chatapp_withfrontend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class handle_clientside {
    private int uid;
    private Socket socket;
    DataOutputStream op;
    DataInputStream ip;
    handle_clientside(int uid, Socket socket) throws  Exception{
        this.uid = uid;
        this.socket = socket;
        op = new DataOutputStream(socket.getOutputStream());
        ip = new DataInputStream(socket.getInputStream());
        try {
            op.writeUTF(Integer.toString(uid));
            op.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendmsg(String msg, String sender_id) throws Exception{
//        System.out.println(msg + " " + sender_id);
        op.writeUTF(msg + " " + sender_id);
        op.flush();
    }
}
