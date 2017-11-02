package test.ServiceTest;

import DataAccess.PersonDAO;
import DataAccess.UserDAO;
import DataTransfer.Response.FillResponse;
import Model.Person;
import Model.User;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FillTest {
    Services services = new Services();
    User user;
    Person person;

    @Before
    public void setUp() throws Exception {
        services.clear();

        user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");
        person = new Person("personID", "userName",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        UserDAO userDAO = new UserDAO();
        PersonDAO personDAO = new PersonDAO();
        userDAO.add(user);
        personDAO.add(person);
    }

    @After
    public void tearDown() throws Exception {
        services.clear();

        user = null;
        person = null;
    }

    @Test
    public void fill() throws Exception {
        FillResponse response = services.fill(user.getUserName(), 4);

        assert(response.getMessage().startsWith("Successfully added "));
        assert(response.getMessage().contains(" persons and "));
        assert(response.getMessage().endsWith(" events to the database."));

        response = services.fill(user.getUserName(), 3);

        assert(response.getMessage().startsWith("Successfully added "));
        assert(response.getMessage().contains(" persons and "));
        assert(response.getMessage().endsWith(" events to the database."));

        response = services.fill(user.getUserName(), 2);

        assert(response.getMessage().startsWith("Successfully added "));
        assert(response.getMessage().contains(" persons and "));
        assert(response.getMessage().endsWith(" events to the database."));

        response = services.fill(user.getUserName(), 1);

        assert(response.getMessage().startsWith("Successfully added "));
        assert(response.getMessage().contains(" persons and "));
        assert(response.getMessage().endsWith(" events to the database."));
    }

    @Test
    public void toLittleGen() throws Exception {
        FillResponse response = services.fill(user.getUserName(), 0);

        assert(response.getMessage().equals(Services.INCORRECT_GENERATIONS_PARAM));
    }

    @Test
    public void invalidUsername() throws Exception {
        FillResponse response = services.fill("", 4);
        assert(response.getMessage().equals(Services.INVALID_USERNAME));

        response = services.fill(null, 4);
        assert(response.getMessage().equals(Services.INVALID_USERNAME));
    }

    @Test
    public void noUser() throws Exception {
        FillResponse response = services.fill("NonExistantUsername", 4);
        assert(response.getMessage().equals(Services.NO_USER_ERROR));

    }
}
