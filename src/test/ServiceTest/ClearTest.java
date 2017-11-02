package test.ServiceTest;

import DataAccess.AuthTokenDAO;
import DataAccess.EventDAO;
import DataAccess.PersonDAO;
import DataAccess.UserDAO;
import Model.AuthToken;
import Model.Event;
import Model.Person;
import Model.User;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class ClearTest {

    Services services;

    @Before
    public void setUp() throws Exception {
        services = new Services();
    }

    @After
    public void tearDown() throws Exception {
        services = null;
    }

    @Test
    public void generalClear() throws Exception {
        AuthToken authToken = new AuthToken("authToken", "userName", LocalTime.now());
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        AuthTokenDAO authTokenDAO = new AuthTokenDAO();
        EventDAO eventDAO = new EventDAO();
        PersonDAO personDAO = new PersonDAO();
        UserDAO userDAO = new UserDAO();

        authTokenDAO.add(authToken);
        eventDAO.add(event);
        personDAO.add(person);
        userDAO.add(user);

        services.clear();

        assert(authTokenDAO.retrieve(authToken.getUsername()) == null);
        assert(eventDAO.retrieve(event.getEventID()) == null);
        assert(personDAO.retrieve(person.getPersonID()) == null);
        assert(userDAO.retrieve(user.getUserName()) == null);
    }
}
