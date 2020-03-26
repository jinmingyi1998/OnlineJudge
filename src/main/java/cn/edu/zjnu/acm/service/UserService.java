package cn.edu.zjnu.acm.service;

import cn.edu.zjnu.acm.entity.Teacher;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.entity.UserProfile;
import cn.edu.zjnu.acm.repo.user.TeacherRepository;
import cn.edu.zjnu.acm.repo.user.UserProfileRepository;
import cn.edu.zjnu.acm.repo.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TeacherRepository teacherRepository;

    @PostConstruct
    public void AddAdministratorAccount(){
        if (!userRepository.findByUsername("administrator").isPresent()){
            String password = System.getenv("ADMIN_PASSWORD");
            if (password==null){
                password="123456";
            }
            User admin = new User("administrator",password,"Administrator","email@address.com","");
            admin = registerUser(admin);
            Teacher teacher = new Teacher(admin,Teacher.ADMIN);
            teacherRepository.save(teacher);
        }
    }

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository, TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.teacherRepository = teacherRepository;
    }

    public Page<User> searchUser(int page, int size, String search) {
        return userRepository.findAllByUsernameContains(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")), search);
    }

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
        u = setUserPassword(u,u.getPassword());
        UserProfile userProfile = new UserProfile();
        u = userRepository.save(u);
        if (u == null)
            return null;
        userProfile.setUser(u);
        userProfileRepository.save(userProfile);
        return u;
    }

    public User setUserPassword(User u, String pwd) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        u.setPassword(encoder.encode(pwd));
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

    /**
     * get user's permission
     *
     * @param user
     * @return -1 if normal users, otherwise return teacher privileges.
     */
    public int getUserPermission(User user) {
        if (!teacherRepository.existsByUser(user))
            return -1;
        return teacherRepository.findByUser(user).get().getPrivilege();
    }
}
