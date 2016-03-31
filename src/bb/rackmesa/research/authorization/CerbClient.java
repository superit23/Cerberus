package bb.rackmesa.research.authorization;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by Dan on 3/29/2016.
 */
public class CerbClient {



    public void authenticate(CerbAuthRequest authRequest)
    {
        JerseyClientBuilder builder = new JerseyClientBuilder();
        ClientConfig cConfig = new ClientConfig();

        Client client = builder.newClient();

        WebTarget webTarget = client.target(Configuration.getInstance().getDbConnectionString() + "/authenticate");

        Invocation.Builder invBuilder = webTarget.request();
        Response response = invBuilder.post(Entity.entity(authRequest, MediaType.APPLICATION_JSON_TYPE));

    }
}

