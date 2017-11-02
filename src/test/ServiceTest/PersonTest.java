package test.ServiceTest;

import DataAccess.AuthTokenDAO;
import DataAccess.PersonDAO;
import DataTransfer.Response.PersonResponse;
import Model.AuthToken;
import Model.Person;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class PersonTest {
    Services services = new Services();
    Person[] persons;
    AuthToken authTokenDescA;
    AuthToken authTokenDescB;

    LocalTime timeA;
    LocalTime timeB;

    @Before
    public void setUp() throws Exception {
        services.clear();

        // generate data
        persons = new Person[5];
        persons[0] = new Person("personID0", "descendantA", "firstName0",
                "lastName0", 'm', "father0", "mother0",
                "eventType0");
        persons[1] = new Person("personID1", "descendantA", "firstName1",
                "lastName1", 'm', "father1", "mother1",
                "spouse1");
        persons[2] = new Person("personID2", "descendantA", "firstName2",
                "lastName2", 'm', "father2", "mother2",
                "spouse2");
        persons[3] = new Person("personID3", "descendantB", "firstName3",
                "lastName3", 'm', "father3", "mother3",
                "spouse3");
        persons[4] = new Person("personID4", "descendantB", "firstName4",
                "lastName4", 'm', "father4", "mother4",
                "spouse4");

        PersonDAO personDAO = new PersonDAO();
        personDAO.add(persons[0]);
        personDAO.add(persons[1]);
        personDAO.add(persons[2]);
        personDAO.add(persons[3]);
        personDAO.add(persons[4]);

        // create and load authToken
        AuthTokenDAO authTokenDAO = new AuthTokenDAO();

        timeA = LocalTime.now();
        authTokenDescA = new AuthToken("authTokenA", "descendantA", timeA);
        timeB = LocalTime.now();
        authTokenDescB = new AuthToken("B", "descendantB", timeB);

        authTokenDAO.add(authTokenDescA);
        authTokenDAO.add(authTokenDescB);
    }

    @After
    public void tearDown() throws Exception {
        services.clear();

        persons = null;
    }

    @Test
    public void generalPersons() throws Exception {
        PersonResponse personResponse = services.person(authTokenDescA.getAuthToken(), null);

        // validate the data of the event response
        assert(personResponse != null);
        Person[] receivedPersons = personResponse.getPersons();
        assert(receivedPersons != null);
        assert(receivedPersons.length == 3);
        assert(receivedPersons[0].getPersonID().equals(persons[0].getPersonID()));
        assert(receivedPersons[1].getPersonID().equals(persons[1].getPersonID()));
        assert(receivedPersons[2].getPersonID().equals(persons[2].getPersonID()));
        assert(personResponse.getMessage() == null);
        assert(personResponse.getPersonID() == null);
        assert(personResponse.getDescendant() == null);
        assert(personResponse.getFirstName() == null);
        assert(personResponse.getLastName() == null);
        assert(personResponse.getFather() == null);
        assert(personResponse.getMother() == null);
        assert(personResponse.getSpouse() == null);

        personResponse = services.person(authTokenDescB.getAuthToken(), null);

        // validate the data of the event response
        assert(personResponse != null);
        receivedPersons = personResponse.getPersons();
        assert(receivedPersons != null);
        assert(receivedPersons.length == 2);
        assert(receivedPersons[0].getPersonID().equals(persons[3].getPersonID()));
        assert(receivedPersons[1].getPersonID().equals(persons[4].getPersonID()));
        assert(personResponse.getMessage() == null);
        assert(personResponse.getPersonID() == null);
        assert(personResponse.getDescendant() == null);
        assert(personResponse.getFirstName() == null);
        assert(personResponse.getLastName() == null);
        assert(personResponse.getFather() == null);
        assert(personResponse.getMother() == null);
        assert(personResponse.getSpouse() == null);
    }

    @Test
    public void generalEventsBadAuth() throws Exception {
        //Bad Auth
        PersonResponse personResponse = services.person(authTokenDescA.getAuthToken() + "BadAuth", null);

        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(personResponse.getPersonID() == null);
        assert(personResponse.getDescendant() == null);
        assert(personResponse.getFirstName() == null);
        assert(personResponse.getLastName() == null);
        assert(personResponse.getFather() == null);
        assert(personResponse.getMother() == null);
        assert(personResponse.getSpouse() == null);

        // null auth data access
        personResponse = services.person(null, null);

        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(personResponse.getPersonID() == null);
        assert(personResponse.getDescendant() == null);
        assert(personResponse.getFirstName() == null);
        assert(personResponse.getLastName() == null);
        assert(personResponse.getFather() == null);
        assert(personResponse.getMother() == null);
        assert(personResponse.getSpouse() == null);

        // empty string auth data access
        personResponse = services.person("", null);

        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(personResponse.getPersonID() == null);
        assert(personResponse.getDescendant() == null);
        assert(personResponse.getFirstName() == null);
        assert(personResponse.getLastName() == null);
        assert(personResponse.getFather() == null);
        assert(personResponse.getMother() == null);
        assert(personResponse.getSpouse() == null);
    }

    @Test
    public void eventIDSpecified() throws Exception {
        PersonResponse personResponse = services.person(authTokenDescA.getAuthToken(), persons[0].getPersonID());

        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage() == null);
        assert(personResponse.getPersonID().equals(persons[0].getPersonID()));
        assert(personResponse.getDescendant().equals(persons[0].getDescendant()));
        assert(personResponse.getFirstName().equals(persons[0].getFirstName()));
        assert(personResponse.getLastName().equals(persons[0].getLastName()));
        assert(personResponse.getGender() == persons[0].getGender());
        assert(personResponse.getFather().equals(persons[0].getFather()));
        assert(personResponse.getMother().equals(persons[0].getMother()));
        assert(personResponse.getSpouse().equals(persons[0].getSpouse()));

    }

    @Test
    public void eventIDSpecifiedBadEventID() throws Exception {
        PersonResponse personResponse = services.person(authTokenDescA.getAuthToken(), persons[0].getPersonID() + "BadID");

        // validate the data of the event response
        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage().equals(Services.NO_PERSONID_ERROR));

        personResponse = services.person(authTokenDescA.getAuthToken(), "");

        // validate the data of the event response
        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage().equals(Services.NO_PERSONID_ERROR));
    }

    @Test
    public void eventIDSpecifiedAccess() throws Exception {
        PersonResponse personResponse = services.person(authTokenDescA.getAuthToken(), persons[3].getPersonID());

        // validate the data of the event response
        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage().equals(Services.INVALID_DATA_ACCESS));

        personResponse = services.person(authTokenDescB.getAuthToken(), persons[0].getPersonID());

        // validate the data of the event response
        assert(personResponse != null);
        assert(personResponse.getPersons() == null);
        assert(personResponse.getMessage().equals(Services.INVALID_DATA_ACCESS));
    }
}
