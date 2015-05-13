package Uloha02;

import Uloha02.Forms.Client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfoReceiver {
    public static void main(String[] args) {
        final Client clientForm = new Client();

        // spustime okienko
        new Thread(clientForm).start();

        // spustene hladania suborov na sieti
        new Thread(new Runnable() {
            public void run() {
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
                            sb.append(paket.getAddress()).append(":");
                            sb.append(fileName).append(" (").append(fileSize).append(") ");
                            sb.append(fileSenderPort).append(" ");
                            sb.append(fileReceiverPort);

                            FileReceiver stahovac = new FileReceiver(
                                    paket.getAddress(),
                                    fileSenderPort,
                                    fileReceiverPort,
                                    new File("income/" + fileName),
                                    fileSize
                            );
                            clientForm.addStahovac(stahovac);
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

        }).start();

        /*int i = 1;
        for (FileReceiver fr : stahovace) {
            System.out.println(i + ": " + fr);
            i++;
        }
        Scanner citac = new Scanner(System.in);
        System.out.print("Ktory subor chces stahovat: ");
        int volba = citac.nextInt();
        i = 1;
        for (FileReceiver fr : stahovace) {
            if(volba==i) {
                System.out.println("idem stahovat " + i);
                ExecutorService spustac = Executors.newCachedThreadPool();
                spustac.execute(fr);
            }
            i++;
        }*/
    }
}
