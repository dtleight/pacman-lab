import java.awt.*;
import java.awt.Graphics;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.geom.AffineTransform;

enum SearchType
{
    Random,
    DFS,
    BFS

}

abstract class pacmanbase extends JFrame
{
    /* default values: */
    protected int mheight = 41;    // default height and width of maze
    protected int mwidth = 51;

    protected int maze_height;
    protected int maze_width;

    protected byte[][] M;    // the array for the maze
    public static final int SOUTH = 0;
    public static final int EAST = 1;
    public static final int NORTH = 2;
    public static final int WEST = 3;

    protected boolean showvalue = false; // affects drawblock
    protected boolean autodelay = true;  // delays automatically between drawdot
    protected boolean usegif = true;
    protected static SearchType searchType = SearchType.Random;

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
    protected Image mirroredGif;
    protected String gifname = "assets/pacman.gif";
    protected static String filepath = "Mazes/pacmaze.txt";

    // constructor, args determine block size, maze height, and maze width
    public pacmanbase(int bh0, int mh0, int mw0)
    { 
        bh = bw = bh0;  mheight = mh0;  mwidth = mw0;
        ah = bh*mheight;
        aw = bw*mwidth;
        maze_height = mh0;
        maze_width = mw0;
        M = new byte[mheight][mwidth];  // initialize maze (all  0's - walls).
        this.setBounds(0,0,aw+10,10+ah+yoff);    
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try{Thread.sleep(500);} catch(Exception e) {} // Synch with system
        g = getGraphics();    //g.setColor(Color.red);
        setup();
    }

    // utility to load animated gif, called from setup after customize()
    protected void loadgif(String filename)
    {
        try {
            animatedgif = Toolkit.getDefaultToolkit().getImage(filename);
            mirroredGif = Toolkit.getDefaultToolkit().getImage(filename).getScaledInstance(-bw, -bh,Image.SCALE_DEFAULT);
            prepareImage(animatedgif,this);
            prepareImage(mirroredGif, this);
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
            g.setFont(new Font("Serif",Font.BOLD,bh*3/4));
        } catch(Exception gfe) {}
        loadgif(gifname);
        generateMaze(filepath); //Generate the maze
        search();  //Search for a goal node
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
            g.drawImage(animatedgif,x*bw, yoff+(y*bh),bw,bh,null);
        }
        if (autodelay) try{Thread.sleep(dtime);} catch(Exception e) {} 
    }

    //Rotate pacman
    public void rotate(int y, int x)
    {
        
        AffineTransform affineTransform = new AffineTransform(); 
        //rotate the image by 45 degrees 
        Image image2 = animatedgif.getScaledInstance(bh*100, bw*100, Image.SCALE_DEFAULT);
        Graphics2D graphics2d = (Graphics2D) g;
        affineTransform.rotate(Math.toRadians(45), -100, -100); 
    }

    public void drawpellet(int y, int x)
    {
        g.setColor(Color.white);
        g.fillOval(x*bw + (bw/3),yoff+y*bh + (bh/3),bw/3,bh/3);
    }
    
    public void drawpowerpellet(int y, int x)
    {
        g.setColor(Color.white);
        g.fillOval(x*bw + bw/3 ,yoff+y*bh - bh/3 + (bh/2),bw/2,bh/2);
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

    /**
     * Determine which search algorithm to run.
     **/
    public void search()
    {
        Node currentNode = startNode;
        switch(searchType)
        {
            case Random: randomSearch(currentNode);
            case DFS: dfsSearch(currentNode);
            case BFS: bfsSearch(currentNode);
        }
    } // solve

    abstract void randomSearch(Node currentNode);
    abstract void dfsSearch(Node currentNode);
    abstract void bfsSearch(Node currentNode);

    public void playback()
    {
        //Pop direction off stack;

    }

    //Pac-Man specific instance variables
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
     *  'o' : power dot
     *  'g' : ghost - not added yet
     */
    public  void generateMaze(String filepath)
    {
        //Parse file into 2D array
        maze_graph= new HashMap<String,Node>();
        mazeArray = new String[maze_height][maze_width];
        try
        {
            Scanner sc = new Scanner(new BufferedReader(new FileReader(filepath)));
           sc.nextLine();
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
        for(int tries = 0; tries < 2; tries++)
        {
            for(int i = 0; i < mazeArray.length; i++)
            {
                for(int j = 0; j < mazeArray[i].length; j++)
                {
                    for(int k = 0; k <4; k++) //DX,DY vector
                    {
                        if(inBounds(i+DX[k],mazeArray.length) && inBounds(j+DY[k],mazeArray[i].length))
                        { 
                            String coord = coordify(i+DX[k], j+DY[k]);
                            int x = i + DX[k];
                            int y = j + DY[k];
                            if(maze_graph.containsKey(coordify(i,j)))
                            {
                                if(MazeComponent.fromString(mazeArray[x][y]) != MazeComponent.UNDEFINED){
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
                    if(MazeComponent.fromString(mazeArray[i][j]) != MazeComponent.UNDEFINED)
                    {
                        M[i][j] = 1;
                        drawblock(i,j);
                    }
                    if(MazeComponent.fromString(mazeArray[i][j]) == MazeComponent.StartSpace)
                    {
                        startNode = maze_graph.get(coordify(i,j));
                        drawdot(i,j);
                    }
                    if(MazeComponent.fromString(mazeArray[i][j]) == MazeComponent.PowerPellet)
                    {
                        goalNode = maze_graph.get(coordify(i,j));
                        goals.add(goalNode);
                        drawpowerpellet(i, j);
                    }
                    if(MazeComponent.fromString(mazeArray[i][j]) == MazeComponent.Pellet)
                    {
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
        Collections.shuffle(arr);
        return arr;
    }

    /**
     * Entrypoint
     * Takes arguments for a file to read from and an integer for which search algorithm to run
     * 1: Random Search
     * 2: Depth First Search
     * 3: Breadth First Search
     * 4: Djikstra's Algorithm
     */
    public static void main(String[] args) throws Exception
    {
        filepath = args.length > 0 ? args[0]: "Mazes/pacmaze.txt";
        try
        {
            searchType = args.length > 1 ? SearchType.values()[Integer.parseInt(args[1])] : SearchType.Random;
        }
        catch(Exception e)
        {
            System.out.println("Invalid Search Type");
        }
        try 
        {
        Scanner sc = new Scanner(new BufferedReader(new FileReader(filepath)));
        String[] line = sc.nextLine().split(",");
        sc.close();
        pacmanbase W = new studentcode(bh,Integer.parseInt(line[0]),Integer.parseInt(line[1]));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Invalid file specified");
        }
    }//main
} // pacmanbase
