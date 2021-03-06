package se.davor.dircbot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.jibble.pircbot.*;

/**
 * This is the actual IRC-client
 * @author Davor
 *
 */
public class IrcBot extends PircBot {
	protected ConfigurationManager configuration;
	private XMPPBot xmppBot;
	private String channel, encoding;

	public 	IrcBot(ConfigurationManager configuration) 
			throws NickAlreadyInUseException, IOException, IrcException {
		this.setName(configuration.getKey("IRCNICK"));
		this.channel = configuration.getKey("CHANNEL");
		this.setVersion("twibot 0.2.0");
		this.configuration = configuration;
		this.encoding = configuration.getKey("ENCODING");
		setAutoNickChange(true); 

		try {
			setVerbose(Boolean.parseBoolean(configuration.getKey("DEBUG")));
			connect(configuration.getKey("SERVER"));
			joinChannel(channel);
		} catch (NoSuchElementException e) {
			System.err.println("Missing vital information " +
					"from configuration file.");
			System.exit(1);
		}
	}

	public String getChannel() {
		return channel;
	}

	public void setXMPPBot(XMPPBot bot) {
		this.xmppBot = bot;
	}

	protected void onDisconnect() {
		try {
			Thread.sleep(1000);
			reconnect();
			joinChannel(channel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onJoin(String channel, String sender, String login, 
			String hostname) {
		xmppBot.onJoin(channel, sender, login, hostname);
	}

	protected void onPart(String channel, String sender,
			String login, String hostname) {
		xmppBot.onPart(channel, sender, login, hostname);

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
				e.printStackTrace();
			}
		}

		/* Above might not to be working. Adding following
		 * to be check if it calls onPart when a netsplit
		 * occurs.
		 * 
		 * Although I think it's been fixed with the recent
		 * changes made to the BotManager class to make the
		 * RECONNECT command in the XMPPBot work.
		 */
		System.out.println(sender+" parted.");
	}

	protected void onQuit(String sourceNick,
			String sourceLogin,
			String sourceHostname,
			String reason) {
		xmppBot.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
	}

	protected void onPrivateMessage(String sender, String login, 
			String hostname, String message) {
		try {
			message = new String(message.getBytes(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		xmppBot.onPrivateMessage(sender, login, hostname, message);
	}

	public void sendMessage(String message) {
		super.sendMessage(configuration.getKey("CHANNEL"), message);
	}

	protected void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		try {
			message = new String(message.getBytes(), encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		xmppBot.onMessage(channel, sender, login, hostname, message);
	}
}
