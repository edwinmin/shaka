package com.edwin.shakautils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author jinming.wu
 * @date 2015-5-24
 */
public class IPHelper {

    private static final String NULL = "";

    public static String getHostIP() {

        String SERVER_IP = NULL;

        try {

            Enumeration<?> netInterfaces = NetworkInterface.getNetworkInterfaces();
            List<NetworkInterface> networklist = Lists.newArrayList();

            while (netInterfaces.hasMoreElements()) {

                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();

                // 过滤lo网卡
                if (ni.isLoopback()) {
                    continue;
                }
                networklist.add(0, ni);
            }

            for (NetworkInterface list : networklist) {

                Enumeration<?> cardipaddress = list.getInetAddresses();
                while (cardipaddress.hasMoreElements()) {
                    InetAddress ip = (InetAddress) cardipaddress.nextElement();
                    if (!ip.isLoopbackAddress()) {
                        if (ip.getHostAddress().equalsIgnoreCase("127.0.0.1")) {
                            continue;
                        }
                    }
                    if (ip instanceof Inet6Address) {
                        continue;
                    }
                    if (ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }

                return InetAddress.getLocalHost().getHostAddress();
            }
        } catch (Exception e) {
            try {
                SERVER_IP = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e1) {
                SERVER_IP = NULL;
            }
        }
        return SERVER_IP;
    }
}
