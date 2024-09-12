package com.openai36.aggregation.eao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WechatUserRepository extends JpaRepository<WechatUserEntity, Integer> {
     Optional<WechatUserEntity> findByOpenId(String openId);

    void deleteByUserId(Integer userId);
}
