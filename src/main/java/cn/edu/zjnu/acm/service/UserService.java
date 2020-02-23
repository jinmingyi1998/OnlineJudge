package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.UserProfile;
import cn.edu.zjnu.acm.repo.user.UserProfileRepository;
import cn.edu.zjnu.acm.repo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserProfileRepository userProfileRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public User registerUser(User u) {
        if (userRepository.findByUsername(u.getUsername()).isPresent())
            return null;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        u.setPassword(encoder.encode(u.getPassword()));
        UserProfile userProfile = new UserProfile();
        u = userRepository.save(u);
        if (u == null)
            return null;
        userProfile.setUser(u);
        userProfileRepository.save(userProfile);
        return u;
    }

    public void updateUserInfo(User user) {
        userRepository.updateUser(user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getIntro());
    }

    public boolean checkPassword(String password, String correct) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, correct);
    }

    public User loginUser(User user) {
        User u = userRepository.findByUsername(user.getUsername()).orElse(null);
        if (u == null)
            return null;
        if (checkPassword(user.getPassword(), u.getPassword()))
            return u;
        return null;
    }

    public List<User> userList() {
        List<User> userList = userRepository.findAll();
        return userList;
    }
}
