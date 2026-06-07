# Rocket League Stats Gatherer


## Configuration
This application is configured entirely via environment variables. You can set these variables either locally using a .env file or within a docker-compose.yml file.

| Environment | Description                                                                                                                          | Default Value       | Required |
| :--- |:-------------------------------------------------------------------------------------------------------------------------------------|:--------------------|:---------|
| `MQTT_HOST` | The hostname or IP address of the MQTT broker.                                                                                       |                     | Yes      |
| `MQTT_SSL` | Enables or disables SSL/TLS encryption for the MQTT connection.                                                                      | `false`             | No       |
| `MQTT_PORT` | The port number to connect to the MQTT broker.                                                                                       | `1883`              | No       |
| `MQTT_USERNAME` | The username for MQTT broker authentication.                                                                                         | *None*              | No       |
| `MQTT_PASSWORD` | The password for MQTT broker authentication.                                                                                         | *None*              | No       |
| `MQTT_CLIENT_ID` | The unique identifier for this MQTT client session. Every client connecting to the same MQTT broker should have a unique identifier. | `rl_stats_gatherer` | No       |
| `DB_HOST` | The hostname or IP address of the database server.                                                                                   |                     | Yes      |
| `DB_USERNAME` | The database user account name.                                                                                                      |                     | Yes      |
| `DB_PASSWORD` | The password for the specified database user.                                                                                        |                     | Yes      |
| `DB_PORT` | The port number to connect to the database server.                                                                                   | `5432`              | No       |
| `DB_NAME` | The name of the specific database to connect to.                                                                                     | `rlstats`           | No       |


### Using a .env File (Local Development)
The easiest way to configure the application locally is to use the provided template to create your local environment file.

- Download the ```example.env``` file.
- Rename it to ```.env```.
- Open the new ```.env``` file and adjust the values to match your setup.

### Using a Docker Compose File
If you prefer to use Docker Compose, you can inject these variables directly into your ```Docker-compose.yml``` file. A ```Docker-compose.yml``` file is provided to help you get started.

- Download the ```Docker-compose.yml``` file.
- Open the  file and adjust the values to match your setup.


## MacOS
### Why are you seeing a "developer cannot be verified" warning?
When you download this executable from GitHub, macOS automatically places it in "Quarantine" because it is not signed by a registered Apple Developer account.

Apple charges an annual fee for this developer status. Because this is a free, open-source project, the binary hasn't been officially signed. Rest assured, the code is fully open-source and safe, but macOS errs on the side of extreme caution.

### How to Run the App (Two Methods)
Give your users options depending on their comfort level with the Terminal.

#### Method 1: The Right-Click Trick (Easiest)
This is the built-in macOS way to grant a one-time exception.

- Open Finder and navigate to the folder where you downloaded the file.
- Right-click (or hold Control and click) the executable file and select Open.
- A dialog box will pop up, but this time it will have an Open button instead of just "Move to Trash".
- Click Open. macOS will remember this choice, and you won’t have to do it again.

_Note_: Double-clicking the file normally will not show the "Open" button. You must right-click and select Open.

#### Method 2: The Terminal Command (For Power Users)
If your users are comfortable with the command line, they can manually strip the quarantine tag. Tell them to open Terminal and run:

```bash
xattr -d com.apple.quarantine RL-Stats-Gatherer.v0.0.32.MacOS
```
_Tip_: They can type xattr -d com.apple.quarantine  and then drag-and-drop the file from Finder into the Terminal window to automatically fill in the path).


# Developer Guide
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/rl-stats-gatherer-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.


## Trigger new releaase

1. Create a tag locally adhering to SemVer
```
git tag -a "v0.0.0" -m "New Release"
```
2. Push the tag to github to launch the Action
```
git push origin v1.0.0
```


## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin
- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL
  database using the reactive pattern
