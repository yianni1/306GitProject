# 306 Scheduling Project

## Contributors
Name: Yianni Bares,   Username: ybar417,  ID: 533468329,  GitHub name: yianni1

Name: Raymond Young,  Username: ryou681,  ID: 564122195,  GitHub name: rayyoung122

Name: Kevin Manson,   Username: kman756,  ID: 978326868,  GitHub name: kevinalexandermanson

Name: Dweep Kapadia,  Username: dkap813,  ID: 559875934,  GitHub name: Cy3b3rZ3r0 & DweeDog

Name: Oliver Li,      Username: mli730,   ID: 939043857,  GitHub name: nolatos





## Installation and Running

Download the scheduler.jar, then place it in a desired directory. This program is designed to be run from the command line from a Linux machine, though it is platform-independent.

In order to run the program, place the input .dot file into the same directory as the jar. Then navigate into the directory from the terminal, and type in the following command:

```
java -jar [JAR-NAME].jar [INPUT-NAME].dot [NUMBER]
```
where [JAR-NAME] is the name of the jar, [INPUT-NAME] is the name of the input file, and [NUMBER] is the number of processors. The output file should then be generated, which should be named [INPUT-NAME]-output.dot. Please make sure the format of your input is correct.

## Options

Some options are available for use in the program. Right now, the only option that is done right now is the -o option, which allows the user to specify the name of the output file. This should be used thus:

```
java -jar [JAR-NAME].jar [INPUT-NAME].dot [NUMBER] -o [DESIRED-OUTPUT-NAME]
```
where [DESIRED-OUTPUT-NAME] is, as the name suggests, the output name which you want. The output file generated should thus have the name [DESIRED-OUTPUT-NAME].dot.

An example use would be 

```
java -jar program.jar sample-input.dot 3 -o OUTPUT
```
which will create a file called OUTPUT.dot

## External Libraries

Apache Commons CLI for command line parsing (The Apache Software License - Version 2.0)

GraphStream for .dot file processing and visualisation (LGPL V3)

JavaFX & jFoenix for GUI (The Apache Software License - Version 2.0)


