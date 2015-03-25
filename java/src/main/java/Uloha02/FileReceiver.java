package Uloha02;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileReceiver implements Runnable {

    public static int CHUNK_SIZE = 1000;

    private InetAddress adresaSender;
    private int portSendera;
    private int mojPort;
    private File subor;
    private long velkostSuboru;

    public FileReceiver(InetAddress adresaSender, int portSendera, int mojPort, File subor, long velkostSuboru) {
        this.adresaSender = adresaSender;
        this.portSendera = portSendera;
        this.mojPort = mojPort;
        this.subor = subor;
        this.velkostSuboru = velkostSuboru;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stahovac: ").append(subor.getName());
        sb.append(" z ipcky ").append(adresaSender.getHostAddress());
        sb.append(" velkost ").append(velkostSuboru);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileReceiver that = (FileReceiver) o;

        if (mojPort != that.mojPort) return false;
        if (portSendera != that.portSendera) return false;
        if (velkostSuboru != that.velkostSuboru) return false;
        if (!adresaSender.equals(that.adresaSender)) return false;
        if (!subor.equals(that.subor)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = adresaSender.hashCode();
        result = 31 * result + portSendera;
        result = 31 * result + mojPort;
        result = 31 * result + subor.hashCode();
        result = 31 * result + (int) (velkostSuboru ^ (velkostSuboru >>> 32));
        return result;
    }

    @Override
    public void run() {
        RandomAccessFile raf = null;
        DatagramSocket soket;
        DatagramPacket paket;
        try {
            raf = new RandomAccessFile(subor, "w");
            raf.setLength(velkostSuboru);
            SortedSet<Long> chybajuceOffsety = new TreeSet<Long>();

            int pocetChunkov = 0;
            for (long i = 0L; i < velkostSuboru; i += CHUNK_SIZE) {
                chybajuceOffsety.add(i);
                pocetChunkov++;
            }

            soket = new DatagramSocket(mojPort);
            soket.setSoTimeout(50 + (int) (Math.random() * 50));

            while (true) {
                byte[] buf = new byte[soket.getReceiveBufferSize()];
                paket = new DatagramPacket(buf, buf.length);
                try {
                    // idu data zo servera
                    soket.receive(paket);
                    byte[] sprava = paket.getData();
                    ByteArrayInputStream bais = new ByteArrayInputStream(sprava);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    long offset = ois.readLong();
                    int velkostChunku = CHUNK_SIZE;
                    if (velkostSuboru - offset < velkostChunku) {
                        velkostChunku = (int) (velkostSuboru - offset);
                    }
                    byte[] dataSubora = new byte[velkostChunku];
                    ois.read(dataSubora);
                    raf.seek(offset);
                    raf.write(dataSubora);
                    chybajuceOffsety.remove(offset);
                    if (chybajuceOffsety.size() == 0) {
                        return;
                    }

                } catch (SocketTimeoutException e) {
                    // idem pytat data
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    if (chybajuceOffsety.size() == pocetChunkov) {
                        // chcem vsetko
                        oos.writeInt(-1);
                    } else {
                        // chyba mi uz len daco
                        int pocet = Math.min((CHUNK_SIZE - 4) / Long.BYTES, chybajuceOffsety.size());
                        oos.writeInt(pocet);
                        for (Long offset : chybajuceOffsety) {
                            if (pocet-- < 0) {
                                break;
                            }
                            oos.writeLong(offset);
                        }
                    }
                    oos.flush();
                    oos.close();
                    byte[] sprava = baos.toByteArray();
                    DatagramPacket poziadavka = new DatagramPacket(sprava, sprava.length);
                    soket.send(poziadavka);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
