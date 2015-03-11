package Cvicenie02;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPKlient {
    public static void main(String[] args) {
        DatagramSocket soket = null;
        try {
            soket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String riadok = scanner.nextLine();

                if ("exit".equals(riadok)) {
                    break;
                }
                byte[] buf = riadok.getBytes();
                DatagramPacket paket = new DatagramPacket(
                        buf,
                        buf.length,
                        InetAddress.getByName("255.255.255.255"),
                        UDPServer.SERVER_PORT
                );
                soket.send(paket);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
