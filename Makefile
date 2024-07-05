JC     = javac
J      = java
JFLAGS = -g
OPT    = -O3
SRCS   = src/it/uninsubria/bookrecommender/*.java
BIN    = bin
OUT    = -d 
CP     = -cp
ENTRY  = it.uninsubria.bookrecommender.BookRecommender

compile:
	$(JC) $(OUT) $(BIN) $(SRCS)

run:
	$(J) $(CP) $(BIN) $(ENTRY)

jar:
	jar cfm BookRecommender.jar manifest.txt -C bin .

docs:
	javadoc -private -d doc $(SRCS)
