package server;

import command.structure.CommandResult;
import data.format.json.JsonFormatHandler;
import data.serialization.file.FileDataLoader;
import data.serialization.file.FileDataSaver;
import exception.LoadDataException;
import exception.SaveDataException;
import exception.runtime.EndOfStreamException;
import exception.ExceptionHandler;
import exception.IOChannelException;
import exception.SelectionKeyProcessingException;
import exception.runtime.InvalidChannelClosingOperationException;
import exception.runtime.ServerStartUpException;
import iochannel.ChannelReader;
import iochannel.ChannelWriter;
import logs.LogsManager;
import command.hierarchy.server.ServerCommandCreator;
import server.model.User;
import server.repository.song.SongRepository;
import server.repository.song.SongRepositoryAPI;
import server.repository.user.UserRepository;
import server.repository.user.UserRepositoryAPI;
import server.lifecycle.StopServerSignalReceiver;
import server.state.ServerState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.Executors;

import static command.hierarchy.server.LogoutClientServerCommand.setLogoutState;

public class SpotifyServer {

    private static final int SERVER_PORT = 8888;
    private static final String SERVER_HOST = "localhost";
    private static final Path LOGS_FILEPATH;
    private static final Path SONGS_CONFIG_FILEPATH;
    private static final Path USERS_CONFIG_FILEPATH;

    private final UserRepositoryAPI userRepository;
    private final SongRepositoryAPI songRepository;

    private final ServerState serverState;

    private Selector serverSelector;
    private final ServerCommandCreator serverCommandCreator;
    private final ExceptionHandler exceptionHandler;
    private StopServerSignalReceiver stopServerSignalReceiver;
    private final ChannelReader channelReader;
    private final ChannelWriter channelWriter;
    private boolean isServerWorking;

    static {
        LOGS_FILEPATH = Paths.get("logs", "serverLog.txt");
        SONGS_CONFIG_FILEPATH = Paths.get("resources", "songs", "songsConfig.json");
        USERS_CONFIG_FILEPATH = Paths.get("resources", "users", "usersConfig.json");
    }

    public SpotifyServer() {
        this.userRepository = new UserRepository();
        this.songRepository = new SongRepository();

        this.serverState = new ServerState();
        this.serverState.setExecutorService(Executors.newVirtualThreadPerTaskExecutor());
        this.serverState.setUserRepository(this.userRepository);
        this.serverState.setSongRepository(this.songRepository);

        this.serverCommandCreator = new ServerCommandCreator();
        this.exceptionHandler = new ExceptionHandler(new LogsManager(new FileDataSaver(LOGS_FILEPATH)));
        this.serverState.setExceptionHandler(this.exceptionHandler);
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
        this.isServerWorking = false;
    }

    public void start() {
        boolean loaded = loadSystemResources();

        if (!loaded) {
            return;
        }

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            operateWithSelectorOn(serverSocketChannel);
        } catch (IOException e) {
            String message = "Unable to start server - could not open the server socket channel!";
            exceptionHandler.handle(new ServerStartUpException(message, e), true);
        } catch (Exception e) {
            exceptionHandler.handle(e, true);
        } finally {
            stop();
        }
    }

    public void stop() {
        this.isServerWorking = false;
        this.serverState.getExecutorService().shutdown();

        if (this.stopServerSignalReceiver != null) {
            this.stopServerSignalReceiver.stop();
        }

        if (serverSelector != null && serverSelector.isOpen()) {
            serverSelector.wakeup();
            closeAllRegisteredChannels();
        }

        tearDownSystemResources();
    }

    public boolean isAlive() {
        return this.isServerWorking;
    }

    private boolean loadSystemResources() {
        try {
            songRepository.loadFrom(new FileDataLoader(SONGS_CONFIG_FILEPATH), new JsonFormatHandler());
            userRepository.loadFrom(new FileDataLoader(USERS_CONFIG_FILEPATH), new JsonFormatHandler());

            return true;
        } catch (LoadDataException e) {
            exceptionHandler.handle(e, true);
        }

        return false;
    }

    private void tearDownSystemResources() {
        try {
            songRepository.saveTo(new FileDataSaver(SONGS_CONFIG_FILEPATH), new JsonFormatHandler());
            userRepository.saveTo(new FileDataSaver(USERS_CONFIG_FILEPATH), new JsonFormatHandler());

        } catch (SaveDataException e) {
            exceptionHandler.handle(e, true);
        }
    }

    private void operateWithSelectorOn(ServerSocketChannel channel) throws IOException, IOChannelException {
        try (Selector selector = Selector.open()) {
            this.serverSelector = selector;

            configureServerSocketChannel(channel, selector);
            startStoppingServerRequestObserver();
            isServerWorking = true;

            loopServerEvaluation();
            closeAllRegisteredChannels();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector)
        throws IOChannelException {
        try {
            channel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new IOChannelException("Could not configure the given channel!", e);
        }
    }

    private void startStoppingServerRequestObserver() {
        stopServerSignalReceiver = new StopServerSignalReceiver(this);

        new Thread(stopServerSignalReceiver).start();
    }

    private void loopServerEvaluation() throws IOException {
        while (isServerWorking) {
            manageSelectorChannels();
        }
    }

    private void manageSelectorChannels() throws IOException {
        try {
            int readyChannels = serverSelector.select();
            if (readyChannels != 0) {
                Iterator<SelectionKey> keyIterator = serverSelector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    handleNextKey(keyIterator);
                    keyIterator.remove();
                }
            }
        } catch (SelectionKeyProcessingException e) {
            exceptionHandler.handle(e, false);
        }
    }

    private void handleNextKey(Iterator<SelectionKey> iterator) throws SelectionKeyProcessingException {
        SelectionKey key = iterator.next();
        this.serverState.setKey(key);

        try {
            if (key.isAcceptable()) {
                accept(serverSelector, key);
            } else if (key.isReadable()) {
                read(key);
            }
        } catch (IOException | IOChannelException e) {
            throw new SelectionKeyProcessingException("Could not process the given selection key!", e);
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();

        if (socketChannel != null) {
            try {
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (Exception e) {
                socketChannel.close();
                throw e;
            }
        }
    }

    private void read(SelectionKey key) throws IOChannelException, IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);

        //Note: enter if client has closed connection
        if (clientInput == null) {
            closeCurrentClientConnection(clientChannel, key);
            return;
        }

        executeClientRequest(clientInput, clientChannel);
    }

    private void executeClientRequest(String clientInput, SocketChannel clientChannel)
        throws IOException, IOChannelException {
        CommandResult result = this.serverCommandCreator.newCommand(clientInput, this.serverState).execute();

        if (result.isCommandSending()) {
            byte[] respond = result.toByteArray();
            channelWriter.writeTo(clientChannel, respond);
        }
    }

    private String getClientInput(SocketChannel clientChannel) {
        try {
            return channelReader.readStringFrom(clientChannel);
        } catch (EndOfStreamException | IOChannelException e) {
            return null;
        }
    }

    private void closeCurrentClientConnection(SocketChannel channel, SelectionKey key) {
        User loggedUser = serverState.getCurrentlyLoggedUser();
        if (loggedUser != null) {
            setLogoutState(loggedUser, serverState);
        }

        try {
            channel.close();
        } catch (IOException e) {
            throw new InvalidChannelClosingOperationException("Could not close socket channel!", e);
        }

        key.cancel();
    }

    private void closeAllRegisteredChannels() {
        if (this.serverSelector != null && this.serverSelector.isOpen()) {
            for (SelectionKey key : this.serverSelector.keys()) {
                try {
                    if (key.isValid()) {
                        key.channel().close();
                    }
                } catch (IOException e) {
                    exceptionHandler.handle(new IOChannelException("Could not close all of the ", e), false);
                }
            }
        }
    }

}
