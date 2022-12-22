package com.spotify.idm.business.service;

import com.spotify.idm.persistence.entity.Role;
import com.spotify.idm.persistence.entity.User;
import com.spotify.idm.persistence.repository.RoleRepository;
import com.spotify.idm.persistence.repository.UserRepository;
import com.spotify.idm.AddRoleRequest;
import com.spotify.idm.CreateUserRequest;
import com.spotify.idm.DeleteRoleRequest;
import com.spotify.idm.DeleteUserRequest;
import com.spotify.idm.GetUserRequest;
import com.spotify.idm.LoginRequest;
import com.spotify.idm.RolesResponse;
import com.spotify.idm.UpdatePasswordRequest;
import com.spotify.idm.UserResponse;
import com.spotify.idm.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {
        User user = toEntity(request);

        return toUserResponse(userRepository.save(user));
    }

    public UserResponse updatePassword(UpdatePasswordRequest request) {
        User user = findUserOrElseThrowException(request.getUserId());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return toUserResponse(userRepository.save(user));
    }

    public UserResponse addRole(AddRoleRequest request) {
        User user = findUserOrElseThrowException(request.getUserId());
        Role role = findRoleOrElseThrowException(request.getRoleId());

        user.getRoles().add(role);

        return toUserResponse(userRepository.save(user));
    }

    public UserResponse deleteRole(DeleteRoleRequest request) {
        User user = findUserOrElseThrowException(request.getUserId());

        for (Role role : user.getRoles()) {
            if (role.getId() == request.getRoleId()) {
                user.getRoles().remove(role);

                return toUserResponse(userRepository.save(user));
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public UserResponse getUser(GetUserRequest request) {
        User user = findUserOrElseThrowException(request.getUserId());

        return toUserResponse(user);
    }

    public UsersResponse getUsers() {
        List<User> users = userRepository.findAll();

        return toUsersResponse(users);
    }

    public UserResponse deleteUser(DeleteUserRequest request) {
        User user = findUserOrElseThrowException(request.getUserId());

        userRepository.deleteById(request.getUserId());

        return toUserResponse(user);
    }

    public RolesResponse getRoles() {
        List<Role> roles = roleRepository.findAll();

        return toRolesResponse(roles);
    }

    public com.spotify.idm.User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)
        );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return toUserDTO(user);
    }

    private User toEntity(CreateUserRequest request) {
        Role clientRole = roleRepository.findById(1).orElseThrow(
                RuntimeException::new
        );

        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(clientRole)))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();

        response.setUser(toUserDTO(user));

        return response;
    }

    private UsersResponse toUsersResponse(List<User> users) {
        UsersResponse response = new UsersResponse();

        response.getUsers().addAll(users.stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList())
        );

        return response;
    }

    private RolesResponse toRolesResponse(List<Role> roles) {
        RolesResponse response = new RolesResponse();

        response.getRoles().addAll(roles.stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList())
        );

        return response;
    }

    private com.spotify.idm.User toUserDTO(User user) {
        List<com.spotify.idm.Role> roles = user.getRoles().stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList());

        com.spotify.idm.User userDTO = new com.spotify.idm.User();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.getRoles().addAll(roles);

        return userDTO;
    }

    private com.spotify.idm.Role toRoleDTO(Role role) {
        com.spotify.idm.Role roleResponse = new com.spotify.idm.Role();

        roleResponse.setId(role.getId());
        roleResponse.setName(role.getName());

        return roleResponse;
    }


    private User findUserOrElseThrowException(Integer userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    private Role findRoleOrElseThrowException(Integer roleId) {
        return roleRepository.findById(roleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }
}
