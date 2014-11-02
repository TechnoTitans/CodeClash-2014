package original;

/**
 * A point on the grid.
 */
public class Point
{
    // the X coordinate
    private int fX;

    // the  coordinate
    private int fY;

    /**
     * Construct a new point.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point( int x, int y )
    {
        fX = x;
        fY = y;
    }

    /**
     * Default constructor makes a point off the field.
     */
    public Point()
    {
        this( -1, -1 );
    }

    /**
     * Gets the X coordinate value.
     *
     * @return the X coordinate value
     */
    public int getX()
    {
        return fX;
    }

    /**
     * Gets the Y coordinate value.
     *
     * @return the Y coordinate value
     */
    public int getY()
    {
        return fY;
    }
}
