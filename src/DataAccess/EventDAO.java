package DataAccess;

import Model.*;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class is used to interface with the Event table of the database
 */
public class EventDAO {

    public EventDAO() {
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");

            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Event" +
                    "(Event_Id      VARCHAR(255) NOT NULL UNIQUE," +
                    "  Descendant   VARCHAR(255) NOT NULL," +
                    "  Person_Id    VARCHAR(255) NOT NULL," +
                    "  Latitude     REAL," +
                    "  Longitude    REAL," +
                    "  Country      VARCHAR(255)," +
                    "  City         VARCHAR(255)," +
                    "  Event_Type   VARCHAR(32) NOT NULL," +
                    "  Year         INT);";
            stmt.executeUpdate(sql);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Used to get an Event from the table
     * @param eventID The eventID to be returned as a string
     * @return Event
     */
    public Event retrieve(String eventID){
        Event event = null; //If null, not added to dao and will be created

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM Event WHERE Event_Id =?;" );
            sql.setString(1, eventID);

            rs = sql.executeQuery();

            if(rs.next()) {
                String event_id = rs.getString("Event_Id");
                String descendant = rs.getString("Descendant");
                String person_id = rs.getString("Person_Id");
                double latitude = rs.getDouble("Latitude");
                double longitude = rs.getDouble("Longitude");
                String country = rs.getString("Country");
                String city = rs.getString("City");
                String event_type = rs.getString("Event_Type");
                int year = rs.getInt("Year");

                event = new Event(event_id, descendant, person_id, latitude, longitude, country, city, event_type, year);
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
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

        return event;
    }

    /**
     * Retrieve all the events attached to a user
     * @param descendant  The user connected with the request
     * @return  Returns those events in an array
     */
    public Event[] retrieveEvents(String descendant){

        ArrayList<Event> events = new ArrayList(); //If null, not added to dao and will be created

        Connection c = null;
        ResultSet rs = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM Event WHERE Descendant=?;" );
            sql.setString(1, descendant);

            rs = sql.executeQuery();

            while ( rs.next() ) {
                String event_id = rs.getString("Event_Id");
                String desc = rs.getString("Descendant");
                String person_id  = rs.getString("Person_Id");
                double latitude = rs.getDouble("Latitude");
                double longitude = rs.getDouble("Longitude");
                String country = rs.getString("Country");
                String city = rs.getString("City");
                String event_type = rs.getString("Event_Type");
                int year = rs.getInt("Year");

                events.add(new Event(event_id, desc, person_id, latitude, longitude, country, city, event_type, year));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
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

        Event[] returnEvents = new Event[events.size()];
        if(returnEvents.length == 0){
            return null;
        } else {
            return events.toArray(returnEvents);
        }
    }

    /**
     * Used to add an Event from the table
     * @param event - to add event
     */
    public boolean add(Event event){
        boolean success = false;

        Connection c = null;
        PreparedStatement sql = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            String s = "INSERT INTO Event (Event_Id,Descendant,Person_Id,Latitude,Longitude,Country,City,Event_Type,Year)" +
                    "VALUES (?,?,?,?,?,?,?,?,?);";

            sql = c.prepareStatement(s);

            sql.setString(1, event.getEventID());
            sql.setString(2, event.getDescendant());
            sql.setString(3, event.getPersonID());
            sql.setString(4, Double.toString(event.getLatitude()));
            sql.setString(5, Double.toString(event.getLongitude()));
            sql.setString(6, event.getCountry());
            sql.setString(7, event.getCity());
            sql.setString(8, event.getEventType());
            sql.setString(9, Integer.toString(event.getYear()));

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

    /**
     * Used to clear the entire table
     * @return true or false depending on if it worked.
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
            String sql = "DROP TABLE Event;";

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

                // Create new Event table
                new EventDAO();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Returns a list of events connected to a person
     * @param personID      The ID of the person
     * @return  An array of event objects
     */
    public Event[] retrieveEventsUsingPersonID(String personID){

        ArrayList<Event> events = new ArrayList(); //If null, not added to dao and will be created

        Connection c = null;
        PreparedStatement sql = null;
        ResultSet rs = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:familymapserver.db");
            c.setAutoCommit(false);

            sql = c.prepareStatement("SELECT * FROM Event WHERE Person_Id=?;");

            sql.setString(1, personID);

            rs = sql.executeQuery();

            while ( rs.next() ) {
                String event_id = rs.getString("Event_Id");
                String descendant = rs.getString("Descendant");
                String person_id  = rs.getString("Person_Id");
                double latitude = rs.getDouble("Latitude");
                double longitude = rs.getDouble("Longitude");
                String country = rs.getString("Country");
                String city = rs.getString("City");
                String event_type = rs.getString("Event_Type");
                int year = rs.getInt("Year");

                events.add(new Event(event_id, descendant, person_id, latitude, longitude, country, city, event_type, year));
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

        Event[] returnEvents = new Event[events.size()];
        if(returnEvents.length == 0){
            return null;
        } else {
            return events.toArray(returnEvents);
        }
    }
}
