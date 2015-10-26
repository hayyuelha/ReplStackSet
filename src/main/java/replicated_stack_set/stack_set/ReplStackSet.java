package replicated_stack_set.stack_set;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class ReplStackSet extends ReceiverAdapter {
	JChannel channel;
    String user_name = System.getProperty("user.name", "n/a");
    Stack<Integer> stack = new Stack<Integer>();
    Set<Integer> set = new HashSet<Integer>();
    boolean isStack = false;
    boolean isSet = false;

    private void start(String collectionType) throws Exception {
        if (collectionType.equals("stack")) {
        	isStack = true;
        } else {
        	isSet = true;
        }
    	channel=new JChannel(); // use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
//        System.out.println(msg.getSrc() + ": " + msg.getObject());
        Integer retval;
        boolean retval2;
        String[] opt = msg.getObject().toString().split("\\s+");
        String output = "";
        if (isStack) {
        	synchronized(stack) {
        		switch (opt[0]) {
        		case "push" :
        			stack.push(Integer.valueOf(opt[1]));
        			break;
        		case "pop" :
        			retval = stack.pop();
        			break;
        		case "top" :
        			retval = stack.peek();
        			System.out.println("Top value is " + retval);
        			break;
        		default :
        			break;
        		}
        		System.out.println(stack.toString());
            }
        } else if (isSet) {
        	synchronized(set) {
        		switch (opt[0]) {
        		case "add" :
        			retval2 = set.add(Integer.valueOf(opt[1]));
        			break;
        		case "contains" :
        			retval2 = set.contains(Integer.valueOf(opt[1]));
        			if (retval2) {
        				System.out.println(opt[1] + " is exist in set.");
        			} else {
        				System.out.println(opt[1] + " is not exist in set.");
        			}
        			break;
        		case "remove" :
        			retval2 = set.remove(Integer.valueOf(opt[1]));
        			break;
        		default :
        			break;
        		}
        		System.out.println(set.toString());
        	}
        }
        
        
    }
    
    public void getState(OutputStream output) throws Exception {
        if (isStack) {
        	synchronized(stack) {
                Util.objectToStream(stack, new DataOutputStream(output));
            }
        } else if (isSet) {
        	synchronized(set) {
                Util.objectToStream(set, new DataOutputStream(output));
            }
        }
    	
    }
    
    public void setState(InputStream input) throws Exception {
    	if (isStack) {
    		Stack<Integer> stackCopied = (Stack<Integer>)Util.objectFromStream(new DataInputStream(input));
            synchronized(stack) {
            	// TODO copy stack
            	stack.clear();
            	stack.addAll(stackCopied);
            }
    	} else if (isSet) {
    		Set<Integer> setCopied = (Set<Integer>)Util.objectFromStream(new DataInputStream(input));
    		synchronized(set) {
    			set.clear();
    			set.addAll(setCopied);
    		}
    	}
    	
    }

    private void eventLoop() {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line=in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;
                
	            Message msg=new Message(null, null, line);
                channel.send(msg);
            }
            catch(Exception e) {
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    	String type = input.readLine();
    	new ReplStackSet().start(type);
    }
}
