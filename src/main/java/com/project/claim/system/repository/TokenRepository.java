package com.project.claim.system.repository;

import com.project.claim.system.entity.TokenEntity;
import org.antlr.v4.runtime.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer>  {


    @Query("SELECT t FROM TokenEntity t " +
            "INNER JOIN t.staff s " +
            "WHERE s.id = :staffId AND (t.expired = false OR t.revoked = false)")
    List<TokenEntity> findAllValidTokensByUser(UUID staffId);

    Optional<TokenEntity> findByToken(String token);



}
