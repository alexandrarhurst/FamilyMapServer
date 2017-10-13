package Model;

import DataAccess.EventDAO;

import java.util.Random;

/**
 * This class is used to create events that occur in the lives of those Persons in the database.
 *
 * Objects contain the following variables
 *String eventID;
 *String descendant;
 *String personID;
 *double latitude;
 *double longitude;
 *String country;
 *String city;
 *String eventType;
 *int year;
 */

public class Event {
    public static final int EVENT_ID_LENGTH = 8;
    public static final String BIRTH = "birth";
    public static final String BAPTISM = "baptism";
    public static final String MARRIAGE = "marriage";
    public static final String DEATH = "death";

    private String eventID;
    private String descendant;
    private String personID;
    private double latitude;
    private double longitude;
    private String city;
    private String country;
    private String eventType;
    private int year;

    public static String randomIDGen(){
        String returnID = "";
        String possibleChars = "abcdefghijklmnopqrstuvwxyz123456789";
        Random rand = new Random();

        for(int i = 0; i < EVENT_ID_LENGTH; i++){
            returnID += possibleChars.charAt(rand.nextInt(35));
        }

        //Check to make sure the ID doesn't already exist
        EventDAO dao = new EventDAO();
        if(dao.retrieve(returnID) != null)
            return randomIDGen();
        else
            return returnID;
    }

    public static int randomYearGen(int min, int max){
        Random rand = new Random();

        return min + rand.nextInt(max - min);
    }

    /**
     * Constructor for the Event class
     */
    public Event(String eventID, String descendant, String personID, double latitude, double longitude, String country, String city, String eventType, int year) {
        this.eventID = eventID;
        this.descendant = descendant;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }


    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public class Location {
        private double latitude;
        private double longitude;
        private String country;
        private String city;

        public Location(double latitude, double longitude, String country, String city) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.country = country;
            this.city = city;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public double getLatitude() {

            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

}