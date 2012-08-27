Rudeltippen
------------------

A football betting game based on the Play Framework and Twitter Bootstrap.

If you like Rudeltippen, [Flattr it][5]. Thanks!

Available tournaments
------------------
- 1. Bundesliga 2012/13
- <del>Euro 2012</del>

Features in a Nutshell
------------------
- Automatic results updates
- Automatic tournament management
- Automatic playing schedule updates
- JSON-API for 3rd party clients
- Fluid responsive layout
- Built-in themes from [Bootswatch][15]
- Smartphone and tablet compatible
- Gravatar support
- Twitter support
- SSL support
- Load balancer support
- Multilingual
- Runs on - Linux, Mac or Windows

Requirements
------------------

- [Java SDK 1.6+][1]
- Apache, Nginx, Lighttpd or any other HTTP-Server with Proxy-Support
- SMTP-Account (with SSL/non-SSL, e.g. [Gmail][12])
- MySQL 5+

Available Languages
------------------

- German
- English

Screenshots
------------------

You can find some screenshots of Rudeltippen [here][13]

Installation guide
------------------

This installation guide assumes that you already have JDK 1.6+ installed and have your database- and SMTP credentials right beside you.

### Step 0

Download and install the [Play Framework 1.2.x][17]

### Step 1

Download the [latest tagged version of Rudeltippen][14] and unzip to your INSTLLATIONFOLDER

### Step 2


Open INSTLLATIONFOLDER/conf/application.conf

Set your Rudeltippen URL

```bash
%prod.app.register.url=http://www.yourdomain.com
```

Set the username and password for initial setup (make it "secure")

```bash
app.setup.username=admin
app.setup.password=admin
```

Set the language for Rudeltippen (currently de or en)

```bash
default.language=de
```

Since version 1.1.0 Rudeltippen has a feature which can automatically updates your playing schedule. By default this feature is not enable. If you want to enable this feature set 'automatic.updates' to true

```bash
%prod.automatic.updates=false
```

Set the application key for Rudeltippen (make it "secure"; e.g. use a key-generator)

```bash
application.secret=yoursecretcryptographicskey
```

Set you database connection. For a simple MySQL connection this will be something like this

```bash
%prod.db=mysql://user:pwd@host/database
```

Set your SMTP credentials (uncomment %prod.mail.smtp.channel if you want to use SSL)

```bash
%prod.mail.smtp.host=127.0.0.1
%prod.mail.smtp.user=admin
%prod.mail.smtp.pass=
#%prod.mail.smtp.channel=ssl
```

If you have a Gmail account, just enter your SMTP credentials as follows

```bash
%prod.mail.smtp.host=smtp.gmail.com
%prod.mail.smtp.user=yourGmailLogin
%prod.mail.smtp.pass=yourGmailPassword
%prod.mail.smtp.channel=ssl
```

Set your E-Mail-Sender and reply-to address

```bash
mailservice.replyto=me@example.de
mailservice.from=Rudelmail <me@example.de>
```

By default Rudeltippen runs with -Xmx=128m -Xmx64m. This just be enough for 50 to 100 users. Change this, if required.

```bash
jvm.memory=-Xmx128m -Xms64m
```

Save application.conf.

### Step 3

Follow the section ['Front-end HTTP server' on the Play Framework Documentation][9] to set up your Front-end HTTP Server with Rudeltippen.

> By default Rudeltippen runs on Port 1904. If you need to change this, you'll find the port configuration in application.conf under 'Server configuration'.

### Step 4

> It will most likely be required to make startup.sh, shutdown.sh and INSTLLATIONFOLDER/play/play executable if you are on UNIX or Mac

You are now ready to start Rudeltippen. If you are on UNIX or Mac you can just run the following command in your INSTLLATIONFOLDER

```bash
startup.sh
```

If you’re on Windows, just calling 'startup.bat' would execute Rudeltippen in the current command window. Thus, closing the window will close Rudeltippen. You'll need PsExec from the [PsTools][16] to solve this issue.
After you have downloaded PsTools and have PsExec installed, make sure that psexe.exe is available on your command line. Otherwise add it to your path variable in your system environment.
You'll find an example file called "startup.bat.example" in INSTLLATIONFOLDER. Open the file and change the last line according to your file system.

Example:
```bash
call psexec /s c:\zeus\play-apps\rudeltippen\em\play\play.bat start c:\zeus\play-apps\rudeltippen\em
```

Save the file and rename it to startup.bat. Start Rudeltippen by executing:

```bash
startup.bat
```

### Step 5

Open your browser and go to http://yourdomain.com/system/setup

> You did set username and password in application.conf (see step 3)

### Step 6

Change the default values if you want to and create an initial user.

> The initial user will be an administrative user and automatically activated.
> After the setup is complete, Rudeltippen will automatically load all data for the selected league.

### Step 7

Login with your just created user and enjoy Rudeltippen!

If you need to stop Rudeltippen. Go to your INSTLLATIONFOLDER and call

```bash
shutdown.sh
```

OR

```bash
shutdown.bat
```

Upgrading
------------------

### Step 0

Please check the release notes (if available) before updating!

Go to your INSTLLATIONFOLDER and make a copy of /conf/application.conf file and /conf/custom folder.

### Step 1

Download the [latest tagged version of Rudeltippen][14]

### Step 2

Go to your INSTLLATIONFOLDER and run

```bash
shutdown.sh
```

OR

```bash
shutdown.bat
```

### Step 3

Delete your INSTLLATIONFOLDER

### Step 4

Unzip the latest version of Rudeltippen to your INSTLLATIONFOLDER

### Step 5

In Step 0 you did copy the application.conf and /custom folder from your previous installation, didn't you? Copy application.conf and /custom folder your INSTLLATIONFOLDER/conf and replace the existing file and folder. If you did not make a copy of application.conf, even though you were told to, you need to rerun Step 2 (and only step 2!) of the installation guide (see above) and re-customize your files in /conf/custom folder.

### Step 6

Rudeltippen has a feature which can automatically updates your playing schedule. By default this feature is not enable. If you want to enable this feature and are upgrading from a version prior to 1.1.0 you need to add the following line to your application.conf:

```bash
%prod.automatic.updates=true
```

### Step 7

You are now ready to start Rudeltippen. If you are on UNIX or Mac you can just run the following command in your INSTLLATIONFOLDER

```bash
startup.sh
```

```bash
startup.bat
```

Advanced configuration
------------------

### SSL

By default Rudeltippen runs without SSL. If you want to enable SSL for Rudeltippen you need a private key and a certificate (this may be self-signed). In INSTLLATIONFOLDER/conf/custom you find two empty files:

```bash
cert.txt
key.txt
```
Paste you private key and your certificate in these files.

Open INSTLLATIONFOLDER/conf/application.conf and uncomment the following lines:

```bash
#https.port=9904
#%prod.certificate.key.file=conf/custom/key.txt
#%prod.certificate.file=conf/custom/cert.txt
```

You need to restart Rudeltippen in order for the changes to take place. After the restart Rudeltippen listens for SSL-Connection on Port 9904. You will need to change your HTTP-Front-End Server settings accordingly. Edit or update your Proxy settings to connect to the new port. Read the documentation of your HTTP-Server on how to configure SSL Proxy support.

### Twitter

Rudeltippen can automatically post the following informations: daily top 3, results updated and new registration to a Twitter-Account. If you want to enable this feature you need a consumerkey, a consumersecret, a token and a secret. Open INSTLLATIONFOLDER/conf/application.conf, uncommend the following lines and paste your data:

```bash
#%prod.twitter.consumerkey=
#%prod.twitter.consumersecret=
#%prod.twitter.token=
#%prod.twitter.secret=
#%prod.twitter.enable=false
```

By default 'twitter.enable' is set to 'false'. Set it to 'true' to enable posting to the Twitter-Account.

You need to restart Rudeltippen in order for the changes to take place.

### Log4j

If you want log4j Support for your Application you find an empty log4j configuration file in INSTLLATIONFOLDER/conf/custom. Edit this file with your required appenders and uncomment the following line in INSTLLATIONFOLDER/application.conf

```bash
#%prod.application.log.path=/custom/log4j.prod.xml
```

You need to restart Rudeltippen in order for the changes to take place.

### Load balancer

As mentioned [in the Play documentation][9] you can use a load balancer with multiple Rudeltippen applications. You need to set a different port for each Rudeltippen application and set these ports in your Front-End HTTP Server configuration.
The jobs Rudeltippen executes do not know about how many instances you run and will by default run in each instance. To avoid this, you can set the name of the application- and the job-instance. Rudeltippen will only execute Jobs in an instance where the name of the appname and jobinstance matches.

```bash
application.name=rudeltippen
app.jobinstance=rudeltippen
```

You need to restart Rudeltippen in order for the changes to take place.

Support
------------------
If you need help, just visit the [Support-Page][6] and drop your question (English or German). If you found a bug or have a feature request, please open an issue on Github.

If you like Rudeltippen, [Flattr it][5]. Thanks!

Licence
------------------

Rudeltippen is distributed under [Apache 2 licence][11]

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.playframework.org/download
[3]: http://demo.rudeltippen.de
[4]: http://www.svenkubiak.de
[5]: https://flattr.com/thing/680536/Rudeltippen
[6]: http://dev.svenkubiak.de/rudeltippen
[7]: http://twitter.github.com/bootstrap/
[8]: http://twitter.com/rudeltippen
[9]: http://www.playframework.org/documentation/1.2.4/production
[11]: http://www.apache.org/licenses/LICENSE-2.0.html
[12]: http://mail.google.com/
[13]: https://picasaweb.google.com/108885060281225128504/Rudeltippen
[14]: https://github.com/svenkubiak/Rudeltippen/tags
[15]: http://bootswatch.com/
[16]: http://technet.microsoft.com/de-de/sysinternals/bb896649.aspx
[17]: http://www.playframework.org/documentation/1.2.5/install