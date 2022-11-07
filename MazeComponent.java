import java.time.temporal.WeekFields;

public enum MazeComponent
{
    Pellet (100),
    EmptySpace (50), 
    StartSpace(50),
    PowerPellet (10000),
    Ghost (-10000),
    UNDEFINED(-1);

    private int weight;
    
    MazeComponent(int weight) 
    {
        this.weight = weight;
    }

    public static MazeComponent fromString(String rep)
    {
         switch(rep)
        {
            case "d": return MazeComponent.Pellet;
            case "*": return MazeComponent.EmptySpace;
            case "o": return MazeComponent.PowerPellet; 
            case "g": return MazeComponent.Ghost;
            case "s": return MazeComponent.StartSpace;
            default:
                return MazeComponent.UNDEFINED;
             
        }
    }

    public int getWeight()
    {
        return weight;
    }
}