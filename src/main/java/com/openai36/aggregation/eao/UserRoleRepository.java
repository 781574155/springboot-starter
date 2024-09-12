package com.openai36.aggregation.eao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {
    List<UserRoleEntity> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
}
