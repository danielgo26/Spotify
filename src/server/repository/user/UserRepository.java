package server.repository.user;

import data.serialization.DataLoader;
import data.serialization.DataSaver;
import exception.DataDeserializationException;
import exception.DataSerializationException;
import exception.InvalidTextFormatException;
import exception.LoadDataException;
import exception.SaveDataException;
import data.format.TextFormatHandler;
import server.idtoken.IdTokenGenerator;
import server.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Verify.verifyNotNull;
import static validation.ObjectValidator.validateNotNull;

public class UserRepository implements UserRepositoryAPI {

    private static final IdTokenGenerator ID_TOKEN_GENERATOR;

    private final HashMap<String, User> users;

    static {
        ID_TOKEN_GENERATOR = new IdTokenGenerator();
    }

    public UserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public boolean contains(String idToken) {
        validateNotNull(idToken, "user id token");

        return users.containsKey(idToken);
    }

    @Override
    public boolean contains(UserCredentials userCredentials) {
        validateNotNull(userCredentials, "user credentials");

        String idToken = ID_TOKEN_GENERATOR.generateCheckSum(userCredentials.getCredentialsString());

        return contains(idToken);
    }

    @Override
    public User getUser(String idToken) {
        validateNotNull(idToken, "user id token");

        return users.get(idToken);
    }

    @Override
    public User getUser(UserCredentials userCredentials) {
        validateNotNull(userCredentials, "user credentials");

        String idToken = ID_TOKEN_GENERATOR.generateCheckSum(userCredentials.getCredentialsString());

        return getUser(idToken);
    }

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(users.values());
    }

    @Override
    public boolean addNewUser(User user) {
        validateNotNull(user, "user");

        String idToken = ID_TOKEN_GENERATOR.generateCheckSum(user.getUserCredentials().getCredentialsString());

        if (this.contains(idToken)) {
            return false;
        }

        users.put(idToken, user);
        return true;
    }

    @Override
    public void loadFrom(DataLoader loader, TextFormatHandler handler) throws LoadDataException {
        verifyNotNull(loader, "users data loader");
        verifyNotNull(handler, "text format handler");

        try {
            String loadedUsersData = loader.load();
            users.putAll(handler.loadMapFromFormat(loadedUsersData, String.class, User.class));
        } catch (DataDeserializationException | InvalidTextFormatException e) {
            throw new LoadDataException("Could not load songs into the system!", e);
        }
    }

    @Override
    public void saveTo(DataSaver saver, TextFormatHandler handler) throws SaveDataException {
        verifyNotNull(saver, "users data saver");
        verifyNotNull(handler, "text format handler");

        try {
            String formattedUsersData = handler.getFormat(users);
            saver.save(formattedUsersData, false);
        } catch (DataSerializationException e) {
            throw new SaveDataException("Could not save the users from system!", e);
        }
    }

}