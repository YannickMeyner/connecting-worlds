package ch.fhnw.devops.connectingworlds;


import io.quarkus.qute.TemplateData;

@TemplateData
public class ChatMessage {

    public final String sourceChatbot;
    public final String targetChatbot;
    public final String message;


    public ChatMessage(String sourceChatbot, String targetChatbot, String message) {
        this.sourceChatbot=sourceChatbot;
        this.targetChatbot=targetChatbot;
        this.message=message;

    }
}
