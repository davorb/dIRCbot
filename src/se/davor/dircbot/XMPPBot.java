package se.davor.dircbot;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

/***
 * Sends incomming IRC messages to a XMPP-account
 * and incomming XMPP messages back to IRC.
 */
public class XMPPBot {
	private XMPPConnection xmppConnection;
	private Chat chat;
	private String trustedSender, receiver;
	private ConfigurationManager configuration;
	private IrcBot ircBot;
	private boolean forwardMessages;

	public XMPPBot(ConfigurationManager configuration, IrcBot ircBot)
	throws NickAlreadyInUseException, IOException, IrcException, XMPPException {
		this.configuration = configuration;
		this.ircBot = ircBot;
		ConnectionConfiguration cconf = 
			new ConnectionConfiguration(configuration.getKey("XMPPSERVER"),
					5222,
			"googlemail.com");
		xmppConnection = new XMPPConnection(cconf);
		xmppConnection.connect();
		try{
			xmppConnection.login(configuration.getKey("XMPPUSER"), 
					configuration.getKey("XMPPPW"));
			receiver = configuration.getKey("XMPPRECEIVER");
		} catch (NoSuchElementException e) {
			System.err.println("Missing vital information from configuration file.");
			System.exit(1);
		}
		chat = xmppConnection.getChatManager().createChat(receiver, new MessageParrot());
		forwardMessages=true;

		try {
			trustedSender = configuration.getKey("XMPPTRUSTEDSENDER");
		} catch (NoSuchElementException e) {
			trustedSender = "";
		}
	}

	/**
	 * When receiving IRC-message
	 */
	protected void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		if (message.equalsIgnoreCase("time")) {
			String time = new java.util.Date().toString();
			ircBot.sendMessage(channel, sender + ": The time is now " + time);
		} else if (message.equalsIgnoreCase("!help")) {
			ircBot.sendMessage(channel, sender + ": I am twibot. " + 
			"I log everything that is being said in this channel and post it to twitter.");
		} else if (message.equalsIgnoreCase("!stop") && sender.startsWith(trustedSender)) {
			forwardMessages = false;
			ircBot.sendMessage(channel, "Stopped forwarding messages.");
		} else if (message.equalsIgnoreCase("!start") && sender.startsWith(trustedSender)) { 
			forwardMessages = true;
			ircBot.sendMessage(channel, "Resumed forwarding messages.");
		} else {
			try {
				if (forwardMessages && !sender.startsWith(trustedSender))
					chat.sendMessage("<"+sender+"> "+message);
			} catch (Exception e) {
				System.err.println("Unable to send XMPP message.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * For receiving XMPP-message
	 */
	private class MessageParrot implements MessageListener {
		private Message msg;

		MessageParrot() {
			msg = new Message(receiver, Message.Type.chat);
		}

		public void processMessage(Chat chat, Message message) {
			if(message.getType().equals(Message.Type.chat) && message.getBody() != null) {
				System.out.println("Received: " + message.getBody());
				if (message.getBody().equalsIgnoreCase("STOP")) {
					forwardMessages=false;
				} else if (message.getBody().equalsIgnoreCase("START")) {
					forwardMessages=true;
				} else if (message.getBody().equalsIgnoreCase("STATUS")) {
					try {
						chat.sendMessage("Commands are 'USERS', 'RECONNECT', 'START' and 'STOP'."+
								"Forwarding is set to "+forwardMessages+".");
					} catch (XMPPException e) {
						System.err.println("Failed to send xmpp message");
					}
				} else if (message.getBody().equalsIgnoreCase("USERS")) {
					try {
						// NOTE: will break if I add support for multiple channels
						User[] users = ircBot.getUsers(ircBot.getChannel());
						String userList = "Users in channel: ";
						for (User u : users) {
							userList += (u.getNick() + " ");
						}
						chat.sendMessage(userList);
					} catch (XMPPException e) {
						System.err.println("Failed to send xmpp message");
					}
				} else if (message.getBody().equalsIgnoreCase("RECONNECT")) { 
					try {
						ircBot.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							chat.sendMessage(e.toString());
						} catch (XMPPException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					ircBot.sendMessage(message.getBody());
				}
			} else {
				System.err.println("Received message that is hard to understand");
			}
		}
	}

	public void onPrivateMessage(String sender, String login, String hostname,
			String message) {
		try {
			chat.sendMessage("<"+sender+"> "+message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void onQuit(String sourceNick, String sourceLogin,
			String sourceHostname, String reason) {
		// do nothing
	}

	public void onJoin(String channel, String sender, String login,
			String hostname) {
		// do nothing
	}

	public void onPart(String channel, String sender, String login,
			String hostname) {
		// do nothing
	}
}
