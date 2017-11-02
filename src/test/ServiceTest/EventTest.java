package test.ServiceTest;

import DataAccess.AuthTokenDAO;
import DataAccess.EventDAO;
import DataTransfer.Request.LoadRequest;
import DataTransfer.Response.EventResponse;
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

public class EventTest {
    Services services = new Services();
    Event[] events;
    AuthToken authTokenDescA;
    AuthToken authTokenDescB;
    LocalTime timeA;
    LocalTime timeB;

    @Before
    public void setUp() throws Exception {
        services.clear();

        // generate data
        events = new Event[5];
        events[0] = new Event("eventID0", "descendantA", "personID0",
                0.0, 0.0, "country0", "city0",
                "eventType0", 1990);
        events[1] = new Event("eventID1", "descendantA", "personID1",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);
        events[2] = new Event("eventID2", "descendantA", "personID2",
                2.2, 2.2, "country2", "city2",
                "eventType2", 1992);
        events[3] = new Event("eventID3", "descendantB", "personID3",
                3.3, 3.3, "country3", "city3",
                "eventType3", 1993);
        events[4] = new Event("eventID4", "descendantB", "personID4",
                4.4, 4.4, "country4", "city4",
                "eventType4", 1994);

        EventDAO eventDao = new EventDAO();
        eventDao.add(events[0]);
        eventDao.add(events[1]);
        eventDao.add(events[2]);
        eventDao.add(events[3]);
        eventDao.add(events[4]);

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

        events = null;
    }

    @Test
    public void generalEvents() throws Exception {
        EventResponse eventResponse = services.event(authTokenDescA.getAuthToken(), null);

        // validate the data of the event response
        assert(eventResponse != null);
        Event[] receivedEvents = eventResponse.getEvents();
        assert(receivedEvents != null);
        assert(receivedEvents.length == 3);
        assert(receivedEvents[0].getEventID().equals(events[0].getEventID()));
        assert(receivedEvents[1].getEventID().equals(events[1].getEventID()));
        assert(receivedEvents[2].getEventID().equals(events[2].getEventID()));
        assert(eventResponse.getMessage() == null);
        assert(eventResponse.getEventID() == null);
        assert(eventResponse.getDescendant() == null);
        assert(eventResponse.getPersonID() == null);
        assert(eventResponse.getLatitude() == null);
        assert(eventResponse.getLongitude() == null);
        assert(eventResponse.getCountry() == null);
        assert(eventResponse.getCity() == null);
        assert(eventResponse.getEventType() == null);
        assert(eventResponse.getYear() == null);

        eventResponse = services.event(authTokenDescB.getAuthToken(), null);

        // validate the data of the event response with authtokenb
        assert(eventResponse != null);
        receivedEvents = eventResponse.getEvents();
        assert(receivedEvents != null);
        assert(receivedEvents.length == 2);
        assert(receivedEvents[0].getEventID().equals(events[3].getEventID()));
        assert(receivedEvents[1].getEventID().equals(events[4].getEventID()));
        assert(eventResponse.getMessage() == null);
        assert(eventResponse.getEventID() == null);
        assert(eventResponse.getDescendant() == null);
        assert(eventResponse.getPersonID() == null);
        assert(eventResponse.getLatitude() == null);
        assert(eventResponse.getLongitude() == null);
        assert(eventResponse.getCountry() == null);
        assert(eventResponse.getCity() == null);
        assert(eventResponse.getEventType() == null);
        assert(eventResponse.getYear() == null);
    }

    @Test
    public void generalEventsBadAuth() throws Exception {
        //Bad Auth
        EventResponse eventResponse = services.event(authTokenDescA.getAuthToken() + "BadAuth", null);

        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(eventResponse.getEventID() == null);
        assert(eventResponse.getDescendant() == null);
        assert(eventResponse.getPersonID() == null);
        assert(eventResponse.getLatitude() == null);
        assert(eventResponse.getLongitude() == null);
        assert(eventResponse.getCountry() == null);
        assert(eventResponse.getCity() == null);
        assert(eventResponse.getEventType() == null);
        assert(eventResponse.getYear() == null);

        // null auth data access
        eventResponse = services.event(null, null);

        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(eventResponse.getEventID() == null);
        assert(eventResponse.getDescendant() == null);
        assert(eventResponse.getPersonID() == null);
        assert(eventResponse.getLatitude() == null);
        assert(eventResponse.getLongitude() == null);
        assert(eventResponse.getCountry() == null);
        assert(eventResponse.getCity() == null);
        assert(eventResponse.getEventType() == null);
        assert(eventResponse.getYear() == null);

        // empty string auth data access
        eventResponse = services.event("", null);

        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage() == Services.AUTH_TOKEN_ERROR);
        assert(eventResponse.getEventID() == null);
        assert(eventResponse.getDescendant() == null);
        assert(eventResponse.getPersonID() == null);
        assert(eventResponse.getLatitude() == null);
        assert(eventResponse.getLongitude() == null);
        assert(eventResponse.getCountry() == null);
        assert(eventResponse.getCity() == null);
        assert(eventResponse.getEventType() == null);
        assert(eventResponse.getYear() == null);
    }

    @Test
    public void eventIDSpecified() throws Exception {
        EventResponse eventResponse = services.event(authTokenDescA.getAuthToken(), events[0].getEventID());

        // validate the data of the event response
        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage() == null);
        assert(eventResponse.getEventID().equals(events[0].getEventID()));
        assert(eventResponse.getDescendant().equals(events[0].getDescendant()));
        assert(eventResponse.getPersonID().equals(events[0].getPersonID()));
        assert(eventResponse.getLatitude().equals(Double.toString(events[0].getLatitude())));
        assert(eventResponse.getLongitude().equals(Double.toString(events[0].getLongitude())));
        assert(eventResponse.getCountry().equals(events[0].getCountry()));
        assert(eventResponse.getCity().equals(events[0].getCity()));
        assert(eventResponse.getEventType().equals(events[0].getEventType()));
        assert(eventResponse.getYear().equals(Integer.toString(events[0].getYear())));

    }

    @Test
    public void eventIDSpecifiedBadEventID() throws Exception {
        EventResponse eventResponse = services.event(authTokenDescA.getAuthToken(), events[0].getEventID() + "BadEventID");

        // validate the data of the event response
        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage().equals(Services.NO_EVENTID_ERROR));

        eventResponse = services.event(authTokenDescA.getAuthToken(), "");

        // validate the data of the event response
        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage().equals(Services.NO_EVENTID_ERROR));
    }

    @Test
    public void eventIDSpecifiedAccess() throws Exception {
        EventResponse eventResponse = services.event(authTokenDescA.getAuthToken(), events[3].getEventID());

        // validate the data of the event response
        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage().equals(Services.INVALID_DATA_ACCESS));

        eventResponse = services.event(authTokenDescB.getAuthToken(), events[0].getEventID());

        // validate the data of the event response
        assert(eventResponse != null);
        assert(eventResponse.getEvents() == null);
        assert(eventResponse.getMessage().equals(Services.INVALID_DATA_ACCESS));
    }
}
