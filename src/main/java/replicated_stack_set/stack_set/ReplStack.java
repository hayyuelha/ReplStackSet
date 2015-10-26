package replicated_stack_set.stack_set;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Stack;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class ReplStack<T> extends ReceiverAdapter {
	private JChannel channel;
    private final Stack<T> stack;

    public ReplStack() throws Exception {
    	stack  = new Stack<>();
    	channel = new JChannel(); // use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("ReplStack");
        channel.getState(null, 10000);
    }
    
    public void push(T object) throws Exception {
    	ReplMessage<T> message = new ReplMessage<>(object, ReplMessage.PUSH);
    	channel.send(new Message(null, null, message));
    }
    
    public T pop() throws Exception {
    	T retval = this.top();
    	
    	ReplMessage<T> message = new ReplMessage<>(ReplMessage.POP);
    	channel.send(new Message(null, null, message));
    
    	return retval;
    }
    
    public T top() throws Exception {
    	if (stack.isEmpty()) {
    		throw new Exception("Stack is empty");
    	}
    	synchronized(stack) {
    		return stack.peek();
    	}
    }

    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        ReplMessage<T> message = (ReplMessage) msg.getObject();
    	synchronized(stack) {
    		switch (message.flag) {
	    		case ReplMessage.PUSH :
	    			stack.push(message.object);
	    			break;
	    		case ReplMessage.POP :
	    			stack.pop();
	    			break;
	    		default :
	    			break;
    		}
        }
    }
    
    public void getState(OutputStream output) throws Exception {
    	synchronized(stack) {
            Util.objectToStream(stack, new DataOutputStream(output));
        }
    }
    
    public void setState(InputStream input) throws Exception {
		Stack<T> stackCopied = (Stack<T>)Util.objectFromStream(new DataInputStream(input));
        synchronized(stack) {
        	stack.clear();
        	stack.addAll(stackCopied);
        }    	
    }
    
    public String print() {
    	synchronized(stack) {
    		return stack.toString();
    	}
    }
    
    public void close() {
        channel.close();
    }
    
    public static void main(String[] args) throws Exception {
    	ReplStack<Integer> stack = new ReplStack<>();
        Integer retval;
    	BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line = in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                String[] opt = line.split("\\s+");
        		switch (opt[0]) {
	        		case "push" :
	        			stack.push(Integer.valueOf(opt[1]));
	        			break;
	        		case "pop" :
	        			retval = stack.pop();
	        			break;
	        		case "top" :
	        			retval = stack.top();
	        			System.out.println("Top value is " + retval);
	        			break;
	        		default :
	        			break;
        		}
    			Thread.sleep(1000);
        		System.out.println(stack.print());
            }
            catch(Exception e) {
            	System.out.println(e.getMessage());
            }
        }
        stack.close();
    }
}
