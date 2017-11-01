package DataAccess;

import Model.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is used to interface with the Person table of the database
 */
public class PersonDAO {

    /**
     * constructor
     */
    public PersonDAO() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Person" +
                    "(Person_Id         VARCHAR(255)    NOT NULL UNIQUE," +
                    "  Descendant       VARCHAR(255)    NOT NULL," +
                    "  First_Name       VARCHAR(255)    NOT NULL," +
                    "  Last_Name        VARCHAR(255)    NOT NULL," +
                    "  Gender           CHAR(1)" +
                    "  CHECK (Gender = \"m\" OR Gender = \"f\")," +
                    "  Father           VARCHAR(255)," +
                    "  Mother           VARCHAR(255)," +
                    "  Spouse           VARCHAR(255));";
            stmt.executeUpdate(sql);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }finally{
            try {
                stmt.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Used to get an Person from the table
     * @return Event
     */
    public Person retrieve(String personID){
        Person person = null;

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            String s = "SELECT * FROM Person WHERE Person_Id=?;";

            sql = c.prepareStatement(s);
            sql.setString(1, personID);

            rs = sql.executeQuery();

            if(rs.next()) {

                String person_id = rs.getString("Person_Id");
                String descendant = rs.getString("Descendant");
                String first_name = rs.getString("First_Name");
                String last_name = rs.getString("Last_Name");
                char gender = rs.getString("Gender").charAt(0);
                String father_id = rs.getString("Father");
                String mother_id = rs.getString("Mother");
                String spouse_id = rs.getString("Spouse");

                person = new Person(person_id, descendant, first_name, last_name, gender, father_id, mother_id, spouse_id);
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

        return person;

    }

    /**
     * Returns all persons attached to a user
     * @param username  Username as a string
     * @return  An array of persons
     */
    public Person[] retrievePersons(String username) {

        ArrayList<Person> persons = new ArrayList(); //If null, not added to dao and will be created

        Connection c = null;
        PreparedStatement sql = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM Person WHERE Descendant=?");
            sql.setString(1, username);

            rs = sql.executeQuery();

            while (rs.next()) {
                String person_id = rs.getString("Person_Id");
                String descendant = rs.getString("Descendant");
                String first_name = rs.getString("First_Name");
                String last_name = rs.getString("Last_Name");
                char gender = rs.getString("Gender").charAt(0);
                String father_id = rs.getString("Father");
                String mother_id = rs.getString("Mother");
                String spouse_id = rs.getString("Spouse");

                persons.add(new Person(person_id, descendant,
                        first_name, last_name, gender,
                        father_id, mother_id, spouse_id));
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            try {
                sql.close();
                rs.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        Person[] returnPersons = new Person[persons.size()];
        return persons.toArray(returnPersons);
    }

    /**
     * Used to add an Person from the table
     * @param person - to add an event
     */
    public boolean add(Person person){
        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("INSERT INTO Person (Person_Id,Descendant,First_Name,Last_Name,Gender,Father,Mother,Spouse) " +
                    "VALUES (?,?,?,?,?,?,?,?);");

            sql.setString(1, person.getPersonID());
            sql.setString(2, person.getDescendant());
            sql.setString(3, person.getFirstName());
            sql.setString(4, person.getLastName());
            sql.setString(5, "" + person.getGender());
            sql.setString(6, person.getFather());
            sql.setString(7, person.getMother());
            sql.setString(8, person.getSpouse());

            sql.executeUpdate();

            c.commit();
            success = true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
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
            String sql = "DROP TABLE Person;";

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

                // Recreate Person table
                new PersonDAO();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }
}

