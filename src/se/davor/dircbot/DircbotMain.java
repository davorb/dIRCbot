package se.davor.dircbot;

import org.jibble.pircbot.*;

public class DircbotMain {
	public static void main(String[] args) throws Exception {
		ConfigurationManager configuration = new ConfigurationManager();
		BotManager botManager = new BotManager(configuration);
		XMPPBot xmppBot = new XMPPBot(configuration, botManager);
		botManager.addXMPPBot(xmppBot);
	}
}
