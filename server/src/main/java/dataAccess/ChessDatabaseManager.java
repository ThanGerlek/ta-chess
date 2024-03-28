package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class ChessDatabaseManager extends DatabaseManager {

    private static final String[] CREATE_TABLE_STATEMENTS = {"""
            CREATE TABLE IF NOT EXISTS auth (
                id INT NOT NULL AUTO_INCREMENT,
                token VARCHAR(128) NOT NULL UNIQUE,
                username VARCHAR(128) NOT NULL,
                PRIMARY KEY (id),
                UNIQUE INDEX (token)
            )""", """
            CREATE TABLE IF NOT EXISTS games (
                gameId INT NOT NULL AUTO_INCREMENT,
                gameName VARCHAR(256),
                game TEXT NOT NULL,
                PRIMARY KEY (gameId)
            )""", """
            CREATE TABLE IF NOT EXISTS roles (
                id INT NOT NULL AUTO_INCREMENT,
                gameId INT NOT NULL,
                username VARCHAR(128) NOT NULL,
                role VARCHAR(32) NOT NULL,
                PRIMARY KEY (id),
                INDEX (gameId)
            )""", """
            CREATE TABLE IF NOT EXISTS users (
                id INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(128) NOT NULL,
                password VARCHAR(128) NOT NULL,
                email VARCHAR(128),
                PRIMARY KEY (id),
                UNIQUE INDEX (username)
            )"""};

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try {
            var conn = DatabaseManager.getConnection();
            for (String statement : CREATE_TABLE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure database: " + e.getMessage());
        }

    }

    static public void test() throws SQLException {
        String connectionUrl = "jdbc:mysql://LAPTOP-ISF44972.local:3308";
        String user = "wsl-student";
        String password = "Urq.y4yTfu3EnHw2";

        System.out.println("RUNNING TEST...");

        String statement = """
                INSERT INTO queue (net_id, phase, time_added)
                VALUES ('mallory', 'phase -1', ?)
                """;

        Connection conn = DriverManager.getConnection(connectionUrl, user, password);
        conn.setCatalog("autograder");


        try (var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setObject(1, Instant.now());
            preparedStatement.executeUpdate();
            System.out.println("IT INSERTED! OH NO!");
        } catch (Exception e) {
            System.out.println("IT DID NOT INSERT! HOORAY!");
            throw e;
        }
    }

    static public boolean didTestSuccessfullyFail() throws SQLException {
        String connectionUrl = "jdbc:mysql://LAPTOP-ISF44972.local:3308";
        String user = "wsl-student";
        String password = "Urq.y4yTfu3EnHw2";

        System.out.println("CHECKING TEST...");

        String statement = "SELECT * FROM queue WHERE net_id = 'mallory'";

        try {
            Connection conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog("autograder");

            try (var preparedStatement = conn.prepareStatement(statement)) {
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    System.out.println("FOUND IT! OH NO!");
                    return false;
                } else {
                    System.out.println("NOT FOUND! HOORAY!");
                    return true;
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Access denied")) return true;
            throw e;
        }
    }


    static void update(String sqlString) throws DataAccessException {
        update(sqlString, sp -> {
        });
    }

    static void update(String sqlString, StatementPreparer sp) throws DataAccessException {
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            sp.prepare(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to run executeUpdate() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            //TODO return connections?
//        } finally {
//            returnConnection(conn);
        }
    }

    static void updateWithParam(String sqlString, String param) throws DataAccessException {
        update(sqlString, preparedStatement -> {
            preparedStatement.setString(1, param);
        });
    }

    static void updateWithParam(String sqlString, int param) throws DataAccessException {
        update(sqlString, preparedStatement -> {
            preparedStatement.setInt(1, param);
        });
    }

    static boolean booleanQueryWithParam(String sqlString, String param) throws DataAccessException {
        return booleanQuery(sqlString, preparedStatement -> {
            preparedStatement.setString(1, param);
        });
    }

    /**
     * Returns true if the SQL query has at least one result.
     *
     * @param sqlString the SQL query to execute
     * @param sp        a lambda that inserts any parameters
     * @return true if the SQL query has at least one result
     */
    static boolean booleanQuery(String sqlString, StatementPreparer sp) throws DataAccessException {
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            sp.prepare(preparedStatement);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            //TODO return connections?
//        } finally {
//            returnConnection(conn);
        }
    }

    static boolean booleanQueryWithParam(String sqlString, int param) throws DataAccessException {
        return booleanQuery(sqlString, preparedStatement -> {
            preparedStatement.setInt(1, param);
        });
    }

    /**
     * Return a list of resultColumnLabel values from the rows returned by the SQL query.
     *
     * @param sqlString         the SQL query to execute
     * @param sp                a lambda that inserts any parameters
     * @param resultColumnLabel the SQL column to return values from
     * @return a list of resultColumnLabel values from rows returned by the query
     */
    static ArrayList<String> queryForString(String resultColumnLabel, String sqlString, StatementPreparer sp)
            throws DataAccessException {
        Connection conn = getConnection();
        try (var preparedStatement = conn.prepareStatement(sqlString)) {
            sp.prepare(preparedStatement);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                ArrayList<String> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rs.getString(resultColumnLabel));
                }
                return results;
            }


        } catch (SQLException e) {
            System.out.println("Failed to run executeQuery() on SQL String: `" + sqlString + "`");
            throw new DataAccessException(e.getMessage());
            //TODO return connections?
//        } finally {
//            returnConnection(conn);
        }
    }
}