dIRCbot
============

Introduction
------------
The most important function for this bot is to relay messages from an IRC
server to a Jabber/XMPP client and vice versa.

This is sort of a semi-weird project. It has a bunch of different modules.
All of them will most likely not fit everybody, but fortunately it's easy
to disable the ones that you don't need. Essentially this is an IRC-bot
with the following functionality:

## XMPPBot
Relays everything that is said in the channel to an 
XMPP/Jabber/Gtalk-account. Also relays private messages and allows you to
restart the server remotely (and do a couple of other things as well).

## RemoteFTPLogBot
Logs everything that is said in the channel and then uploads it to a remote
FTP-server.

## TwitterBot (has been disabled for now)
Tweets everything that is said in the channel.

Supports the following options in the config file:

  - XMPPSERVER (true/false) Enables/disables the XMPP-bot. Will default to false if this key doesn't exist. None of the other values are necessary if the module is turned off.
  
  - XMPPPORT
  
  - XMPPUSER
  
  - XMPPPW
  
  - XMPPRECEIVER The account to send the messages to
  
  - (optional) XMPPTRUSTEDSENDER Ignores messages from this senders who's name starts with this and allows you to send certain commands. If the sender is set to "A" then both "Ab" and "Ac" will fall under this category.

Please note
-----------
The bot is in quite an early stage at this point. The TwitterBot stopped
working after Twitter changed how they require you to authenticate and I
will have to get around to fix that eventually.

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
it's options to false or don't write it out in the first place.

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
