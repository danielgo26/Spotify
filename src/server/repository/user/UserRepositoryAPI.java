package server.repository.user;

import server.model.User;
import server.repository.SerializableRepository;

import java.util.Set;

public interface UserRepositoryAPI extends SerializableRepository {

    boolean contains(String idToken);

    boolean contains(UserCredentials userCredentials);

    User getUser(String idToken);

    User getUser(UserCredentials userCredentials);

    Set<User> getAllUsers();

    boolean addNewUser(User user);

}
