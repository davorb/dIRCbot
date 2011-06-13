dIRCbot
============

Introduction
------------
This is sort of a semi-weird project. It has a bunch of different modules.
All of them will most likely not fit everybody, but fortunately it's easy
to disable the ones that you don't need. Essentially this is an IRC-bot
with the following functionality:

  - XMPPBot
Relays everything that is said in the channel to an 
XMPP/Jabber/Gtalk-account. Also relays private messages and allows you to
restart the server remotely (and do a couple of other things as well).

  - RemoteFTPLogBot
Logs everything that is said in the channel and then uploads it to a remote
FTP-server.

  - TwitterBot (has been disabled for now)
Tweets everything that is said in the channel.

Please note
-----------
The bot is in quite an early stage at this point. The TwitterBot stopped
working after Twitter changed how they require you to authenticate and I
will have to get around to fix that eventually.

Also, right now, you have to "manually" add your bots to DircbotMain.java
if you want to disable/enable any bots besides the default one. I will
get around to fixing this eventually as well.

If you decide to write your own bot, it might be a good idea to keep in
mind that I haven't added multithreading yet (comming soon!). So if one
bot is really really slow, it's going to slow down the other bots.

Installation
------------
Create a file called "settings.conf" in the same directory as your jar file.
These are some of the settings you might want to change:

    xmppbot=true
    remoteftpbot=true
    SERVER=se.quakenet.org
    CHANNEL=#mychannel
    DEBUG=true
    XMPPSERVER=talk.google.com
    XMPPPORT=5222
    XMPPUSER=mymail@gmail.com
    IRCNICK=twibot-test
    FTPHOST=myhost.com
    FTPUSER=myuser
    FTPPW=password

To disable one of the bots, like the XMPP-bot or the remote ftp bot, set
it's options to false.

After that type this to run the program:
    java -jar dircbot.jar
    
    
Required libraries
------------------
The following libraries are required to be able to compile the source code:

  - log4j
  - edtFTPj
  - pircbot
  - smack
  - twitter4j

Contact
-------
If there's anything else, feed free to contact me @ davor @ davor.se