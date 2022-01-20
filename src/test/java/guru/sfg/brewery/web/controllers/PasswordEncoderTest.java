package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class PasswordEncoderTest{

    static final String PASSWORD = "password";

    @Test
    void bcript(){
        PasswordEncoder bcrypt = new BCryptPasswordEncoder();

        System.out.println(bcrypt.encode(PASSWORD));
    }

    @Test
    void testCha256(){
        PasswordEncoder sha256 = new StandardPasswordEncoder();

        System.out.println(sha256.encode(PASSWORD));
    }

    @Test
    void testLDAP(){
        PasswordEncoder ldap = new LdapShaPasswordEncoder();
        System.out.println(ldap.encode(PASSWORD));

        assertTrue(ldap.matches(PASSWORD, ldap.encode(PASSWORD)));
    }

    @Test
    public void testNoop(){
        PasswordEncoder noop = NoOpPasswordEncoder.getInstance();
        System.out.println(noop.encode(PASSWORD));
    }

    @Test
    public void hashingExample(){
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));

        String salted = PASSWORD + "salt";

        System.out.println(DigestUtils.md5DigestAsHex(salted.getBytes()));
    }

}
