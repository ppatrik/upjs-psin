package Cvicenie01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerVlakno implements Runnable {
    private Socket socket;

    public ServerVlakno(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String riadok;
            while ((riadok = br.readLine()) != null) {
                System.out.println(socket.getInetAddress() + ": " + riadok);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
