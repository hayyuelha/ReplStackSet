package replicated_stack_set.stack_set;

import junit.framework.TestCase;

public class AppTest extends TestCase {
	
	public void testSet() {
		try {
			ReplSet<Integer> set1 = new ReplSet<>();
			ReplSet<Integer> set2 = new ReplSet<>();
			set1.add(5);
			Thread.sleep(1000);
			assertEquals("check equal sets", set1.print(), set2.print());
			assertEquals("add existing element", false, set2.add(5));
			Thread.sleep(1000);
			assertEquals("check set1 contain", true, set1.contains(5));
			assertEquals("check set2 contain", true, set2.contains(5));
			assertEquals("check set1 not contain", false, set1.contains(6));
			assertEquals("check set2 not contain", false, set2.contains(6));
			assertEquals("remove element not exist", false, set2.remove(6));
			Thread.sleep(1000);
			assertEquals("remove element", true, set2.remove(5));
			Thread.sleep(1000);
			assertEquals("check equal sets", set1.print(), set2.print());
			set1.close();
			set2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testStack() {
		try {
			ReplStack<Integer> stack1 = new ReplStack<>();
			ReplStack<Integer> stack2 = new ReplStack<>();
			stack1.push(5);
			Thread.sleep(1000);
			assertEquals("check equal stacks", stack1.print(), stack2.print());
			assertEquals("check stack1 top", new Integer(5), stack1.top());
			assertEquals("check stack2 top", new Integer(5), stack2.top());
			stack1.push(6);
			Thread.sleep(1000);
			assertEquals("pop", new Integer(6), stack2.pop());
			Thread.sleep(1000);
			assertEquals("check stack1 top", new Integer(5), stack1.top());
			assertEquals("check stack2 top", new Integer(5), stack2.top());
			stack1.close();
			stack2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
