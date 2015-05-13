package Cvicenie04;

import Cvicenie03.*;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Icmp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Pinger {

    /**
     * Main startup method
     *
     * @param args ignored
     */
    public static void main(String[] args) {
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

        PcapIf device = alldevs.get(1); // We know we have atleast 1 device
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


        for (int j = 0; j < 4; j++) {
            int icmpId = (int)(Math.random()*65536);
            int idIP = (int)(Math.random()*65536);
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
                try {
                    PcapPacket receivePacket = radPaketov.poll(1, TimeUnit.SECONDS); // ze nic nechodi
                    if (receivePacket == null) {
                        break;
                    }
                    Icmp icmp = new Icmp();
                    if (receivePacket.hasHeader(icmp)) {
                        System.out.println("prislo ICMP po case " + (System.nanoTime() - startTime) / 1000000.0 + " ms");
                    }
                } catch (InterruptedException e) {

                }
            }
        }

        pcap.close();
    }
}
