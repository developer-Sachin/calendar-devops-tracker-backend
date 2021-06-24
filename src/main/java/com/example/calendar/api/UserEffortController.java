package com.example.calendar.api;

import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.UserEffort;
import com.example.calendar.business.users.UserEffortsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class UserEffortController {

    private final UserEffortsService userEffortsService;

    @Autowired
    public UserEffortController(UserEffortsService userEffortsService) {
        this.userEffortsService = userEffortsService;
    }

    @GetMapping(value = "/{projectType}/getUserEfforts")
    public List<UserEffort> getUsersEfforts(@PathVariable ProjectType projectType) {
        return userEffortsService.getUserEfforts(projectType);
    }
}
