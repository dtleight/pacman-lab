public class PriorityQueue<T>
{
    class PriorityNode<ST> //This class wraps each queue item giving them a priority and an item.
    {
        protected ST item;
        protected int priority;
        PriorityNode(ST item, int priority)
        {
            this.item = item;
            this.priority = priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

    PriorityNode<T>[] items;
    int size;
    PriorityQueue(int cap)
    {
        items = makeTArray(cap);
    }

    /**
     * //This avoids a nuance in the Java language that doesn't allow generic arrays using typecasting.
     */
    PriorityNode<T>[] makeTArray(int cap)
    {
        return (PriorityNode<T>[]) new Object[cap];
    }


    void resize(int newcap){
        PriorityNode<T>[] temp = makeTArray(newcap);
        System.arraycopy(items, 0, temp, 0, size);
        this.items = temp;
    };

    int parent(int i) { return (i-1)/2;}
    int left(int i) { return (2*i)+1;}
    int right(int i){return (2*i)+2;}

    T peek() throws Exception
    {
        if(size > 0)
            return items[0].item;
        else
        {
            throw new Exception("Stack Underflow");
        }
    }

    public void push(T item, int priority)
    {
        
    }

    public void heapify()
    {

    }

    public boolean swapUp()
    {
        return false;
    }

    public boolean swapDown()
    {
        return false;
    }
}