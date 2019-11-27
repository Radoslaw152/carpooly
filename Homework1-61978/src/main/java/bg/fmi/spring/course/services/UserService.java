package bg.fmi.spring.course.services;


import java.util.List;

import bg.fmi.spring.course.models.User;

public interface UserService {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUser(String id);

    User getUserByUsername(String username);

    User deleteUser(String id);
}
