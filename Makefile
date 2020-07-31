JAVAC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)
JFLAGS = -g

all: sattothreesat coltosat threesattocol co perm

sattothreesat: $(classes)

coltosat: $(classes)

threesattocol: $(classes)

co:
	cp ~/Documents/CS3052/Practical2/sattothreesat ~/Documents/CS3052/Practical2/src/
	cp ~/Documents/CS3052/Practical2/coltosat ~/Documents/CS3052/Practical2/src/
	cp ~/Documents/CS3052/Practical2/threesattocol ~/Documents/CS3052/Practical2/src/

clean:
	rm -f *.class
	rm manifest.txt

perm:
	chmod a+x sattothreesat
	chmod a+x coltosat
	chmod a+x threesattocol

default: all

%.class: %.java
	$(JAVAC) $<