package se.davor.dircbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import org.apache.log4j.*;
import org.jibble.pircbot.*;
import com.enterprisedt.net.ftp.*;

/***
 * Logs IRC messages to HTML file on a remote
 * FTP server.
 */
public class LogBot extends Bot {
	private static String DATE_FORMAT = "yy-MM-dd";
	private static String EXTENDED_DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static String TIME_FORMAT = "HH:mm";
	//private static String REMOTE_DIRECTORY = "irc";

	private String currentDate;
	private Logger logger;
	private FileAppender fileAppender;
	private FileTransferClient ftp;

	public LogBot(ConfigurationManager configuration, BotManager botManager)
	throws NickAlreadyInUseException, IOException, IrcException, FTPException {
		super(configuration, botManager);

		logger = Logger.getLogger("LogBot.class");
		currentDate=getDate();
		// HTMLLayout
		fileAppender = new FileAppender(new SimplifiedHTMLLayout(), currentDate+".html");
		logger.addAppender(fileAppender);
		ftp = new FileTransferClient();
		try {
			ftp.setRemoteHost(configuration.getKey("FTPHOST"));
			ftp.setUserName(configuration.getKey("FTPUSER"));
			ftp.setPassword(configuration.getKey("FTPPW"));
		} catch (NoSuchElementException e) {
			System.out.println("Missing vital information from configuration file.");
			System.exit(1);
		}

		makeFileIndex();
	}
	
	private void logMessage(String message) {
		if (!getDate().equals(currentDate)) {
			try {
				logger.removeAppender(fileAppender);
				fileAppender.close();
				uploadFile(currentDate+".html");
				File f = new File(currentDate+".html");
				if (f.exists() && f.canWrite()) {
					f.delete();
				} else {
					System.err.println("Can delete file "+currentDate+".html. "+
					"Either it doesn't exist or we can't write to it.");
					System.exit(1);
				}
				currentDate = getDate();
				fileAppender = new FileAppender(new HTMLLayout(), currentDate+".html");
				logger.addAppender(fileAppender);
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		logger.info(message);
		uploadFile(fileAppender.getFile());
		try {
			makeFileIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	protected synchronized void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		logMessage("["+getTime()+"]<"+sender+"> "+message);
	}
	
	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {	
		// ignores privmsgs
	}
	
	@Override
	protected void onJoin(String channel, String sender, String login,
			String hostname) {
		logMessage("["+getTime()+"] " + sender + " joined the channel.");
	}
	
	@Override
	protected void onPart(String channel, String sender, String login,
			String hostname) {
		logMessage("["+getTime()+"] " + sender + " left the channel.");
	}

	@Override
	protected void onQuit(String sourceNick, String sourceLogin,
			String sourceHostname, String reason) {
		onPart(null, sourceNick, null, null);	
	}
	
	private String getDate(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	private String getDate() {
		return getDate(DATE_FORMAT);
	}

	private String getTime() {
		return getDate(TIME_FORMAT);
	}

	private synchronized void uploadFile(String fileName) {
		try {
			ftp.connect();
			//ftp.changeDirectory(REMOTE_DIRECTORY);
			ftp.uploadFile(fileName, fileName);
			ftp.disconnect();
		} catch (FTPException e) {
			System.out.println(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				//System.exit(1);
			} 
		}

	}

	private synchronized void makeFileIndex() throws FTPException, IOException {
		try {
			ftp.connect();
			//ftp.changeDirectory(REMOTE_DIRECTORY);
			FTPFile files[]=null;
			try {
				files = ftp.directoryList(".");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			FileWriter fstream = new FileWriter("index.tmp");
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("<html><head><title>Twibot Log</title></head><body>" +
					"<h1>Twibot Log</h1>Last modified: "+
					getDate(EXTENDED_DATE_FORMAT)+"<p>");
			out.newLine();
			for (FTPFile f : files) { // TODO: reverse sorting order
				if (!(f.getName().equals(".") ||
						f.getName().equals("..") ||
						f.getName().equals("index.html"))) {
					out.write("<a href="+'"'+f.getName()+'"'+">"+f.getName()+"</a><br>");
					out.newLine();
				}
			}
			out.write("</body></html>");
			out.close();
			ftp.uploadFile("index.tmp", "index.html");
			File f = new File("index.tmp");
			f.delete();
		} finally {
			ftp.disconnect();
		}
	}

}
