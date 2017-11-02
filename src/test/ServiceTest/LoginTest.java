package test.ServiceTest;

import DataAccess.UserDAO;
import DataTransfer.Request.LoginRequest;
import DataTransfer.Response.LoginResponse;
import Model.User;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {
    Services services = new Services();
    UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        services.clear();

        userDAO = new UserDAO();

    }

    @After
    public void tearDown() throws Exception {
        services.clear();

        userDAO = null;
    }

    @Test
    public void login() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        userDAO.add(user);
        LoginRequest request = new LoginRequest("userName", "password");
        LoginResponse response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == null);
        assert(response.getAuthToken() != null);
        assert(response.getUserName().equals(user.getUserName()));
        assert(response.getPersonID().equals(user.getPersonID()));
    }

    @Test
    public void badUsername() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        userDAO.add(user);
        LoginRequest request = new LoginRequest("", "password");
        LoginResponse response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.INVALID_REQUEST_DATA);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);

        request = new LoginRequest(null, "password");
        response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.INVALID_REQUEST_DATA);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);
    }

    @Test
    public void badPassword() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        userDAO.add(user);
        LoginRequest request = new LoginRequest("userName", "");
        LoginResponse response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.INVALID_REQUEST_DATA);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);

        request = new LoginRequest("userName", null);
        response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.INVALID_REQUEST_DATA);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);
    }

    @Test
    public void userDoesntExist() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        userDAO.add(user);
        LoginRequest request = new LoginRequest("NonExistantUser", "password");
        LoginResponse response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.USER_DOESNT_EXIST);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);
    }

    @Test
    public void incorrectPassword() throws Exception {
        User user = new User("userName", "password", "email@email.com",
                "firstName", "lastName", 'm', "personID");

        userDAO.add(user);
        LoginRequest request = new LoginRequest("userName", "incorrectPassword");
        LoginResponse response = services.login(request);

        assert(response != null);
        assert(response.getMessage() == Services.INCORRECT_PASSWORD);
        assert(response.getAuthToken() == null);
        assert(response.getUserName() == null);
        assert(response.getPersonID() == null);
    }
}
