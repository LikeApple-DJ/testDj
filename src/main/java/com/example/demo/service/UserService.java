package com.example.demo.service;
import com.example.demo.model.User;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.jwtUtil = jwtUtil;
    }
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User(req.getUsername(), passwordEncoder.encode(req.getPassword()),
                req.getPersonType(), req.getPersonLevel(), req.getPersonDept());
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        AuthResponse resp = new AuthResponse(user.getId(), user.getUsername(), token);
        resp.setPersonType(user.getPersonType());
        resp.setPersonLevel(user.getPersonLevel());
        resp.setPersonDept(user.getPersonDept());
        return resp;
    }
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        AuthResponse resp = new AuthResponse(user.getId(), user.getUsername(), token);
        resp.setPersonType(user.getPersonType());
        resp.setPersonLevel(user.getPersonLevel());
        resp.setPersonDept(user.getPersonDept());
        return resp;
    }
}