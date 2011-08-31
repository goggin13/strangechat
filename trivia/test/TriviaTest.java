import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class TriviaTest extends UnitTest {

    @Test
    public void testBasics() {
    	Category c = Category.getOrCreate("Entertainment");
    	List<Answer> answers = new LinkedList<Answer>();
    	answers.add(new Answer("answer isnt a", false));
    	answers.add(new Answer("answer isnt b", false));
    	answers.add(new Answer("answer is c", true));
    	answers.add(new Answer("answer isnt d", false));
    	Question q = new Question("What is the answer?", answers, c);
    	
    	assertFalse(q.isCorrect('a'));
    	assertFalse(q.isCorrect('b'));
    	assertTrue(q.isCorrect('c'));
    	assertFalse(q.isCorrect('d'));
    	assertEquals("Entertainment", q.category.name);
    }

}
