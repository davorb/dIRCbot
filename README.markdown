dIRCbot
=======

Introduction
============
This bot relays messages between IRC and Jabber/XMPP/Gtalk. It will also
relay private messages.


Installation
============
Create a file called `settings.conf` in the same directory as your jar file.
The easiest thing will most likely be if you base in on the 
settings.conf.example file that can be found in this repository.

After that type this to run the program:

     java -jar dircbot.jar
    
I suggest that you run this program with 
[screen](http://www.debian-administration.org/articles/34).

Configuration
=============
Use settings.conf.example as a base for a new example file. The example file
needs to be placed in the same folder as the jar-file.

Most of the settings are self-explanatory except perhaps the optional option
XMPPTRUSTEDSENDER. This option is basically tells dIRCbot to ignore all users
that have this prefix in their nickname.

So lets say that you set the option to "al" and have three users in an IRC
channel named "al-2", "al" and "bob". dIRCbot will now only relay messages
from the user bob.

A list of supported encodings can be found 
[here](http://docs.oracle.com/javase/1.4.2/docs/guide/intl/encoding.doc.html).

Commands
========

IRC-commands
------------
The following commands can only be run by XMPPTRUSTEDSENDER. They need to be
written in the IRC channel.

* `!time` displayes the current time.
* `!help` shows a quick help message.
* `!stop` stops forwarding messages.
* `!start` resumes forwarding messages.

XMPP-commands
-------------
The following commands can only be sent from the XMPP client.

* `STOP` stops forwarding messages from the IRC channel to the XMPP client.
* `START` resumes forwarding messages.
* `STATUS` tells you if message forwarding is turned on or off.
* `USERS` shows a list of users connected to the channel.
* `RECONNECT` disconnects from the IRC server and connects back to it.

Required libraries
==================
The following libraries are required to be able to compile the source code:

  - [pircbot](http://www.jibble.org/pircbot.php)
  - [smack](http://www.igniterealtime.org/projects/smack/)

Contact
=======
If there's anything else, feed free to contact me at 
[davor@davor.se](mailto:davor@davor.se)
