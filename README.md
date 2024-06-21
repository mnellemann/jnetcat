# jnetcat

Lightweight Java version of the netcat tool.

## Requirements

You need Java (JRE) version 8 or later to run jnetcat.

## Usage Instructions

- Install the jnetcat package (*.deb*, *.rpm* or *.jar*) from [releases](https://github.com/mnellemann/jnetcat/releases) or compile from source.
- Run **/opt/jnetcat/bin/jnetcat**, if installed from package, or as **java -jar /path/to/jnetcat.jar**

```shell
Usage: jnetcat [-hV] [-b=NUM] [-p=NUM] (-c=SRV | -s)
For more information visit https://git.data.coop/nellemann/jnetcat
  -b, --buffer=NUM    Buffer size in kB [default: 32].
  -c, --connect=SRV   Connect to remote server (client).
  -h, --help          Show this help message and exit.
  -p, --port=NUM      Network port [default: 4445].
  -s, --server        Run server and wait for client (server).
  -V, --version       Print version information and exit.
```


## Examples

On *host A* run jnetcat as a server (receiver) waiting for a connection from a client and writing data to stdout:

```shell
java -jar jnetcat-x.y.z-all.jar -s > output.file
```

On *host B* run jnetcat as a client (sender) connecting to the server and reading data from stdint:

```shell
java -jar jnetcat-x.y.z-all.jar -c server-ip < input.file
```

-----

<details closed>
  <summary><B>Development and Local Testing</B></summary>

## Development Information

You need Java (JDK) version 8 or later to build jnetcat.


### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
