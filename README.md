# VWO Java SDK Example

[vwo-java-sdk](https://github.com/wingify/vwo-java-sdk) allows you to A/B Test your Website at server-side.

This repository provides a basic demo of how server-side works with VWO Java SDK.

## Requirements

The Java SDK supports:

* Open JDK 8, 11, 12
* Oracle JDK 8, 9, 11, 12

## SDK INSTALLATION

Install dependencies using `mvn install`.

## Documentation

Refer [VWO Official Server-side Documentation](https://developers.vwo.com/reference#fullstack-introduction)

## Scripts

1. Install dependencies

```bash
mvn install
```

2. Update your app with your accountId, sdk-key, campaign-test-key and goal-identifier inside `Config.java`

```java
public class Config {
  public static String accountId = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String sdkKey = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String campaignKey = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String goalIdentifier  = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String featureRolloutCampaignKey = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String featureTestCampaignKey = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String featureTestGoalIdentifier = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static Object featureTestRevenue = "REPLACE_THIS_WITH_CORRECT_VALUE";
  public static String variableKey = "REPLACE_THIS_WITH_CORRECT_VALUE";
}
```

3. Run application

## Basic Usage

**GET SETTING FILE**

Each VWO SDK client corresponds to the settingsFIle representing the current state of the campaign settings, that is, a list of server-side running campaign settings.
Setting File is a pre-requisite for initiating the VWO CLIENT INSTANCE.

```java
String settingsFile = VWO.getSettingsFile(accountId, sdkKey));
```

**INSTANTIATION**


SDK provides a method to instantiate a VWO client as an instance. The method accepts an object to configure the VWO client.
The mandatory parameter for instantiating the SDK is settingsFile.

```java
import com.vwo.VWO;

VWO vwoInstance = VWO.launch(settingsFile).build();
```

The VWO client class needs to be instantiated as an instance that exposes various API methods like activate, getVariation and track.

**USER STORAGE SERVICE**

```java
String settingsFile = VWO.getSettingsFile(accountId, sdkKey);

Storage.User userStorage = return new Storage.User() {
     @Override
     public Map<String, String> get(String userId, String campaignName) {
        for (Map<String, String> savedCampaign: campaignStorageArray) {
            if (savedCampaign.get("userId").equals(userId) && savedCampaign.get("campaignKey").equals(campaignName)) {
               return savedCampaign;
            }
        }
        return null;
     }

     @Override
     public void set(Map<String, String> map){
        campaignStorageArray.add(map);
     }
};

VWO vwo = VWO.launch(settingsFile).withUserStorage(userStorage).build();
```

**LOGGER**

JAVA SDK utilizes a logging facade, SL4J (https://www.slf4j.org/) as the logging api layer. If no binding is found on the class path,
then you will see the following logs

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
You can get the SDK to log by providing a concrete implementation for SLF4J.
```

What it means is that at runtime, the logging `implementation` (or the logger binding) is missing , so slf4j simply use a "NOP" implementation, which does nothing.
If you need to output JAVA SDK logs, there are different approaches for the same.

SIMPLE IMPLEMENTATION
If there are no implementation in your project , you may provide a simple implementation that does not require any configuration at all.
Add following code to you pom.xml,

```java
 <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.6.4</version>
</dependency>
```

Now you see logging output on STDOUT with INFO level. This simple logger will default show any INFO level message or higher.
In order to see DEBUG messages, you would need to pass -Dorg.slf4j.simpleLogger.defaultLogLevel=debug or simplelogger.properties file on the classpath
See http://www.slf4j.org/api/org/slf4j/impl/SimpleLogger.html for details

CONCRETE IMPLEMENTATION

sl4j supports various logging framework. Refer here ->https://www.slf4j.org/manual.html

We have provided our example with Logback
If you have logback in your class path, to get console logs add following Appender and Logger in logback for formatted logs.

```java
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <Pattern>
            %cyan(VWO-SDK) [%date] %highlight([%level]) %cyan([%logger{10} %file:%line]) %msg%n
        </Pattern>
    </encoder>

    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>DEBUG</level>
    </filter>
</appender>

<Logger name="com.vwo.sdk" additivity="false">
    <appender-ref ref="STDOUT"/>
</Logger>
```

```java
new VWOLogger(VWO.Enums.LOGGER_LEVEL.DEBUG.value()) {
    @Override
    public void trace(String message, Object... params) {
        LOGGER.trace(message, params);
    }

    @Override
    public void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }

    @Override
    public void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    @Override
    public void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    @Override
    public void error(String message, Object... params) {
        LOGGER.error(message, params);
    }
};
```

For more appenders, refer [this](https://logback.qos.ch/manual/appenders.html).

## Authors

* Core Contributor & Maintainer - [pntgupta](https://github.com/pntgupta)
* Main Contributor - [sakshimahendruvk](https://github.com/sakshimahendruvk)
* Repo health maintainer - [softvar](https://github.com/softvar)

## Contributing

Please go through our [contributing guidelines](https://github.com/wingify/vwo-java-sdk-example/blob/master/CONTRIBUTING.md)

## Code of Conduct

[Code of Conduct](https://github.com/wingify/vwo-java-sdk-example/blob/master/CODE_OF_CONDUCT.md)

## License

[Apache License, Version 2.0](https://github.com/wingify/vwo-java-sdk-example/blob/master/LICENSE)

Copyright 2019-2021 Wingify Software Pvt. Ltd.
