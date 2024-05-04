package za.co.theemlaba;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.io.*;
import java.io.*;
import java.net.*;
import java.util.*;

// public class MathTriviaServer {
//     private static final int PORT = 12345;
//     private static final String QUESTIONS_FILE = "questions.csv";
//     private static final int NUM_QUESTIONS = 5; // Number of questions to ask

//     private List<Question> questions;
//     private List<ClientHandler> clients;
//     private CountDownLatch startSignal;

//     public MathTriviaServer() {
//         questions = readQuestionsFromCSV();
//         clients = new ArrayList<>();
//         startSignal = new CountDownLatch(2); // Set to 2 for synchronized start with 2 clients
//     }

//     public void start() {
//         try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//             System.out.println("Server is running...");
            
//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
//                 ClientHandler clientHandler = new ClientHandler(clientSocket);
//                 clients.add(clientHandler);
//                 clientHandler.start();
                
//                 // Send synchronization signal to client
//                 clientHandler.sendSyncSignal();

//                 // Check if at least 2 clients are connected to start the game
//                 if (clients.size() >= 2) {
//                     startSignal.countDown(); // Decrement the countdown latch
//                     if (startSignal.getCount() == 0) {
//                         System.out.println("Starting the game...");
//                         startGame();
//                     }
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void startGame() {
//         // Shuffle questions
//         List<Question> shuffledQuestions = new ArrayList<>(questions);
//         Random random = new Random();
//         for (int i = shuffledQuestions.size() - 1; i > 0; i--) {
//             int j = random.nextInt(i + 1);
//             Question temp = shuffledQuestions.get(i);
//             shuffledQuestions.set(i, shuffledQuestions.get(j));
//             shuffledQuestions.set(j, temp);
//         }

//         // Send questions to clients
//         for (int i = 0; i < NUM_QUESTIONS; i++) {
//             Question question = shuffledQuestions.get(i);
//             for (ClientHandler client : clients) {
//                 client.sendQuestion(question);
//             }

//             // Wait for answers from clients
//             Map<ClientHandler, Integer> answerTimes = new HashMap<>();
//             for (ClientHandler client : clients) {
//                 String answer = client.getAnswer(question);
//                 if (answer != null) {
//                     // Record time taken for answer
//                     int timeTaken = client.getTimeTaken();
//                     answerTimes.put(client, timeTaken);
//                 }
//             }

//             // Calculate scores
//             // For simplicity, let's assume each correct answer reduces time taken to 0
//             Map<ClientHandler, Integer> scores = new HashMap<>();
//             for (Map.Entry<ClientHandler, Integer> entry : answerTimes.entrySet()) {
//                 ClientHandler client = entry.getKey();
//                 int timeTaken = entry.getValue();
//                 int score = question.getAnswer() == Integer.parseInt(client.getLastAnswer()) ? 1 : 0;
//                 scores.put(client, timeTaken - score); // Adjust time taken by score
//             }

//             // Determine the winner
//             ClientHandler winner = null;
//             int minTime = Integer.MAX_VALUE;
//             for (Map.Entry<ClientHandler, Integer> entry : scores.entrySet()) {
//                 if (entry.getValue() < minTime) {
//                     minTime = entry.getValue();
//                     winner = entry.getKey();
//                 }
//             }

//             // Send results to clients
//             for (ClientHandler client : clients) {
//                 client.sendResult(scores, winner);
//             }
//         }
//     }

//     private List<Question> readQuestionsFromCSV() {
//         List<Question> questions = new ArrayList<>();
//         try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
//             String line;
//             while ((line = br.readLine()) != null) {
//                 String[] data = line.split(",");
//                 if (data.length == 2) {
//                     String expression = data[0].trim();
//                     int answer = Integer.parseInt(data[1].trim());
//                     questions.add(new Question(expression, answer));
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         return questions;
//     }

//     public static void main(String[] args) {
//         MathTriviaServer server = new MathTriviaServer();
//         server.start();
//     }
// }

// class Question {
//     private String expression;
//     private int answer;

//     public Question(String expression, int answer) {
//         this.expression = expression;
//         this.answer = answer;
//     }

//     public String getExpression() {
//         return expression;
//     }

//     public int getAnswer() {
//         return answer;
//     }
// }

// class ClientHandler extends Thread {
//     private Socket socket;
//     private BufferedReader in;
//     private PrintWriter out;
//     private String lastAnswer;
//     private int timeTaken;

//     public ClientHandler(Socket socket) {
//         this.socket = socket;
//         try {
//             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out = new PrintWriter(socket.getOutputStream(), true);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public void run() {
//         // Handle client communication
//     }

//     public void sendQuestion(Question question) {
//         out.println(question.getExpression());
//     }

//     public String getAnswer(Question question) {
//         try {
//             socket.setSoTimeout(10000); // 10 seconds timeout for answering each question
//             long startTime = System.currentTimeMillis();
//             String answer = in.readLine();
//             long endTime = System.currentTimeMillis();
//             timeTaken = (int) (endTime - startTime);
//             lastAnswer = answer;
//             return answer;
//         } catch (IOException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     public int getTimeTaken() {
//         return timeTaken;
//     }

//     public String getLastAnswer() {
//         return lastAnswer;
//     }

//     public void sendSyncSignal() {
//         out.println("Start");
//     }

//     public void sendResult(Map<ClientHandler, Integer> scores, ClientHandler winner) {
//         StringBuilder sb = new StringBuilder();
//         for (Map.Entry<ClientHandler, Integer> entry : scores.entrySet()) {
//             ClientHandler client = entry.getKey();
//             int score = entry.getValue();
//             sb.append("Player: ").append(client.socket.getInetAddress().getHostAddress())
//                     .append(", Score: ").append(score).append("\n");
//         }
//         sb.append("Winner: ").append(winner.socket.getInetAddress().getHostAddress());
//         out.println(sb.toString());
//     }
// }

import java.io.*;
import java.net.*;
import java.util.*;

public class MathTriviaServer {
    private static final int PORT = 12345;
    private static final String QUESTIONS_FILE = "questions.csv";
    private static final int NUM_QUESTIONS = 5; // Number of questions to ask
    private static final int COUNTDOWN_DURATION = 5; // Countdown duration in seconds

    private List<Question> questions;
    private List<ClientHandler> clients;
    private int connectedClientCount;
    private boolean gameStarted;

    public MathTriviaServer() {
        questions = new ArrayList<>();
        questions.add(new Question("5+5", 10));
        clients = new ArrayList<>();
        connectedClientCount = 0;
        gameStarted = false;
    }

    public void start() {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        System.out.println("Server is running...");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
            ClientHandler clientHandler = new ClientHandler(clientSocket, this); // Pass the MathTriviaServer instance as an argument
            this.clients.add(clientHandler);
            System.out.println(this.clients.size());
            clientHandler.start();

            connectedClientCount++;

            clientHandler.sendQuestion("Welele");

            // Check if all clients are connected
            if (connectedClientCount == 1) {
                clientHandler.sendStartPrompt();
                break;
            } else if (connectedClientCount > 1) {
                for (ClientHandler client : this.clients) {
                    client.sendStartPrompt();
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            // Countdown
            for (int i = COUNTDOWN_DURATION; i > 0; i--) {
                System.out.println("Game starts in " + i + " seconds...");
                clients.get(0).sendQuestion("Game starts in " + i + " seconds...");
                try {
                    Thread.sleep(1000); // Wait for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Start game
            System.out.println("Starting the game...");
            clients.get(0).sendQuestion("Starting the game...");
            // playGame();
        }
    }

    public void playGame() {
        // Shuffle questions
        List<Question> shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);
        Map<ClientHandler, Integer> scores = new HashMap<>();

        System.out.println("Question sennnnttttttt");

        questions = new ArrayList<>();
        questions.add(new Question("5+6", 10));
        questions.add(new Question("5+5", 10));
        questions.add(new Question("5+7", 10));

        for (Question question : questions) {
            this.clients.get(0).sendQuestion(question);
        }

        System.out.println(this.clients.size());
        
        
        this.clients.get(0).sendQuestion("Welele");
        this.clients.get(0).sendQuestion("Welele");
        this.clients.get(0).sendQuestion("Welele");


        // Send questions to clients
        for (Question question : questions) {

            System.out.println("question found");

            for (ClientHandler client : this.clients) {
                System.out.println("Question sennnnttttttt");

                System.out.println(client.socket.getInetAddress().getHostAddress());
                client.sendQuestion(question);
                
                client.sendQuestion("Welele");
                
                client.sendStartPrompt();

                
            }

            // Receive and evaluate answers
            Map<ClientHandler, String> answers = new HashMap<>();
            for (ClientHandler client : clients) {
                String answer = client.receiveAnswer();
                answers.put(client, answer);
            }

            // Calculate scores
            // Map<ClientHandler, Integer> scores = new HashMap<>();
            for (Map.Entry<ClientHandler, String> entry : answers.entrySet()) {
                ClientHandler client = entry.getKey();
                String answer = entry.getValue();
                int score = evaluateAnswer(question, answer) ? 1 : 0;
                scores.put(client, score);
            }

            // Determine the winner of the current question
            ClientHandler winner = determineWinner(scores);

            // Send question results to clients
            for (ClientHandler client : clients) {
                client.sendQuestionResult(question, scores.get(client), winner == client);
            }
        }

        // Determine the overall winner of the game
        ClientHandler overallWinner = determineOverallWinner(scores);
        
        // Send game results to clients
        for (ClientHandler client : clients) {
            client.sendGameResult(overallWinner);
        }
    }

    private boolean evaluateAnswer(Question question, String answer) {
        try {
            int userAnswer = Integer.parseInt(answer);
            return userAnswer == question.getAnswer();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ClientHandler determineWinner(Map<ClientHandler, Integer> scores) {
        // Find the client with the highest score
        ClientHandler winner = null;
        int maxScore = Integer.MIN_VALUE;
        for (Map.Entry<ClientHandler, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winner = entry.getKey();
            }
        }
        return winner;
    }

    private ClientHandler determineOverallWinner(Map<ClientHandler, Integer> scores) {
        // Find the client with the highest total score
        ClientHandler overallWinner = null;
        int maxTotalScore = Integer.MIN_VALUE;
        for (Map.Entry<ClientHandler, Integer> entry : scores.entrySet()) {
            int totalScore = entry.getValue();
            if (totalScore > maxTotalScore) {
                maxTotalScore = totalScore;
                overallWinner = entry.getKey();
            }
        }
        return overallWinner;
    }

    private List<Question> readQuestionsFromCSV() {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String expression = data[0].trim();
                    int answer = Integer.parseInt(data[1].trim());
                    questions.add(new Question(expression, answer));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void main(String[] args) {
        MathTriviaServer server = new MathTriviaServer();
        server.start();
        
    }
}

class Question {
    private String expression;
    private int answer;

    public Question(String expression, int answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public String getExpression() {
        return expression;
    }

    public int getAnswer() {
        return answer;
    }
}

class ClientHandler extends Thread {
    public Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public MathTriviaServer server;
    private final Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;
    String clientIdentifier;
    Scanner commandLine;

    public ClientHandler(Socket socket, MathTriviaServer server) {
        this.socket = socket;
        this.clientSocket = socket;
        this.server = server; // Assign the server instance passed from the main method
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void run() {
    //     try {
    //         String inputLine;
    //         while ((inputLine = in.readLine()) != null) {
    //             if ("start".equalsIgnoreCase(inputLine.trim())) {
    //                 synchronized (server) {
    //                     server.startGame(); // Start the game using the server instance passed from the main method
    //                 }
    //             }
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
    
    @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                // String clientIdentifier = getClientIdentifier(clientSocket);
                Scanner commandLine = new Scanner(System.in);

                while (true) {
                    String message = dis.readUTF();

                    if (message.equalsIgnoreCase("quit")) {
                        break;
                    }
                    
                    System.out.println("Client " + clientIdentifier + " says: " + message);

                    // Send a response to the client
                    
                    // String response = commandLine.nextLine();
                    String response = "hello";
                    dos.writeUTF(response);
                    dos.flush(); // Ensure the response is sent immediately
                }

                System.out.println("Client " + clientIdentifier + " disconnected.");
                // clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    

    public void sendStartPrompt() {
        out.println("Type 'start' to begin the game.");
    }

    public void sendQuestion(String otherQuestion) {
        out.println(otherQuestion);
    }

    public void sendQuestion(Question question) {
        out.println(question.getExpression());
        System.out.println(question.getExpression());
    }

    public String receiveAnswer() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendQuestionResult(Question question, int score, boolean isWinner) {
        if (isWinner) {
            out.println("Correct! You scored " + score + " point(s) for the question: " + question.getExpression());
        } else {
            out.println("You scored " + score + " point(s) for the question: " + question.getExpression());
        }
    }

    public void sendGameResult(ClientHandler overallWinner) {
        if (overallWinner == this) {
            out.println("Congratulations! You won the game!");
        } else {
            out.println("You didn't win the game. Better luck next time!");
        }
    }
}
