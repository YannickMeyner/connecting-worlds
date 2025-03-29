package ch.fhnw.devops.connectingworlds;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;


@Path("/")
@ApplicationScoped
public class ConversationResource {

    private static final Logger LOG = Logger.getLogger(ConversationResource.class);

    @Inject
    Template index;

    @Inject
    ConnectingBots connectingBots;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getConversation() {
        LOG.info("Size of messages:"+ connectingBots.messages.size());
        return index.data("messages", connectingBots.messages);
    }

}
