package server.state;

import exception.ExceptionHandler;
import server.model.User;
import server.repository.song.SongRepositoryAPI;
import server.repository.user.UserRepositoryAPI;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

//Note: here we intentionally allow the fields to take null values
public class ServerState {

    private UserRepositoryAPI userRepository = null;
    private SongRepositoryAPI songRepository = null;
    private ExecutorService executorService = null;
    private ExceptionHandler exceptionHandler;
    private SelectionKey key = null;

    public UserRepositoryAPI getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepositoryAPI userRepository) {
        this.userRepository = userRepository;
    }

    public SongRepositoryAPI getSongRepository() {
        return songRepository;
    }

    public void setSongRepository(SongRepositoryAPI songRepository) {
        this.songRepository = songRepository;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public User getCurrentlyLoggedUser() {
        if (key == null) {
            return null;
        }

        String idToken = (String) key.attachment();
        if (idToken == null) {
            return null;
        }

        return userRepository.getUser(idToken);
    }

}