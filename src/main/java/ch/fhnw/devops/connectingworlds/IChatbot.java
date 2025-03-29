package ch.fhnw.devops.connectingworlds;

public interface IChatbot {

    String name();

    String url();

    class NoChatbot implements IChatbot{

        @Override
        public String name() {
            return "Dummy";
        }


        @Override
        public String url() {
            return "";
        }
    }

}
