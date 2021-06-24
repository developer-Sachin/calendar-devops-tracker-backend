package com.example.calendar.business.users;

import com.example.calendar.business.devops.DevopsDataService;
import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.User;
import com.example.calendar.business.domains.UserEffort;
import com.example.calendar.business.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserEffortsService {

    private UserService userService;
    private DevopsDataService devopsDataService;

    @Autowired
    public UserEffortsService(UserService userService, DevopsDataService devopsDataService) {
        this.userService = userService;
        this.devopsDataService = devopsDataService;
    }

    public List<UserEffort> getUserEfforts(ProjectType projectType) {
        List<UserEffort> userEfforts = new ArrayList<>();
        userService.findAllUsersForProject(projectType)
                .forEach(user -> userEfforts.add(new UserEffort(Collections.singletonList(getUserEffort(user, projectType)),user.getFirstName() + " , " + user.getLastName())));
        return userEfforts;
    }

    private Double getUserEffort(User user, ProjectType projectType) {
        return devopsDataService.getEfforts(user, projectType);
    }
}
