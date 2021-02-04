package se.visionmate.solution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.visionmate.solution.model.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    @Query("select r from Role r WHERE r.roleName = :roleName")
    Optional<Role> findRoleByRoleName(@Param("roleName") String roleName);

    @Modifying
    @Query("delete from Role r WHERE r.roleName = :roleName")
    void deleteByRoleName(@Param("roleName") String roleName);
}
