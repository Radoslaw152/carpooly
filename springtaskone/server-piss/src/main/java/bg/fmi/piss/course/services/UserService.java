package bg.fmi.piss.course.services;


import java.util.List;

import bg.fmi.piss.course.models.User;

public interface UserService {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUser(String id);

    User deleteUser(String id);
}
