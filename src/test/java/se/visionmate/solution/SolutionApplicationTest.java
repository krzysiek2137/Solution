package se.visionmate.solution;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import se.visionmate.solution.exposure.model.RoleRepresentation;
import se.visionmate.solution.exposure.model.UserCredencialRepresentation;
import se.visionmate.solution.exposure.model.UserRequestRepresentation;

@SpringBootTest
@ComponentScan(basePackages = { "se.visionmate.solution"})
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties")
@Transactional
public class SolutionApplicationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldPassTestScenario() throws Exception  {
        //login as ADMIN
        String userName = "ADMIN";
        UserCredencialRepresentation userCredential = new UserCredencialRepresentation();
        userCredential.setUserName(userName);
        userCredential.setPassword("qwerty123");
        MvcResult loginResults = mvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCredential)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();
        String adminToken = loginResults.getResponse().getContentAsString();

        //createNewRole
        String roleName = "CREATE_AND_LIST";
        RoleRepresentation roleRepresentation =
            new RoleRepresentation(roleName, Arrays.asList("Create user", "List users"));
        mvc.perform(post("/role").header("Authorization", "basic " + adminToken)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(roleRepresentation)))
            .andExpect(status().isOk());

        //createNewUser
        String newUserName1 = "test1";
        UserRequestRepresentation userRequestRepresentation = new UserRequestRepresentation(newUserName1, "qwerty123", roleName);
        mvc.perform(post("/user").header("Authorization", "basic " + adminToken)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequestRepresentation)))
            .andExpect(status().isOk());

        //Login as a new user
        UserCredencialRepresentation newUserCredential = new UserCredencialRepresentation();
        newUserCredential.setUserName(newUserName1);
        newUserCredential.setPassword("qwerty123");
        MvcResult newUserLoginResults = mvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newUserCredential)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();
        String testUserToken = newUserLoginResults.getResponse().getContentAsString();

        //New user create user -- success
        String newUserName2 = "test11";
        UserRequestRepresentation userRequestRepresentation2 = new UserRequestRepresentation(newUserName2, "qwerty123", roleName);
        mvc.perform(post("/user").header("Authorization", "basic " + testUserToken)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequestRepresentation2)))
            .andExpect(status().isOk());

        //New user create new role -- failure, no access
        RoleRepresentation roleRepresentation1 = new RoleRepresentation("DELETE", Arrays.asList("Delete user"));
        mvc.perform(post("/role").header("Authorization", "basic " + testUserToken)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(roleRepresentation1)))
            .andExpect(status().is4xxClientError()).andExpect(content().string("Access not allowed"));
        //New user remove user -- failure, no access
        mvc.perform(post("/user/asdas").header("Authorization", "basic " + testUserToken)
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(roleRepresentation1)))
            .andExpect(status().is4xxClientError()).andExpect(content().string("Access not allowed"));
    }
}
