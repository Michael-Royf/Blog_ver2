package com.michael.blog.repository;

import com.michael.blog.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t from Token t join User u on t.user.id =u.id where  u.id =:userId and (t.expired =false or t.revoked =false)")
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String token);


}
