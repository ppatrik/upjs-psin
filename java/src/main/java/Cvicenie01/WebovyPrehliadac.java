package Cvicenie01;

import java.io.*;
import java.net.Socket;

public class WebovyPrehliadac {
    public static void main(String[] args) {
        //String site = "siete.gursky.sk";
        String site = "www.ics.upjs.sk";
        int port = 80;
        Socket socket = null;
        try {
            socket = new Socket(site, port);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            // poslanie poziadavky
            pw.println("GET / HTTP/1.1");
            pw.println("Host: "+site);
            pw.println();

            // odoslanie dat ktore su pripavene v streame
            pw.flush();

            // spravovanie poziadavky

            String riadok;
            while ((riadok = br.readLine()) != null) {
                System.out.println(riadok);
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
