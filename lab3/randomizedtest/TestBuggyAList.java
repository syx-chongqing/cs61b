package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> integerAListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> integerBuggyAList = new BuggyAList<>();
        integerBuggyAList.addLast(4);
        integerBuggyAList.addLast(5);
        integerBuggyAList.addLast(6);
        integerAListNoResizing.addLast(4);
        integerAListNoResizing.addLast(5);
        integerAListNoResizing.addLast(6);
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
    }
    @Test
    public void randomTest() {
        AListNoResizing<Integer> integerAListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> integerBuggyAList = new BuggyAList<>();
        for (int i = 0; i < 5000; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                integerAListNoResizing.addLast(randVal);
                integerBuggyAList.addLast(randVal);
            } else if (operationNumber == 1) {
                //size
                assertEquals(integerBuggyAList.size(), integerAListNoResizing.size());
            } else if (operationNumber == 2) {
                //getLast
                if (integerBuggyAList.size() != 0 && integerAListNoResizing.size() != 0) {
                    assertEquals(integerBuggyAList.getLast(), integerAListNoResizing.getLast());
                } else {
                    continue;
                }
            } else {
                //removeLast
                if (integerBuggyAList.size() != 0 && integerAListNoResizing.size() != 0) {
                    assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
                } else {
                    continue;
                }
            }
        }
    }
}
