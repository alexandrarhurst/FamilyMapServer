package test.DataAccessTest;

import DataAccess.PersonDAO;
import Model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class PersonDAOTest {

    PersonDAO dao = new PersonDAO();

    @Before
    public void setUp() throws Exception {
        dao = new PersonDAO();

        dao.clear();
    }

    @After
    public void tearDown() throws Exception {
        dao.clear();

        dao = null;
    }

    @Test
    public void retrieve() throws Exception {
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        dao.add(person);

        Person retrievedPerson = dao.retrieve("personID");

        // check that retrieve worked
        assert(retrievedPerson != null);
        assert(retrievedPerson.getPersonID().equals("personID"));
        assert(retrievedPerson.getDescendant().equals("descendant"));
        assert(retrievedPerson.getFirstName().equals("firstName"));
        assert(retrievedPerson.getLastName().equals("lastName"));
        assert(retrievedPerson.getGender() == 'm');
        assert(retrievedPerson.getFather().equals("father"));
        assert(retrievedPerson.getMother().equals("mother"));
        assert(retrievedPerson.getSpouse().equals("spouse"));
    }

    @Test
    public void retrieveNegative() throws Exception {
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        dao.add(person);

        Person retrievedPerson = dao.retrieve("personIDNonExist");
        assert(retrievedPerson == null);
        retrievedPerson = dao.retrieve("");
        assert(retrievedPerson == null);
        retrievedPerson = dao.retrieve(null);
        assert(retrievedPerson == null);

    }

    @Test
    public void retrievePersons() throws Exception {
        Person person1 = new Person("personID1", "descendant",
                "firstName1", "lastNam1e", 'm',
                "father1", "mother1", "spouse1");
        Person person2 = new Person("personID2", "descendant",
                "firstName2", "lastName2", 'm',
                "father2", "mother2", "spouse2");

        // add person of different descendant
        Person diffPerson = new Person("diffPersonID", "diffDescendant",
                "firstName2", "lastName2", 'm',
                "father2", "mother2", "spouse2");

        dao.add(person1);
        dao.add(person2);
        dao.add(diffPerson);

        Person[] persons = dao.retrievePersons("descendant");

        // check that retrievePersons works
        assert(persons != null);
        // check that 3 persons weren't received
        assert(persons.length == 2);
        // check that data is accurate
        assert(persons[0].getPersonID().equals("personID1"));
        assert(persons[1].getPersonID().equals("personID2"));
    }

    @Test
    public void retrievePersonsNegative() throws Exception {
        Person person1 = new Person("personID1", "descendant",
                "firstName1", "lastNam1e", 'm',
                "father1", "mother1", "spouse1");
        Person person2 = new Person("personID2", "descendant",
                "firstName2", "lastName2", 'm',
                "father2", "mother2", "spouse2");

        // add person of different descendant
        Person diffPerson = new Person("diffPersonID", "diffDescendant",
                "firstName2", "lastName2", 'm',
                "father2", "mother2", "spouse2");

        dao.add(person1);
        dao.add(person2);
        dao.add(diffPerson);

        Person[] persons = dao.retrievePersons("descendantNonExist");
        assert(persons == null);
        persons = dao.retrievePersons("");
        assert(persons == null);
        persons = dao.retrievePersons(null);
        assert(persons == null);
    }

    @Test
    public void add() throws Exception {
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        assert(dao.add(person));
    }

    @Test
    public void addNegative() throws Exception {
        Person person = new Person(null, null, null, null, ' ', null, null, null);

        assertFalse(dao.add(person));
        assertFalse(dao.add(null));
    }

    @Test
    public void addDataValidation() throws Exception {
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        dao.add(person);

        /**
         * checks that personID must be unique
         */
        assert(!dao.add(person));

        /**
         * checks that gender must be 'm' or 'f'
         */
        assert(!dao.add(new Person("personIDBadGender", "descendant",
                "firstName", "lastName", 'a',
                "father", "mother", "spouse")));
    }

    /**
     * Tests the functionality of the clear method. clearNegative not needed.
     * @throws Exception
     */
    @Test
    public void clear() throws Exception {
        Person person = new Person("personID", "descendant",
                "firstName", "lastName", 'm',
                "father", "mother", "spouse");

        dao.add(person);

        // clear works
        assert(dao.clear());
        //actually cleared data
        assert(dao.retrieve("personID") == null);
    }

}