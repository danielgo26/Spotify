package server.lifecycle;

import output.Color;
import server.SpotifyServer;

import java.util.Scanner;

import static output.StandardOutputVisualizer.visualize;

public class StopServerSignalReceiver implements Runnable {

    private final SpotifyServer server;

    private boolean isRunning;

    public StopServerSignalReceiver(SpotifyServer server) {
        this.server = server;

        isRunning = true;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);

        while (isRunning) {
            String enterCommandMsg = "Press [Enter] to exit server: ";
            visualize(enterCommandMsg, Color.WHITE, false);

            try {
                sc.nextLine();
                server.stop();
            } catch (Exception e) {
                isRunning = false;
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

}