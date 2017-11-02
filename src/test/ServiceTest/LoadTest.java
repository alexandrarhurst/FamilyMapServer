package test.ServiceTest;

import DataAccess.EventDAO;
import DataAccess.PersonDAO;
import DataAccess.UserDAO;
import DataTransfer.Request.LoadRequest;
import DataTransfer.Response.LoadResponse;
import Model.Event;
import Model.Person;
import Model.User;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoadTest {
    Services services = new Services();

    User[] goodUsers;
    Person[] goodPersons;
    Event[] goodEvent;

    User[] badUsers;
    Person[] badPersons;
    Event[] badEvent;

    UserDAO userDAO;
    PersonDAO personDAO;
    EventDAO eventDAO;

    @Before
    public void setUp() throws Exception {
        services.clear();

        userDAO = new UserDAO();
        personDAO = new PersonDAO();
        eventDAO = new EventDAO();

        goodUsers = new User[2];
        goodPersons = new Person[2];
        goodEvent = new Event[2];

        badUsers = new User[2];
        badPersons = new Person[2];
        badEvent = new Event[2];

        goodUsers[0] = new User("userName0", "password0", "email@email.com0",
                "firstName0", "lastName0", 'm', "personID0");
        goodUsers[1] = new User("userName1", "password1", "email@email.com1",
                "firstName1", "lastName1", 'm', "personID1");


        goodPersons[0] = new Person("personID0", "userName0",
                "firstName0", "lastName0", 'm',
                "father0", "mother0", "spouse0");
        goodPersons[1] = new Person("personID1", "userName1",
                "firstName1", "lastName1", 'm',
                "father1", "mother1", "spouse1");

        goodEvent[0] = new Event("eventID0", "userName0", "personID0",
                0.0, 0.0, "country0", "city0",
                "eventType0", 1990);
        goodEvent[1] = new Event("eventID1", "userName1", "personID1",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);


        badUsers[0] = new User(null, null, null, null, null, ' ', null);
        badUsers[1] = null;

        badPersons[0] = new Person(null, null, null, null, ' ', null, null, null);
        badPersons[1] = null;

        badEvent[0] = new Event(null, null, null, 0.0 , 0.0 , null, null, null, 0);
        badEvent[0] = null;
    }

    @After
    public void tearDown() throws Exception {
        services.clear();
    }

    @Test
    public void loadGood() throws Exception {
        LoadRequest loadRequest = new LoadRequest(goodUsers, goodPersons, goodEvent);
        LoadResponse loadResponse = services.load(loadRequest);

        assert(loadResponse != null);
        assert(loadResponse.getMessage().equals("Successfully added " + goodUsers.length
                + " users, " + goodPersons.length
                + " people, and " + goodEvent.length
                + " events to the database."));
    }

    @Test
    public void loadBad() throws Exception {
        LoadRequest loadRequest = new LoadRequest(badUsers, badPersons, badEvent);
        LoadResponse loadResponse = services.load(loadRequest);

        assert(loadResponse != null);
        assert(loadResponse.getMessage().equals(Services.INVALID_REQUEST_DATA));

        loadRequest = new LoadRequest(null, null, null);
        loadResponse = services.load(loadRequest);

        assert(loadResponse != null);
        assert(loadResponse.getMessage().equals(Services.INVALID_REQUEST_DATA));
    }
}
