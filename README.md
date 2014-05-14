Rudeltippen [![Build Status](https://secure.travis-ci.org/svenkubiak/Rudeltippen.png?branch=master)](http://travis-ci.org/svenkubiak/Rudeltippen)
------------------

A football betting game based on the Play Framework and Twitter Bootstrap.

If you like Rudeltippen, [Flattr it][5]. Thanks!

Available tournaments
------------------
- 2014 FIFA World Cup

Features in a Nutshell
------------------
- Mobile first layout based Twitter Bootstrap 3
- Automatic results updates
- Automatic tournament management
- Automatic playing schedule updates
- User and Tournament statistics
- Gravatar support
- Multilingual
- ...and a lot more

Screenshots
------------------
![Rudeltippen login](https://raw.github.com/svenkubiak/Rudeltippen/master/assets/rudelscreen.png)
![Rudeltippen overview](https://raw.github.com/svenkubiak/Rudeltippen/master/assets/rudelscreen-2.png)

Screenshots made with some awesomeness from [Am I Responsive][2].

Requirements
------------------

- [Java SDK 1.7+][1]
- Nginx, Apache, Lighttpd or any other HTTP-Server with Proxy-Support
- SMTP-Account (with SSL/non-SSL, e.g. [Gmail][12])
- MySQL 5+

Available Languages
------------------

- German
- English


Installation guide
------------------

This installation guide assumes that you already have JDK 1.7+ installed and that you have your database- and SMTP-credentials ready.

### Step 0

Download and install [Play Framework 1.2.7][17] on your server. Make sure, that your path to play is set as an environment variable. Test your play installation by running the following command

```bash
play
```

Set your play installation to production environment, by running the follwing command

```bash
play id
```

When prompted, type "prod" (without the qoutes).

### Step 1

Download the [latest release of Rudeltippen][14] (the X.X.X.zip version file, not the source code zip) and unzip it to your INSTLLATIONFOLDER

### Step 2

Rename INSTLLATIONFOLDER/conf/application.conf.example to application.conf

Open application.conf

Set your Rudeltippen URL

```bash
%prod.app.register.url=http://www.yourdomain.com
```

Set the default language for Rudeltippen (de or en)

```bash
default.language=de
```

Set your timezone and your date and time format
```bash
app.timezone=Europe/Berlin
app.dateformat=dd.MM.yyyy
app.timeformat=HH:mm
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

By default, Rudeltippen runs with -Xmx=128m -Xmx64m which should be enough memory for at least 30 users.

```bash
jvm.memory=-Xmx128m -Xms64m
```

### Step 3

Follow the section ['Front-end HTTP server' on the Play Framework Documentation][9] to set up your Front-end HTTP Server with Rudeltippen.

> By default Rudeltippen runs on port 1904. If you need to change this, you'll find the port configuration in application.conf under 'Server configuration'.

### Step 4

Start Rudeltippen by executing the following command in your INSTLLATIONFOLDER:

```bash
play start
```

> Note: play start and play stop will only work correctly under Linux and Mac. If you close your command prompt on windows, the Play application will stop also. I recommend using PsExec for running the Play application in the background. You'll find an example script [here][18].

### Step 5

Open a Browser and go to http://www.yourdomain.com

The application will now be set up and the initial data will be loaded. After setup has finished you will be redirect to the login page.

The initial data will create an admin user with the following credentials:

```bash
Username: admin
Password: admin
E-Mail: admin@foo.bar
```

Changing of this credentials is mandatory! You can change your credentials under "My Profile".

### Step 6

If you need to stop Rudeltippen at any time. Go to your INSTLLATIONFOLDER and execute the following command

```bash
play stop
```

Upgrading
------------------

### Step 1

Download the [latest release of Rudeltippen][14] (the X.X.X.zip version file, not the source code zip)

### Step 2

Go to your INSTLLATIONFOLDER and stop Rudeltippen by calling the following command

```bash
play stop
```

### Step 3

Delete everything in your INSTLLATIONFOLDER except the /conf folder.

Unzip the latest version of Rudeltippen to your INSTLLATIONFOLDER and replace all existing file.

### Step 4

Start the application with the following command in your INSTLLATIONFOLDER

```bash
play start
```

Advanced configuration
------------------

### Log4j

If you want log4j Support for your Application you'll find an empty log4j configuration file (log4j.prod.xml.example) in INSTLLATIONFOLDER/conf. Rename the file to log4j.prod.xml and edit this file with your required appenders. Afterwards uncomment the following line in INSTLLATIONFOLDER/application.conf

```bash
#%prod.application.log.path=/log4j.prod.xml
```

You need to restart Rudeltippen in order for the changes to take effect.

### Load balancer

As mentioned [in the Play documentation][9] you can use a load balancer with multiple Rudeltippen applications. You need to set a different port for each Rudeltippen application and set these ports in your Front-End HTTP Server configuration.
The jobs Rudeltippen executes do not know about how many instances you run and will by default run in each instance. To avoid this, you can set the name of the application- and the job-instance. Rudeltippen will only execute Jobs in an instance where the name of the appname and jobinstance matches.

```bash
application.name=rudeltippen
app.jobinstance=rudeltippen
```

You need to restart Rudeltippen in order for the changes to take effect.

Development
------------------
If you work with the master branch or the source code releases you need to run "ant" in the home directory. The ant jobs copies the JavaScript- and CSS-Resources into single files (combined.min.js and combined.min.css) and builds a ZIP-File (rudeltippen.zip) ready for testing or deployment.

Licence
------------------

Rudeltippen is distributed under [Apache 2 licence][11]

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://ami.responsivedesign.is
[5]: https://flattr.com/thing/1628177/Rudeltippen
[9]: http://www.playframework.com/documentation/1.2.7/production
[11]: http://www.apache.org/licenses/LICENSE-2.0.html
[12]: http://mail.google.com/
[14]: https://github.com/svenkubiak/Rudeltippen/releases
[17]: http://www.playframework.com/documentation/1.2.7/install
[18]: http://pastebin.com/Aqby1atw 