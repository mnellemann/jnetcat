# jnetcat

Lightweight Java version of the netcat tool.

## Requirements

You need Java (JRE) version 8 or later to run jnetcat.

## Usage Instructions

- Install the jnetcat package (*.deb*, *.rpm* or *.jar*) from [releases](https://github.com/mnellemann/jnetcat/releases) or compile from source.
- Run **/opt/jnetcat/bin/jnetcat**, if installed from package, or as **java -jar /path/to/jnetcat.jar**

```shell
Usage: jnetcat [-huV] [-l=NUM] [-n=NUM] [-p=NUM] [-t=SEC] (-c=SRV | -s)
For more information visit https://github.com/mnellemann/jnetcat
  -c, --connect=SRV   Connect to remote server (client).
  -h, --help          Show this help message and exit.
  -l, --pkt-len=NUM   Packet size in bytes (client) [default: 1432].
  -n, --pkt-num=NUM   Number of packets to send (client) [default: 150000].
  -p, --port=NUM      Network port [default: 4445].
  -s, --server        Run server and wait for client (server).
  -t, --runtime=SEC   Time to run, supersedes pkt-num (client) [default: 0].
  -u, --udp           Use UDP network protocol [default: false].
  -V, --version       Print version information and exit.
```


## Examples

On *host A* run jnetcat as a server waiting for a connection from a client:

```shell
java -jar jnetcat-x.y.z-all.jar -s
```

On *host B* run jnetcat as a client connecting to the server and sending data:

```shell
java -jar jnetcat-x.y.z-all.jar -c server-ip
```


## Development Information

You need Java (JDK) version 8 or later to build jnetcat.


### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```
