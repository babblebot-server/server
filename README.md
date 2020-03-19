# BabbleBot-Server

[![GitHub issues](https://img.shields.io/github/issues/bendavies99/babblebot-server)](https://github.com/bendavies99/babblebot-api/issues)
[![GitHub forks](https://img.shields.io/github/forks/bendavies99/babblebot-server)](https://github.com/bendavies99/babblebot-api/network)
[![GitHub stars](https://img.shields.io/github/stars/bendavies99/babblebot-server)](https://github.com/bendavies99/babblebot-api/stargazers)
[![GitHub license](https://img.shields.io/github/license/bendavies99/babblebot-server)](https://github.com/bendavies99/babblebot-api)
[![Commitizen friendly](https://img.shields.io/badge/commitizen-friendly-brightgreen.svg)](http://commitizen.github.io/cz-cli/)
[![Twitter](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Fgithub.com%2Fbendavies99%2Fbabblebot-server)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fbendavies99%2Fbabblebot-server)
[![codecov](https://codecov.io/gh/bendavies99/babblebot-server/branch/master/graph/badge.svg)](https://codecov.io/gh/bendavies99/babblebot-api)
[![Build Status](https://travis-ci.org/bendavies99/babblebot-server.svg?branch=master)](https://travis-ci.org/bendavies99/babblebot-api)

## Getting Started
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

This project is licensed under the GPLv3 or later License - see the [LICENSE.md](LICENSE.md) file for details
