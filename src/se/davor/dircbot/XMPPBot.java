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
public class XMPPBot extends Bot {
	private XMPPConnection xmppConnection;
	private BotManager botManager;
	private Chat chat;
	private String trustedSender;
	
	private boolean forwardMessages;

	public XMPPBot(ConfigurationManager configuration, BotManager botManager)
	throws NickAlreadyInUseException, IOException, IrcException, XMPPException {
		super(configuration, botManager);

		ConnectionConfiguration cconf = 
			new ConnectionConfiguration(configuration.getKey("XMPPSERVER"),
					5222,
			"googlemail.com");
		xmppConnection = new XMPPConnection(cconf);
		xmppConnection.connect();
		try{
			xmppConnection.login(configuration.getKey("XMPPUSER"), 
					configuration.getKey("XMPPPW"));
		} catch (NoSuchElementException e) {
			System.out.println("Missing vital information from configuration file.");
			System.exit(1);
		}
		chat = xmppConnection.getChatManager().createChat("davor@davor.se", new MessageParrot());
		forwardMessages=true;
		this.botManager = botManager;
		
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
			botManager.sendMessage(channel, sender + ": The time is now " + time);
		} else if (message.equalsIgnoreCase("!help")) {
			botManager.sendMessage(channel, sender + ": I am twibot. " + 
			"I log everything that is being said in this channel and post it to twitter.");
		} else if (message.equalsIgnoreCase("!stop") && sender.equals(trustedSender)) {
			forwardMessages = false;
			botManager.sendMessage(channel, "Stopped forwarding messages.");
		} else if (message.equalsIgnoreCase("!start") && sender.equals(trustedSender)) {
			forwardMessages = true;
			botManager.sendMessage(channel, "Resumed forwarding messages.");
		} else {
			try {
				if (forwardMessages && !sender.equals(trustedSender))
					chat.sendMessage("<"+sender+"> "+message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Unable to send tweet.");
				e.printStackTrace();
			}
		}
	}

	private Bot getThis() {
		return this;
	}

	/**
	 * For receiving XMPP-message
	 */
	private class MessageParrot implements MessageListener {
		private Message msg;

		MessageParrot() {
			// TODO: Need to add this to config file
			msg = new Message("davor@davor.se", Message.Type.chat);
		}

		public void processMessage(Chat chat, Message message) {
			if(message.getType().equals(Message.Type.chat) && message.getBody() != null) {
				System.out.println("Received: " + message.getBody());
				if (message.getBody().equalsIgnoreCase("STOPFW")) {
					forwardMessages=false;
				} else if (message.getBody().equalsIgnoreCase("STARTFW")) {
					forwardMessages=true;
				} else if (message.getBody().equalsIgnoreCase("STATUS")) {
					try {
						chat.sendMessage("Commands are 'USERS', 'RECONNECT', 'STARTFW' and 'STOPFW'."+
								"Forwarding is set to "+forwardMessages+".");
					} catch (XMPPException e) {
						System.out.println("Failed to send xmpp message");
					}
				} else if (message.getBody().equalsIgnoreCase("USERS")) {
					try {
						// NOTE: will break if I add multiple channel funcitonality
						User[] users = botManager.getUsers(botManager.getChannel());
						String userList = "Users in channel: ";
						for (User u : users) {
							userList += (u.getNick() + " ");
						}
						chat.sendMessage(userList);
					} catch (XMPPException e) {
						System.out.println("Failed to send xmpp message");
					}
				} else if (message.getBody().equalsIgnoreCase("RECONNECT")) { 
					try {
						botManager.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							chat.sendMessage(e.toString());
						} catch (XMPPException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					botManager.sendMessage(message.getBody(), getThis());
				}
			} else {
				System.out.println("I got a message I didn''t understand");
			}
		}
	}
	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		onMessage(null, sender, login, hostname, "[PRIVMSG]"+message);
	}

	@Override
	protected void onJoin(String channel, String sender, String login,
			String hostname) { /* do nothing */ }

	@Override
	protected void onPart(String channel, String sender, String login,
			String hostname) { /* do nothing */ }

	@Override
	protected void onQuit(String sourceNick, String sourceLogin,
			String sourceHostname, String reason) { /* do nothing */ }
}
