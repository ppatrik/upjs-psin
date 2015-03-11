package Cvicenie02;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {
    public static final int SERVER_PORT = 9000;

    public static void main(String[] args) {
        try {
            DatagramSocket soket = new DatagramSocket(SERVER_PORT);
            DatagramPacket paket = null;
            while (true) {
                byte[] buf = new byte[soket.getReceiveBufferSize()];
                paket = new DatagramPacket(buf, buf.length);
                soket.receive(paket);
                buf = paket.getData();
                String riadok = new String(buf);
                System.out.println(paket.getAddress() + " " + riadok);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
