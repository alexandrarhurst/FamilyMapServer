package test.DataAccessTest;

import DataAccess.AuthTokenDAO;
import Model.AuthToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class AuthTokenDAOTest {

    AuthTokenDAO dao;

    @Before
    public void setUp() throws Exception {
        dao = new AuthTokenDAO();

        dao.clear();
    }

    @After
    public void tearDown() throws Exception {
        dao.clear();

        dao = null;
    }

    @Test
    public void retrieveAuthTokens() throws Exception {
        LocalTime time0 = LocalTime.now();
        AuthToken authToken0 = new AuthToken("authToken0", "username0", time0);
        LocalTime time1 = LocalTime.now();
        AuthToken authToken1 = new AuthToken("authToken1", "username1", time1);

        dao.add(authToken0);
        dao.add(authToken1);

        AuthToken[] authTokens = dao.retrieveAuthTokens();

        assert(authTokens != null);
        assert(authTokens.length == 2);
        assert(authTokens[0].getAuthToken().equals(authToken0.getAuthToken()));
        assert(authTokens[0].getUsername().equals(authToken0.getUsername()));
        assert(authTokens[0].getTime().equals(authToken0.getTime()));
        assert(authTokens[1].getAuthToken().equals(authToken1.getAuthToken()));
        assert(authTokens[1].getUsername().equals(authToken1.getUsername()));
        assert(authTokens[1].getTime().equals(authToken1.getTime()));
    }

    @Test
    public void retrieveAuthTokensNegative() throws Exception {
        AuthToken[] authTokens = dao.retrieveAuthTokens();
        assert(authTokens == null);

        LocalTime time0 = LocalTime.now();
        AuthToken authToken0 = new AuthToken("authToken0", "username0", time0);
        LocalTime time1 = LocalTime.now();
        AuthToken authToken1 = new AuthToken("authToken1", "username1", time1);

        dao.add(authToken0);
        dao.add(authToken1);

        authTokens = dao.retrieveAuthTokens();
        assert(authTokens != null);
        assert(authTokens.length <= 2);
    }

    @Test
    public void retrieve() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken retrievedAuthToken = dao.retrieve(authToken.getUsername());

        assert(retrievedAuthToken != null);
        assert(retrievedAuthToken.getAuthToken().equals(authToken.getAuthToken()));
        assert(retrievedAuthToken.getUsername().equals(authToken.getUsername()));
        assert(retrievedAuthToken.getTime().equals(authToken.getTime()));

    }

    @Test
    public void retrieveNegative() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken retrievedAuthToken = dao.retrieve(authToken.getUsername() + "NonExist");
        assert(retrievedAuthToken == null);
        retrievedAuthToken = dao.retrieve("");
        assert(retrievedAuthToken == null);
        retrievedAuthToken = dao.retrieve(null);
        assert(retrievedAuthToken == null);
    }

    @Test
    public void retrieveUsingAuthToken() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken retrievedAuthToken = dao.retrieveUsingAuthToken(authToken.getAuthToken());

        assert(authToken != null);
        assert(retrievedAuthToken.getAuthToken().equals(authToken.getAuthToken()));
        assert(retrievedAuthToken.getUsername().equals(authToken.getUsername()));
        assert(retrievedAuthToken.getTime().equals(authToken.getTime()));
    }

    @Test
    public void retrieveUsingAuthTokenNegative() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken retrievedAuthToken = dao.retrieveUsingAuthToken(authToken.getAuthToken() + "NonExist");
        assert(retrievedAuthToken == null);
        retrievedAuthToken = dao.retrieveUsingAuthToken("");
        assert(retrievedAuthToken == null);
        retrievedAuthToken = dao.retrieveUsingAuthToken(null);
        assert(retrievedAuthToken == null);
    }

    @Test
    public void add() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        assert(dao.add(authToken));
    }

    @Test
    public void addNegative() throws Exception {
        AuthToken authToken = new AuthToken(null, null, null);

        assertFalse(dao.add(authToken));
        assertFalse(dao.add(null));
    }

    @Test
    public void addDataValidation() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        /**
         * checks that authToken must be unique
         */
        assert(!dao.add(authToken));
    }

    @Test
    public void clear() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        // clear works
        assert(dao.clear());
        //actually cleared data
        assert(dao.retrieve(authToken.getUsername()) == null);
    }

    @Test
    public void delete() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        // delete works
        assert(dao.delete(authToken.getAuthToken()));
        //actually cleared data
        assert(dao.retrieve(authToken.getUsername()) == null);
    }

    @Test
    public void deleteNegative() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        assertFalse(dao.delete(authToken.getAuthToken() + "NonExist"));
        assertFalse(dao.delete(""));
        assertFalse(dao.delete(null));
        assert(dao.retrieve(authToken.getUsername()) != null);
    }

    @Test
    public void authTokenRefresh() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken refreshedAuthToken = dao.authTokenRefresh(authToken.getUsername());

        assert(refreshedAuthToken.getUsername().equals(authToken.getUsername()));
        assert(refreshedAuthToken.getAuthToken().equals(authToken.getAuthToken()));
        assert(!refreshedAuthToken.getTime().equals(authToken.getTime()));
    }

    @Test
    public void authTokenRefreshNegative() throws Exception {
        LocalTime time = LocalTime.now();
        AuthToken authToken = new AuthToken("authToken", "username", time);

        dao.add(authToken);

        AuthToken refreshedAuthToken = dao.authTokenRefresh(authToken.getUsername() + "NonExist");
        assert(refreshedAuthToken == null);
        refreshedAuthToken = dao.authTokenRefresh("");
        assert(refreshedAuthToken == null);
        refreshedAuthToken = dao.authTokenRefresh(null);
        assert(refreshedAuthToken == null);
    }
}