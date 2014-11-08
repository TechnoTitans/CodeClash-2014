package SampleJavaPlayer;

import java.util.Random;

/**
 * Class that holds the state of a child.
 */
public class Child{
    // Location of the child.
    protected Point fPos;

    // True if the child is standing.
    protected boolean fIsStanding;

    // True if the child is defending
    protected boolean fIsDefending;

    // Side the child is on.
    protected int fColor;

    // What the child is holding.
    protected int fHolding;

    // Number of paintballs being held by the child
    protected int fPaintballs;

    protected Random fRandomGenerator;

    public Child( int color )
    {
        fColor       = color;
        fPos         = new Point();
        fIsStanding  = true;
        fIsDefending = false;
        fHolding     = 0;
        fPaintballs  = 0;

        fRandomGenerator = new Random();
    }

    /**
     * Sets the position of this child.
     *
     * @param point the coordinates of the child
     */
    public void setPosition( Point point )
    {
        fPos = point;
    }

    /**
     * Sets the posture of this child (either standing or crouching).
     *
     * @param isStanding true if the child is standing, false if the child is crouching
     */
    public void setIsStanding( boolean isStanding )
    {
        fIsStanding = isStanding;
    }

    /**
     * Sets the defensive mode of this child (either defending or not defending).
     *
     * @param isDefending true if the child is defending, false if the child is not
     */
    public void setIsDefending( boolean isDefending )
    {
        fIsDefending = isDefending;
    }

    /**
     * Sets what the child is holding.  This is an encoding of the possible inventory combinations.
     *
     * @param holding the code for what the child is holding
     */
    public void setHolding( int holding )
    {
        fHolding = holding;
    }

    /**
     * Sets the number of paintballs this child is holding.
     *
     * @param numPaintballs the number of paintballs the child is holding
     */
    public void setPaintballCount( int numPaintballs )
    {
        fPaintballs = numPaintballs;
    }

    /**
     * Chooses the next action for this child. If we know the action chosen won't
     * work, then we will just default to a move action.
     *
     * @param field the current field
     *@Override
     * @return the child's next action
     */
    public Action chooseAction( int[][] field )
    {
        // just choose a totally random action
        int randomNumber = fRandomGenerator.nextInt( 100 );

        // to make things a little more interesting, we don't want an exactly even distribution of action choices
        // idle  =     2
        // move =     25
        // pickup =   18
        // launch =   17
        // defend =    5
        // undefend =  5
        // stand =     8
        // crouch =    2
        // drop =     10
        // plant =     8

        int actionType;
        if ( randomNumber < 2 )
        {
            actionType = 0;
        }
        else if ( randomNumber < 27 )
        {
            actionType = 1;
        }
        else if ( randomNumber < 45 )
        {
            actionType = 2;
        }
        else if ( randomNumber < 62 )
        {
            actionType = 3;
        }
        else if ( randomNumber < 67 )
        {
            actionType = 4;
        }
        else if ( randomNumber < 72 )
        {
            actionType = 5;
        }
        else if ( randomNumber < 80 )
        {
            actionType = 6;
        }
        else if ( randomNumber < 82 )
        {
            actionType = 7;
        }
        else if ( randomNumber < 92 )
        {
            actionType = 8;
        }
        else
        {
            actionType = 9;
        }

        switch ( actionType )
        {
            case 0:
            {
                // idle action
                return Action.makeIdleAction();
            }
            case 1:
            {
                // move action
                // the Euclidean distance we can move is based on our current stance
                int distance = ( fIsStanding ? ( fIsDefending ? 2 : 3 ) : 1 );
                return Action.makeMoveAction( getRandomPoint( distance ) );
            }
            case 2:
            {
                // pickup action
                // let's at least try to pickup something if we are near it
                for ( int dx = -1; dx <= 1; dx++ )
                {
                    for ( int dy = -1; dy <= 1; dy++ )
                    {
                        int x = clamp( fPos.getX() + dx );
                        int y = clamp( fPos.getY() + dy );
                        int fieldContents = field[ x ][ y ];

                        if ( fieldContents >= 6 || fieldContents < 0 )
                        {
                            // this space holds something we can pick up
                            return Action.makePickupItemAction( x, y );
                        }
                    }
                }
                // there is nothing to pick up, so just try to move somewhere
                return Action.makeMoveAction( getRandomPoint( fIsStanding ? ( fIsDefending ? 2 : 3 ) : 1) );
            }
            case 3:
            {
                // launch action
                if ( isHoldingRapidFireLauncher() )
                {
                    // do a rapid fire launch command
                    Point p1 = getRandomPoint( Constants.MAX_LAUNCH_DISTANCE );
                    Point p2 = getRandomPoint( Constants.MAX_LAUNCH_DISTANCE );
                    return Action.makeLaunchAction( p1, p2 );
                }
                else
                {
                    return Action.makeLaunchAction( getRandomPoint( Constants.MAX_LAUNCH_DISTANCE ) );
                }
            }
            case 4:
            {
                // defend action. this will only work if we are holding a shield.  otherwise, just remain idle.
                if ( isHoldingShield() )
                {
                    return Action.makeDefendAction();
                }
                else
                {
                    // since we don't have a shield, just try to move somewhere instead
                    return Action.makeMoveAction( getRandomPoint( fIsStanding ? ( fIsDefending ? 2 : 3 ) : 1 ) );
                }
            }
            case 5:
            {
                // undefend action.  this only has effect if we are currently defending.
                if ( fIsDefending )
                {
                    return Action.makeUndefendAction();
                }
                else
                {
                    // since we are already not defending, just try to move somewhere instead
                    return Action.makeMoveAction( getRandomPoint( fIsStanding ? 2 : 1 ) );
                }
            }
            case 6:
            {
                // drop some paintballs (but not more than half)
                if ( fPaintballs > 1 )
                {
                    int numPaintballsToDrop = fRandomGenerator.nextInt( fPaintballs / 2 );
                    return Action.makeDropAction( getRandomPoint( 1 ), numPaintballsToDrop );
                }
                else
                {
                    return Action.makeMoveAction( getRandomPoint( fIsStanding ? ( fIsDefending ? 2 : 3 ) : 1 ) );
                }
            }
            case 7:
            {
                // crouch action
                if ( fIsStanding )
                {
                    return Action.makeCrouchAction();
                }
                else
                {
                    // since we are already crouching, just try to crawl somewhere instead
                    return Action.makeMoveAction( getRandomPoint( 1 ) );
                }
            }
            case 8:
            {
                // stand action
                if ( !fIsStanding )
                {
                    return Action.makeStandAction();
                }
                else
                {
                    // since we are already standing, just move somewhere instead
                    return Action.makeMoveAction( getRandomPoint( fIsDefending ? 2 : 3 ) );
                }
            }
            case 9:
            {
                // plant a flag
                // just pick first empty adjacent space
                for ( int dx = -1; dx <= 1; dx++ )
                {
                    for ( int dy = -1; dy <= 1; dy++ )
                    {
                        // can't plant a flag where we are standing
                        if ( dx != 0 && dy != 0 )
                        {
                            int x = clamp( fPos.getX() + dx );
                            int y = clamp( fPos.getY() + dy );
                            if ( field[ x ][ y ] == Constants.EMPTY )
                            {
                                // this is an empty, adjacent space into which we can plant a flag
                                return Action.makePlantAction( x, y );
                            }
                        }
                    }
                }
                // no empty spaces, so no place to move either
                return Action.makeIdleAction();
            }
        }

        // can never get here
        return null;
    }

    /**
     * Create the move action.  We do this separately so that it an be a default action
     * when another action can't be taken (and this is more interesting that just being idle).
     *
     * We move in an entirely random direction that may not even make any sense.
     *
     * @return the randomly chosen move action
     */
    private Point getRandomPoint( int distance )
    {
        // randomly choose a delta-x magnitude
        int dx = fRandomGenerator.nextInt( distance + 1 );

        // the remaining Euclidean distance is assigned to the delta-y magnitude
        int dy = distance - dx;

        // and then choose a random direction (left or right)
        dx *= fRandomGenerator.nextBoolean() ? -1 : 1;

        // and then choose a random direction (up or down)
        dy *= fRandomGenerator.nextBoolean() ? -1 : 1;

        // create and return the actual move command
        return new Point( clamp( fPos.getX() + dx ), clamp( fPos.getY() + dy ) );
    }

    /**
     * This utility function ensures that a coordinate is within the valid range of 0 <= c < 31.
     * This ensures we won't accidentally try to index a field coordinate that is off the field.
     *
     * @param coordinate the coordinate we want to check
     *
     * @return either the coordinate if it is between the edges of the range, or the edge of the range
     */
    private static int clamp( int coordinate )
    {
        if ( coordinate < 0 )
        {
            return 0;
        }
        else if ( coordinate >= Constants.FIELD_DIMENSION )
        {
            return Constants.FIELD_DIMENSION - 1;
        }
        return  coordinate;
    }

    /**
     * Determines if this child is holding a shield.
     *
     * @return true if the child is holding a shield, false otherwise.
     */
    private boolean isHoldingShield()
    {
        return  fHolding == Constants.ONE_SHIELD ||
                fHolding == Constants.SHIELD_AND_BASIC_LAUNCHER ||
                fHolding == Constants.SHIELD_AND_RAPID_FIRE_LAUNCHER;
    }

    /**
     * Determines if this child is holding a rapid fire launcher.
     *
     * @return true if the child is holding a rapid fire launcher, false otherwise.
     */
    private boolean isHoldingRapidFireLauncher()
    {
        return  fHolding == Constants.ONE_RAPID_FIRE_LAUNCHER ||
                fHolding == Constants.SHIELD_AND_RAPID_FIRE_LAUNCHER;
    }
}
