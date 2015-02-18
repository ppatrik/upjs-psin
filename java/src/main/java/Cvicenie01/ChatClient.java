package Cvicenie01;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        String site = "localhost";
        int port = 88;
        Socket socket = null;
        try {
            socket = new Socket(site, port);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            // poslanie poziadavky
            //pw.println("GET / HTTP/1.1");

            // poslanie poziadavky ktoru piseme z konzoly
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String riadok = scanner.nextLine();
                if ("exit".equals(riadok)) {
                    break;
                }
                pw.println(riadok);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}