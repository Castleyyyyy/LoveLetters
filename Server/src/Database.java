/**
 * Benedikt R.
 */
public class Database {

    DatabaseConnector db;

    public Database(String pIP, int pPort, String pDatabase, String pUsername, String pPassword) {
        db = new DatabaseConnector(pIP, pPort, pDatabase, pUsername, pPassword);

    }

    public String writeUserIntoDb(String pUsername, int pHearts, int pRound) {
        db.executeStatement("INSERT INTO games (username, hearts, round) VALUES ('"+ pUsername +"', '"+ pHearts+"', '"+ pRound+"')");

        return db.getErrorMessage();
    }
}
