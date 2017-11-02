package test.DataAccessTest;

import DataAccess.EventDAO;
import Model.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventDAOTest {

    EventDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = new EventDAO();

        dao.clear();
    }

    @After
    public void tearDown() throws Exception {
        dao.clear();

        dao = null;
    }

    @Test
    public void retrieve() throws Exception {
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);

        dao.add(event);

        Event retrievedEvent = dao.retrieve("eventID");

        assert(retrievedEvent != null);
        assert(retrievedEvent.getEventID().equals(event.getEventID()));
        assert(retrievedEvent.getDescendant().equals(event.getDescendant()));
        assert(retrievedEvent.getPersonID().equals(event.getPersonID()));
        assert(retrievedEvent.getLatitude() == event.getLatitude());
        assert(retrievedEvent.getLongitude() == event.getLongitude());
        assert(retrievedEvent.getCountry().equals(event.getCountry()));
        assert(retrievedEvent.getCity().equals(event.getCity()));
        assert(retrievedEvent.getEventType().equals(event.getEventType()));
        assert(retrievedEvent.getYear() == event.getYear());
    }

    @Test
    public void retrieveNegative() throws Exception {
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);

        dao.add(event);

        Event retrievedEvent = dao.retrieve("eventIDNonExist");
        assertFalse(retrievedEvent != null);
        retrievedEvent = dao.retrieve("");
        assertFalse(retrievedEvent != null);
        retrievedEvent = dao.retrieve(null);
        assertFalse(retrievedEvent != null);

    }

    @Test
    public void retrieveEvents() throws Exception {
        Event event1 = new Event("eventID1", "descendant", "personID1",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);
        Event event2 = new Event("eventID2", "descendant", "personID2",
                2.2, 2.2, "country2", "city2",
                "eventType2", 1992);

        // add event of different descendant
        Event eventDiff = new Event("eventIDDiff", "descendantDiff", "personIDDiff",
                9.9, 9.9, "countryDiff", "cityDiff",
                "eventTypeDiff", 1999);

        dao.add(event1);
        dao.add(event2);
        dao.add(eventDiff);

        Event[] events = dao.retrieveEvents("descendant");

        // check that retrieveEvents works
        assert(events != null);
        // check that 3 events weren't received
        assert(events.length == 2);
        // check that data is accurate
        assert(events[0].getEventID().equals(event1.getEventID()));
        assert(events[1].getEventID().equals(event2.getEventID()));
    }

    @Test
    public void retrieveEventsNegative() throws Exception {
        Event event1 = new Event("eventID1", "descendant", "personID1",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);
        Event event2 = new Event("eventID2", "descendant", "personID2",
                2.2, 2.2, "country2", "city2",
                "eventType2", 1992);

        // add event of different descendant
        Event eventDiff = new Event("eventIDDiff", "descendantDiff", "personIDDiff",
                9.9, 9.9, "countryDiff", "cityDiff",
                "eventTypeDiff", 1999);

        dao.add(event1);
        dao.add(event2);
        dao.add(eventDiff);

        Event[] events = dao.retrieveEvents("descendantNonExist");
        assert(events == null);
        events = dao.retrieveEvents("");
        assert(events == null);
        events = dao.retrieveEvents(null);
        assert(events == null);
    }

    @Test
    public void add() throws Exception {
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);

        assert(dao.add(event));
    }

    @Test
    public void addNegative() throws Exception {
        Event event = new Event(null, null, null, 0.0, 0.0, null, null, null, 0);

        assertFalse(dao.add(event));
        assertFalse(dao.add(null));

    }

    @Test
    public void addDataValidation() throws Exception {
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);

        dao.add(event);

        /**
         * checks that eventID must be unique
         */
        assert(!dao.add(event));
    }

    @Test
    public void clear() throws Exception {
        Event event = new Event("eventID", "descendant", "personID",
                0.0, 0.0, "country", "city",
                "eventType", 1999);

        dao.add(event);

        // clear works
        assert(dao.clear());
        //actually cleared data
        assert(dao.retrieve(event.getEventID()) == null);
    }

    @Test
    public void retrieveEventsUsingPersonID() throws Exception {
        Event event1 = new Event("eventID1", "descendant", "personID",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);
        Event event2 = new Event("eventID2", "descendant", "personID",
                2.2, 2.2, "country2", "city2",
                "eventType2", 1992);

        // add event of different descendant and personID
        Event eventDiff = new Event("eventIDDiff", "descendantDiff", "personIDDiff",
                9.9, 9.9, "countryDiff", "cityDiff",
                "eventTypeDiff", 1999);

        dao.add(event1);
        dao.add(event2);
        dao.add(eventDiff);

        Event[] events = dao.retrieveEventsUsingPersonID(event1.getPersonID());

        // check that retrieveEvents works
        assert(events != null);
        // check that 3 events weren't received
        assert(events.length == 2);
        // check that data is accurate
        assert(events[0].getEventID().equals(event1.getEventID()));
        assert(events[1].getEventID().equals(event2.getEventID()));
    }

    @Test
    public void retrieveEventsUsingPersonIDNegative() throws Exception {
        Event event1 = new Event("eventID1", "descendant", "personID",
                1.1, 1.1, "country1", "city1",
                "eventType1", 1991);
        Event event2 = new Event("eventID2", "descendant", "personID",
                2.2, 2.2, "country2", "city2",
                "eventType2", 1992);

        // add event of different descendant and personID
        Event eventDiff = new Event("eventIDDiff", "descendantDiff", "personIDDiff",
                9.9, 9.9, "countryDiff", "cityDiff",
                "eventTypeDiff", 1999);

        dao.add(event1);
        dao.add(event2);
        dao.add(eventDiff);

        Event[] events = dao.retrieveEventsUsingPersonID(event1.getPersonID() + "NonExist");
        assert(events == null);
        events = dao.retrieveEventsUsingPersonID("");
        assert(events == null);
        events = dao.retrieveEventsUsingPersonID(null);
        assert(events == null);
    }

}