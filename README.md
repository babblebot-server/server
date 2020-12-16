<p align="center"><img src="https://socialify.git.ci/bendavies99/Babblebot-Server/image?description=1&font=Source%20Code%20Pro&language=1&owner=0&pattern=Floating%20Cogs&theme=Dark" alt="BabbleBot-Server" width="640" height="320" /></p>  

<p align="center">  
 <a href="https://github.com/bendavies99/BabbleBot-Server/actions"><img src="https://action-badges.now.sh/bendavies99/Babblebot-Server?action=build" alt="Build Status"></a>  
 <a href="https://www.codacy.com/gh/bendavies99/BabbleBot-Server/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bendavies99/BabbleBot-Server&amp;utm_campaign=Badge_Grade"><img src="https://app.codacy.com/project/badge/Grade/e3a344a5c508491096ca105b27e92ed9" alt="Coverage Status"></a>  
 <a href="https://github.com/bendavies99/BabbleBot-Server/releases"><img src="https://img.shields.io/github/v/release/bendavies99/Babblebot-Server" alt="Version"></a>  
 <a href="https://github.com/bendavies99/BabbleBot-Server/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/bendavies99/babblebot-server" alt="License"></a>  
 <a href="https://discord.gg/uUdX9pw"><img src="https://img.shields.io/discord/409004433750360090?color=7289da&label=Babblebot&logo=discord" alt="Chat"></a>  
 <br>  
 <a href="https://javadoc.io/doc/co.uk.bjdavies/babblebot-server-api"><img src="https://javadoc.io/badge2/co.uk.bjdavies/babblebot-server-api/javadoc.svg?label=API%20Javadoc" alt="API Javadoc"></a>  
 <a href="https://javadoc.io/doc/co.uk.bjdavies/babblebot-server-db"><img src="https://javadoc.io/badge2/co.uk.bjdavies/babblebot-server-db/javadoc.svg?label=DB%20Javadoc" alt="DB Javadoc"></a>  
 <a href="https://javadoc.io/doc/co.uk.bjdavies/babblebot-server-web"><img src="https://javadoc.io/badge2/co.uk.bjdavies/babblebot-server-web/javadoc.svg?label=Web%20Javadoc" alt="Web Javadoc"></a>  
</p>  

## Table of Contents

[Getting Started](#getting-started)

*   [Installation](#installation)
*   [Configuration](#configuration)
*   [Updating](#updating)

[Plugins](#plugins)

*   [Officially Supported Plugins](#officially-supported-plugins)
*   [Community Plugins](#community-plugins)
*   [Installing Plugins](#installing-plugins)
*   [Write your own plugin](#write-your-own-plugin)

[Commands](#commands)

[Contributing](#contributing)

[Authors](#authors)

[License](#license)

## Getting Started

A fully modular bot for Discord built on top of Discord4J which can be found [here](https://github.com/Discord4J/Discord4J)

Features include:

*   Fully configurable, Lightweight server
*   Plugin system (configuration, persistence, middleware, web-views) (🚧)
*   Variable parser (using ${var} in response messages)
*   Persistent data for plugins (🚧)
*   Web client (🚧)
*   Automatic updates (for minor and patch updates)
*   Announcement System (Notify users through discord)
*   Fully featured help system

```text
NOTE: The emoji "🚧" means that the current features are under heavy development and are experimental
```

## Installation

### Prerequisites:

*   Java 11
*   Discord bot token (**Unsure?** Follow this [guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token))
*   Ensure your bot is on your server

### Steps to install Babblebot-Server:

*   Download the latest release from: [Releases](https://github.com/bendavies99/BabbleBot-Server/releases)
*   Extract the archive anywhere on your machine
*   Open up the `bin` folder if you are on **Windows** run `./Babblebot.bat`; **Linux** run `./Babblebot`
*   On your first run a folder called `config` will be created please add your `Discord bot token` to `config/config.json`
*   Run the application again, then your bot should be online (**Any issues?** please create a discussion [here](https://github.com/bendavies99/BabbleBot-Server/discussions))

## Configuration

Please look at this configuration guide [here](https://github.com/bendavies99/BabbleBot-Server/wiki/Configuration)

## Updating

### Manual Updating

Steps to update manually:

*   Download the latest release from: [Releases](https://github.com/bendavies99/BabbleBot-Server/releases)
*   Extract the archive anywhere on your machine
*   Replace the `lib` folder and replace `Babblebot` and `Babblebot.bat`

```text
NOTE: if you don't care about keeping the same folder you can copy over the folders `config` and `plugins` and your database file if you have one
```

### Automatic Updates

Babblebot has a system in place to automatically update itself when a new release is made to turn this off under the config there is `system.autoUpdate` change this to `false`

The system will only update when there is a minor or patch update which would be only Y and Z in this example `X.Y.Z`

```text
NOTE: you can update to a major version by using --updateMajor when running babblebot it is not recommended as plugins may not support this version use with caution
```

## Plugins

Plugins are what give power to Babblebot. Without them Babblebot will be a strong server for just 3 commands

Plugins Offer:

*   More Commands
*   Middleware for the command dispatcher
*   More variables (🚧)

### Officially Supported Plugins

| Name | Description | Link | Server Versions supported | Namespace |
| ---- | ----------- | ---- | ------------------------------- | ---- |
| audiodj | Play audio in a voice server from various sources | [Link](https://github.com/bendavies99/babblebot-audiodj-java) | 2.x.x, 3.x.x | Configurable (Default: None) |

### Community Plugins

| Name | Description | Link | Namespace |
| ---- | ----------- | ---- | --------- |

If you wish to have your plugin displayed here please create a discussion [here](https://github.com/bendavies99/BabbleBot-Server/discussions)

### Installing Plugins

Steps to install a plugin:

*   Locate and download the latest version of a plugin it should be a `.jar` file
*   If not already created, create a `plugins` folder inside the `bin` folder
*   Create a new folder for your plugin it needs to be the same name as the `.jar` file
*   Place your jar file inside that folder
*   Open up your `config/config.json` file and add inside
    ```json
      // Old
      "plugins": []
      // New
      "plugins": [{ "pluginLocation": "<!-- use folder name you used earlier -->" }]
    ```
    For additional plugins add a `,` between objects e.g.
    ```json
      // Old
      "plugins": [{ "pluginLocation": "plg1" }]
      // New
      "plugins": [{ "pluginLocation": "plg1" }, { "pluginLocation": "plg2" }]
    ```
*   Save & Restart your server

### Write your own plugin

If you wish to write your own plugin please follow this guide [here](https://github.com/bendavies99/BabbleBot-Server/wiki/Creating-A-Plugin)

## Commands

Babblebot has a `core` plugin which by is installed by default it cannot be **disabled** it has 3 commands that every server will have:

| Command (aliases) | Description | Parameters | Since |
| ------- | ----------- | ---------- | ----- |
| help | Display all the commands registered to the server | -(cmd?)=\* (Command to have help with) | 1.0.0 |
| ignore | Ignore a text channel on your discord server | N/A | 1.0.0 |
| listen | Listen to a text channel on your discord server | N/A | 1.0.0 |
| register-announcement-channel, register-ac | Register text channel as your announcements channel | N/A | 2.0.0 |
| remove-announcement-channel, remove-ac | Remove text channel as your announcements channel | N/A | 2.0.0 |
| restart | Restart your server from discord | -(password\*)=\* (Password to stop unwanted restarts) | 2.1.0 |

## Contributing

Firstly, Thank you for taking the time to contribute to Babblebot if you wish to contribute please read the contributing guide [here](https://github.com/bendavies99/babblebot-server/blob/master/CONTRIBUTING.md)

## Authors

*   **Ben Davies** - *Lead Developer* - [Github](https://github.com/bendavies99)

Currently, there is only me working on Babblebot, but it's always open for new ideas and contributions!

See also the list of [contributors](https://github.com/bendavies99/babblebot-server/contributors) who participated in this project.

## License

This project is licensed under the MIT Licence - see the [LICENSE.md](LICENSE.md) file for details
