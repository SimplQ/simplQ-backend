package com.example.restservice.dao;

import com.example.restservice.constants.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

  @Modifying
  @Query("update User u set u.status = :status where u.tokenId = :tokenId")
  void setUserStatusById(@Param("status") UserStatus status, @Param("tokenId") String tokenId);
}
