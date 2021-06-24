package com.example.calendar.api;

import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.User;
import com.example.calendar.business.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/add-user")
    public void addUser(@RequestBody User user) {
        this.userService.saveUser(user);
    }

    @GetMapping(value = "/{selectedProject}/get-users")
    public List<User> getAllUsers(@PathVariable ProjectType selectedProject) {
        return userService.getAllUsers(selectedProject);
    }

    @PostMapping(value = "/delete-user")
    public void deleteUser(@RequestBody String userName) {
        userService.deleteUser(userName);
    }

    @GetMapping(value = "/search-user")
    public Page<User> getAllUsers(
            @RequestParam(name = "filter", required = false) String filterValue,
            @RequestParam(name = "showUsersWithPermissions") Boolean showUsersWithPermissions,
            @RequestParam(name = "projectType", required = true) ProjectType projectType,
            @PageableDefault(value = 25) Pageable page) {
        return userService.getAllUser(filterValue, projectType, page);
    }
}
