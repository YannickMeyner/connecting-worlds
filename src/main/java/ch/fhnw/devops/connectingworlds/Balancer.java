package ch.fhnw.devops.connectingworlds;

import java.util.List;
import java.util.Objects;

public abstract class Balancer {

    abstract List<IChatbot> getRegisteredChatbots();

    abstract IChatbot getNextChatbot();

    abstract  IChatbot getCurrentChatbot();

    public static Balancer getBalancer(final List<IChatbot> pChatbots) {
        return new RoundRobinBalancer(pChatbots);
    }

    static class RoundRobinBalancer extends Balancer {

        final List<IChatbot> mChatbots;
        int currentIndex;

        private RoundRobinBalancer(final List<IChatbot> pChatbots) {
            mChatbots = pChatbots;
            currentIndex=0;
        }

        @Override
        List<IChatbot> getRegisteredChatbots() {
            return mChatbots;
        }

        @Override
        IChatbot getNextChatbot() {
            IChatbot chatbot = mChatbots.get(++currentIndex%mChatbots.size());
            return Objects.requireNonNullElseGet(chatbot, IChatbot.NoChatbot::new);
        }

        @Override
        IChatbot getCurrentChatbot() {
            IChatbot chatbot = mChatbots.get(currentIndex%mChatbots.size());
            return Objects.requireNonNullElseGet(chatbot, IChatbot.NoChatbot::new);
        }
    }

}
