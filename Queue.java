import java.util.*;
public class Queue<T>
{
    java.util.Queue<T> queue = new LinkedList<T>();
    public Queue()
    {

    }

    public T pop() { return queue.remove();} 
    
    public void push(T t)
    {
        queue.add(t);
    }
}
