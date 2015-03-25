package Uloha02;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class InfoSender {

    public static final int INFO_RECEIVER_PORT = 9000;
    public static final int FILE_SENDER_BASE_PORT = 15000;
    public static final int FILE_RECEIVER_BASE_PORT = 20000;

    private List<File> subory = new ArrayList<File>();

    public static void main(String[] args) {
        InfoSender infoSender = new InfoSender();
        infoSender.pridajSubory();
        infoSender.posielajInformacie();
    }

    private void posielajInformacie() {
        try {
            DatagramSocket soket = new DatagramSocket();
            while (true) {
                System.out.println("Sending files: " + subory.size());
                for (int i = 0; i < subory.size(); i++) {
                    File subor = subory.get(i);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);

                    oos.writeLong(subor.length());
                    oos.writeUTF(subor.getName());
                    oos.writeInt(FILE_SENDER_BASE_PORT + i);
                    oos.writeInt(FILE_RECEIVER_BASE_PORT + i);
                    oos.flush();

                    oos.close();

                    DatagramPacket paket = new DatagramPacket(
                            baos.toByteArray(),
                            baos.size(),
                            InetAddress.getByName("255.255.255.255"),
                            INFO_RECEIVER_PORT
                    );
                    soket.send(paket);
                }
                Thread.sleep(5000);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void pridajSubory() {
        while (true) {
            JFileChooser chooser = new JFileChooser();
            int coSaStalo = chooser.showDialog(null, "zdieľaj");
            if (coSaStalo == JFileChooser.APPROVE_OPTION) {
                File subor = chooser.getSelectedFile();
                subory.add(subor);
            } else {
                break;
            }
        }
    }
}
