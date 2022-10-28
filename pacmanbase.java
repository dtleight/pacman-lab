import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

// last updated 2/2020
abstract class pacmanbase extends JFrame implements KeyListener
{
    /* default values: */
    protected int mheight = 41;    // default height and width of maze
    protected int mwidth = 51;

    protected byte[][] M;    // the array for the maze
    public static final int SOUTH = 0;
    public static final int EAST = 1;
    public static final int NORTH = 2;
    public static final int WEST = 3;

    protected boolean showvalue = false; // affects drawblock
    protected boolean autodelay = true;  // delays automatically between drawdot
    protected boolean usegif = true;

    // graphical properties:
    protected static int bh = 24;     // height of a graphical block 24
    protected static int bw = 24;    // width of a graphical block 24
    protected int ah, aw;    // height and width of graphical maze
    protected int yoff = 42;    // init y-cord of maze
    protected Graphics g;
    protected int dtime = 100;   // ms delay time (for autodelay)
    protected Color wallcolor = Color.blue;
    protected Color pathcolor = Color.black;
    protected Color dotcolor = Color.red;
    protected Color pencolor = Color.yellow;    
    protected Image animatedgif;
    protected String gifname = "pacman.gif";
    protected static String filepath = "Mazes/pacmaze.txt";

    // constructor, args determine block size, maze height, and maze width
    public pacmanbase(int bh0, int mh0, int mw0)
    { 
        bh = bw = bh0;  mheight = mh0;  mwidth = mw0;
        ah = bh*mheight;
        aw = bw*mwidth;
        M = new byte[mheight][mwidth];  // initialize maze (all  0's - walls).
        this.setBounds(0,0,aw+10,10+ah+yoff);    
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        try{Thread.sleep(500);} catch(Exception e) {} // Synch with system
        g = getGraphics();    //g.setColor(Color.red);
        setup();
    }

    // utility to load animated gif, called from setup after customize()
    protected void loadgif(String filename)
    {
        try {
            animatedgif = Toolkit.getDefaultToolkit().getImage(filename);
            prepareImage(animatedgif,this);
            Thread.sleep(100); // Synch with system
        } catch(Exception e) {animatedgif=null; usegif=false;} 
    }//loadgif

    public void paint(Graphics g) {} // override automatic repaint

    public void setup()
    {
        customize(); // optional startupcode       
        g.setColor(wallcolor);
        g.fill3DRect(0,yoff,aw,ah,true);  // fill raised rectangle
        g.setColor(pathcolor);
        try {
            g.setFont(new Font("Serif",Font.BOLD,bh*3/4));      // might not work
        } catch(Exception gfe) {}
        customize(); // optional startupcode
        loadgif(gifname);
        generateMaze(filepath); //Generate the maze
        solve();  //Search for a goal node
    }   

    public void delay(int ms)
    {   
        try {Thread.sleep(ms);} catch(Exception e) {}
    }

    public void drawblock(int y, int x)
    {
        g.setColor(pathcolor);
        g.fillRect(x*bw,yoff+(y*bh),bw,bh);
        g.setColor(pencolor);
        // following line displays value of M[y][x] in the graphical maze:
        if (showvalue)
            g.drawString(""+M[y][x],(x*bw)+(bw/2-4),yoff+(y*bh)+(bh/2+6));
    }

    public void drawdot(int y, int x)
    {

        g.setColor(dotcolor);
        g.fillOval(x*bw,yoff+(y*bh),bw,bh);
        if (autodelay) try{Thread.sleep(dtime);} catch(Exception e) {} 
    }

    public void drawpacman(int y, int x)
    {
        if (usegif && animatedgif!=null)
        {
            g.drawImage(animatedgif,x*bw,yoff+(y*bh),bw,bh,null);
        }
        if (autodelay) try{Thread.sleep(dtime);} catch(Exception e) {} 
    }

    public void drawpellet(int y, int x)
    {
        g.setColor(Color.white);
        g.fillOval(x*bw + (bw/3),yoff+y*bh + (bh/3),bw/3,bh/3);
    }

    public void drawgif(int y, int x) { drawdot(y,x); }  //alias
    public void drawMessage(String m)
    {
        g.setColor(wallcolor);
        g.fillRect(0,yoff,bw*mwidth,bh);
        g.setColor(pencolor); // erase line
        g.drawString(m,10,yoff+bh-4);    
    }

    ////// the following functions are to be overriden in subclass:

    abstract void customize();//This sets up maze generation parameters

    /* Write a search function to solve the maze.
     */
    abstract void solve(); // solve

    public void play() 
    {
        // code to setup game
    }
    // for this part you may also define some other instance vars outside of
    // the play function.

    // skeleton implementation of KeyListener interface
    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) // override for key event handling
    {
        int key = e.getKeyCode();       // code for key pressed      
        System.out.println("YOU JUST PRESSED KEY "+key);
    }
    protected Map<String, Node> maze_graph;
    protected  String [][] mazeArray;
    protected  Node startNode = null;
    protected Node goalNode = null;
    protected ArrayList<Node> goals = new ArrayList<Node>();
    /**
     * Pacman Maze Generation Code
     * 
     * This method parses a maze at a given file path.
     * The mazes can be created with the following characters
     *  '-' : walls
     *  '*' : empty space
     *  's' : start space
     *  'd' : dot
     */
    public  void generateMaze(String filepath)
    {
        //Parse file into 2D array
        maze_graph= new HashMap<String,Node>();
        mazeArray = new String[9][28];
        try
        {
            Scanner sc = new Scanner(new BufferedReader(new FileReader(filepath)));
            while(sc.hasNextLine()) {
                for (int i=0; i<mazeArray.length; i++) {
                    String[] line = sc.nextLine().trim().split("");
                    for (int j=0; j<line.length; j++) {
                        mazeArray[i][j] = line[j];
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //DX,DY vectors for determining adjacency
        int[] DX = { 0, 1, 0, -1};
        int[] DY = {1, 0, -1, 0};

        //Naive graph generation algorithm: O(8n^2)
        int rows = 9;
        int columns = 28;
        for(int tries = 0; tries < 2; tries++)
        {
            for(int i = 0; i < rows; i++)
            {
                for(int j = 0; j < columns; j++)
                {
                    for(int k = 0; k <4; k++) //DX,DY vector
                    {
                        if(inBounds(i+DX[k],rows) && inBounds(j+DY[k],columns))
                        { 
                            String coord = coordify(i+DX[k], j+DY[k]);
                            int x = i + DX[k];
                            int y = j + DY[k];
                            if(maze_graph.containsKey(coordify(i,j)))
                            {
                                if(mazeArray[x][y].equals("*")|| mazeArray[x][y].equals("s") || mazeArray[x][y].equals("d"))  
                                {
                                    Node currNode;
                                    if(maze_graph.get(coord) != null)
                                    {
                                        currNode = maze_graph.get(coord);
                                    }
                                    else
                                    {
                                        currNode = new Node(i,j,coord, mazeArray[x][y]);
                                    }
                                    maze_graph.get(coordify(i,j)).addNeighbor(4-k, currNode);
                                }
                            }
                        }

                    }
                    maze_graph.putIfAbsent(coordify(i,j), new Node(i,j,coordify(i,j), mazeArray[i][j]));
                    if(mazeArray[i][j].equals("s") || mazeArray[i][j].equals("*") || mazeArray[i][j].equals("d"))
                    {
                        M[i][j] = 1;
                        drawblock(i,j);
                    }
                    if(mazeArray[i][j].equals("s"))
                    {
                        startNode = maze_graph.get(coordify(i,j));
                        drawdot(i,j);
                    }
                    if(mazeArray[i][j].equals("d"))
                    {
                        goalNode = maze_graph.get(coordify(i,j));
                        goals.add(goalNode);
                        drawpellet(i,j);
                    }
                }
            }
        }
    }

    /**
     * Convert two integers into a coordinate string
     **/
    public String coordify(int i, int k)
    {
        return "(" + i + "," + k + ")";
    }

    /**
     * Check if a coordinate is within the bounds of the matrix.
     */
    public boolean inBounds(int i, int j)
    {
        return i < j && i >= 0;
    }

    public ArrayList<Node> permutate(ArrayList<Node> arr)
    {
        //Permutation generation from printout - working
        //delay(100);
        for(int i = 0; i < arr.size(); i++)
        {
            int r = i + (int)(Math.random() * arr.size() - i); // r is between i and P.length -1
            Node temp = arr.get(i);
            arr.set(i,arr.get(i));
            arr.set(i,temp);
        }
        return arr;
    }

    /**
     * Main code
     */
    public static void main(String[] args) throws Exception
    {
        if(args.length > 0)
        {
             filepath = args[0];  
        }
        pacmanbase W = new studentcode(bh,9,29);
    }//main

} // pacmanbase
