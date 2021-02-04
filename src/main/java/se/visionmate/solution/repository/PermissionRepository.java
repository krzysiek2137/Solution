package se.visionmate.solution.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.visionmate.solution.model.Permission;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {

    @Query("select p from Permission p WHERE p.permissionName = :permissionName")
    Optional<Permission> findPermissionByPermissionName(@Param("permissionName") String permissionName);
}
