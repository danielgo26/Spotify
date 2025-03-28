package iochannel;

import exception.runtime.EndOfStreamException;
import exception.IOChannelException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static validation.ObjectValidator.validateNotNull;

public class ChannelReader {

    private static final int BUFFER_CAPACITY = 1024;
    private final ByteBuffer bufferForReading;

    public ChannelReader() {
        this.bufferForReading = ByteBuffer.allocateDirect(BUFFER_CAPACITY);
    }

    public String readStringFrom(SocketChannel channel) throws IOChannelException {
        return new String(getBytes(channel));
    }

    public byte[] readBytesFrom(SocketChannel channel) throws IOChannelException {
        return getBytes(channel);
    }

    private byte[] getBytes(SocketChannel channel) throws IOChannelException {
        validateNotNull(channel, "socket channel");

        bufferForReading.clear();
        int bytesRead = readFromChannel(channel);

        if (bytesRead < 0) {
            throw new EndOfStreamException("There are no available bytes to read! End of stream reached!");
        }

        bufferForReading.flip();
        byte[] data = new byte[bufferForReading.remaining()];
        bufferForReading.get(data);

        return data;
    }

    private int readFromChannel(SocketChannel channel) throws IOChannelException {
        try {
            return channel.read(bufferForReading);
        } catch (IOException e) {
            throw new IOChannelException("Cannot read from channel!", e);
        }
    }

}