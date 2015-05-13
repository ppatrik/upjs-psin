package Uloha02;

import Uloha02.Forms.Server;

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

    private List<FileSender> odosielace = new ArrayList<FileSender>();

    public static void main(String[] args) {
        InfoSender infoSender = new InfoSender();
        infoSender.pridajSubory();
        infoSender.posielajInformacie();
    }

    private Server serverForm;

    public InfoSender() {
        serverForm = new Server();
        serverForm.parent = this;
        new Thread(serverForm).run();
    }

    private void posielajInformacie() {
        try {
            DatagramSocket soket = new DatagramSocket();
            while (true) {
                serverForm.println("Sending files: " + odosielace.size());
                serverForm.setSendingFiles(odosielace.size());
                for (int i = 0; i < odosielace.size(); i++) {
                    File subor = odosielace.get(i).getSubor();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);

                    oos.writeLong(subor.length());
                    oos.writeUTF(subor.getName());
                    oos.writeInt(odosielace.get(i).getSenderPort());
                    oos.writeInt(odosielace.get(i).getManagementPort());
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

    public int nextIndex = 0;

    public void pridajSubory() {
        while (true) {
            JFileChooser chooser = new JFileChooser();
            int coSaStalo = chooser.showDialog(null, "zdieľaj");
            if (coSaStalo == JFileChooser.APPROVE_OPTION) {
                File subor = chooser.getSelectedFile();
                FileSender odosielac = new FileSender(
                        FILE_RECEIVER_BASE_PORT + nextIndex,
                        FILE_SENDER_BASE_PORT + nextIndex,
                        subor,
                        subor.length(),
                        serverForm
                );
                odosielace.add(odosielac);
                nextIndex++;
                serverForm.println("Pridany subor: " + subor.getName());
            } else {
                break;
            }
        }
    }
}
