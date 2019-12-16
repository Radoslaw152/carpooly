package bg.fmi.piss.course.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import bg.fmi.piss.course.dao.UserRepository;
import bg.fmi.piss.course.exceptions.EntityNotFoundException;
import bg.fmi.piss.course.models.User;
import bg.fmi.piss.course.services.UserService;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;
    private InMemoryUserDetailsManager detailsManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.detailsManager = new InMemoryUserDetailsManager();
    }

    @PostConstruct
    public void loadAccounts() {
        List<User> users = getAllUsers();
        User admin = new User(null, "admin",
                "admin",
                "admin",
                passwordEncoder.encode("admin"),
                "administrator",
                new ArrayList<>());
        if (users.isEmpty()) {
            this.detailsManager.createUser(admin);
            this.userRepository.save(admin);
        } else {
            users.forEach(user -> this.detailsManager.createUser(user));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User result = userRepository.insert(user);
        detailsManager.createUser(result);
        return result;
    }

    @Override
    public User updateUser(User user) {
        User result = userRepository.save(user);
        detailsManager.updateUser(user);
        return result;
    }

    @Override
    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("There is no entity with %s ID", id)));
    }

    @Override
    public User deleteUser(String id) {
        User user = getUser(id);
        userRepository.deleteById(id);
        detailsManager.deleteUser(user.getUsername());
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails result = getAllUsers()
                .stream()
                .filter(user -> user.getEmail().equals(username))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There is no entity with %s username", username)));
        return result;
    }
}
