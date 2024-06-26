# jnetcat

Lightweight Java clone of the netcat tool.

## Requirements

You need Java (JRE) version 8 or later to run jnetcat.

## Usage Instructions

- Install the jnetcat package (*.deb*, *.rpm* or *.jar*) from [releases](https://github.com/mnellemann/jnetcat/releases) or compile from source.
- Run **/opt/jnetcat/bin/jnetcat**, if installed from package, or as **java -jar /path/to/jnetcat.jar**

```shell
Usage: jnetcat [-hV] [-b=NUM] [-p=NUM] (-c=SRV | -s)
For more information visit https://github.com/mnellemann/jnetcat
  -b, --buffer=NUM    Buffer size in kB [default: 128].
  -c, --connect=SRV   Connect to remote server (client).
  -h, --help          Show this help message and exit.
  -p, --port=NUM      Network port [default: 4445].
  -s, --server        Run server and wait for client (server).
  -V, --version       Print version information and exit.
```


### Example

On *host A* run jnetcat as a server (receiver) waiting for a connection from a client and writing data to stdout:

```shell
java -jar jnetcat-x.y.z-all.jar -s > output.file
```

On *host B* run jnetcat as a client (sender) connecting to the server and reading data from stdin:

```shell
java -jar jnetcat-x.y.z-all.jar -c server-ip < input.file
```


-----
<details closed>
<summary><b>Example from 1Gb network</b></summary>

On the sender host:

```shell
$ dd if=/dev/urandom of=/tmp/test-5g.file bs=1000000 count=5000

$ md5sum /tmp/test-5g.file
1a967529d848d97e425099743ab52000  /tmp/test-5g.file

$ /opt/jnetcat/bin/jnetcat -c reciving-host < /tmp/test-5g.file
2024-06-26 10:21:28 -  Status:      119013376 B/s       119013 KB/s      119 MB/s
2024-06-26 10:21:29 -  Status:      118095872 B/s       118095 KB/s      118 MB/s
2024-06-26 10:21:30 -  Status:      118226944 B/s       118226 KB/s      118 MB/s
2024-06-26 10:21:31 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:21:32 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:33 -  Status:      118358016 B/s       118358 KB/s      118 MB/s
2024-06-26 10:21:34 -  Status:      119013376 B/s       119013 KB/s      119 MB/s
2024-06-26 10:21:35 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:21:36 -  Status:      118226944 B/s       118226 KB/s      118 MB/s
2024-06-26 10:21:37 -  Status:      118358016 B/s       118358 KB/s      118 MB/s
2024-06-26 10:21:38 -  Status:      118489088 B/s       118489 KB/s      118 MB/s
2024-06-26 10:21:39 -  Status:      118489088 B/s       118489 KB/s      118 MB/s
2024-06-26 10:21:40 -  Status:      118489088 B/s       118489 KB/s      118 MB/s
2024-06-26 10:21:41 -  Status:      118489088 B/s       118489 KB/s      118 MB/s
2024-06-26 10:21:42 -  Status:      118095872 B/s       118095 KB/s      118 MB/s
2024-06-26 10:21:43 -  Status:      118620160 B/s       118620 KB/s      118 MB/s
2024-06-26 10:21:44 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:45 -  Status:      119144448 B/s       119144 KB/s      119 MB/s
2024-06-26 10:21:48 -  Status:       25821184 B/s        25821 KB/s       25 MB/s
2024-06-26 10:21:52 -  Status:        7995392 B/s         7995 KB/s        7 MB/s
2024-06-26 10:21:53 -  Status:      108527616 B/s       108527 KB/s      108 MB/s
2024-06-26 10:21:54 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:55 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:56 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:57 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:21:58 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:21:59 -  Status:      119144448 B/s       119144 KB/s      119 MB/s
2024-06-26 10:22:00 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:22:01 -  Status:      104857600 B/s       104857 KB/s      104 MB/s
2024-06-26 10:22:02 -  Status:      117571584 B/s       117571 KB/s      117 MB/s
2024-06-26 10:22:02 - Average:      110685934 B/s       110685 KB/s      110 MB/s     0.11 GB/s
2024-06-26 10:22:03 -  Status:      119144448 B/s       119144 KB/s      119 MB/s
2024-06-26 10:22:04 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:22:05 -  Status:      117702656 B/s       117702 KB/s      117 MB/s
2024-06-26 10:22:06 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:22:07 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:22:08 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:22:09 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:22:10 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:22:11 -  Status:      119013376 B/s       119013 KB/s      119 MB/s
2024-06-26 10:22:12 -  Status:      119013376 B/s       119013 KB/s      119 MB/s
2024-06-26 10:22:13 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:22:14 -  Status:      117833728 B/s       117833 KB/s      117 MB/s
2024-06-26 10:22:15 -  Status:      117964800 B/s       117964 KB/s      117 MB/s
2024-06-26 10:22:16 -  Status:      118489088 B/s       118489 KB/s      118 MB/s

2024-06-26 10:22:17 - Summary:     5000000000 B        5000000 KB       5000 MB       5.00 GB

```

On the receiving host:

```shell
$ /opt/jnetcat/bin/jnetcat -s > /tmp/jnetcat.out
2024-06-26 10:21:27 -  Status:              0 B/s            0 KB/s        0 MB/s
2024-06-26 10:21:28 -  Status:      117331440 B/s       117331 KB/s      117 MB/s
2024-06-26 10:21:29 -  Status:      117722400 B/s       117722 KB/s      117 MB/s
2024-06-26 10:21:30 -  Status:      117697784 B/s       117697 KB/s      117 MB/s
2024-06-26 10:21:31 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:32 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:21:33 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:34 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:35 -  Status:      117725296 B/s       117725 KB/s      117 MB/s
2024-06-26 10:21:36 -  Status:      117694888 B/s       117694 KB/s      117 MB/s
2024-06-26 10:21:37 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:38 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:21:39 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:40 -  Status:      117720952 B/s       117720 KB/s      117 MB/s
2024-06-26 10:21:41 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:42 -  Status:      117689096 B/s       117689 KB/s      117 MB/s
2024-06-26 10:21:43 -  Status:      117689096 B/s       117689 KB/s      117 MB/s
2024-06-26 10:21:44 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:45 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:21:48 -  Status:       30938088 B/s        30938 KB/s       30 MB/s
2024-06-26 10:21:52 -  Status:        8126464 B/s         8126 KB/s        8 MB/s
2024-06-26 10:21:53 -  Status:      111062640 B/s       111062 KB/s      111 MB/s
2024-06-26 10:21:54 -  Status:      117732536 B/s       117732 KB/s      117 MB/s
2024-06-26 10:21:55 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:21:56 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:57 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:21:58 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:21:59 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:00 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:01 -  Status:      103420504 B/s       103420 KB/s      103 MB/s
2024-06-26 10:22:01 - Average:      110201737 B/s       110201 KB/s      110 MB/s     0.11 GB/s
2024-06-26 10:22:02 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:03 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:04 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:05 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:22:06 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:07 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:08 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:22:09 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:10 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:11 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:22:12 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:22:13 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:14 -  Status:      117678960 B/s       117678 KB/s      117 MB/s
2024-06-26 10:22:15 -  Status:      117741224 B/s       117741 KB/s      117 MB/s
2024-06-26 10:22:16 -  Status:      117678960 B/s       117678 KB/s      117 MB/s

2024-06-26 10:22:17 - Summary:     5000000000 B        5000000 KB       5000 MB       5.00 GB

$ md5sum /tmp/jnetcat.out 
1a967529d848d97e425099743ab52000  /tmp/jnetcat.out
```

</details>

-----

<details closed>
  <summary><B>Development and Local Testing</B></summary>

You need Java (JDK) version 8 or later to build jnetcat.


### Build & Test

Use the gradle build tool, which will download all required dependencies:

```shell
./gradlew clean build
```

</details>