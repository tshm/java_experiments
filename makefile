# makefile
all:
	cd Fullerene && $(MAKE)
	cd JavaGraph && $(MAKE)
	cd Jball && $(MAKE)
	cd Jlauncher && $(MAKE)
	cd Pickie && $(MAKE)
	cd down && $(MAKE)
	cd hello && $(MAKE)
	cd jchat && $(MAKE)
	cd ppserve && $(MAKE)

clean:
	cd Fullerene && $(MAKE) clean
	cd JavaGraph && $(MAKE) clean
	cd Jball && $(MAKE) clean
	cd Jlauncher && $(MAKE) clean
	cd Pickie && $(MAKE) clean
	cd down && $(MAKE) clean
	cd hello && $(MAKE) clean
	cd jchat && $(MAKE) clean
	cd ppserve && $(MAKE) clean
	cd test && $(MAKE) clean
	cd test && $(MAKE) clean
