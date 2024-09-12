package com.openai36.aggregation.eao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSecretKeyRepository extends JpaRepository<UserSecretKeyEntity, Integer> {
    Optional<UserSecretKeyEntity> findBySecretKey(String secretKey);
    List<UserSecretKeyEntity> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
