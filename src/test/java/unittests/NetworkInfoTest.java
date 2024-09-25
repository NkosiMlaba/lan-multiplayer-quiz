package unittests;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import za.co.theemlaba.server.networkinfo.NetworkInfo;

class NetworkInfoTest {


    @Test
    void testMainWithNoValidInterfaces() throws SocketException {
        try (MockedStatic<NetworkInterface> mockedNetworkInterface = Mockito.mockStatic(NetworkInterface.class)) {
            mockedNetworkInterface.when(NetworkInterface::getNetworkInterfaces).thenReturn(Collections.emptyEnumeration());
            
            String result = NetworkInfo.main(new String[]{});
            assertEquals("", result);
        }
    }

    @Test
    void testMainWithSocketException() throws SocketException {
        try (MockedStatic<NetworkInterface> mockedNetworkInterface = Mockito.mockStatic(NetworkInterface.class)) {
            mockedNetworkInterface.when(NetworkInterface::getNetworkInterfaces).thenThrow(new SocketException("Test exception"));
            
            assertThrows(RuntimeException.class, () -> NetworkInfo.main(new String[]{}));
        }
    }

    @Test
    void testMainWithOnlyIPv6Addresses() throws SocketException {
        NetworkInterface mockInterface = mock(NetworkInterface.class);
        InetAddress mockAddress = mock(InetAddress.class);
        
        when(mockInterface.isLoopback()).thenReturn(false);
        when(mockInterface.isUp()).thenReturn(true);
        when(mockAddress.getAddress()).thenReturn(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
        
        Enumeration<NetworkInterface> interfaces = Collections.enumeration(Collections.singletonList(mockInterface));
        Enumeration<InetAddress> addresses = Collections.enumeration(Collections.singletonList(mockAddress));
        
        when(mockInterface.getInetAddresses()).thenReturn(addresses);
        
        try (MockedStatic<NetworkInterface> mockedNetworkInterface = Mockito.mockStatic(NetworkInterface.class)) {
            mockedNetworkInterface.when(NetworkInterface::getNetworkInterfaces).thenReturn(interfaces);
            
            String result = NetworkInfo.main(new String[]{});
            assertEquals("", result);
        }
    }
}
