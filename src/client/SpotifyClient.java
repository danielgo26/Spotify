package client;

import command.hierarchy.client.ClientCommandCreator;
import command.structure.CommandResult;
import exception.IOChannelException;
import output.Color;
import data.serialization.file.FileDataSaver;
import exception.ConnectionException;
import client.state.ClientState;
import exception.ExceptionHandler;
import logs.LogsManager;
import validation.ObjectValidator;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static command.hierarchy.client.LogoutClientCommand.setLogoutState;
import static output.StandardOutputVisualizer.visualize;

public class SpotifyClient {

    private static final Path LOGS_FILEPATH;

    private final ClientState clientState;
    private final ClientCommandCreator commandCreator;
    private final ExceptionHandler exceptionHandler;

    private final int serverPort;
    private final String serverHost;

    static {
        LOGS_FILEPATH = Paths.get("logs", "clientLog.txt");
    }

    public SpotifyClient(int serverPort, String serverHost) {
        ObjectValidator.validateNotNull(serverHost, "server host");

        this.serverPort = serverPort;
        this.serverHost = serverHost;

        this.clientState = new ClientState();
        this.commandCreator = new ClientCommandCreator();

        this.exceptionHandler = new ExceptionHandler(new LogsManager(new FileDataSaver(LOGS_FILEPATH)));
        this.clientState.setExceptionHandler(this.exceptionHandler);
    }

    public void start() {
        try (SocketChannel clientChannel = SocketChannel.open()) {
            connect(clientChannel);
            this.clientState.setClientChannel(clientChannel);

            printEvalLoop();
        } catch (Exception e) {
            exceptionHandler.handle(e, true);
        }
    }

    private void connect(SocketChannel socketChannel) throws IOException, ConnectionException {
        try {
            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));
        } catch (ConnectException e) {
            throw new ConnectionException("Could not connect to server!");
        }
    }

    private void printEvalLoop() {
        boolean isRunning = true;
        Scanner inputScanner = new Scanner(System.in);

        while (isRunning) {
            String userInput = getUserInput(inputScanner);

            try {
                CommandResult result = this.commandCreator.newCommand(userInput, clientState).execute();
                isRunning = manageCommandResult(result);
            } catch (IOChannelException e) {
                exceptionHandler.handle(e, true, false);

                isRunning = stopProgramRunning();

                visualize("Server unavailable! Exiting the system...", Color.RED, true);
            } catch (Exception e) {
                exceptionHandler.handle(e, true);
            }
        }
    }

    private String getUserInput(Scanner sc) {
        visualize("Enter command: ", Color.WHITE, false);

        return sc.nextLine();
    }

    private boolean manageCommandResult(CommandResult result) {
        boolean continueRunning = !result.isCommandTerminatingProgram();

        if (result.isCommandVisualizing()) {
            visualize(result);
        }

        return continueRunning;
    }

    private boolean stopProgramRunning() {
        if (clientState.isClientLogged()) {
            setLogoutState(clientState);
        }

        return false;
    }

}