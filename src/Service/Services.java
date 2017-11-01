package Service;

import DataAccess.*;
import Model.*;
import Model.Event.Location;
import DataTransfer.Request.*;
import DataTransfer.Response.*;
import Service.Data.*;

import java.time.LocalTime;
import java.util.Calendar;

/**
 * This class is designed to execute specific command sent to it by the Server and handler classes.
 */

public class Services {
    public static final String AUTH_TOKEN_ERROR = "Error: Authentication Token not found";
    public static final String INVALID_REQUEST_DATA = "Error: Invalid request error";
    public static final String USERNAME_NOT_AVAILABLE = "Error: Username not available";
    public static final String INVALID_DATA_ACCESS = "Error: You do not have access to this data";
    public static final String INVALID_REQUEST_PROPERTY = "Error: Request property missing or has invalid value";
    public static final String NO_USER_ERROR = "Error: No user found by that username";
    public static final String FILL_ERROR = "Error: Fill Error";
    public static final String USER_DOESNT_EXIST = "Error: User doesn't exist";
    public static final String INCORRECT_PASSWORD = "Error: Incorrect Password";
    public static final String CLEAR_SUCCESS = "Clear succeeded";
    public static final String CLEAR_ERROR = "Error: Clear failed";
    public static final String INCORRECT_GENERATIONS_PARAM = "Error: Generations must be greater than or equal to 1";
    public static final String INVALID_USERNAME = "Error: Invalid Username";
    public static final String INTERNAL_SERVER_ERROR = "Error: Internal Server Error";

    // Used for to calculate the response for the register
    private static int newPersonsCount = 0;
    private static int newEventsCount = 0;


    private AuthTokenDAO authTokenDAO = new AuthTokenDAO();
    private EventDAO eventDAO = new EventDAO();
    private PersonDAO personDAO = new PersonDAO();
    private UserDAO userDAO = new UserDAO();

    // Used for generating new random data
    private FNames fNames = new FNames("json/fnames.json");
    private Locations locations = new Locations("json/locations.json");
    private MNames mNames = new MNames("json/mnames.json");
    private SNames sNames = new SNames("json/snames.json");

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new user, logs the user in, and returns an auth token.
     *
     *  Errors:
     *  1) Request property missing or has invalid value
     *  2) Username already taken by another user
     *  3) Internal server error
     *
     * @return RegisterResponse
     */
    public RegisterResponse register(RegisterRequest input) {
        try {
            if (input.getUserName() == null || input.getPassword() == null ||
                    input.getEmail() == null ||
                    input.getFirstName() == null || input.getLastName() == null ||
                    (input.getGender() != 'm' && input.getGender() != 'f')) {       // Error 1: Request property missing or has invalid value
                // If any properties are missing
                return new RegisterResponse(INVALID_REQUEST_PROPERTY, null, null, null);
            } else if (userDAO.retrieve(input.getUserName()) != null) {              // Error 2: Username already taken by another user
                // If username is taken
                return new RegisterResponse(USERNAME_NOT_AVAILABLE, null, null, null);
            }

            RegisterResponse response;

            String newPersonID = Person.randomIDGen();
            String fatherID = Person.randomIDGen();
            String motherID = Person.randomIDGen();

            //Creating new user, and person to go with that.
            User user = new User(input.getUserName(), input.getPassword(), input.getEmail(),
                    input.getFirstName(), input.getLastName(), input.getGender(), newPersonID);
            userDAO.add(user);
            Person userPerson = new Person(newPersonID, input.getUserName(),
                    input.getFirstName(), input.getLastName(), input.getGender(), fatherID, motherID, null);
            personDAO.add(userPerson);

            //Generating random events
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Event.Location birthLoc = locations.getRandomLocation();
            Event.Location baptLoc = locations.getRandomLocation();
            Event birth = new Event(Event.randomIDGen(), input.getUserName(), userPerson.getPersonID(),
                    birthLoc.getLatitude(), birthLoc.getLongitude(), birthLoc.getCountry(), birthLoc.getCity(),
                    Event.BIRTH, Event.randomYearGen(currentYear - 30, currentYear - 18));
            eventDAO.add(birth);
            Event baptism = new Event(Event.randomIDGen(), input.getUserName(), userPerson.getPersonID(),
                    baptLoc.getLatitude(), baptLoc.getLongitude(), baptLoc.getCountry(), baptLoc.getCity(),
                    Event.BAPTISM, Event.randomYearGen(birth.getYear() + 8, birth.getYear() + 10));
            eventDAO.add(baptism);
            Event marriage = null;
            Event death = null;

            //Continue onto 4 more generations
            FillResponse temp = fill(user.getUserName(), 4);

            //Construct response based on return
            if (temp.getMessage().equals(NO_USER_ERROR)) {
                response = new RegisterResponse(NO_USER_ERROR, null, null, null);
            } else if (temp.getMessage().equals(FILL_ERROR)) {
                response = new RegisterResponse(FILL_ERROR, null, null, null);
            } else {
                // Create new authToken and add to database
                AuthToken newAuthToken = new AuthToken();
                newAuthToken.generate(user.getUserName());
                authTokenDAO.add(newAuthToken);

                response = new RegisterResponse(null, newAuthToken.getAuthToken(), user.getUserName(), user.getPersonID());
            }

            return response;
        }catch(Exception ex){       // Error 3: Internal server error
            return new RegisterResponse(INTERNAL_SERVER_ERROR, null, null, null);
        }
    }


    /**
     * Logs the user in and returns a LoginResponse object containting the authentication token,
     * the username, and the person ID. If error occurs, message field is filled with a description of the error.
     *
     *  Errors:
     *  1) Request property missing or has invalid value
     *  2) Username does
     *  3) Internal server error
     *
     * @param loginRequest      A request with all the information for a login
     * @return LoginResponse
     */
    public LoginResponse login(LoginRequest loginRequest) {
        //authTokenDAO.updateAuthTokens();

        if(loginRequest == null ||
                loginRequest.getUserName() == null ||
                loginRequest.getUserName().equals("") ||
                loginRequest.getPassword() == null ||
                loginRequest.getPassword().equals("")){     // Error 1: Request property missing or has invalid value
            return new LoginResponse(INVALID_REQUEST_DATA, null, null, null);
        } else if(userDAO.retrieve(loginRequest.getUserName()) == null){
            return new LoginResponse(USER_DOESNT_EXIST, null, null, null);
        } else {
            if (!userDAO.retrieve(loginRequest.getUserName()).getPassword().equals(loginRequest.getPassword())) {     //If password is incorrect
                return new LoginResponse(INCORRECT_PASSWORD, null, null, null);
            } else {       //If username matches password
                AuthToken authToken;

                if(authTokenDAO.retrieve(loginRequest.getUserName()) == null){
                    authToken = new AuthToken();
                    authToken.generate(loginRequest.getUserName());
                    authTokenDAO.add(authToken);
                } else{
                    authTokenDAO.authTokenRefresh(loginRequest.getUserName());
                    authToken = authTokenDAO.retrieve(loginRequest.getUserName());
                }

                return new LoginResponse(null, authToken.getAuthToken(), loginRequest.getUserName(), userDAO.retrieve(loginRequest.getUserName()).getPersonID());
            }
        }
    }

    /**
     * Deletes All data from the database, including user accounts, auth tokens and generated person and event data.
     * If error occurs, description is included in response.
     *
     * @return ClearResponse        Responds with a clear response object
     */
    public ClearResponse clear() {
        if(authTokenDAO.clear() && userDAO.clear() && personDAO.clear() && eventDAO.clear())
            return new ClearResponse(CLEAR_SUCCESS);
        else
            return new ClearResponse(CLEAR_ERROR);
    }

    /**
     * Populates the server's database with generated data for the specified user name.
     * The required "username" parameter must be a user already registered with the server. If there is
     * any data in the database already associated with the given user name, it is deleted. The
     * optional generations parameter lets the caller specify the number of generations of ancestors
     * to be generated, and must be a non-negative integer (the default is 4, which results in 31 new
     * persons each with associated events).
     *
     * @param username      The username of the user in the form of a string
     * @param generations   The amount of generations to be filled
     */
    public FillResponse fill(String username, int generations) {
        FillResponse response = new FillResponse(null);

        if (generations < 1) {
            return new FillResponse(INCORRECT_GENERATIONS_PARAM);
        } else if (username == null || username.equals("")) {
            return new FillResponse(INVALID_USERNAME);
        } else if (userDAO.retrieve(username) == null){
            return new FillResponse(NO_USER_ERROR);
        }else {
            userDAO.clearUserData(username);

            newPersonsCount = 0;
            newEventsCount = 0;

            Person person = personDAO.retrieve(userDAO.retrieve(username).getPersonID());
            int birthYear = person.getBirth().getYear();

            boolean success = fillHelp(generations, username, person, birthYear);

            if (success) {
                response.setMessage("Successfully added " + newPersonsCount + " persons and " + newEventsCount + " events to the database.");

                newPersonsCount = 0;
                newEventsCount = 0;
            } else {
                response.setMessage(FILL_ERROR);
                newPersonsCount = 0;
                newEventsCount = 0;
            }

            return response;
        }
    }

    //Used to recursively help the fill function
    private boolean fillHelp(int generationsLeft, String descendantUserName, Person child, int childBirthYear){
        generationsLeft--;

        //Generate personID's for parents
        String manFatherID = Person.randomIDGen();
        String manMotherID = Person.randomIDGen();
        String womanFatherID = Person.randomIDGen();
        String womanMotherID = Person.randomIDGen();

        //create man and woman
        Person man = new Person(child.getFather(), descendantUserName, fNames.getRandomFName(), child.getLastName(), 'm', manFatherID, manMotherID, child.getMother());
        personDAO.add(man);
        newPersonsCount++;
        Person woman = new Person(child.getMother(), descendantUserName, mNames.getRandomMName(), sNames.getRandomSName(), 'f', womanFatherID, womanMotherID, child.getFather());
        personDAO.add(woman);
        newPersonsCount++;

        //Generate man events
        Location birthLoc = locations.getRandomLocation();
        Location baptLoc = locations.getRandomLocation();
        Location marriageLoc = locations.getRandomLocation();
        Location deathLoc = locations.getRandomLocation();
        Event manBirth = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                birthLoc.getLatitude(), birthLoc.getLongitude(), birthLoc.getCountry(), birthLoc.getCity(),
                Event.BIRTH, Event.randomYearGen(childBirthYear - 40, childBirthYear - 30));
        eventDAO.add(manBirth);
        Event manBaptism = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                baptLoc.getLatitude(), baptLoc.getLongitude(), baptLoc.getCountry(), baptLoc.getCity(),
                Event.BAPTISM, Event.randomYearGen(manBirth.getYear() + 8, manBirth.getYear() + 10));
        eventDAO.add(manBaptism);
        Event manMarriage = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                marriageLoc.getLatitude(), marriageLoc.getLongitude(), marriageLoc.getCountry(), marriageLoc.getCity(),
                Event.MARRIAGE, Event.randomYearGen(manBirth.getYear() + 18, childBirthYear));
        eventDAO.add(manMarriage);
        newEventsCount += 3;
        Event manDeath = null;
        if(manBirth.getYear() < Calendar.getInstance().get(Calendar.YEAR) - 90) {     //Always die at 90
            manDeath = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                    deathLoc.getLatitude(), deathLoc.getLongitude(), deathLoc.getCountry(), deathLoc.getCity(),
                    Event.DEATH, manBirth.getYear() + 90);
            eventDAO.add(manDeath);
            newEventsCount++;
        }

        //Generate woman events
        birthLoc = locations.getRandomLocation();
        baptLoc = locations.getRandomLocation();
        deathLoc = locations.getRandomLocation();
        Event womanBirth = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                birthLoc.getLatitude(), birthLoc.getLongitude(), birthLoc.getCountry(), birthLoc.getCity(),
                Event.BIRTH, Event.randomYearGen(childBirthYear - 40, childBirthYear - 30));
        eventDAO.add(womanBirth);
        Event womanBaptism = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                baptLoc.getLatitude(), baptLoc.getLongitude(), baptLoc.getCountry(), baptLoc.getCity(),
                Event.BAPTISM, Event.randomYearGen(manBirth.getYear() + 8, manBirth.getYear() + 10));
        eventDAO.add(womanBaptism);
        Event womanMarriage = new Event(Event.randomIDGen(), descendantUserName, woman.getPersonID(),
                manMarriage.getLatitude(), manMarriage.getLongitude(), manMarriage.getCountry(), manMarriage.getCity(),
                Event.MARRIAGE, manMarriage.getYear());
        eventDAO.add(womanMarriage);
        newEventsCount += 3;
        Event womanDeath = null;
        if(manBirth.getYear() < Calendar.getInstance().get(Calendar.YEAR) - 90) {
            womanDeath = new Event(Event.randomIDGen(), descendantUserName, man.getPersonID(),
                    deathLoc.getLatitude(), deathLoc.getLongitude(), deathLoc.getCountry(), deathLoc.getCity(),
                    Event.DEATH, manBirth.getYear() + 90);
            eventDAO.add(womanDeath);
            newEventsCount++;
        }

        //If last generation, set parents to null
        if(generationsLeft <= 0){
            man.setFather(null);
            man.setMother(null);
            woman.setFather(null);
            woman.setMother(null);

            return true;
        }

        // Continues on with mother and father
        if(fillHelp(generationsLeft, descendantUserName, man, manBirth.getYear()) &&
                fillHelp(generationsLeft, descendantUserName, woman, womanBirth.getYear())){
            return true;
        } else
            return false;
    }

    /**
     * Clears all data from the database (just like the /clear API), and then loads the
     * posted user, person, and event data into the database.
     *
     * @param input     The loadrequest object containing the information on what is to be loaded
     */
    public LoadResponse load(LoadRequest input) {
        boolean success;

        User[] users = input.getUsers();
        Person[] persons = input.getPersons();
        Event[] events = input.getEvents();

        for(User u : users){
            if(!userDAO.add(u))
                return new LoadResponse(INVALID_REQUEST_DATA);
        }
        for(Person p : persons){;
            if(!personDAO.add(p))
                return new LoadResponse(INVALID_REQUEST_DATA);
        }
        for(Event e : events){
            if(!eventDAO.add(e))
                return new LoadResponse(INVALID_REQUEST_DATA);
        }

        return new LoadResponse("Successfully added " + users.length
                + " users, " + persons.length
                + " people, and " + events.length
                + " events to the database.");
    }

    /**
     * ​Returns a single family members of the current user. The current user is determined from the provided auth token and the person from the ID
     * if personID is null, no person was specified
     *
     * @param authToken     The auth token sent by the client in the form of a string. Used to validate the authenticity of the request
     * @param personID      The ID of the person who's information is being requested. If null, return list of all people
     * @return PersonResponse     A PersonResponse object of all the information to be returned
     */
    public PersonResponse person(String authToken, String personID) {
        //updateAuthTokens(authToken);

        if (authTokenDAO.retrieveUsingAuthToken(authToken) != null) {
            if (personID != null) {      //If ID is given
                if(authTokenDAO.retrieveUsingAuthToken(authToken).getUsername().equals(personDAO.retrieve(personID).getDescendant())) {          //If authtoken user matches descendant
                    AuthToken authTokenObject = authTokenDAO.retrieveUsingAuthToken(authToken);
                    Person person = personDAO.retrieve(personID);

                    return new PersonResponse(null, null, authTokenObject.getUsername(),
                            person.getPersonID(), person.getFirstName(), person.getLastName(), person.getGender(),
                            person.getFather(), person.getMother(), person.getSpouse());
                } else{     //If authtoken doesn't user match descendant
                    return new PersonResponse(INVALID_DATA_ACCESS, null, null, null, null, null, ' ', null, null, null);
                }
            } else {         //If ID is not given
                return new PersonResponse(null, personDAO.retrievePersons(authTokenDAO.retrieveUsingAuthToken(authToken).getUsername()), null, null, null, null, ' ', null, null, null);
            }
        } else {
            return new PersonResponse(AUTH_TOKEN_ERROR, null, null, null, null, null, ' ', null, null, null);
        }
    }

    /**
     * ​Returns the single Event object with the specified ID. If eventID is Null, no event was specified
     * @param eventID       The event Id of the event to be returned. If null, return all events.
     * @param authToken     The auth token sent by the client in the form of a string. Used to validate the authenticity of the request
     * @return EventResponse    An EventResponse with all the information requested.
     */
    public EventResponse event(String authToken, String eventID) {
        //updateAuthTokens(authToken);

        if(authTokenDAO.retrieveUsingAuthToken(authToken) != null){         //If authtoken is valid
            if(eventID != null) {      //If ID is given

                if(authTokenDAO.retrieveUsingAuthToken(authToken).getUsername().equals(eventDAO.retrieve(eventID).getDescendant())) {      //If authtoken user matches descendant
                    AuthToken authTokenObject = authTokenDAO.retrieveUsingAuthToken(authToken);
                    Event event = eventDAO.retrieve(eventID);

                    return new EventResponse(null, null, authTokenDAO.retrieveUsingAuthToken(authToken).getUsername(),
                            event.getEventID(), event.getPersonID(), Double.toString(event.getLatitude()), Double.toString(event.getLongitude()), event.getCountry(), event.getCity(),
                            event.getEventType(), event.getYear());
                } else {     //if authtoken user doesn't match descendant
                    return new EventResponse(INVALID_DATA_ACCESS, null, null, null, null,
                            null, null, null, null, null, 0);
                }
            } else{         //If ID is not given
                return new EventResponse(null, eventDAO.retrieveEvents(authTokenDAO.retrieveUsingAuthToken(authToken).getUsername()),
                        null, null, null,
                        null, null, null, null, null, 0);
            }
        } else{         //If authtoken isn't valid
            return new EventResponse(AUTH_TOKEN_ERROR, null, null, null, null,
                    null, null, null, null, null, 0);
        }
    }

    private AuthToken updateAuthTokens(String authToken){
        AuthToken[] authTokens = authTokenDAO.retrieveAuthTokens();
        AuthToken authTokenObj = authTokenDAO.retrieveUsingAuthToken(authToken);

        for(AuthToken a : authTokens){
            if(a.getAuthToken().equals(authToken)) {
                if (LocalTime.now().isBefore(a.getTime().plusMinutes(60)))
                    return authTokenDAO.authTokenRefresh(a.getUsername());
                else
                    authTokenDAO.delete(a.getAuthToken());
            }
        }
        return null;
    }
}
