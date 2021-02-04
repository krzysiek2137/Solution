package se.visionmate.solution;

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
import se.visionmate.solution.exposure.model.PasswordResetRepresentation;
import se.visionmate.solution.exposure.model.RoleRepresentation;
import se.visionmate.solution.exposure.model.UserCredencialRepresentation;
import se.visionmate.solution.exposure.model.UserRequestRepresentation;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

@SpringBootTest
@ComponentScan(basePackages = { "se.visionmate.solution"})
@AutoConfigureMockMvc
@TestPropertySource(
	locations = "classpath:application-integrationtest.properties")
public class AuthControllerTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void shouldThrowErrorWhenNotExistingUserTryToLogin() throws Exception {
		String userName = "testUserName";
		UserCredencialRepresentation userCredencial = new UserCredencialRepresentation();
		userCredencial.setUserName(userName);
		userCredencial.setPassword("samplePassword");

		mvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCredencial)))
			.andExpect(status().is5xxServerError())
			.andExpect(content().string("User "+ userName + " not found"));
	}

	@Test
	public void shouldLoginAsExistingUser() throws Exception {
		String userName = "ADMIN";
		UserCredencialRepresentation userCredential = new UserCredencialRepresentation();
		userCredential.setUserName(userName);
		userCredential.setPassword("qwerty123");

		mvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCredential)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/plain;charset=UTF-8"));
	}

	@Test
	public void shouldGetResetLinkAndResetPasswordAndLoginWithNewPassword() throws Exception {
		//login as admin
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
		String roleName = "CREATE_ROLES";
		RoleRepresentation roleRepresentation =
			new RoleRepresentation(roleName, Arrays.asList("Create user", "List users"));
		mvc.perform(post("/role").header("Authorization", "basic " + adminToken)
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(roleRepresentation)))
			.andExpect(status().isOk());

		//createNewUser
		String newUserName1 = "test112";
		UserRequestRepresentation userRequestRepresentation = new UserRequestRepresentation(newUserName1, "qwerty123", roleName);
		mvc.perform(post("/user").header("Authorization", "basic " + adminToken)
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRequestRepresentation)))
			.andExpect(status().isOk());

		//reset pass
		PasswordResetRepresentation passwordReset = new PasswordResetRepresentation();
		passwordReset.setUserName(newUserName1);
		MvcResult resetResults = mvc.perform(post("/reset")
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(passwordReset)))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("http://"))).andReturn();

		String[] resetFullLink = resetResults.getResponse().getContentAsString().split("/");
		String resetEndpoint = "/" + resetFullLink[resetFullLink.length-2] + "/" + resetFullLink[resetFullLink.length-1];

		MvcResult remindResults = mvc.perform(get(resetEndpoint))
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/plain;charset=UTF-8")).andReturn();

		//Login with new pass
		UserCredencialRepresentation userCredential1 = new UserCredencialRepresentation();
		userCredential1.setUserName(newUserName1);
		userCredential1.setPassword(remindResults.getResponse().getContentAsString());
		mvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userCredential)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("text/plain;charset=UTF-8"));
	}
}
