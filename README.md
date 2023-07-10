<p align="center"><img src="https://socialify.git.ci/babblebot-server/server/image?description=1&font=Source%20Code%20Pro&language=1&owner=0&pattern=Floating%20Cogs&theme=Dark" alt="BabbleBot-Server" width="640" height="320" /></p>  

<p align="center">  
 <a href="https://github.com/babblebot-server/server/actions"><img src="https://action-badges.now.sh/bendavies99/Babblebot-Server?action=build" alt="Build Status"></a>  
 <a href="https://app.codacy.com/gh/babblebot-server/server/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade"><img src="https://app.codacy.com/project/badge/Grade/4a3e186e56b44261b6632944801b6987"/></a>
 <a href="https://github.com/babblebot-server/server/releases"><img src="https://img.shields.io/github/v/release/bendavies99/Babblebot-Server" alt="Version"></a>  
 <a href="https://github.com/babblebot-server/server/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/bendavies99/babblebot-server" alt="License"></a>  
 <a href="https://discord.gg/uUdX9pw"><img src="https://img.shields.io/discord/409004433750360090?color=7289da&label=Babblebot&logo=discord" alt="Chat"></a>  
 <br>  
 <a href="https://jitpack.io/#net.babblebot/server"><img src="https://jitpack.io/v/net.babblebot/server.svg" alt="Jitpack badge"></a>  
 <a href="https://javadoc.io/doc/net.babblebot/babblebot-server-api"><img src="https://javadoc.io/badge2/net.babblebot/babblebot-server-api/javadoc.svg?label=API%20Javadoc" alt="API Javadoc"></a>  
</p>  

## Table of Contents

[Getting Started](#getting-started)

* [Installation](#installation)
* [Configuration](#configuration)
* [Updating](#updating)

[Plugins](#plugins)

* [Officially Supported Plugins](#officially-supported-plugins)
* [Community Plugins](#community-plugins)
* [Installing Plugins](#installing-plugins)
* [Write your own plugin](#write-your-own-plugin)

[Commands](#commands)

[Contributing](#contributing)

[Authors](#authors)

[License](#license)

## Getting Started

A fully modular bot for Discord built on top of Discord4J which can be
found [here](https://github.com/Discord4J/Discord4J)

Features include:

* Fully configurable, Lightweight server
* Plugin system (configuration, persistence, middleware, web-views) (🚧)
* Variable parser (using ${var} in response messages)
* Persistent data for plugins (🚧)
* REST API
* Announcement System (Notify users through discord)
* Fully featured help system

```text
NOTE: The emoji "🚧" means that the current features are under heavy development and are experimental
```

## Installation

### Prerequisites:

* Java 17
* Discord bot token (**Unsure?** Follow
  this [guide](https://github.com/reactiflux/discord-irc/wiki/Creating-a-discord-bot-&-getting-a-token))
* Ensure your bot is on your server
* DB Server

### Docker Installation

#### Compose

```yaml
version: '2.8'

volumes:
    db-data:
    plugins:

networks:
    bb:

services:
    db:
        container_name: db
        image: postgres:latest
        restart: unless-stopped
        environment:
            POSTGRES_PASSWORD: 'password-goes-here'
            POSTGRES_USER: 'dbadmin'
            POSTGRES_DB: 'babblebot'
        ports:
            - "5432:5432"
        volumes:
            - db-data:/var/lib/postgresql/data
        networks:
            - bb
    babblebot-server:
        container_name: babblebot-server
        image: babblebot/server:latest
        restart: unless-stopped
        environment:
            spring.datasource.driver-class-name: 'org.postgresql.Driver'
            spring.datasource.url: 'jdbc:postgresql://db/babblebot'
            spring.datasource.username: 'dbadmin'
            spring.datasource.password: 'password-goes-here'
            spring.jpa.hibernate.ddl-auto: 'update'
            DISCORD_TOKEN: 'token-goes-here'
        volumes:
            - plugins:/workspace/plugins
        ports:
            - "21132:8080"
        networks:
            - bb
```

## Configuration

Please look at this configuration guide [here](https://github.com/babblebot-server/server/wiki/Configuration)

## Updating

### Manual Updating

Update the docker tag

### Automatic Updates

You can use watchtower if you are using docker [Watchtower](https://github.com/containrrr/watchtower)

## Plugins

Plugins are what give power to Babblebot. Without them Babblebot will be a strong server for just 3 commands

Plugins Offer:

* More Commands
* Middleware for the command dispatcher
* More variables (🚧)

### Officially Supported Plugins

| Name    | Description                                       | Link                                                          | Server Versions supported | Namespace                    |
|---------|---------------------------------------------------|---------------------------------------------------------------|---------------------------|------------------------------|
| audiodj | Play audio in a voice server from various sources | [Link](https://github.com/bendavies99/babblebot-audiodj-java) | 2.x.x, 3.x.x              | Configurable (Default: None) |

### Community Plugins

| Name | Description | Link | Namespace |
|------|-------------|------|-----------|

If you wish to have your plugin displayed here please create a
discussion [here](https://github.com/babblebot-server/server/discussions)

### Installing Plugins

Steps to install a plugin:

* Locate and download the latest version of a plugin it should be a `.jar` file
* If not already created, create a `plugins` folder inside the `bin` folder
* Create a new folder for your plugin it needs to be the same name as the `.jar` file
* Place your jar file inside that folder
* Open up your `config/config.json` file and add inside
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
* Save & Restart your server

### Write your own plugin

If you wish to write your own plugin please follow this
guide [here](https://github.com/babblebot-server/server/wiki/Creating-A-Plugin)

## Commands

Babblebot has a `core` plugin which by is installed by default it cannot be **disabled** it has 3 commands that every
server will have:

| Command (aliases)                          | Description                                         | Parameters                                            | Since |
|--------------------------------------------|-----------------------------------------------------|-------------------------------------------------------|-------|
| help                                       | Display all the commands registered to the server   | -(cmd?)=\* (Command to have help with)                | 1.0.0 |
| ignore                                     | Ignore a text channel on your discord server        | N/A                                                   | 1.0.0 |
| listen                                     | Listen to a text channel on your discord server     | N/A                                                   | 1.0.0 |
| register-announcement-channel, register-ac | Register text channel as your announcements channel | N/A                                                   | 2.0.0 |
| remove-announcement-channel, remove-ac     | Remove text channel as your announcements channel   | N/A                                                   | 2.0.0 |
| restart                                    | Restart your server from discord                    | -(password\*)=\* (Password to stop unwanted restarts) | 2.1.0 |

## Contributing

Firstly, Thank you for taking the time to contribute to Babblebot if you wish to contribute please read the contributing
guide [here](https://github.com/babblebot-server/server/blob/master/CONTRIBUTING.md)

## Authors

* **Ben Davies** - *Lead Developer* - [Github](https://github.com/bendavies99)

Currently, there is only me working on Babblebot, but it's always open for new ideas and contributions!

See also the list of [contributors](https://github.com/babblebot-server/server/contributors) who participated in
this project.

## License

This project is licensed under the MIT Licence - see the [LICENSE.md](LICENSE.md) file for details
