package server.audiostream;

import exception.IOChannelException;
import exception.runtime.AudioStreamingException;
import exception.runtime.InvalidChannelClosingOperationException;
import exception.runtime.InvalidStreamingClosingOperationException;
import exception.runtime.UserNotExistingException;
import iochannel.ChannelReader;
import iochannel.ChannelWriter;
import server.model.User;
import server.state.ServerState;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.rmi.UnexpectedException;

import static validation.ObjectValidator.validateNotNull;

public class AudioStreamSender implements Runnable {

    private static final String INPUT_SPLIT_PATTERN = " ";
    private static final String SONGS_FILEPATH = "resources\\songs\\%s.wav";
    private static final int BUFFER_SIZE = 1024;
    private final ByteBuffer streamingBuffer;

    private final String songKey;
    private final ServerState serverState;

    private final SocketChannel streamingChannel;
    private final ChannelReader channelReader;
    private final ChannelWriter channelWriter;
    private final User userStreamingClient;
    private AudioInputStream audioStream;
    private boolean isStreaming;

    public AudioStreamSender(String songKey, ServerState serverState) {
        validateNotNull(songKey, "music title");
        validateNotNull(serverState, "server state");

        this.serverState = serverState;
        this.songKey = songKey;

        this.streamingChannel = (SocketChannel) serverState.getKey().channel();
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
        this.userStreamingClient = getUserStreamingClient();
        this.streamingBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        this.isStreaming = false;
    }

    @Override
    public void run() {
        try {
            audioStream = createAudioStream();
            sendFormat(streamingBuffer, audioStream.getFormat(), streamingChannel);

            isStreaming = true;
            while (isStreaming) {
                stream();
            }
        } catch (IOChannelException | IOException e) {
            serverState.getExceptionHandler().handle(e, false);
        } catch (Exception e) {
            String message = "Unexpected error occurred!";

            serverState.getExceptionHandler().handle(new UnexpectedException(message, e), false);
        } finally {
            setStopState();
        }
    }

    public void stop() {
        if (!streamingChannel.isOpen()) {
            return;
        }

        isStreaming = false;
        String stopCommand = "stop";

        try {
            streamingChannel.write(ByteBuffer.wrap(stopCommand.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            serverState.getExceptionHandler().handle(
                new InvalidStreamingClosingOperationException("Could not stop streaming!", e), false);
        }
    }

    private User getUserStreamingClient() {
        String idToken = (String) serverState.getKey().attachment();
        User userToBeStreaming = serverState.getUserRepository().getUser(idToken);

        if (userToBeStreaming == null) {
            throw new UserNotExistingException("The user that to be streaming cannot be found in the system!");
        }

        return userToBeStreaming;
    }

    private AudioInputStream createAudioStream() throws IOException {
        File pathToMusicFile = new File(String.format(SONGS_FILEPATH, songKey));

        AudioInputStream audioStream;
        try {
            audioStream = AudioSystem.getAudioInputStream(pathToMusicFile);
        } catch (UnsupportedAudioFileException e) {
            throw new AudioStreamingException("The given audio file is not supported by the system!", e);
        }

        userStreamingClient.setAudioStreamSender(this);

        return audioStream;
    }

    private void stream() throws IOException, IOChannelException {
        String[] clientInputArgs = getClientInputArgs(streamingChannel);

        switch (clientInputArgs[0]) {
            case "stream" -> {
                byte[] byteStreamArray = new byte[BUFFER_SIZE];

                if (audioStream.read(byteStreamArray, 0, byteStreamArray.length) == -1) {
                    isStreaming = false;
                    return;
                }
                channelWriter.writeTo(streamingChannel, byteStreamArray);
            }
            case "stop" -> isStreaming = false;
        }
    }

    private String[] getClientInputArgs(SocketChannel clientChannel) throws IOChannelException {
        byte[] clientInputBytes = channelReader.readBytesFrom(clientChannel);

        String clientInput = new String(clientInputBytes, StandardCharsets.UTF_8);

        return clientInput.split(INPUT_SPLIT_PATTERN);
    }

    private void sendFormat(ByteBuffer buffer, AudioFormat format, SocketChannel clientSocketChannel)
        throws IOException {
        AudioFormat.Encoding encoding = format.getEncoding();
        float sampleRate = format.getSampleRate();
        int sampleSizeInBits = format.getSampleSizeInBits();
        int channels = format.getChannels();
        int frameSize = format.getFrameSize();
        float frameRate = format.getFrameRate();
        boolean bigEndian = format.isBigEndian();

        buffer.clear();
        buffer.putFloat(sampleRate);
        buffer.putInt(sampleSizeInBits);
        buffer.putInt(channels);
        buffer.putInt(frameSize);
        buffer.putFloat(frameRate);
        buffer.put((byte) (bigEndian ? 1 : 0));
        buffer.put(encoding.toString().getBytes(StandardCharsets.UTF_8));

        buffer.flip();
        clientSocketChannel.write(buffer);
    }

    private void setStopState() {
        closeAudioStream();
        closeStreamingChannel();

        userStreamingClient.setAudioStreamSender(null);
        isStreaming = false;
    }

    private void closeAudioStream() {
        try {
            audioStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeStreamingChannel() {
        try {
            if (streamingChannel.isOpen()) {
                streamingChannel.close();
            }
        } catch (IOException e) {
            throw new InvalidChannelClosingOperationException("Could not close streaming channel!", e);
        }
    }

}