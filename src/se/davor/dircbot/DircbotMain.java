package se.davor.dircbot;

import org.jibble.pircbot.*;

public class DircbotMain {
	public static void main(String[] args) throws Exception {
		ConfigurationManager configuration = new ConfigurationManager();
		BotManager botManager = new BotManager(configuration);
		botManager.add(new XMPPBot(configuration, botManager));
		botManager.add(new RemoteFTPLogBot(configuration, botManager));
	}
}
