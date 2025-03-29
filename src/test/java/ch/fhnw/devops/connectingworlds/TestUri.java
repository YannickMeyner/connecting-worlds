package ch.fhnw.devops.connectingworlds;

import ch.fhnw.devops.connectingworlds.ConnectingBots;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;


class TestUri {

    final static String[] testData ={
            "Please%20go%20",
            "Please%20go%20>>",
            "<error>%20I'm%20having%20internal%20problems,%20when%20trying%20to%20work%20out%20what%20to%20say"
    };

    @Test
    public void test() {
        for (String singleTest : testData) {
            String uriString = String.format("%s%s", "http://eliza.chatbots.svc.cluster.local/message/", singleTest);
            String invalidChars = uriString.replaceAll(ConnectingBots.POSITIVEURLCHARS, "");
            System.out.println("InvalidChars found "+invalidChars);
            for (char toReplace : invalidChars.toCharArray()){
                uriString = uriString.replaceAll(Character.toString(toReplace),"");
            }
            System.out.println(uriString);
            final URI uri =  URI.create(uriString);
        }

    }

}
