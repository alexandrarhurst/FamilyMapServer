package DataAccess;

import Model.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Class used to access the AuthToken database
 */

public class AuthTokenDAO {

    /**
     * Used as a constructor for the class
     */
    public AuthTokenDAO() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS AuthToken" +
                    "(Auth_Token    VARCHAR(255)    NOT NULL UNIQUE," +
                    "Username       VARCHAR(255)    NOT NULL," +
                    "Time_Created   VARCHAR(255)    NOT NULL);";
            stmt.executeUpdate(sql);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally{
            try {
                stmt.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Retrieve all the Authtokens
     */
    public AuthToken[] retrieveAuthTokens(){
        ArrayList<AuthToken> authTokens = new ArrayList(); //If null, not added to dao and will be created

        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM AuthToken;");

            while ( rs.next() ) {
                String auth_token = rs.getString("Auth_Token");
                String  user_name = rs.getString("Username");
                LocalTime time_created  = LocalTime.parse(rs.getString("Time_Created"));

                authTokens.add(new AuthToken(auth_token, user_name,time_created));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally{
            try {
                stmt.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        AuthToken[] returnAuthTokens = new AuthToken[authTokens.size()];
        if(returnAuthTokens.length == 0){
            return null;
        } else {
            return authTokens.toArray(returnAuthTokens);
        }
    }


    /**
     * Used to get an Authtoken from the table
     * @return AuthToken
     */
    public AuthToken retrieve(String username){

        AuthToken authToken = null;

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            sql = c.prepareStatement("SELECT * FROM AuthToken WHERE Username=?;");
            sql.setString(1, username);
            rs = sql.executeQuery();

            while ( rs.next() ) {
                String auth_token = rs.getString("Auth_Token");
                String  user_name = rs.getString("Username");
                String time_created  = rs.getString("Time_Created");

                authToken = new AuthToken(auth_token, user_name, LocalTime.parse(time_created));
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally{
            try {
                sql.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return authToken;
    }


    public AuthToken retrieveUsingAuthToken(String authToken){
        AuthToken newAuthToken = null; //If null, not added to dao and will be created

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM AuthToken WHERE Auth_Token=?;" );
            sql.setString(1, authToken);

            rs = sql.executeQuery();

            while ( rs.next() ) {
                String auth_token = rs.getString("Auth_Token");
                String  user_name = rs.getString("Username");
                String time_created  = rs.getString("Time_Created");

                newAuthToken = new AuthToken(auth_token, user_name, LocalTime.parse(time_created));

            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }finally{
            try {
                sql.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return newAuthToken;
    }

    /**
     * Used to add an AuthToken from the table
     * @param authToken - For authentication
     */
    public boolean add(AuthToken authToken) {
        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("INSERT INTO AuthToken (Auth_Token,Username,Time_Created) " +
                    "VALUES (?,?,?);");

            sql.setString(1, authToken.getAuthToken());
            sql.setString(2, authToken.getUsername());
            sql.setString(3, authToken.getTime().toString());

            sql.executeUpdate();

            c.commit();
            success = true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }finally{
            try {
                sql.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Used to clear the entire table
     */
    public boolean clear(){
        boolean success = false;

        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "DROP TABLE AuthToken;";
            stmt.executeUpdate(sql);

            c.commit();

            success = true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }finally{
            try {
                stmt.close();
                c.close();

                // Create empty table
                new AuthTokenDAO();

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Used to delete a certain authtoken
     * @param authToken     the authtoken as a string
     * @return      Returns true if worked correctly
     */
    public boolean delete(String authToken){
        // validate
        if(authToken == null)
            return false;
        else if(retrieveUsingAuthToken(authToken) == null)
            return false;

        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("DELETE FROM AuthToken WHERE Auth_Token=?;");

            sql.setString(1, authToken);

            sql.executeUpdate();

            c.commit();
            c.close();

            success = true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }finally{
            try {
                sql.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Used to update the time on an authtoken
     * @param username      The username associated with the authtoken
     * @return      The updated authtoken
     */
    public AuthToken authTokenRefresh(String username) {
        AuthToken authToken = null;

        Connection c = null;
        PreparedStatement sql = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("UPDATE AuthToken SET Time_Created=? WHERE Username=?");

            sql.setString(1, LocalTime.now().toString());
            sql.setString(2, username);

            sql.executeUpdate();

            sql = c.prepareStatement("SELECT * FROM AuthToken WHERE Username=?");
            sql.setString(1, username);

            rs = sql.executeQuery();

            if(rs.next()){
                String auth_token = rs.getString("Auth_Token");
                String  user_name = rs.getString("Username");
                LocalTime time_created  = LocalTime.parse(rs.getString("Time_Created"));

                authToken = new AuthToken(auth_token, user_name, time_created);
            }

            c.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                sql.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return authToken;
    }

    /**
     * Deletes old authtokens
     */
    public void updateAuthTokens(){
        AuthToken[] authTokens = this.retrieveAuthTokens();

        for(AuthToken a : authTokens){
            if (LocalTime.now().isAfter(a.getTime().plusMinutes(60))){
                this.delete(a.getAuthToken());
            }
        }
    }
}
