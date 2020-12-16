<p align="center"><img src="https://socialify.git.ci/bendavies99/Babblebot-Server/image?description=1&font=Source%20Code%20Pro&language=1&owner=0&pattern=Floating%20Cogs&theme=Dark" alt="BabbleBot-Server" width="640" height="320" /></p>  
  
<p align="center">  
 <a href="https://github.com/bendavies99/BabbleBot-Server/actions"><img src="https://action-badges.now.sh/bendavies99/Babblebot-Server?action=build" alt="Build Status"></a>  
 <a href="https://www.codacy.com/gh/bendavies99/BabbleBot-Server/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bendavies99/BabbleBot-Server&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/e3a344a5c508491096ca105b27e92ed9" alt="Coverage Status"></a>  
 <a href="https://github.com/bendavies99/BabbleBot-Server/releases"><img src="https://img.shields.io/github/v/release/bendavies99/Babblebot-Server" alt="Version"></a>  
 <a href="https://github.com/bendavies99/BabbleBot-Server/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/bendavies99/babblebot-server" alt="License"></a>  
 <a href="https://discord.gg/uUdX9pw"><img src="https://img.shields.io/discord/409004433750360090?color=7289da&label=Babblebot&logo=discord" alt="Chat"></a>  
 <br>  
 <a href="https://javadoc.io/doc/co.uk.bjdavies/babblebot-server-api"><img src="https://javadoc.io/badge2/co.uk.bjdavies/babblebot-server-api/javadoc.svg" alt="Javadoc"></a>  
</p>  
  
## Table of Contents  
  
[Getting Started](#getting-started)  
 * [Installing](#installing)  
 * [Configuration](#configuration)  
 * [Updating](#updating)  
   * [Manual Update](#manual-update)  
   * [Automatic Update](#automatic-update)  
 
 [Plugins](#plugins)  
 * [Officially Supported Plugins](#officially-supported-plugins)  
 * [Community Plugins](#community-plugins)  
 * [Installing Plugins](#installing-plugins)  
 * [Write your own Plugin](#write-your-own-plugin) 

[Commands](#commands) 
 
  [Contributing](#contributing)  
  
[Contributors](#contributors)  
  
[License](#license)  
    
## Getting Started
Fully modular bot for Discord built on top of Discord4J which can be found [here](https://github.com/Discord4J/Discord4J)

Features include:
- Lightweight base server
- Fully configurable server
- Plugin system (configuration, persistence, middleware, web-views) (🚧)
- Variable parser (using ${var} in response messages)
- Persistent data for plugins (🚧)
- Web client (🚧)
- Automatic updates (for minor and patch updates)
- Announcement System (Notify users through discord)
- Fully featured help system

```md
NOTE: The emoji "🚧" means that the current features are under heavy development and are experimental
```
//TODO:

Babblebot-Server is a bot for discord everything is fully modular the server comes with a core plugin.   
This plugin comes with 3 commands they are:-  
- **help** - This will send the user making the command a DM with all the registered commands  
- **ignore** - If this command is used the channel it has been used in will be ignored by the bot.  
- **listen** - This will make the bot start listening to the channel again.  
  
### Config  
Example config:  
```json  
{  
  "discord": {  
  "token": "<-- Your discord token -->",  
  "commandPrefix": "!"  
  },  
  "http": {  
  "port": 8090  
  },  
  "database": {  
  "type": "sqlite",  
  "database": "Core.db"  
  }  
}  
```   
  
  
#### Discord  
This is the settings for discord part of the server  
##### Token  
Get your bot token at [Discord Developers](https://discord.com/developers)  
##### Command Prefix  
This will be what you use to activate command e.g. !help or $help etc.  
  
#### HTTP  
This is the settings for the web-server **COMING SOON**  
##### PORT  
This is the port you cant to the run the server on  
  
#### DATABASE  
This is the way to persist data for your server and your plugins  
##### Type  
The type of database  
- MySQL  
- PostgreSQL  
- SQLite (**currently only supported**)  
- MongoDB  
##### Database  
This is the database name as SQLite just has a file for a database it's the filename.  
  
  
  
  
### Modules  
  
#### [Server](https://github.com/bendavies99/BabbleBot-Server/tree/master/server)  
This is the main application where all the logic comes in for:-  
- Config  
- Http  
- Discord  
- Commands  
  
#### [Agent](https://github.com/bendavies99/BabbleBot-Server/tree/master/agent)  
This handles the instrumentation of the Database models  
  
#### [Core](https://github.com/bendavies99/BabbleBot-Server/tree/master/core)  
This is the core plugin that is used by the server.  
  
#### [API](https://github.com/bendavies99/BabbleBot-Server/tree/master/api)  
This is the **Java** API for developing plugins please refer to the API readme for more details.  
  
Note: If you would like a JS/TS API please refer to [babblebot-api](https://github.com/bendavies99/babblebot-api)  
  
#### [App](https://github.com/bendavies99/BabbleBot-Server/tree/master/app)  
The app is the executable that can be ran by the end-user,  
  
### Installing  
  
Please find the latest release fom here [Releases](https://github.com/bendavies99/BabbleBot-Server/releases).  
  
When you download the release extract the zip or tar.gz, you will find a **bin** folder open that   
and then open up app.bat or app.sh  
  
##### Enjoy!  
  
## Versioning  
  
We use [SemVer](http://semver.org/) for versioning. For the versions available,   
see the [tags on this repository](https://github.com/bendavies99/babblebot-api/tags).  
  
## Authors  
  
- **Ben Davies** - _Lead Developer_ - [Github](https://github.com/bendavies99)  
  
See also the list of [contributors](https://github.com/bendavies99/babblebot-api/contributors) who participated in this project.  
  
## License  
  
This project is licensed under the MIT Licence - see the [LICENSE.md](LICENSE.md) file for details