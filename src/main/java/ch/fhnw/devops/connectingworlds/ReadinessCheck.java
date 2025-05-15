package ch.fhnw.devops.connectingworlds;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.inject.Inject;


@Readiness
public class ReadinessCheck implements HealthCheck{

    @Inject
    ConnectingBots botConnector;

    @Override
    public HealthCheckResponse call() {
        if (botConnector.isReady()) {
            return HealthCheckResponse.up("chatbot-readiness");
        } else {
            return HealthCheckResponse.down("chatbot-readiness");
        }
    }

}
