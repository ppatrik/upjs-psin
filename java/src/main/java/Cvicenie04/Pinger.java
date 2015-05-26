package Cvicenie04;

import Cvicenie03.*;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetpcap.PcapSockAddr;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Pinger {


    public static String dstIp = "192.168.12.1";

    private static byte[] getRouterMac(BlockingQueue<PcapPacket> radPaketov) {
        try {
            String ipSkNic = JnetpcapUtilities.getIPFromBytes(InetAddress.getByName("localhost").getAddress());
            URL url = new URL("http://localhost/");
            InputStream is = url.openStream();
            is.close();
            while (true) {
                PcapPacket paket = radPaketov.take();
                Ip4 ip4 = new Ip4();
                if(paket.hasHeader(ip4)) {
                    if(ipSkNic.equals(JnetpcapUtilities.getIPFromBytes(ip4.destination()))) {
                        // cielova IP je rovnaka ako ipSkNic-u
                        Ethernet ethernet = paket.getHeader(new Ethernet());
                        return ethernet.destination();
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Main startup method
     *
     * @param args ignored
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        /***************************************************************************
         * First get a list of devices on this system
         **************************************************************************/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }

        System.out.println("Network devices found:");

        int i = 0;
        for (PcapIf device : alldevs) {
            String description =
                    (device.getDescription() != null) ? device.getDescription()
                            : "No description available";
            System.out.printf("#%d: %s [%s] %s\n", i++, device.getName(), description, device.toString());
        }

        PcapIf device = alldevs.get(6); // We know we have atleast 1 device
        System.out
                .printf("\nChoosing '%s' on your behalf:\n",
                        (device.getDescription() != null) ? device.getDescription()
                                : device.getName());

        /***************************************************************************
         * Second we open up the selected device
         **************************************************************************/
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 1;           // 10 seconds in millis
        final Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        final BlockingQueue<PcapPacket> radPaketov = new LinkedBlockingQueue<PcapPacket>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.execute(new Runnable() {
            public void run() {
                PcapPacketHandler<String> nasHandler = new PcapPacketHandler<String>() {
                    public void nextPacket(PcapPacket pcapPacket, String s) {
                        radPaketov.add(pcapPacket);
                    }
                };
                pcap.loop(-1, nasHandler, "jNetPcap rocks!");
            }
        });

        byte[] myMac = device.getHardwareAddress();
        String srcMac = JnetpcapUtilities.getMacFromBytes(myMac);
        System.out.println("Moja mac: " + srcMac);

        byte[] myIp = null;
        List<PcapAddr> adresy = device.getAddresses();
        for (PcapAddr adresa : adresy) {
            if (adresa.getAddr().getFamily() == PcapSockAddr.AF_INET) {
                myIp = adresa.getAddr().getData();
            }
        }
        String srcIp = JnetpcapUtilities.getIPFromBytes(myIp);
        System.out.println("Moja ip: " + srcIp);

        String dstMac = JnetpcapUtilities.getMacFromBytes(getRouterMac(radPaketov));


        for (int j = 0; j < 4; j++) {
            int icmpId = (int) (Math.random() * 65536);
            int idIP = (int) (Math.random() * 65536);
            JPacket sendPacket = JnetpcapUtilities.getICMPPaket(
                    srcMac,
                    dstMac,
                    srcIp,
                    dstIp,
                    idIP,
                    128,
                    8,
                    0,
                    icmpId,
                    j
            );
            pcap.sendPacket(sendPacket);
            long startTime = System.nanoTime();
            while (System.nanoTime() - startTime < 1000000000L) { // ze chodia somariny
                PcapPacket receivePacket = radPaketov.poll(1, TimeUnit.SECONDS); // ze nic nechodi
                if (receivePacket == null) {
                    break;
                }
                Icmp icmp = new Icmp();
                if (receivePacket.hasHeader(icmp)) {
                    int type = icmp.getByte(0);
                    int code = icmp.getByte(1);
                    int id = icmp.getByte(5);
                    int seq = icmp.getByte(7);
                    System.out.println("prislo type: "+type+" code: "+code+" id: "+id +" seq: " + seq + " time: " + (System.nanoTime() - startTime) / 1000000.0 + " ms");
                    if(type == 0 && code == 0 && id == icmpId && seq == j) {
                        System.out.println("prislo ICMP po case " + (System.nanoTime() - startTime) / 1000000.0 + " ms");
                    }
                }
            }
        }

        pcap.breakloop();
        executorService.shutdown();

        pcap.close();
    }
}
