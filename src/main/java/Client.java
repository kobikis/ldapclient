import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ldap.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;

/**
 * Created by kobi on 4/23/14.
 */
public class Client {

    private ApplicationContext applicationContext;
    private AuthenticationManager authenticationManager;


    /**
     * Initialize the security manager using the spring security configuration.
     */
    public void init() throws com.gigaspaces.security.SecurityException {

        System.setProperty("javax.net.ssl.trustStore","/home/kobi/dev/ldapclient/src/main/resources/jssecacerts");
        System.setProperty("javax.net.ssl.keyStore","/home/kobi/dev/ldapclient/src/main/resources/keystore.ks");
        System.setProperty("javax.net.ssl.keyStorePassword","password");
		/*
		 * Extract Spring AuthenticationManager definition
		 */
        applicationContext = new ClassPathXmlApplicationContext("ldap-security-config.xml");
        Map<String, AuthenticationManager> beansOfType = applicationContext.getBeansOfType(AuthenticationManager.class);
        if (beansOfType.isEmpty()) {
            throw new SecurityException("No bean of type '"+AuthenticationManager.class.getName()+"' is defined in classpath");
        }
        if (beansOfType.size() > 1) {
            throw new SecurityException("More than one bean of type '"+AuthenticationManager.class.getName()+"' is defined in classpath");
        }
        authenticationManager = beansOfType.values().iterator().next();
    }

    protected org.springframework.security.core.Authentication createAuthenticationRequest(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
    }


    public void authenticate(String username, String password) throws AuthenticationException {
        org.springframework.security.core.Authentication authenticate = authenticationManager.authenticate(createAuthenticationRequest(username, password));
        if (!authenticate.isAuthenticated()) {
            throw  new RuntimeException("Authentication failed");
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
        client.authenticate("Superuser", "Superuser");
    }

}
