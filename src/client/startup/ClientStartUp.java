package client.startup;

import client.SpotifyClient;

public class ClientStartUp {

    private static final int SERVER_PORT = 8888;
    private static final String SERVER_HOST = "localhost";

    public static void main(String... args) {
        new SpotifyClient(SERVER_PORT, SERVER_HOST).start();
    }

}