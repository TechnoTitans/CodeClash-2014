package SampleJavaPlayer;

/**
 * Class for generating the action commands.
 */
public class Action
{
    private String fCommand;

    private Action( String command )
    {
        fCommand = command;
    }

    @Override
    public String toString()
    {
        return fCommand;
    }

    /**
     * Create an idle action.
     *
     * @return the idle action
     */
    public static Action makeIdleAction()
    {
        return new Action( "idle" );
    }

    /**
     * Create a move action.
     *
     * @param target the target point on the grid
     *
     * @return a move action to the target
     */
    public static Action makeMoveAction( Point target )
    {
        return new Action( String.format( "move %d %d", target.getX(), target.getY() ) );
    }


    /**
     * Creates an action to pickup the object at the given coordinates.
     *
     * @param x the x coordinate of the target
     * @param y the y coordinate of the target
     *
     * @return a pickup action
     */
    public static Action makePickupItemAction( int x, int y )
    {
        return new Action( String.format( "pickup %d %d", x, y ) );
    }

    /**
     * Creates an action to launch a paintball at a target.
     *
     * @param target the coordinates of the target as a point
     *
     * @return a launch action
     */
    public static Action makeLaunchAction( Point target )
    {
        return new Action( String.format( "launch %d %d", target.getX(), target.getY() ) );
    }

    /**
     * Creates an action to launch a paintball using rapid-fire mode.
     *
     * @param start the first target coordinate
     * @param end the last target coordinate
     *
     * @return a rapid-fire launch command
     */
    public static Action makeLaunchAction( Point start, Point end )
    {
        return new Action( String.format( "launch %d %d %d %d", start.getX(), start.getY(), end.getX(), end.getY() ) );
    }

    /**
     * Creates a defend action.
     *
     * @return a defend action
     */
    public static Action makeDefendAction()
    {
        return new Action( "defend" );
    }

    /**
     * Creates an undefend action (exits defensive mode).
     *
     * @return an undefend action
     */
    public static Action makeUndefendAction()
    {
        return new Action( "undefend" );
    }

    /**
     * Creates a crouch action.
     *
     * @return a crouch action
     */
    public static Action makeCrouchAction()
    {
        return new Action( "crouch" );
    }

    /**
     * Creates a stand action.
     *
     * @return a stand action
     */
    public static Action makeStandAction()
    {
        return new Action( "stand" );
    }

    /**
     * Creates an action to plant a flag at a location.
     *
     * @param x the x coordinate of the target
     * @param y the y coordinate of the target
     *
     * @return an action to plant a flag
     */
    public static Action makePlantAction( int x, int y )
    {
        return new Action( String.format( "plant %d %d", x, y ) );
    }

    /**
     * Creates an action to drop some number of paintballs.
     *
     * @param dropTarget the coordinates where to drop
     * @param numPaintballsToDrop the number of paintballs to drop
     *
     * @return an action to drop paintballs
     */
    public static Action makeDropAction( Point dropTarget, int numPaintballsToDrop )
    {
        return new Action( String.format( "drop %d %d %d", dropTarget.getX(), dropTarget.getY(), numPaintballsToDrop ) );
    }
}