package test.ServiceTest;

import DataAccess.UserDAO;
import DataTransfer.Request.RegisterRequest;
import DataTransfer.Response.RegisterResponse;
import Service.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterTest {
    Services services = new Services();
    UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        services.clear();

    }

    @After
    public void tearDown() throws Exception {
        services.clear();
    }

    @Test
    public void request() throws Exception {
        RegisterRequest request = new RegisterRequest("userName", "password", "email@email.com",
                "firstName", "lastName", 'm');

        RegisterResponse response = services.register(request);
        assert(response != null);
        assert(response.getMessage() == null);
        assert(response.getAuthToken() != null);
        assert(response.getUserName().equals(request.getUserName()));
        assert(response.getPersonID() != null);
    }

    @Test
    public void badRequestData() throws Exception {
        RegisterRequest request = new RegisterRequest(null, "password", "email@email.com",
                "firstName", "lastName", 'm');
        RegisterResponse response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));

        request = new RegisterRequest("userName", null, "email@email.com",
                "firstName", "lastName", 'm');
        response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));

        request = new RegisterRequest("userName", "password", null,
                "firstName", "lastName", 'm');
        response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));

        request = new RegisterRequest("userName", "password", "email@email.com",
                null, "lastName", 'm');
        response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));

        request = new RegisterRequest("userName", "password", "email@email.com",
                "firstName", null, 'm');
        response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));

        request = new RegisterRequest("userName", null, "email@email.com",
                "firstName", "lastName", 'a');
        response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.INVALID_REQUEST_PROPERTY));
    }

    @Test
    public void usernameNotAvailable() throws Exception {
        RegisterRequest request = new RegisterRequest("userName", "password", "email@email.com",
                "firstName", "lastName", 'm');
        services.register(request);
        RegisterResponse response = services.register(request);
        assert(response != null);
        assert(response.getMessage().equals(Services.USERNAME_NOT_AVAILABLE));

    }
}
