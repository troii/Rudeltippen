Rudeltippen
===========

A betting game based on the Play Framework and Twitter Bootstrap. Ready to go for the Euro 2012.

Features in a Nutshell
===========
- Automatic results updates
- Automatic Tournament Management
- Themeable (through [Bootstrap][7])
- Responsive Layout - works on iPhone and iPad (through [Bootstrap][7])
- Supports multiple Databases
- Multilingual

Requirements
===========

- [Java SDK 1.6+][1]
- Apache, Nginx or any other HTTP-Server with Proxy-Support (Apache recommended)
- SMTP-Server (SSL/non-SSL)
- Linux, Mac or Windows
- MySQL, PostgreSQL, MSSQL or Oracle Databse (MySQL recommended)

Translations
===========

- German
- English

Demo
===========
You can find a Demo [here][3]. Login with the following credentials:


Username: demo@rudeltippen.de

Password: demo12345


Please note that the above user is not an administrative user, so some Features are not available. Also Registration is not enabled.

Installation
===========

This installation guide assumes that you already have JDK 1.6+ installed and running, and have your Database- and SMTP-Credentials right beside you.

Step 1
------------------

Download the latest version of Rudeltippen and unzip to your INSTLLATIONFOLDER

Step 2
------------------

Open INSTLLATIONFOLDER/conf/application.conf

Set your Rudeltippen URL

```bash
%prod.app.register.url=http://www.yourdomain.com
```

Set the Username and Password for inital setup

```bash
app.setup.username=admin
app.setup.password=admin
```

Set you Database connection. For a simple MySQL-Connection this will be something like this

```bash
%prod.db=mysql://user:pwd@host/database
```

Set your SMTP-Credentials (uncomment %prod.mail.smtp.channel if you want to use SSL)

```bash
%prod.mail.smtp.host=127.0.0.1
%prod.mail.smtp.user=admin
%prod.mail.smtp.pass=
#%prod.mail.smtp.channel=ssl
```

Set your E-Mail-Sender and reply-to Address

```bash
mailservice.replyto=me@example.de
mailservice.from=Rudelmail <me@example.de>
```

Set the Twitter-Feed which is displayed at the Dashboard. Leave this blank, if you don't want that Feature.

```bash
dashboard.twitter.username=mytwitterusername
```

By defautl Rudeltippen runs with -Xmx=128m -Xmx64m. Change this if need.

```bash
jvm.memory=-Xmx128m -Xms64m
```

Save application.conf.

Step 3
------------------

Follow the Section ['Front-end HTTP server' on the Play Framework Documentation][9] to set up your Fron-end HTTP Server with Rudeltippen.

> By default Rudeltippen runs on Port 1904. If you need to change this, you'll find the Port Configuration in application.conf 'Server configuration'.

Step 4
------------------

You are now ready to start Rudeltippen. If you are on UNIX or Mac you can just run the following command in your INSTLLATIONFOLDER

```bash
startup.sh
```

If you’re on Windows, just calling 'startup.bat' will execute Rudeltippen in the current Command-Window. Thus, closing the Window will close Rudeltippen. To solve this issue, download and install [PsExec from the PsTools][10] and start Rudeltippen with

```bash
/path/to/psexec/psexec.exe /path/to/play/play.bat start /path/to/rudeltippen
```

Step 5
------------------

Open your Browser and go to http://yourdomain.com/setup

> You did set username and password in application.conf (see 3.)
> After the Setup is complete Rudeltippen will automaticly load all games for the Euro 2012.

Step 6
------------------

Change the default values if you want to and create an inital user.

> The inital user will be an administrative user and automaticly activated.

Step 7
------------------

Login with your newely create user and enjoy Rudeltippen!

If you need to stop Rudeltippen. Got to your INSTLLATIONFOLDER and call

```bash
shutdown.sh
```

OR

```bash
shutdown.bat
```


Upgrading
===========

Step 0
------------------

Go to your INSTLLATIONFOLDER and make a copy of /conf/application.conf. We will need this File once upgrading is finished.

Step 1
------------------

Download the latest of Rudeltippen.

Step 2
------------------

Go to your INSTLLATIONFOLDER rudeltippen and run

```bash
shutdown.sh
```

OR

```bash
shutdown.bat
```

Step 3
------------------

Completly delete your INSTLLATIONFOLDER

Step 4
------------------

Unzip the latest version of Rudeltippen to your INSTLLATIONFOLDER

Step 5
------------------

In Step 0 you did copy the application.conf from your previous installation, didn't you? Copy this file to your INSTLLATIONFOLDER/conf and replace the existing file. If you did not make of copy application.conf, even of you were told to, you need to rerun Step 2 (and only step 2!) of the Installation guid (see above).

Step 6
------------------

You are now ready to start Rudeltippen. If you are on UNIX or Mac you can just run the following command in your INSTLLATIONFOLDER

```bash
startup.sh
```

If you’re on Windows, just calling 'startup.bat' will execute Rudeltippen in the current Command-Window. Thus, closing the Window will close Rudeltippen. Do solve this issue, download and install [PsExec from the PsTools][10] and start Rudeltippen with

```bash
/path/to/psexec/psexec.exe /path/to/play/play.bat start /path/to/INSTLLATIONFOLDER
```

Step 7
------------------

Enjoy your latest version of Rudeltippen!


Support
===========

If you need help, just visit the [Support-Page][6] and paste your Question (English or German). If you found a Bug, please open an Issue on Github.


Stuff
===========

- [Author's Homepage (German)][4]
- Follow [@rudeltippen][8] for the latest development News
- [flattr][5]
- [Support-Page (German or Englisch)][6]

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.playframework.org/download
[3]: http://demo.rudeltippen.de
[4]: http://www.svenkubiak.de
[5]: https://flattr.com/thing/680536/Rudeltippen
[6]: http://dev.svenkubiak.de/rudeltippen
[7]: http://twitter.github.com/bootstrap/
[8]: http://twitter.com/rudeltippen
[9]: http://www.playframework.org/documentation/1.2.4/production
[10]: http://technet.microsoft.com/de-de/sysinternals/bb897553.aspx