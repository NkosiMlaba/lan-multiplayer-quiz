package za.co.theemlaba.server.login;

import za.co.theemlaba.server.*;

public class Login {
    ClientHandler clientHandler;

    public void startLogin (ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        sendWelcomeOptions();
        String userAnswer = clientHandler.readRequest().strip();
        
    }

    public void validateUserLoginOption(String userAnswer) {
        switch (userAnswer) {
            case "1":
                
                break;
            case "2":
                
                break;
            case "3":
                clientHandler.disconnectClient();
                break;
            default:
                return;
        }
    }

    public void sendWelcomeOptions () {
        String message = "Choose and option below\n"+
                        "[1] Login\n" +
                        "[2] Create a new account\n" +
                        "[3] Exit\n";
        clientHandler.sendMessage(message);
    }

    public void sendLoginOptions () {
        String message = "Choose and option below\n"+
                        "[1] Login\n" +
                        "[2] Create a new account\n" +
                        "[3] Exit\n";
        clientHandler.sendMessage(message);
    }
}
