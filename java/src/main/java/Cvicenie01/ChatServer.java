package Cvicenie01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static void main(String[] args) {
        try {
            ExecutorService es = Executors.newCachedThreadPool();
            ServerSocket ss = new ServerSocket(88);
            Socket s;
            while ((s = ss.accept()) != null) {
                ServerVlakno serverVlakno = new ServerVlakno(s);
                es.execute(serverVlakno);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
