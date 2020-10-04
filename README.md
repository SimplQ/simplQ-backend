# SimplQ Backend

[![Build Status](https://travis-ci.org/SimplQ/simplQ-backend.svg?branch=master)](https://travis-ci.org/SimplQ/simplQ-backend)

[SimplQ](https://simplq.me) is a completely web based queue management solution that anyone can use to create instant queues. 

### Development Envirmonment Setup Instructions

The project is written in Java, and deployed on AWS. These steps are to be followed when you are running the project for the first time.

1. Install Java 11 and maven. 
2. Clone this project
3. Build the jar:

```
mvn package
```
4. Run the jar:
```
java -jar simplq/target/simplq-0.0.1-SNAPSHOT.jar 
```
We follow Google's [Java Style Guidelines](https://github.com/google/styleguide). For Intellij, you can install the [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) plugin, Eclipse and other IDE users can find more instructions [here](https://github.com/google/google-java-format).

# Contributing

Feel free to fork and improve, and do send a pull request. We will be delighed to work with you. 

There are a ton of features being planned. So if you are considering contributing to this repository, please first discuss the change you wish to make via the issue tracker. Let's work together.

A test postman collection is available [here](https://www.getpostman.com/collections/252a096a86fc550fb5fb).
