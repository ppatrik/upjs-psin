package Cvicenie03;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler implements PcapPacketHandler<String> {
    private class ArpZapis {
        public long time;
        public String mac;
        public String ip;

        public ArpZapis(String mac, String ip) {
            this.mac = mac;
            this.ip = ip;
            this.time = System.nanoTime();
        }

        public boolean set(String mac, String ip) {
            if (this.mac.equals(mac)) {
                this.time = System.nanoTime();
                return true;
            } else {
                if (System.nanoTime() - 60 * 100 * 1000 < this.time) {
                    this.time = System.nanoTime();
                    return false;
                } else {
                    this.time = System.nanoTime();
                    this.mac = mac;
                    return true;
                }
            }
        }

        @Override
        public String toString() {
            return mac;/*"ArpZapis{" +
                    "time=" + time +
                    ", mac='" + mac + '\'' +
                    ", ip='" + ip + '\'' +
                    '}';*/
        }
    }

    /**
     * Map<String, ArpZapis>
     * ==
     * Map<IPcka, ARP zapis>
     */
    Map<String, ArpZapis> mojaArp = new HashMap<String, ArpZapis>();

    public void nextPacket(PcapPacket packet, String user) {
        Arp arp = new Arp();

        if (packet.hasHeader(arp)) {

            if (arp.operationEnum() == Arp.OpCode.REQUEST) {
                String senderMac = JnetpcapUtilities.getMacFromBytes(arp.sha());
                String senderIp = JnetpcapUtilities.getIPFromBytes(arp.spa());
                String targetMac = JnetpcapUtilities.getMacFromBytes(arp.tha());
                String targetIp = JnetpcapUtilities.getIPFromBytes(arp.tpa());

                /*System.out.printf("Sender: (%s, %s), Target: (%s, %s) - ",
                        senderMac,
                        senderIp,
                        targetMac,
                        targetIp
                );*/

                if (!mojaArp.containsKey(senderIp)) {
                    // Vytvorenie noveho nasho zaznamu arp
                    ArpZapis arpZapis = new ArpZapis(senderMac, senderIp);
                    mojaArp.put(senderIp, arpZapis);
                    //System.out.println("Bol vytvoreny novy zaznam ARP");
                } else {
                    // ip uz existuje
                    ArpZapis arpZapis = mojaArp.get(senderIp);
                    if (arpZapis.set(senderMac, senderIp)) {
                        // bolo to len "osviežene"
                        //System.out.println("Prisiel ARP - rovnaky ako pred <60s");

                    } else {
                        // mac adresa pre danu IP bola zmenena
                        System.out.println("");
                        System.err.println("Prisla zla mac adresa " + targetMac + " pre " + targetIp + "! <60s");
                    }
                }
                System.out.println(mojaArp);
            }
        }
    }
}
