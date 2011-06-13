package se.davor.dircbot;

import org.jibble.pircbot.*;

public class DircbotMain {
	public static void main(String[] args) throws Exception {
		ConfigurationManager configuration = new ConfigurationManager();
		BotManager botManager = new BotManager(configuration);

		if(configuration.isTrue("xmppbot"))
			botManager.add(new XMPPBot(configuration, botManager));

		if(configuration.isTrue("remoteftpbot"))
			botManager.add(new RemoteFTPLogBot(configuration, botManager));
	}
}
