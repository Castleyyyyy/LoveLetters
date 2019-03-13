/**
 * @author: Benedikt Ricken
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class GameClient extends Client {

    private String username;
    private boolean eliminated = false;

    public GameClient(String pIPAdresse, int pPortNr) {
        super(pIPAdresse, pPortNr);
        listenOnInput();
    }

    /**
     * listenOnInput runs an infinite loop which reads the user input if there is one.
     * Then it sends the input to the server.
     */
    private void listenOnInput() {
        InputStreamReader fileInputStream = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(fileInputStream);

        while (true) {
            try {
                if (bufferedReader.ready()) {
                    send(bufferedReader.readLine());
                }
            } catch (IOException e) {
                System.out.println("Something got wrong!");
            }
        }

    }

    @Override
    public void processMessage(String pMessage) {
        switch (pMessage.split(":")[0]) {
            case "-FAIL":
                this.handleError(pMessage);
                break;
            case "+NAME":
                this.handleUsername();
                break;
            case "+USER_JOINED":
                System.out.println("New User connected: " + pMessage.split(":")[1]);
                break;
            case "+PLAYER_JOINED":
                System.out.println("New Player joined the game: " + pMessage.split(":")[1]);
                break;
            case "+GAME_STARTED":
                System.out.println("The game has started!");
                break;
            case "+CARD_PLAYED":
                this.handleCardPlayed(pMessage);
                break;
            case "+PLAYER_ROTATED":
                this.handlePlayerRotated(pMessage);
                break;
            case "+PLAYER_OUT":
                this.handlePlayerOut(pMessage);
                break;
            case "+ROUND_FINISHED":
                this.handleRoundFinished(pMessage);
                break;
            case "+GAME_FINISHED":
                this.handleGameFinished(pMessage);
                break;
            case "+CARDS_SWAPPED":
                this.handleCardsSwapped(pMessage);
                break;
            case "+PLAYER_PROTECTED":
                System.out.println("The player " + pMessage.split(":")[1] + " is now protected.");
                break;
            case "+CARD_REVEALED":
                this.handleCardRevealed(pMessage);
                break;
            case "+CARD_DRAWN":
                System.out.println("You`ve drawn the card " + pMessage.split(":")[1] + ".");
                break;
            case "+CARD_CHANGED":
                System.out.println("Your card has changed to " + pMessage.split(":")[1] +".");
                break;
            case "+CARD_SHOWN_TO_PLAYER":
                this.handleCardShownToPlayer(pMessage);
                break;
            case "+PROTECTED_PLAYERS":
                this.handleProtectedPlayers(pMessage);
                break;
            case "+HELP":
                System.out.println(pMessage.split(":")[1]);
                break;
            case "+RANK":
                System.out.println("Your current rank is " + pMessage.split(":")[1]);
                break;
            case "+CARDS":
                System.out.println("You have the following cards: " + pMessage.split(":")[1]);
            case "+PLAYER_EXITED_GAME":
                System.out.println("The player " + pMessage.split(":")[1] + " exited the game.");
                break;
            case "+USER_QUIT":
                System.out.println("The user" + pMessage.split(":")[1] + "left the game.");
            default:
                System.out.println(pMessage);
                break;
        }
    }


    private void handleError(String pMessage) {
        System.out.println(pMessage.split(":")[1]);
    }

    private void handleUsername() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your name: ");
        this.username = sc.next();

        this.send("USERNAME:" + this.username);

    }

    private void handleCardPlayed(String pMessage) {
        String card = pMessage.split(":")[1];
        String target = pMessage.split(":")[2];

        if (!target.equals("")) {
            System.out.println("The card " + card + " with the target " + target + " was played.");
        } else {
            System.out.println("The card " + card + " was played.");
        }
    }

    private void handlePlayerRotated(String pMessage) {
        String nextPlayer = pMessage.split(":")[1];

        if (nextPlayer.equals(this.username)) {
            System.out.println("It is your turn!");
        }else {
            System.out.println("Next player: " + nextPlayer);
        }
    }

    private void handlePlayerOut(String pMessage) {
        String player = pMessage.split(":")[1];

        if (this.username.equals(player)) {
            System.out.println("You have been eliminated!");
            this.eliminated = true;
        } else {
            System.out.println("The player " + player + " has been eliminated.");
        }
    }

    private void handleRoundFinished(String pMessage) {
        this.eliminated = true;
        System.out.println("The round has finished. The winner is " + pMessage.split(":")[1]);
    }

    private void handleGameFinished(String pMessage) {
        this.eliminated = true;
        System.out.println("The game has finished. The winner is " + pMessage.split(":")[1]);
    }

    private void handleCardsSwapped(String pMessage) {
        System.out.println("The cards of player " + pMessage.split(":")[1] + " and " + pMessage.split(":")[2] + " have been swapped.");
    }

    private void handleCardRevealed(String pMessage) {
        System.out.println("The card of the player " + pMessage.split(":")[1] + " is " + pMessage.split(":")[2]);
    }

    private void handleCardShownToPlayer(String pMessage) {
        System.out.println("The player " + pMessage.split(":")[1] +" showed only you his card: "+ pMessage.split(":")[2]);
    }

    private void handleProtectedPlayers(String pMessage) {
        if (pMessage.length() == 19) System.out.println("There are no protected players.");

        String players = "";
        String[] messageSplit = pMessage.substring(18).split("");

        for (String protectedPlayer : messageSplit) {
            players += protectedPlayer + ", ";
        }

        System.out.println("This is a list of all protected players: " + players);

    }

    public static void main(String[] args) {
        new GameClient("localhost", 3333);
    }

}
