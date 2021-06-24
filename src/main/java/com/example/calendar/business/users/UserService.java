package com.example.calendar.business.users;

import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.User;
import com.example.calendar.exceptions.GenericException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user){
        validateUser(user);
        this.userRepository.save(user);
    }

    public List<User> getAllUsers(ProjectType selectedProject){
       return this.userRepository.findAll().stream()
               .filter(user -> user.getAssignedProjects().contains(selectedProject)).collect(Collectors.toList());
    }

    public void deleteUser(String userName){
        this.userRepository.deleteById(userName);
    }

    private void validateUser(User user) {
        if(user.getUserName() == null || user.getEmailId() == null || user.getFirstName() == null || user.getLastName() == null){
            LOG.error("Exception Occurred :: Incomplete Information Passed");
            throw new GenericException("Incomplete Information !");
        }
    }

    public Page<User> getAllUser(String filterValue,ProjectType projectType, Pageable page) {
        Pageable pageable = page;
        if (page.getSort().isEmpty()) {
            pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(),
                    Sort.by("lastName").ascending().and(Sort.by("firstName").ascending())
                            .and(Sort.by("userName").ascending()));
        }

        if(filterValue != null) {
            return userRepository.findByAssignedProjectsContainingAndFirstNameContainingOrLastNameContaining( projectType, filterValue, filterValue , pageable);
        }
        return userRepository.findByAssignedProjectsContaining(projectType,pageable);
    }

    public List<User> findAllUsersForProject(ProjectType projectType) {
        return userRepository.findByAssignedProjectsContaining(projectType);
    }
}
