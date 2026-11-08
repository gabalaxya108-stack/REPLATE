package com.replate.repository;

import com.replate.domain.entity.BaseUser;
import com.replate.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BaseUser, Long> {

    Optional<BaseUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<BaseUser> findByRole(UserRole role);

    List<BaseUser> findByRoleAndStatus(UserRole role, String status);
}

