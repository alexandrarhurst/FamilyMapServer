package DataAccess;

import Model.*;

import java.sql.*;

/**
 * This class is used to interface with the user table of the database
 */
public class UserDAO {

    public UserDAO() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS User " +
                    "(USername      VARCHAR(255)    PRIMARY KEY NOT NULL UNIQUE," +
                    "Password       VARCHAR(255)    NOT NULL," +
                    "Email          VARCHAR(255)    NOT NULL," +
                    "First_Name     VARCHAR(255)    NOT NULL," +
                    "Last_Name      VARCHAR(255)    NOT NULL," +
                    "Gender           CHAR(1)" +
                    "CHECK (Gender = \"m\" OR Gender = \"f\")," +
                    "Person_Id      VARCHAR(255));";
            stmt.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
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
     * Used to get an User from the table
     * @return User
     */
    public User retrieve(String username){
        User user = null;

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM User WHERE Username=?;");
            sql.setString(1, username);

            rs = sql.executeQuery();

            if(rs.next()) {
                String _username = rs.getString("Username");
                String password = rs.getString("Password");
                String email = rs.getString("Email");
                String first_name = rs.getString("First_Name");
                String last_name = rs.getString("Last_Name");
                char gender = rs.getString("Gender").charAt(0);
                String person_id = rs.getString("Person_Id");


                user = new User(_username, password, email, first_name, last_name, gender, person_id);
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }finally{
            try {
                sql.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return user;
    }

    /**
     * Used to add an User from the table
     * @param user
     */
    public boolean add(User user){
        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);


            sql = c.prepareStatement("INSERT INTO User (Username,Password,Email,First_Name,Last_Name,Gender,Person_Id) " +
                    "VALUES (?,?,?,?,?,?,?);");

            sql.setString(1, user.getUserName());
            sql.setString(2, user.getPassword());
            sql.setString(3, user.getEmail());
            sql.setString(4, user.getFirstName());
            sql.setString(5, user.getLastName());
            sql.setString(6, "" + user.getGender());
            sql.setString(7, user.getPersonID());

            sql.executeUpdate();

            c.commit();
            success = true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
        } finally{
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
            String sql = "DROP TABLE User;";

            stmt.executeUpdate(sql);

            c.commit();
            success = true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally{
            try {
                stmt.close();
                c.close();

                // Recreate the User table
                new UserDAO();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return success;
    }

    public boolean clearUserData(String username){
        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("DELETE FROM Event WHERE Descendant=? AND Person_Id!=?;");

            sql.setString(1, username);
            sql.setString(2, retrieve(username).getPersonID());

            sql.executeUpdate();

            sql = c.prepareStatement("DELETE FROM Person WHERE Descendant=? AND Person_Id!=?;");

            sql.setString(1, username);
            sql.setString(2, retrieve(username).getPersonID());

            sql.executeUpdate();

            c.commit();
            success = true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally{
            try {
                sql.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }
}
