import java.awt.*;
import java.util.*;
class studentcode extends pacmanbase
{
    public studentcode(int bh, int mh, int mw) // don't change constructor
    { 
        super(bh,mh,mw); 
    } 

    public void search()
    {
        autodelay = true;
        Node currentNode = startNode;
        //randomSearch(currentNode);
        //bfsSearch(currentNode);
        dfsSearch(currentNode);
    }

    public void randomSearch(Node currentNode)
    {
        Random random = new Random();
        while(!currentNode.equals(goalNode))
        {
            ArrayList<Node> neighbors = currentNode.getNeighbors();
            drawblock(currentNode.x, currentNode.y);
            if(neighbors.contains(goalNode))
            {
                currentNode = goalNode;
            }
            else
            {
                currentNode = neighbors.get(random.nextInt(neighbors.size()));
            }
            drawblock(currentNode.x, currentNode.y);
            drawpacman(currentNode.x, currentNode.y);
        }
    }
    
     public void dfsSearch(Node currentNode)
    {
        Stack<Node> nodeQueue = new Stack<Node>();
        HashMap<Node,Node> visitedNodes = new HashMap<Node,Node>();
        nodeQueue.add(currentNode);
        showvalue = true;
        while(!currentNode.equals(goalNode))
        {
            drawblock(currentNode.x, currentNode.y);
            currentNode = nodeQueue.pop();
            visitedNodes.put(currentNode,currentNode);
            M[currentNode.x][currentNode.y]+= 1;
            drawpacman(currentNode.x, currentNode.y);
            if(!currentNode.equals(goalNode))
            {
                for(Node n: permutate(currentNode.getNeighbors()))
                {
                    if(!visitedNodes.containsKey(n))
                    {
                        nodeQueue.add(n);
                    }
                }
            }
        }
        System.out.println("Found a solution");
    }
    
    
    public void bfsSearch(Node currentNode)
    {
        Queue<Node> nodeQueue = new Queue<Node>();
        HashMap<Node,Node> visitedNodes = new HashMap<Node,Node>();
        nodeQueue.push(currentNode);
        showvalue = true;
        while(!currentNode.equals(goalNode))
        {
            drawblock(currentNode.x, currentNode.y);
            currentNode = nodeQueue.pop();
            visitedNodes.put(currentNode,currentNode);
            M[currentNode.x][currentNode.y]+= 1;
            drawdot(currentNode.x, currentNode.y);
            if(!currentNode.equals(goalNode))
            {
                for(Node n: currentNode.getNeighbors())
                {
                    if(!visitedNodes.containsKey(n))
                    {
                        nodeQueue.push(n);
                    }
                }
            }
        }
        System.out.println("Found a solution");
    }

    public void customize()
    {
        wallcolor = Color.blue; // you need to import java.awt.Graphics;
        pathcolor = Color.black; // look in superclass for available colors
        dotcolor = Color.red; // color of solid circle (if !usegif)
        pencolor = Color.yellow; // color of text
        dtime = 200; // 30 ms default delay time -applies to drawdot only
        showvalue = true; // affects drawblock
        autodelay = true; // delays automatically after drawdot by dtime. But you can also set this to false and call delay(ms) yourself. This variable does not affect the drawblock function (and so does not affect digout).
        usegif=true; // if true, replaces dot with (animated) gif image.
        gifname ="pacman.gif"; // filename of gif image in same directory
    }
}//studentcode subclass

/**
 * This class represents a graph node. It stores adjacent nodes in an array 
 * based off of their direction.
 * North: 3
 * East:  2
 * South: 1
 * West:  0
 */
class Node
{
    Node[] adjacencies = {null,null,null,null};
    String representation; //Default space to unfilled
    int x; int y;
    String coordinate;

    public Node(int x, int y,String coordinate, String representation)
    {
        this.x = x;
        this.y = y;
        this.coordinate = coordinate;
        this.representation = representation;
    }

    /**
     * This function adds a node to the list of adjacencies at the direction specified.
     */
    public void addNeighbor(int dir, Node node)
    {
        adjacencies[dir-1] = node; //Use 0-based indexing
    }

    /**
     * This function returns the indexes of valid neighbor nodes
     **/
    public ArrayList<Node> getNeighbors()
    {
        ArrayList<Node> validNodes = new ArrayList<Node>(); //This could be a set
        for(int i = 0; i < adjacencies.length; i++)
        {
            if(adjacencies[i] != null)
            {
                validNodes.add(adjacencies[i]);
            }
        }
        return validNodes;
    }

    /**
     * Print out the list of adjacent nodes
     */
    @Override
    public String toString()
    {
        String output = "Adjacent Nodes: [";
        for(Node n: adjacencies)
        {
            if(n!= null)
            {
                output += n.coordinate + ", ";
            }
        }
        return output.substring(0,output.length() -2) + "]";
    }

}