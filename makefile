SERVER_JAR_FILE = target/mysockets-1.0-SNAPSHOT-server-jar-with-dependencies.jar
CLIENT_JAR_FILE = target/mysockets-1.0-SNAPSHOT-client-jar-with-dependencies.jar
WEBAPI_JAR_FILE = target/mysockets-1.0-SNAPSHOT-webapi-jar-with-dependencies.jar

# Default target
.PHONY: all
all: clean build

# Clean the project
.PHONY: clean
clean:
	mvn clean

# Build the project and package it into a JAR file
.PHONY: build
build:
	mvn package

# Run tests
.PHONY: tests
tests:
	mvn test


# Run the client
.PHONY: client
client: build
	java -jar $(CLIENT_JAR_FILE)

# Run the server
.PHONY: server
server: build
	java -jar $(SERVER_JAR_FILE)

# Target to stop the server using the PID
.PHONY: stop-server
stop-server:
	
	@if netstat -tuln | grep ':3000' > /dev/null; then \
		PID=$$(netstat -tulnp 2>/dev/null | grep ':3000' | awk '{print $$7}' | cut -d'/' -f1); \
		if [ -n "$$PID" ]; then \
			kill $$PID && rm -f $(PID_FILE); \
			echo "Stopped server process $$PID"; \
		fi \
	else \
		echo "No server process found on port 3000"; \
	fi
	
	@if netstat -tuln | grep ':5000' > /dev/null; then \
		PID=$$(netstat -tulnp 2>/dev/null | grep ':5000' | awk '{print $$7}' | cut -d'/' -f1); \
		if [ -n "$$PID" ]; then \
			kill $$PID && rm -f $(PID_FILE); \
			echo "Stopped server process $$PID"; \
		fi \
	else \
		echo "No server process found on port 5000"; \
	fi
# Target to stop the server using the PID
.PHONY: stop
stop: stop-server

# Target to bild and run webapi
.PHONY: webapi
webapi: build
	java -jar $(WEBAPI_JAR_FILE)

# Target to run webapi directly without building
.PHONY: run-webapi
run-webapi: 
	java -jar $(WEBAPI_JAR_FILE)

# Target to run server directly without building
.PHONY: run-server
run-server: 
	java -jar $(SERVER_JAR_FILE)

# Target to run client directly without building
.PHONY: run-client
run-client: 
	java -jar $(CLIENT_JAR_FILE)

# Target to automate pushing to github
.PHONY: push
push:
	@read -p "Enter commit message: " msg; \
	git add .; \
	git commit -m "$$msg"; \
	git push