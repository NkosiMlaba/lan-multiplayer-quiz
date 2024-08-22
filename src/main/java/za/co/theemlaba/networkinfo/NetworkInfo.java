package za.co.theemlaba.networkinfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;


/**
 * Utility class to retrieve network information.
 */
public class NetworkInfo {

    /**
     * Retrieves the primary IPv4 address of the host machine.
     * @return The primary IPv4 address as a string.
     */
    public static String main(String[] args) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaces)) {
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Checks for IPv4 addresses only
                    if (addr.getAddress().length == 4) {
                        return addr.getHostAddress();
                    }
                }
            }

            return "";
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}