package se.visionmate.solution.exposure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.visionmate.solution.exposure.model.RoleRepresentation;
import se.visionmate.solution.service.RoleService;

@RestController
@RequestMapping(value = "/role")
public class RoleController {

    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createRole(@RequestBody RoleRepresentation roleRepresentation) {
        roleService.createRole(roleRepresentation);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RoleRepresentation>>  getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoles());
    }

    @RequestMapping(value = "/{roleName}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public void updateRole(@RequestBody RoleRepresentation roleRepresentation) {
        roleService.updateRole(roleRepresentation);
    }
}
