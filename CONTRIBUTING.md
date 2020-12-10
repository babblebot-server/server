# Contributing to Babblebot Server

:tada: Firstly, thank you for taking the time to contribute to Babblebot-Server :tada:

This document is a set of guidelines to follow while working with babblebot and what our best practices are

#### Table of contents

[What should I do to get started](#what-should-i-do-to-get-started)
  * [Starting Babblebot-Server](#starting-babblebot-server)
  * [Config](#config)

[Getting started with contributing](#getting-started-with-contributing)
  * [Issues](#issues)
  * [Pull Requests](#pull-requests)
  * [Change Guidelines](#change-guidelines)
  * [Your first contribution](#your-first-contribution)
  
[Style guidelines](#style-guidelines)
  * [Commits](#commits)
  * [Java](#java)
  * [Java documentation](#java-documentation)

## What should I do to get started
Firstly, you will need to fork the project and then run git clone

Any IDE is supported provided they support the following features:
 - Gradle
 - Lombok
 - Java SE 11
 - EditorConfig
 

### Starting Babblebot-Server
To start babblebot-server ensure gradle is installed and all dependencies are installed

Then start the task `:app:run` either from the ide or `./gradlew :app:run` in the terminal

```text
NOTE: When you first run babblebot-server for the first you will get an error to fill out the config then if you get this then you can move on to the Config section
```

### Config
You should find your new config file located at: `app/config/config.json`

It will look like this:
```json
{
  "discord": {
    "token": "Your discord token",
    "commandPrefix": "!",
    "shutdownPassword": "password"
  },
  "system": {
    "autoUpdate": true
  },
  "http": {
    "port": 25565,
    "maxWorkerThreads": 3
  },
  "database": {
    "type": "sqlite",
    "database": "Core.db"
  },
  "plugins": []
}
```

Everything is set up here for you just need to provide a discord token from [Discord Developers](https://discord.com/developers) at path `discord.token`

## Getting started with contributing

### Issues

### Pull Requests

### Change Guidelines

### Your first contribution

## Style guidelines

### Commits

### Java

### Java documentation
Java documentation is important so other developers can understand what you have written and extend if needed be or use your class

Rules:
- All packages except server, core, and app will fail Pull requests if the documentation guidelines are not met, especially any code that is exposed to plugin developers
- Respect documentation helps all contributors
- Please be as descriptive as possible

#### Class Documentation
When documenting a class please provide a descriptive overview of the class, a use case of the class, and some code examples

Please follow this style on every class you create:
```java
/**
* Class to describe how to write java docs
* Used to show the contributors about the style of class java docs
* JavaDoc example = new JavaDoc();
* example.generateTemplate();
*
* @author Name - email@email.com (Optional!)
* @since __RELEASE_VERSION__
*/
public class JavaDoc {}
```

The rules shown here are:-
 - First Line: Class Description
 - Second Line: Class use case
 - Third Line & Fourth Line: Class Example usage
 - @author: This is up to you, just as long you use something that can relate to your github profile
 - @since: Please keep this at `__RELEASE_VERSION__` Semantic Release will handle this
 - Always leave a space between text and `@author` and `@since`



#### Method Documentation
When documenting a method please provide a descriptive overview of the method, author if you did not create the class, params, return, and a since tag

Please follow this style on every method you create:
```java
public class JavaDoc {
   /**
   * Generate a JavaDoc template from a template string
   * 
   * @param template non-transformed template in string format
   * @return {@link Template} a transformed template object 
   *
   * @author Name - email@email.com (Optional!)
   * @since __RELEASE_VERSION__
   */
   public Template generateTemplate(String template) {
      return TemplateBuilder.fromString(template);
   }   
}
```


The rules shown here are:-
 - First Line: Method Description
 - Third Line & Fourth Line: Params and return
 - @author: This is up to you, just as long you use something that can relate to your github profile
 - @since: Please keep this at `__RELEASE_VERSION__` Semantic Release will handle this
 - Always leave a space between text and `@author` and `@since`
 - @param: This must be `@param paramName description`
 - @return: This must be `@return {@link ReturningObject} description`
 - @author: is not needed if the authored user is the same
 - @since: is not needed if the method is created the same time as the class
 
 ```text
NOTE: if you fail a PR Review due to documentation style please commit style(docs): Updated JavaDocs to match specification
```
```text
NOTE: if you notice any classes or methods in babblebot-server that don't match specification and want to help, please fix them and open a pull request! 
Remember to use the above commit message 
```
