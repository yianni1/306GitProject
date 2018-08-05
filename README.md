# 306Project

## Installation and Running

Download the jar, then place it in a desired directory. This program is designed to be run from the command line from a Linux machine, though it is platform-independent.

In order to run the program, place the input .dot file into the same directory as the jar. Then navigate into the directory from the terminal, and type in the following command:

```
java -jar [JAR-NAME].jar [INPUT-NAME].jar [NUMBER]
```
where [JAR-NAME] is the name of the jar, [INPUT-NAME] is the name of the input file, and [NUMBER] is the number of processors. The output file should then be generated, which should be named [INPUT-NAME]-output.dot. Please make sure the format of your input is correct.

## Options

Some options are available for use in the program. Right now, the only option that is done right now is the -o option, which allows the user to specify the name of the output file. This should be used thus:

```
java -jar [JAR-NAME].jar [INPUT-NAME].jar [NUMBER] -o [DESIRED-OUTPUT-NAME]
```
where [DESIRED-OUTPUT-NAME] is, as the name suggests, the output name which you want. The output file generated should thus have the name [DESIRED-OUTPUT-NAME].dot.

An example use would be 

```
java -jar program.jar sample-input.jar 3 -o OUTPUT
```
which will create a file called OUTPUT.dot
