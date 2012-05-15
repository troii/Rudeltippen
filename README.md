PLEASE NOT: Current Version 1.0.0.Beta1 is for testing purposes only! Final Version of 1.0.0 is planned for end of May.

Rudeltippen
===========

A betting game based on the Play Framework and Twitter Bootstrap.

Features in a Nutshell
===========
- Automatic Result updates
- Supports multiple Databases
- Tournament Management
- Themeable (through [Bootstrap][7])
- Multilingual

Requirements
===========

- [Java SDK 1.6+][1]
- [Play Framework 1.2.x][2]
- Apache, Nginx or any other HTTP-Server with Proxy-Support
- SMTP-Server (SSL/non-SSL)
- Linux, Mac or Windows
- MySQL, PostgreSQL, MSSQL or Oracle

Translations
===========

- German
- English (TBD)

Demo
===========
You can find a Demo [here][3]. You can login with the following credentials:


Username: demo@rudeltippen.de

Password: demo12345


Please note that this is not an administrative Account, so some Features are not available. Also User Registration is not enabled.

Installation
===========

This installation guide assumes that you already have JDK 1.6+ running, have your Database configured and your SMTP-Credentials right at your side.

1. Download [Play Framework 1.2.x][2] and unzip. From the Play Documentation:

For convenience, you should add the framework installation directory to your system PATH. On UNIX systems will be something like:

```bash
export PATH=$PATH:/path/to/play
```

On windows systems you'll need to set it in the global environment variables.

> If you’re on UNIX, make sure that the play script is executable (otherwise do a chmod a+x play).

2. Download the current Version of Rudeltippen and unzip.

3. Open <INSTLLATIONFOLDER>/conf/application.conf

Upgrade
===========

Support
===========

If you need help, just visit the [Support-Page][6] and paste your Question (English or German). I'll get to it ASAP. If you found a Bug, please open an Issue on Github.

Stuff
===========

- [Author's Homepage (German)][4]
- [flattr][5]
- [Support-Page (German or Englisch)][6]

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.playframework.org/download
[3]: http://demo.rudeltippen.de
[4]: http://www.svenkubiak.de
[5]: https://flattr.com/thing/29899/svenkubiak-de
[6]: http://dev.svenkubiak.de/rudeltippen
[7]: http://twitter.github.com/bootstrap/