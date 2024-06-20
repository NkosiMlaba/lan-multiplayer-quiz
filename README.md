# LAN Multiplayer Quiz

This is a simple multiplayer quiz game that can be played over a local area network (LAN). The game is built using Java core and Socket.IO.

## System Requirements:

- A linux operating system
- Java 11 or higher
- Maven (for dependency management)
- Access to a terminal that supports ANSI colors and Unicode characters
- Internet access

### Setup:

1. On a linux machine, connect to the internet and run:
        
        sudo apt update

2. To install maven run:
        
        sudo apt install maven -y

3. To verify the installation, run:
        
        mvn -version

4. Install java using:
        
        sudo apt install default-jdk -y

5. To verify the installation, run:
        
        java -version

### Additional Setup Instructions (If intending to host the Server):
1. Install python:

        sudo apt install python3

2. Verify the Installation:

        python3 --version

3. Install Groq python library:

        pip install groq

4. Insert your Groq API key:
    Replace: `"<KEY>" with your own Groq API key in the llama3client.py file.`
    Location of file: `lan-multiplayer-quiz/src/main/java/za/co/theemlaba/server/llama3client.py`

## Getting Started

1. Clone the repository: `git clone https://github.com/NkosiMlaba/lan-multiplayer-quiz`
2. Install dependencies: ` install`
3. Start the server: `node server.js`
4. Open multiple browser windows or tabs and navigate to `http://localhost:3000` to join the game.

## How to Play

1. One player will be designated as the host and will create a new game room.
2. Other players can join the game room by entering the address provided by the host.
3. Each player can start the game once joined.
4. Questions will be displayed one by one, and players will have a limited time to answer each question.
5. Players' scores will be updated after each question, and the final scores will be displayed at the end of the game.
6. At the end of the game the player can review their scores and answers to the questions
7. The player can then ask for an explanation on the answers they got wrong.

## Features

- Multiplayer support over LAN
- Real-time updates and synchronization
- Customizable questions and answers
- Leaderboard and score tracking

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.

## Contributor

Nkosikhona Mlaba (nkosimlaba397@gmail.com)

## License

This project is licensed under the [MIT License](LICENSE).
