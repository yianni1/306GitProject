java -jar s.jar threeParents.dot 2 
echo Normal test
java -jar s.jar threeParents.dot 3 -o OUTPUT
echo Normal test with -o option
java -jar s.jar threeParent 3
echo Test non-existent input file
java -jar s.jar threeParents.dot 2 -p 4
echo Test -p option
java -jar s.jar threeParents.dot phuke
echo Test where the processor number is not a number
java -jar s.jar threeParents.dot 2 -o OUTPUTS -p 4
echo tests nested -o -p options
java -jar s.jar threeParents.dot 2 -p shight
echo test -p option where core number is not a number
java -jar s.jar threeParents.dot 4 -o OUTPUT sadfjasdf
echo test -o with two options
java -jar s.jar threeParents.dot 4 -v -o OUTPUTA
echo test -v and -o nested
java -jar s.jar threeParents.dot 4 -o OUTPUTTTT -o randomWord
echo test two -o options with different arguments
java -jar s.jar
echo test no args
java -jar s.jar threeParents.dot 2 -o OUTPUTTTTING -v -p 2
echo test -o and -v and -p nest
java -jar s.jar threeParents.dot 2 random -o random
echo test arg before option
java -jar s.jar threeParents.dot 2 -o -p 1
echo test -o with no args followed by a -p
java -jar s.jar threeParents.dot 7 -p 1 -p 3
echo test two -p calls
