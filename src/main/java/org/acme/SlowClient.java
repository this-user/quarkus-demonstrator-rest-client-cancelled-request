package org.acme;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/slow")
@RegisterRestClient(configKey = "r")
public interface SlowClient {

    @POST
    @Path("/{name}")
    Uni<String> call(@PathParam("name") String name);
}
