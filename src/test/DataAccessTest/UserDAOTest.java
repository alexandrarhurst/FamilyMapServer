package test.DataAccessTest;

import DataAccess.EventDAO;
import DataAccess.PersonDAO;
import DataAccess.UserDAO;
import Model.Event;
import Model.Person;
import Model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserDAOTest {

    UserDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = new UserDAO();

        dao.clear();
    }

    @After
    public void tearDown() throws Exception {
        dao.clear();

        dao = null;
    }

    @Test
    public void retrieve() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");
        dao.add(user);

        User retrievedUser = dao.retrieve("userName");

        // check that the object was retrieved, and that the data is consistant
        assert(retrievedUser != null);
        assert(retrievedUser.getUserName().equals("userName"));
        assert(retrievedUser.getPassword().equals("password"));
        assert(retrievedUser.getEmail().equals("email@email.com"));
        assert(retrievedUser.getFirstName().equals("firstName"));
        assert(retrievedUser.getLastName().equals("lastName"));
        assert(retrievedUser.getGender() == 'm');
        assert(retrievedUser.getPersonID().equals("personID"));
    }

    @Test
    public void retrieveNegative() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");
        dao.add(user);

        User retrievedUser = dao.retrieve("userNameNonExist");
        assert(retrievedUser == null);
        retrievedUser = dao.retrieve("");
        assert(retrievedUser == null);
        retrievedUser = dao.retrieve(null);
        assert(retrievedUser == null);
    }

    /**
     * Tests to make sure that basic add functionality works.
     * @throws Exception
     */
    @Test
    public void add() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        assert(dao.add(user));
    }

    @Test
    public void addNegative() throws Exception {
        User user = new User(null, null, null, null, null, ' ', null);

        assert(!dao.add(user));
        assert(!dao.add(null));
    }

    /**
     * Tests if duplicate usernames are rejected.
     * @throws Exception
     */
    @Test
    public void addDataValidation() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        dao.add(user);

        /**
         * Checks for duplicate names
         */
        assert(!dao.add(user));

        /**
         * Checks that gender is 'm' or 'f'
         */
        assert(!dao.add(new User("userNameBadGender", "password", "email@email.com",
                "firstName", "lastName", 'a', "personID")));
    }

    /**
     * Tests if the clear method works properly. No clearNegative tests are required
     * @throws Exception
     */
    @Test
    public void clear() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");
         dao.add(user);

         assert(dao.clear());

         // fail is the user still exists
        assert(dao.retrieve("userName") == null);
    }

    @Test
    public void clearUserData() throws Exception {
        // add data for user1
        User user1 = new User("userName1", "password1", "email@email.com1",
                "firstName1", "lastName1", 'm', "personID1");
        dao.add(user1);
        Person ancestor1 = new Person("ancestor1", "userName1",
                "anc1FirstName", "anc1LastName", 'm',
                "anc1Father", "anc1Mother", "anc1Spouse");
        PersonDAO personDAO = new PersonDAO();
        personDAO.add(ancestor1);
        Event anc1Event = new Event("anc1EventID", "userName1", "ancestor1",
                1.1 , 1.1, "country1", "city1", "eventType1", 1991);
        EventDAO eventDAO = new EventDAO();
        eventDAO.add(anc1Event);

        // add data for user2
        User user2 = new User("userName2", "password2", "email@email.com2",
                "firstName2", "lastName2", 'm', "personID2");
        dao.add(user2);
        Person ancestor2 = new Person("ancestor2", "userName2",
                "anc2FirstName", "anc2LastName", 'm',
                "anc2Father", "anc2Mother", "anc2Spouse");
        personDAO.add(ancestor2);
        Event anc2Event = new Event("anc2EventID", "userName2", "ancestor2",
                2.2 , 2.2, "country2", "city2", "eventType2", 1992);
        eventDAO.add(anc2Event);

        // remove the user's ancestor data
        assert(dao.clearUserData("userName1"));
        // the user should still be in database
        assert(dao.retrieve("userName1") != null);
        // the user's ancestor data should be gone
        assert(personDAO.retrieve("ancestor1") == null);
        assert(eventDAO.retrieve("anc1EventID") == null);

        // user2's data should all exist
        assert(dao.retrieve("userName2") != null);
        // the user's ancestor data should be gone
        assert(personDAO.retrieve("ancestor2") != null);
        assert(eventDAO.retrieve("anc2EventID") != null);

    }

    @Test
    public void clearUserDataNegative() throws Exception {
        // add data for user1
        User user1 = new User("userName1", "password1", "email@email.com1",
                "firstName1", "lastName1", 'm', "personID1");
        dao.add(user1);
        Person ancestor1 = new Person("ancestor1", "userName1",
                "anc1FirstName", "anc1LastName", 'm',
                "anc1Father", "anc1Mother", "anc1Spouse");
        PersonDAO personDAO = new PersonDAO();
        personDAO.add(ancestor1);
        Event anc1Event = new Event("anc1EventID", "userName1", "ancestor1",
                1.1 , 1.1, "country1", "city1", "eventType1", 1991);
        EventDAO eventDAO = new EventDAO();
        eventDAO.add(anc1Event);

        // add data for user2
        User user2 = new User("userName2", "password2", "email@email.com2",
                "firstName2", "lastName2", 'm', "personID2");
        dao.add(user2);
        Person ancestor2 = new Person("ancestor2", "userName2",
                "anc2FirstName", "anc2LastName", 'm',
                "anc2Father", "anc2Mother", "anc2Spouse");
        personDAO.add(ancestor2);
        Event anc2Event = new Event("anc2EventID", "userName2", "ancestor2",
                2.2 , 2.2, "country2", "city2", "eventType2", 1992);
        eventDAO.add(anc2Event);

        // remove the user's ancestor data
        assert(!dao.clearUserData(null));
        assert(!dao.clearUserData("userNameNonExist"));
    }
}