package client.state;

import client.audiostream.AudioStreamReceiver;
import exception.ExceptionHandler;

import java.nio.channels.SocketChannel;

public class ClientState {

    private SocketChannel clientChannel = null;
    private boolean isClientLogged = false;
    private String idToken = null;
    private AudioStreamReceiver audioStreamReceiver = null;
    private ExceptionHandler exceptionHandler;
    private String probableUsername = null;
    private Boolean isStreaming = false;

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    public void setClientChannel(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public boolean isClientLogged() {
        return isClientLogged;
    }

    public void setClientLogged(boolean clientLogged) {
        isClientLogged = clientLogged;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public AudioStreamReceiver getAudioStreamReceiver() {
        return audioStreamReceiver;
    }

    public void setAudioStreamReceiver(AudioStreamReceiver audioStreamReceiver) {
        this.audioStreamReceiver = audioStreamReceiver;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public String getProbableUsername() {
        return probableUsername;
    }

    public void setProbableUsername(String probableUsername) {
        this.probableUsername = probableUsername;
    }

    public Boolean isClientStreaming() {
        return isStreaming;
    }

    public void setIsClientStreaming(Boolean streaming) {
        isStreaming = streaming;
    }

    public void stopStreaming() {
        isStreaming = false;
        audioStreamReceiver = null;
    }

}