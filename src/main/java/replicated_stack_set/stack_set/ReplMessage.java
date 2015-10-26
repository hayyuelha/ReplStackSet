package replicated_stack_set.stack_set;

import java.io.Serializable;

public class ReplMessage<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3774342696645139392L;
	public static final int PUSH = 1;
	public static final int POP = 2;
	public static final int ADD = 3;
	public static final int REMOVE = 4;

	public T object;
	public int flag;
	
	public ReplMessage(int flag) {
		this.flag = flag;
	}
	
	public ReplMessage(T object, int flag) {
		this.object = object;
		this.flag = flag;
	}
}
