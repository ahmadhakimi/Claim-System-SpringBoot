//TokenRepository.java
package com.project.claim.system.repository;

import com.project.claim.system.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    void deleteByToken(String tokenValue);
}
