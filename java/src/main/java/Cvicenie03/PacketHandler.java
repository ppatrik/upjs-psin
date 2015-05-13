package Cvicenie03;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler implements PcapPacketHandler<String> {
    Map<String, String> mojaArp = new HashMap<String, String>();

    public void nextPacket(PcapPacket packet, String user) {
        Arp arp = new Arp();

        if (packet.hasHeader(arp)) {
            if (arp.operation() == Arp.OpCode.REQUEST) {
                if (!mojaArp.containsKey(JnetpcapUtilities.getIPFromBytes(arp.tpa()))) {
                    mojaArp.put(JnetpcapUtilities.getIPFromBytes(arp.tpa()), JnetpcapUtilities.getMacFromBytes(arp.tha()));
                    System.out.println("len novy zapis");
                } else {
                    String predchadzajucaMac = mojaArp.get(JnetpcapUtilities.getIPFromBytes(arp.tpa()));
                    if (predchadzajucaMac.equals(JnetpcapUtilities.getMacFromBytes(arp.tha()))) {
                        System.out.printf("Mac v ARP sa nezmenila ");
                    } else {
                        System.err.printf("Mac v ARP sa zmenila ");
                    }
                }

                System.out.printf("HW [%s] PROTOCOL [%s] HW [%s] PROTOCOL [%s]\n",
                        JnetpcapUtilities.getMacFromBytes(arp.sha()),
                        JnetpcapUtilities.getIPFromBytes(arp.spa()),
                        JnetpcapUtilities.getMacFromBytes(arp.tha()),
                        JnetpcapUtilities.getIPFromBytes(arp.tpa())
                );
            }
        }
    }
}
