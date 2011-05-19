package se.davor.dircbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.jibble.pircbot.*;

import twitter4j.*;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public abstract class Bot {

	protected ConfigurationManager configuration;
	protected BotManager botManager;
	
	public 	Bot(ConfigurationManager configuration, BotManager botManager) {
		this.configuration = configuration;
		this.botManager = botManager;
	}	
	
	/***
	 * The received IRC message is sent along to the bots for
	 * parsing.
	 * @param channel
	 * @param sender
	 * @param login
	 * @param hostname
	 * @param message
	 */
	protected abstract void onMessage(String channel, String sender,
			String login, String hostname, String message);
	
	protected abstract void onPrivateMessage(String sender, String login, 
			String hostname, String message);
	
	protected abstract void onJoin(String channel, String sender, 
			String login, String hostname);
	
	protected abstract void onPart(String channel, String sender,
            String login, String hostname);
	
	protected abstract void onQuit(String sourceNick,
            String sourceLogin,
            String sourceHostname,
            String reason);
	
}
