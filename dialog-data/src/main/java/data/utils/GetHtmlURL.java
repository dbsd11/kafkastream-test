package data.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class GetHtmlURL {
    private static List<Proxy> proxyList = new ArrayList<>(Collections.EMPTY_LIST);
    private static List<Proxy> abandomProxy = new ArrayList<>(Collections.EMPTY_LIST);
    private static int timeout = 3000;
    private static int duple = 6;

    public static void initProxy(String ipString) {
        String[] ipS = ipString.split(",");
        Proxy proxy = null;
        Socket socket = null;
        for (String ip : ipS) {
            socket = null;
            if (ip.trim().length() == 0) {
                continue;
            }
            try {
                socket = new Socket();
                socket.connect(
                        new InetSocketAddress(ip.split(":")[0], Integer
                                .parseInt(ip.split(":")[1])), timeout);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if ((socket != null) && (socket.isConnected())) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                proxy = new Proxy(Type.HTTP, socket.getRemoteSocketAddress());
                proxyList.add(proxy);
            }
        }
        log.info("proxyList size:" + proxyList.size());
    }

    public GetHtmlURL(int timeout, int duple) {
        this.timeout = timeout;
        this.duple = duple;
    }

    public String GetHtml(String path) {
        log.info(path);
        String html = "";
        if (!path.contains("haodf.com")) {
            return html;
        }
        URLConnection connection = null;
        Proxy proxy = null;
        int count = 0;
        while (count < duple) {
            try {
                proxy = getAvalableProxy();
                if (proxy != null) {
                    connection = new URL(path).openConnection(proxy);
                    ++count;
                }
                if (proxy == null) {
                    connection = new URL(path).openConnection();
                }
                connection.setConnectTimeout(2 * timeout);
                connection.setReadTimeout(4 * timeout);
                break;
            } catch (Exception e) {
                log.info(e.getMessage(), e);
                abandomProxy.add(proxy);
                count++;
            }
        }
        if (count < duple) {
            try {
                InputStream ins = connection.getInputStream();
                if (ins != null) {
                    byte[] b = new byte[500];
                    while (ins.read(b) != -1) {
                        html += new String(b, "GBK");
                    }
                    html += new String(b, "GBK");
                    ins.close();
                }
            } catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }
        return html;
    }

    private Proxy getAvalableProxy() {
        Proxy proxy = null;
        int count = 0;
        do {
            proxy = proxyList.get((int) (Math.random() * proxyList.size()));
            count++;
        } while (abandomProxy.contains(proxy) && (count < 10));
        return new Proxy(Type.HTTP, proxy.address());
    }

}
