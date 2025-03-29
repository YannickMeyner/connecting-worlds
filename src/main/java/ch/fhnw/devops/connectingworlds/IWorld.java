package ch.fhnw.devops.connectingworlds;

import io.smallrye.config.ConfigMapping;

import java.util.List;

@ConfigMapping(prefix = "app")
public interface IWorld {

    List<IChatbot> chatbots();

}
