package chatapp_withfrontend;


import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    ServerSocket serverSocket;
    DataInputStream ip;
    public server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startserver() throws Exception{

        while(!serverSocket.isClosed()){
            Socket socket = serverSocket.accept();
            ip = new DataInputStream(socket.getInputStream());
            String id = ip.readUTF();
            System.out.println(id + "  " + "client Joined");
            handleclient client = new handleclient(socket, Integer.parseInt(id));
            Thread thread = new Thread(client);
            thread.start();
        }
    }

    public static void main(String args[]) throws  Exception{
        ServerSocket ss = new ServerSocket(9000);
        server create_connection = new server(ss);
        create_connection.startserver();
    }

}
