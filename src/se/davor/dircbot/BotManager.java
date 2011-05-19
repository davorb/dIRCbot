package se.davor.dircbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.jibble.pircbot.*;

/**
 * This is the actual IRC-client
 * @author Davor
 *
 */
public class BotManager extends PircBot {
	protected ConfigurationManager configuration;
	private ArrayList<Bot> bots;
	private String channel;

	public 	BotManager(ConfigurationManager configuration) 
	throws NickAlreadyInUseException, IOException, IrcException {
		this.setName(configuration.getKey("IRCNICK"));
		this.channel = configuration.getKey("CHANNEL");
		this.setVersion("twibot 0.1.0");
		this.configuration = configuration;
		this.bots = new ArrayList<Bot>();
		setAutoNickChange(true); 

		try {
			setVerbose(Boolean.parseBoolean(configuration.getKey("DEBUG"))); // debugging	
			connect(configuration.getKey("SERVER"));
			joinChannel(channel);
		} catch (NoSuchElementException e) {
			System.out.println("Missing vital information from configuration file.");
			System.exit(1);
		}
	}

	public String getChannel() {
		return channel;
	}

	public void add(Bot bot) {
		bots.add(bot);
	}

	public void remove(Bot bot) {
		bots.remove(bot);
	}

	protected void onDisconnect() {
		try {
			reconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onJoin(String channel, String sender, String login, String hostname) {
		for (Bot bot : bots) {
			bot.onJoin(channel, sender, login, hostname);
		}
	}

	protected void onPart(String channel, String sender,
			String login, String hostname) {
		for (Bot bot : bots) {
			bot.onPart(channel, sender, login, hostname);
		}

		/* Quick hack: Ideally, I would do this periodically.
		 * This might have to be placed somewhere else. The
		 * goal of this is for the bot to figure out that
		 * he isn't supposed to be alone in the channel.
		 * 
		 * TODO: Add option for this under settings. This WILL
		 * cause problems for people outside QNET
		 */
		if (getUsers(channel).length == 0) {
			try {
				Thread.sleep(1000);
				reconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void onQuit(String sourceNick,
			String sourceLogin,
			String sourceHostname,
			String reason) {
		for (Bot bot : bots) {
			bot.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
		}
	}

	protected void onPrivateMessage(String sender, String login, 
			String hostname, String message) {
		for (Bot bot : bots) {
			bot.onPrivateMessage(sender, login, hostname, message);
		}
	}

	public void sendMessage(String message, Bot senderBot) {
		super.sendMessage(configuration.getKey("CHANNEL"), message);

		for (Bot bot : bots) {
			if (!bot.equals(senderBot)) {
				bot.onMessage(null, this.getNick(), null, null, message);
			}
		}
	}

	protected void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		for (Bot bot : bots) {
			bot.onMessage(channel, sender, login, hostname, message);
		}
	}
}
