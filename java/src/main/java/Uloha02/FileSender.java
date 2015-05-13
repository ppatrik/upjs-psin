package Uloha02;

import Uloha02.Forms.Server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

// TODO: vytvorit server ktory bude cakat na poziadavku od klienta a potom odosielat do broadcastu jednotlive chunky
public class FileSender implements Runnable{

    private int managementPort;
    private int senderPort;
    private File subor;
    private Long fileSize;
    private Server serverForm;

    public FileSender(int managementPort, int senderPort, File subor, Long fileSize, Server serverForm) {
        this.managementPort = managementPort;
        this.senderPort = senderPort;
        this.subor = subor;
        this.fileSize = fileSize;
        this.serverForm = serverForm;
    }

    public int getManagementPort() {
        return managementPort;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public File getSubor() {
        return subor;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void run() {
        try {
            DatagramSocket soket = new DatagramSocket(getManagementPort());
            DatagramPacket paket = null;

            while (true) {
                try {
                    byte[] buf = new byte[soket.getReceiveBufferSize()];
                    paket = new DatagramPacket(buf, buf.length);
                    soket.receive(paket);
                    buf = paket.getData();

                    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                    ObjectInputStream ois = new ObjectInputStream(bais);

                    int pocetPartov = ois.readInt();
                    if(pocetPartov==-1) {
                        // posielame vsetky casti
                        serverForm.println("Poziadavka na vsetky party");
                    } else {
                        // precitame ktore party si ziada
                        serverForm.println("Poziadavka na " + pocetPartov);
                    }
                    // TODO: samotne odosielanie casti suborov
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
