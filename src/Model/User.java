package Model;

import Service.Services;

/**
 * Used as a way of storing the data on the user of the application.
 *
 * Uses the following variables:
 * String username;
 * String password;
 * String firstName;
 * Sring lastName;
 * char gender;
 * String personID;
 */
public class User {;

    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private char gender;
    private String personID;

    /**
     * Constructor for the User class
     */
    public User(String userName, String password, String email, String firstName, String lastName, char gender, String personID) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.personID = personID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
