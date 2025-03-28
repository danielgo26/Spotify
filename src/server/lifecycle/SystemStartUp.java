package server.lifecycle;

import server.SpotifyServer;

public class SystemStartUp {

    public static void main(String... args) {
        new SpotifyServer().start();
    }

}