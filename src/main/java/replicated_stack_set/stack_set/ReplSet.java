package replicated_stack_set.stack_set;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class ReplSet<T> extends ReceiverAdapter {
	private JChannel channel;
    private Set<T> set = new HashSet<T>();

    public ReplSet() throws Exception {
    	channel = new JChannel(); // use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("ReplSet");
        channel.getState(null, 10000);
    }
    
    public boolean add(T object) throws Exception {
    	if (this.contains(object)) {
    		return false;
    	}

    	ReplMessage<T> message = new ReplMessage<>(object, ReplMessage.ADD);
    	channel.send(new Message(null, null, message));
    	return true;
    }
    
    public boolean contains(T object) {
    	synchronized(set) {
    		return set.contains(object);
    	}
    }
    
    public boolean remove(T object) throws Exception {
    	if (!this.contains(object)) {
    		return false;
    	}

    	ReplMessage<T> message = new ReplMessage<>(object, ReplMessage.REMOVE);
    	channel.send(new Message(null, null, message));
    	return true;
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        ReplMessage<T> message = (ReplMessage<T>) msg.getObject();
    	synchronized(set) {
    		switch (message.flag) {
	    		case ReplMessage.ADD:
	    			set.add(message.object);
	    			break;
	    		case ReplMessage.REMOVE:
	    			set.remove(message.object);
	    			break;
	    		default :
	    			break;
    		}
    	}
    }
    
    public void getState(OutputStream output) throws Exception {
    	synchronized(set) {
            Util.objectToStream(set, new DataOutputStream(output));
        }
    }
    
    public void setState(InputStream input) throws Exception {
		Set<T> setCopied = (Set<T>)Util.objectFromStream(new DataInputStream(input));
		synchronized(set) {
			set.clear();
			set.addAll(setCopied);
		}
    }
    
    public String print() {
    	synchronized(set) {
    		return set.toString();
    	}
    }
    
    public void close(){
        channel.close();
    }
    
    public static void main(String[] args) throws Exception {
    	ReplSet<Integer> set = new ReplSet<>();
        boolean retval;
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                System.out.print("> "); System.out.flush();
                String line=in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit"))
                    break;

                String[] opt = line.split("\\s+");
        		switch (opt[0]) {
            		case "add" :
            			retval = set.add(Integer.valueOf(opt[1]));
            			break;
            		case "contains" :
            			retval = set.contains(Integer.valueOf(opt[1]));
            			if (retval) {
            				System.out.println(opt[1] + " is exist in set.");
            			} else {
            				System.out.println(opt[1] + " is not exist in set.");
            			}
            			break;
            		case "remove" :
            			retval = set.remove(Integer.valueOf(opt[1]));
            			break;
            		default :
            			break;
        		}
    			Thread.sleep(1000);
        		System.out.println(set.print());
            }
            catch(Exception e) {
            }
        }
        set.close();
    }
}
