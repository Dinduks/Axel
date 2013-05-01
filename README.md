# Axel

A Clojure program designed to download files. Quickly.

I created this program in order to learn Clojure and deal with its different aspects:
being a LISP, syntax, immutability, I/O, Java interoperability, etc.

## Usage
Create a jar of the program using `lein uberjar`, then run Axel using:

    java -jar target/axel-0.1.0-SNAPSHOT-standalone.jar FILE_URL DESTINATION_FILE

You would want to alias the command above for more convenience.

    alias axel="java -jar target/axel-0.1.0-SNAPSHOT-standalone.jar"

## How does it work?
Axel downloads many parts of the file in parallel, in order to utilize the maximum
bandwith available.

## How can Axel be improved?
By:

* Downloading the file parts via streams and write them to the disk *on the fly*
* Displaying a progression bar
* Allowing to resume interrupted downloads
* Supporting HTTP authentication
* Automatically detecting the filename
* Improving ergonomy

## License
Copyright © 2013 Samy Dindane

Distributed under the GPL.
