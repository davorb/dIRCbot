
package se.davor.dircbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.jibble.pircbot.*;

import twitter4j.*;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 * This stopped working after twitter changed the 
 * authentication mechanism. Need to fix.
 * @author Davor
 *
 */
public class TwitterBot { //extends Bot {
	
	public TwitterBot(ConfigurationManager configuration,
			BotManager botManager) {
		//super(configuration, botManager);
	}
/*
	public void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		if (message.equalsIgnoreCase("time")) {
			String time = new java.util.Date().toString();
			botManager.sendMessage(channel, sender + ": The time is now " + time);
		} else if (message.equalsIgnoreCase("!help")) {
			botManager.sendMessage(channel, sender + ": I am twibot. " + 
			"I log everything that is being said in this channel and post it to twitter.");
		} else {
			try {
				sendTweet("<"+sender+"> "+message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Unable to send tweet.");
				e.printStackTrace();
			}
		}
	}

	private void sendTweet(String message) {
		Twitter twitter = new TwitterFactory().getInstance("lineroirc", "YZ-sCQy"); // TODO: fix
		try {
			Status status = twitter.updateStatus(message);
			System.out.println("Updated status to: " + status.getText());
		} catch (TwitterException te) {
			System.out.println("Failed to send message: " + te.getMessage());
		}
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		// TODO Auto-generated method stub
		
	}*/
}
