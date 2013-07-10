Rudeltippen
------------------

A football betting game based on the Play Framework and Twitter Bootstrap.

If you like Rudeltippen, [Flattr it][5]. Thanks!

Available tournaments
------------------
- 1. Bundesliga 2013/14

Features in a Nutshell
------------------
- Automatic results updates
- Automatic tournament management
- Automatic playing schedule updates
- Fluid responsive layout using Twitter Bootstrap
- Smartphone and tablet compatible
- Gravatar support
- Multilingual

Requirements
------------------

- [Java SDK 1.7+][1]
- Apache, Nginx, Lighttpd or any other HTTP-Server with Proxy-Support
- SMTP-Account (with SSL/non-SSL, e.g. [Gmail][12])
- MySQL 5+

Available Languages
------------------

- German
- English


Installation guide
------------------

This installation guide assumes that you already have JDK 1.7+ installed and have your database- and SMTP-credentials right beside you.

### Step 0

Download and install [Play Framework 1.2.x][17]

### Step 1

Download the [latest tagged version of Rudeltippen][14] and unzip to your INSTLLATIONFOLDER

### Step 2

Rename INSTLLATIONFOLDER/conf/application.conf.example to application.conf

Open application.conf

Set your Rudeltippen URL

```bash
%prod.app.register.url=http://www.yourdomain.com
```

Set the language for Rudeltippen (de or en)

```bash
default.language=de
```

Set the application key for Rudeltippen (make it "secure")

```bash
application.secret=yoursecretcryptographicskey
```

Set you MySQL database connection.

```bash
%prod.db=mysql://user:pwd@host/database
```

Set your SMTP credentials (uncomment %prod.mail.smtp.channel if you want to use SSL)

```bash
%prod.mail.smtp.host=127.0.0.1
%prod.mail.smtp.user=admin
%prod.mail.smtp.pass=
%prod.mail.smtp.channel=ssl
```

If you have a Gmail account, just enter your SMTP credentials as follows

```bash
%prod.mail.smtp.host=smtp.gmail.com
%prod.mail.smtp.user=yourGmailLogin
%prod.mail.smtp.pass=yourGmailPassword
%prod.mail.smtp.channel=ssl
```

Set your eMail-Sender and reply-to address

```bash
mailservice.replyto=me@example.de
mailservice.from=Rudelmail <me@example.de>
```

By default, Rudeltippen runs with -Xmx=128m -Xmx64m which should be enough memory for at least 50 users. Change this, if required.

```bash
jvm.memory=-Xmx128m -Xms64m
```

Rename application.conf.example to application.conf and save application.conf

### Step 3

Follow the section ['Front-end HTTP server' on the Play Framework Documentation][9] to set up your Front-end HTTP Server with Rudeltippen.

> By default Rudeltippen runs on port 1904. If you need to change this, you'll find the port configuration in application.conf under 'Server configuration'.

### Step 4

Start Rudeltippen by executing the following command in your INSTLLATIONFOLDER:

```bash
play start
```

> Note: play start and play stop will only work correctly under Linus and Mac. If you close your command prompt on windows, the Play application will stop also. I recommend using PsExec for running the Play application in the background. You'll find an example script [here][18].

### Step 5

Open a Browser and go to http://www.yourdomain.com

The application will now be setup and the initial data will be loaded. After setup has finished you will be redirect to the login page.

The initial data will create an admin user with the following credentials:

Username: admin
Password: admin
E-Mail: admin@example.com

Changing of this credentials is mandatory!

### Step 6

If you need to stop Rudeltippen at any time. Go to your INSTLLATIONFOLDER and execute the followin command

```bash
play stop
```

Upgrading
------------------

### Step 1

Download the [latest tagged version of Rudeltippen][14]

### Step 2

Go to your INSTLLATIONFOLDER and stop Rudeltippen by calling the following command

```bash
play stop
```

### Step 3

Unzip the latest version of Rudeltippen to your INSTLLATIONFOLDER and replace every every existing file.

### Step 4

You are now ready to start Rudeltippen. If you are on UNIX or Mac you can just run the following command in your INSTLLATIONFOLDER

```bash
play start
```

Advanced configuration
------------------

### Log4j

If you want log4j Support for your Application you find an empty log4j configuration file in INSTLLATIONFOLDER/conf/custom. Edit this file with your required appenders and uncomment the following line in INSTLLATIONFOLDER/application.conf

```bash
#%prod.application.log.path=/log4j.prod.xml
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

Licence
------------------

Rudeltippen is distributed under [Apache 2 licence][11]

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.playframework.org/download
[3]: http://demo.rudeltippen.de
[4]: http://www.svenkubiak.de
[5]: https://flattr.com/thing/1628177/Rudeltippen
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
[18]: http://pastebin.com/Aqby1atw 