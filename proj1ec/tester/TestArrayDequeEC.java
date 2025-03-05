package tester;
import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;
public class TestArrayDequeEC {
    @Test
    public void myTest() {
        StudentArrayDeque<Integer> integerStudentArrayDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> integerArrayDequeSolution = new ArrayDequeSolution<>();
        String message = "";
        while (true) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                //addFirst
                Integer randVal = StdRandom.uniform(0, 100);
                integerArrayDequeSolution.addFirst(randVal);
                integerStudentArrayDeque.addFirst(randVal);
                message = message + "addFirst(" + randVal + ")\n";
                assertEquals(message, randVal, randVal);
            } else if (operationNumber == 1) {
                //addLast
                Integer randVal = StdRandom.uniform(0, 100);
                integerArrayDequeSolution.addLast(randVal);
                integerStudentArrayDeque.addLast(randVal);
                message = message + "addLast(" + randVal + ")\n";
                assertEquals(message, randVal, randVal);
            } else if (operationNumber == 2) {
                //removeFirst
                if (integerArrayDequeSolution.size() == 0 || integerStudentArrayDeque.size() == 0) {
                    continue;
                }
                Integer i = integerArrayDequeSolution.removeFirst();
                Integer i1 = integerStudentArrayDeque.removeFirst();
                message = message + "removeFirst()\n";
                assertEquals(message, i, i1);
            } else {
                //removeLast
                if (integerArrayDequeSolution.size() == 0 || integerStudentArrayDeque.size() == 0) {
                    continue;
                }
                Integer i = integerArrayDequeSolution.removeLast();
                Integer i1 = integerStudentArrayDeque.removeLast();
                message = message + "removeLast()\n";
                assertEquals(message, i, i1);
            }
        }
    }
}
