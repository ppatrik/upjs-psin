package Uloha01;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class InfoReceiver {
    public static void main(String[] args) {
        try {
            DatagramSocket soket = new DatagramSocket(InfoSender.INFO_RECEIVER_PORT);
            DatagramPacket paket = null;
            while (true) {
                try {
                    byte[] buf = new byte[soket.getReceiveBufferSize()];
                    paket = new DatagramPacket(buf, buf.length);
                    soket.receive(paket);
                    buf = paket.getData();

                    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                    ObjectInputStream ois = new ObjectInputStream(bais);

                    long fileSize = ois.readLong();
                    String fileName = ois.readUTF();
                    int fileSenderPort = ois.readInt();
                    int fileReceiverPort = ois.readInt();

                    StringBuilder sb = new StringBuilder();
                    sb.append(paket.getAddress() + ":");
                    sb.append(fileName + " (" + fileSize + ") ");
                    sb.append(fileSenderPort + " ");
                    sb.append(fileReceiverPort);

                    System.out.println(sb.toString());
                } catch (SocketException e) {
                    System.out.println(paket.getAddress() + ":");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println(paket.getAddress() + ":");
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
