package com.galapea.techblog.volunteer_matching.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO get(String id);

    String create(UserDTO userDTO);

    void update(String id, UserDTO userDTO);

    void delete(String id);

    boolean idExists(String id);

    boolean emailExists(String email);

    public Optional<UserDTO> getOneByEmail(final String email);
}
