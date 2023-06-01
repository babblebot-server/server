# API

This is used for [Babblebot-Server](https://github.com/bendavies99/BabbleBot-Server) where you can write your plugins

## Getting Started

These instructions will get you a copy of the project up and running on your 
local machine for development and testing purposes. See deployment 
for notes on how to deploy the project on a live system.

### Installing

A step by step series of examples that tell you how to get a development env running

#### Gradle
```groovy
repositories {
    jcenter()
}

dependancies {
   implementation 'net.bdavies:babblebot-server-api:1.0.0'
}
```

#### Maven
```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>


<dependencies>
  <dependency>
    <groupId>net.bdavies</groupId>
    <artifactId>babblebot-server-api</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## Documentation
Please  refer to the JavaDoc for creating a plugin or look at the Core Plugin for examples.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/bendavies99/babblebot-api/tags).

## Authors

- **Ben Davies** - _Lead Developer_ - [Github](https://github.com/bendavies99)

See also the list of [contributors](https://github.com/bendavies99/babblebot-api/contributors) who participated in this project.

## License

This project is licensed under the MIT Licence - see the [LICENSE.md](LICENSE.md) file for details
