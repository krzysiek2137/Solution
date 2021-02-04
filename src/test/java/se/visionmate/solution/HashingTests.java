package se.visionmate.solution;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HashingTests {

    @Test
    public void hashPasswordTest() {
        String password = "qwerty123";
        String incorectPassword = "Qwerty123";
        String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt());

        Assert.assertTrue(BCrypt.checkpw(password, hashedPass));
        Assert.assertFalse(BCrypt.checkpw(incorectPassword, hashedPass));
    }
}
