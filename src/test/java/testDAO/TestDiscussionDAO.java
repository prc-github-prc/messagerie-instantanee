package testDAO;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import static messagerie_instantanee.server.database.DAO.DiscussionDAO.findAllDiscussions;
import messagerie_instantanee.server.models.Discussion;
public class TestDiscussionDAO {
    @Test
    public void testFindAllDiscussion(){
        List<Discussion> lst_discussion = findAllDiscussions();
        assertEquals(lst_discussion.size(), 2);
    }
}