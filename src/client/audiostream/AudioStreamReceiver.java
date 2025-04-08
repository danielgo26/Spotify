package client.audiostream;

import client.state.ClientState;
import exception.runtime.AudioStreamingException;
import exception.ConnectionException;
import exception.IOChannelException;
import exception.runtime.EndOfStreamException;
import exception.runtime.UnsupportedAudioFormatException;
import iochannel.ChannelReader;
import iochannel.ChannelWriter;
import output.Color;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static output.StandardOutputVisualizer.visualize;
import static validation.ObjectValidator.validateNotNull;

public class AudioStreamReceiver implements Runnable {

    private static final int SERVER_PORT = 8888;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_CAPACITY = 1024;

    private volatile boolean isRunning;
    private final ByteBuffer buffer;
    private final SocketChannel socketChannel;
    private final ChannelReader channelReader;
    private final ChannelWriter channelWriter;

    private final String songKey;
    private final ClientState clientState;

    public AudioStreamReceiver(String songKey, ClientState clientState) throws ConnectionException {
        validateNotNull(songKey, "song key");
        validateNotNull(songKey, "client state");

        this.songKey = songKey;
        this.clientState = clientState;

        this.isRunning = true;
        this.buffer = ByteBuffer.allocateDirect(BUFFER_CAPACITY);
        this.socketChannel = initializeSocketChannel();
        this.channelReader = new ChannelReader();
        this.channelWriter = new ChannelWriter();
    }

    @Override
    public void run() {
        clientState.setIsClientStreaming(true);

        try {
            channelWriter.writeTo(socketChannel, "play " + songKey);
            startStreamingLoop(getSourceDataLine());
            endStream();
        } catch (EndOfStreamException e) {
            handleEndOfStream();
        } catch (UnsupportedAudioFormatException e) {
            handleUnsupportedAudioFormat(e);
        } catch (LineUnavailableException | IOException | IOChannelException e) {
            handleStreamingError(e);
        } finally {
            stop();
        }
    }

    public SocketChannel getSocketChannel() {
        return this.socketChannel;
    }

    public void stop() {
        isRunning = false;
        clientState.stopStreaming();
    }

    private SocketChannel initializeSocketChannel() throws ConnectionException {
        try {
            SocketChannel socket = SocketChannel.open();
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            return socket;
        } catch (IOException e) {
            throw new ConnectionException("Could not connect to server in order to stream music!", e);
        }
    }

    private void startStreamingLoop(SourceDataLine sourceLine) throws IOException, IOChannelException {
        while (isRunning) {
            channelWriter.writeTo(socketChannel, "stream");

            byte[] audioData = channelReader.readBytesFrom(socketChannel);

            if (audioData.length > 0) {
                sourceLine.write(audioData, 0, audioData.length);
            } else {
                isRunning = false;
            }
        }

        closeSourceLine(sourceLine);
    }

    private SourceDataLine getSourceDataLine() throws IOException, LineUnavailableException {
        AudioFormat format = readFormat(socketChannel);

        try {
            SourceDataLine sourceLine = AudioSystem.getSourceDataLine(format);
            sourceLine.open(format);
            sourceLine.start();

            return sourceLine;
        } catch (IllegalArgumentException e) {
            throw new UnsupportedAudioFormatException("Unsupported audio format!", e);
        }
    }

    private AudioFormat readFormat(SocketChannel socket) throws IOException {
        buffer.clear();
        socket.read(buffer);

        buffer.flip();
        float sampleSize = buffer.getFloat();
        int sampleSizeInBits = buffer.getInt();
        int channels = buffer.getInt();
        int frameSize = buffer.getInt();
        float frameRate = buffer.getFloat();
        boolean bigEndian = buffer.get() == 1;
        String encodingStr = StandardCharsets.UTF_8.decode(buffer).toString();
        AudioFormat.Encoding encoding = new AudioFormat.Encoding(encodingStr);

        return new AudioFormat(encoding, sampleSize, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
    }

    private void endStream() throws IOChannelException, IOException {
        channelWriter.writeTo(socketChannel, "stop");

        if (socketChannel.isOpen()) {
            socketChannel.close();
        }
    }

    private void handleEndOfStream() {
        visualize("Song has ended!", Color.GREEN, true);

        visualize("Enter command: ", Color.WHITE, false);
    }

    private void handleUnsupportedAudioFormat(UnsupportedAudioFormatException e) {
        clientState.getExceptionHandler().handle(e, true, false);

        visualize("Enter command: ", Color.WHITE, false);
    }

    private void handleStreamingError(Exception e) {
        clientState.getExceptionHandler().handle(
            new AudioStreamingException("Audio streaming error!", e), true);

        visualize("Enter command: ", Color.WHITE, false);
    }

    private void closeSourceLine(SourceDataLine sourceDataLine) {
        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

}
