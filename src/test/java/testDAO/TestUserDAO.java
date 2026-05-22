package testDAO;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import static messagerie_instantanee.server.database.DAO.UserDAO.findUserByUsername;
import messagerie_instantanee.server.models.User;
public class TestUserDAO {
    

    @Test
    public void testFindByUsername(){
        User user = findUserByUsername("admin");
        assertEquals(user.getUsername(), "admin");
        assertEquals(user.getPassword(), "root");
    }
}
