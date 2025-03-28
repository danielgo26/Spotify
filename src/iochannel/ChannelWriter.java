package iochannel;

import exception.IOChannelException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static validation.ObjectValidator.validateNotNull;

public class ChannelWriter {

    private static final int BUFFER_CAPACITY = 1024;
    private final ByteBuffer bufferForWriting;

    public ChannelWriter() {
        bufferForWriting = ByteBuffer.allocateDirect(BUFFER_CAPACITY);
    }

    public void writeTo(SocketChannel channel, String message) throws IOChannelException {
        validateNotNull(message, "string message");

        writeTo(channel, message.getBytes(StandardCharsets.UTF_8));
    }

    public void writeTo(SocketChannel channel, byte[] dataArray) throws IOChannelException {
        validateNotNull(channel, "socket channel");

        bufferForWriting.clear();
        bufferForWriting.put(dataArray);
        bufferForWriting.flip();

        try {
            channel.write(bufferForWriting);
        } catch (IOException e) {
            throw new IOChannelException("Cannot write to channel!", e);
        }
    }

}