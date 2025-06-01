package ch.fhnw.devops.connectingworlds;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static io.smallrye.config._private.ConfigLogging.log;

@Singleton
public class ConnectingBots {

    private static final Logger LOG = Logger.getLogger(ConnectingBots.class);

    public static String POSITIVEURLCHARS = "[A-Za-z0-9-._~:\\/?\\[\\]@!$&'()*+,;=%]+";
 
    private boolean hasStartedChatting = false; //Readiness flag

    private final int MAX_MESSAGES = 250;
    // Linked is more efficient for removeFirst and addLast
    List<ChatMessage> messages = new LinkedList<>();

    @Inject
    IWorld configuration;

    Balancer balancer;

    String lastMessage;

    private final HttpClient httpClient;

    @Inject
    MeterRegistry registry;

    public ConnectingBots() {
        httpClient = HttpClient.newBuilder()
                .build();
        lastMessage = "Please suggest a thoughful conversation topic that is morally questionable";
        hasStartedChatting = true;
    }

    @Scheduled(every = "30s")
    @Timed
    void chat() {
        if (balancer == null) {
            balancer = Balancer.getBalancer(configuration.chatbots());
            String lognames = balancer.getRegisteredChatbots().stream()
                    .map(IChatbot::name)
                    .collect(Collectors.joining(";", "{", "}"));
            String logurls = balancer.getRegisteredChatbots().stream()
                    .map(IChatbot::url)
                    .collect(Collectors.joining(";", "{", "}"));
            LOG.info("Chatbotnames registered: "+lognames);
            LOG.info("Chatboturls registered: "+logurls);
        }
        final IChatbot lastChatbot = balancer.getCurrentChatbot();
        final IChatbot nextChatbot = balancer.getNextChatbot();
        if (nextChatbot instanceof IChatbot.NoChatbot) {
            LOG.info("No chatbot registered, doing nothing");
        } else {
            lastMessage = lastMessage.replace(" ", "%20");
            String newMessage = getMessage(nextChatbot, lastMessage);
            ChatMessage messageToAdd = new ChatMessage(lastChatbot.name(), nextChatbot.name(), newMessage);
            LOG.info("Message: " + messageToAdd.toString()); 
            this.addMessage(messageToAdd);
            LOG.info(nextChatbot.name()+" => "+ lastChatbot.name()+":"+ newMessage);
            registry.counter("coordinated.message", "from", nextChatbot.name(), "to",lastChatbot.name(),"message", newMessage)
                    .increment();
            lastMessage=newMessage;
        }
    }


    /**
     * This Method ensures that no memory leak happens if the app 
     * is left running for a longer time. 
     * @param message
     */
    private synchronized void addMessage(ChatMessage message) {
        if (messages.size() >= this.MAX_MESSAGES) {
            messages.removeFirst(); // discard oldest
        }
        messages.addLast(message);
    }

    String getMessage(IChatbot chatbot, String message) {
        try {
            String uriString = String.format("%s%s", chatbot.url(), message);
            LOG.debug("Constructing "+uriString+" out of "+chatbot.url()+" and "+message);
            if (!uriString.matches(POSITIVEURLCHARS)) {
                String invalidChars = uriString.replaceAll(POSITIVEURLCHARS, "");
                LOG.debug("InvalidChars found "+invalidChars);
                for (char toReplace : invalidChars.toCharArray()){
                    uriString = uriString.replace(Character.toString(toReplace),"");
                }
                LOG.debug("URI new set to "+uriString);
            }
            final URI uri =  URI.create(uriString);
            final HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            return this.httpClient.send(request,HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            LOG.error("HTTPConnection not working" + e.getMessage());
            return "";
        }
    }

    public boolean isReady() {
        return hasStartedChatting;
    }
}
