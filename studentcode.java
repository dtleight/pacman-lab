import java.awt.*;
import java.util.*;

class studentcode extends pacmanbase {
    public studentcode(int bh, int mh, int mw) // don't change constructor
    {
        super(bh, mh, mw);
    }

    public void search() {
        autodelay = true;
        showvalue = true;
        super.search();
    }

    public void randomSearch(Node currentNode) {
        Random random = new Random();
        while (!currentNode.equals(goalNode)) {
            ArrayList<Node> neighbors = currentNode.getNeighbors();
            M[currentNode.x][currentNode.y] += 1;
            drawblock(currentNode.x, currentNode.y);
            if (neighbors.contains(goalNode)) {
                currentNode = goalNode;
            } else {
                currentNode = neighbors.get(random.nextInt(neighbors.size()));
            }
            drawblock(currentNode.x, currentNode.y);
            drawpacman(currentNode.x, currentNode.y);
        }
    }

    public void dfsSearch(Node currentNode) {
        
    }

    public void bfsSearch(Node currentNode) {
    }

    public void customize() {
        wallcolor = Color.blue; // you need to import java.awt.Graphics;
        pathcolor = Color.black; // look in superclass for available colors
        dotcolor = Color.red; // color of solid circle (if !usegif)
        pencolor = Color.yellow; // color of text
        dtime = 100; // 30 ms default delay time -applies to drawdot only
        showvalue = false; // affects drawblock
        autodelay = true; // delays automatically after drawdot by dtime. But you can also set this to
                          // false and call delay(ms) yourself. This variable does not affect the
                          // drawblock function (and so does not affect digout).
        usegif = true; // if true, replaces dot with (animated) gif image.
        gifname = "assets/pacman.gif"; // filename of gif image in same directory
    }
}// studentcode subclass

/**
 * This class represents a graph node. It stores adjacent nodes in an array
 * based off of their direction.
 * North: 3
 * East: 2
 * South: 1
 * West: 0
 */
class Node {
    Node[] adjacencies = { null, null, null, null };
    String representation; // Default space to unfilled
    int x;
    int y;
    int weight;
    String coordinate;

    public Node(int x, int y, String coordinate, String representation) {
        this.x = x;
        this.y = y;
        this.coordinate = coordinate;
        this.weight = MazeComponent.fromString(representation).getWeight();
        this.representation = representation;
    }

    /**
     * This function adds a node to the list of adjacencies at the direction
     * specified.
     */
    public void addNeighbor(int dir, Node node) {
        adjacencies[dir - 1] = node; // Use 0-based indexing
    }

    /**
     * This function returns an arraylist of valid neighbor nodes
     **/
    public ArrayList<Node> getNeighbors() {
        ArrayList<Node> validNodes = new ArrayList<Node>(); // This could be a set
        for (int i = 0; i < adjacencies.length; i++) {
            if (adjacencies[i] != null) {
                validNodes.add(adjacencies[i]);
            }
        }
        return validNodes;
    }

    /**
     * Print out the list of adjacent nodes
     */
    @Override
    public String toString() {
        String output = "Adjacent Nodes: [";
        for (Node n : adjacencies) {
            if (n != null) {
                output += n.coordinate + ", ";
            }
        }
        return output.substring(0, output.length() - 2) + "]";
    }

}