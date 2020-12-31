package org.multiplay.client;

import org.multiplay.ConnectionToken;
import org.multiplay.DeviceToken;
import org.multiplay.GameToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Connection {
    private final GameToken gameToken;
    private final DeviceToken deviceToken;
    private final URL serverURL;
    private final ExecutorService executor;
    private ConnectionToken connectionToken = null;

    Connection(GameToken gameToken, DeviceToken deviceToken, URL serverURL, ExecutorService executor) {
        this.gameToken = gameToken;
        this.deviceToken = deviceToken;
        this.serverURL = serverURL;
        if (executor != null) {
            this.executor = executor;
        }
        else {
            this.executor = Executors.newCachedThreadPool();
        }
    }

    /**
     * Connect to the game's instance of the multiplay service.
     * @return {Promise} a promise that is fulfilled when the game is connected.
     */
    Future<?> connect() {
        String protocol = serverURL.getProtocol();
        String host = serverURL.getHost();
        int port = serverURL.getPort();
        return executor.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL connectUrl = new URL(protocol, host, port, "connect.json?game=" + gameToken.toString());
                    conn = (HttpURLConnection) connectUrl.openConnection();
                    // TODO: grab the response as JSON and get the connection token
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
                finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
    }
}
