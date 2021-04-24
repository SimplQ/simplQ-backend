package me.simplq.dao;

import java.util.stream.Stream;
import me.simplq.constants.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface TokenRepository extends JpaRepository<Token, String> {

  @Modifying
  @Query("update Token t set t.status = :status where t.tokenId = :tokenId")
  void setTokenStatusById(@Param("status") TokenStatus status, @Param("tokenId") String tokenId);

  @Query("select max(t.tokenNumber) from Token t where t.queue.queueId = :queueId")
  Integer getLastTokenNumberForQueue(String queueId);

  Stream<Token> findByOwnerId(String ownerId);

  @Query("select case when count(t)> 0 then true else false end from Token t where "
      + "t.queue.id = :queueId "
      + "and t.contactNumber = :contactNumber "
      + "and t.status =  me.simplq.constants.TokenStatus.WAITING")
  boolean existsAlreadyInQueue(@Param("queueId") String queueId,
      @Param("contactNumber") String contactNumber);
}
