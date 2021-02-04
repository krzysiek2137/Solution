package se.visionmate.solution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.visionmate.solution.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Modifying
    @Query("update User u set u.password = :hashedPassword where u.userName = :userName")
    void updatePassword(@Param("userName") String userName, @Param("hashedPassword") String hashedPassword);

    @Query("select u FROM User u inner join fetch u.role r where u.userName = :userName")
    Optional<User> findUserWithRoleByUserName(@Param("userName") String userName);

    @Modifying
    @Query("delete from User u WHERE u.userName = :userName")
    void deleteByUserName(@Param("userName") String userName);
}
