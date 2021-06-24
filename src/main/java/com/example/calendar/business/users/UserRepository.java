package com.example.calendar.business.users;

import com.example.calendar.business.domains.ProjectType;
import com.example.calendar.business.domains.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>{

    Page<User> findByAssignedProjectsContainingAndFirstNameContainingOrLastNameContaining(
            ProjectType projectType, String firstName, String lastName, Pageable page);

    Page<User> findByAssignedProjectsContaining(ProjectType projectType, Pageable page);

    List<User> findByAssignedProjectsContaining(ProjectType projectType);
}
