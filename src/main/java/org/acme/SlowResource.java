package org.acme;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("/slow")
public class SlowResource {

    private static final Logger LOG = Logger.getLogger(SlowResource.class);

    public static final List<String> RECEIVED = new CopyOnWriteArrayList<>();

    @POST
    @Path("/{name}")
    public String call(@PathParam("name") String name) throws InterruptedException {
        LOG.infof("server received %s", name);
        RECEIVED.add(name);
        if (name.equals("A")) {
            Thread.sleep(1500);
        }
        return name;
    }
}
