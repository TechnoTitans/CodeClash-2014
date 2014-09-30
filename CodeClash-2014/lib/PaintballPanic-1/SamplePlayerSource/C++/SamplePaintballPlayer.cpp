/**
 * This class implements a rather simple, basic Paintball Panic player.
 *
 * Feel free to use the code in this class as a starting point for your own player.  In particular,
 * you can use the code to read the game state and write your move to the game engine exactly as given here.
 */

#include "Paintball.h"
#include <vector>
#include <string>
#include <iostream>
#include <cstdlib>

using namespace std;
const int field_symbol_map[] =
{
    ADAPTER, BLUE_FLAG, EMPTY, EMPTY, EMPTY,
    RAPID_FIRE_LAUNCHER, EMPTY, EMPTY, EMPTY, EMPTY,
    EMPTY, BASIC_LAUNCHER, EMPTY, EMPTY, EMPTY,
    EMPTY, EMPTY, RED_FLAG, SHIELD, TREE,
    EMPTY, LOW_WALL, HIGH_WALL, EMPTY, EMPTY, EMPTY
};

/** Representation of a 2D point */
struct Point
{
    // x and y coordinates
    int x;
    int y;
};

/** Simple representation for a child in the Game. */
struct Child
{
    // Location of the child.
    Point pos;

    // Side the child is on.
    int color;

    // True if the child is standing.
    bool standing;

    // True if the child is defending
    bool defending;

    // Child's inventory
    int holding;

    // Number of paintballs this child is carrying
    int paintballs;
};


// Current game score, for red and blue
int score[ 2 ];

// Content of each space on field
vector< vector< int > > field( FIELD_DIMENSION, vector< int >( FIELD_DIMENSION ) );

// List of children on the field
vector< Child > children( NUM_CHILDREN_PER_TEAM * 2 );

/** Makes sure that a coordinate is a valid field index */
int clamp( int x )
{
    if ( x < 0 )
    {
        return 0;
    }
    if ( x > ( FIELD_DIMENSION - 1 ) )
    {
        return FIELD_DIMENSION - 1;
    }
    return x;
}

/** Returns true if the child is holding a shield */
bool isHoldingShield( int inventory )
{
    return inventory == ONE_SHIELD ||
            inventory == SHIELD_AND_BASIC_LAUNCHER ||
            inventory == SHIELD_AND_RAPID_FIRE_LAUNCHER;
}

/** Chooses a random point within a certain disance */
Point getRandomPoint( Point& p, int distance )
{
    int dx = rand() % ( distance + 1 );
    int dy = distance - dx;

    // choose a random direction (left or right)
    dx = dx * ( ( rand() % 2 ) == 0 ? 1 : -1 );

    // choose a random direction (up of down)
    dy = dy * ( ( rand() % 2 ) == 0 ? 1 : -1 );

    Point target;
    target.x = clamp( p.x + dx );
    target.y = clamp( p.y + dy );
    return target;
}

/** Get the distance this child can move */
int getDistance( Child& child )
{
    if ( child.standing )
    {
        if ( child.defending )
        {
            return 2;
        }
        else
        {
            return 3;
        }
    }
    else
    {
        return 1;
    }
}

/** Move a child */
void doMoveAction( Child& child )
{
    int distance = getDistance( child );
    Point target = getRandomPoint( child.pos, distance );

    cout << "move " << target.x << " " << target.y << endl;
}

/** Choose a move for this child */
void chooseAction( int i )
{
    // Child to work on
    Child &child = children[ i ];

    int action = rand() % 100;

    if ( action < 2 )
    {
        // idle
        cout << "idle" << endl;
    }
    else if ( action < 27 )
    {
        // move
        doMoveAction( child );
    }
    else if ( action < 45 )
    {
        // pickup
        for ( int dx = -1; dx <= 1; dx++ )
        {
            for ( int dy = -1; dy <= 1; dy++ )
            {
                int check_x = clamp( child.pos.x + dx );
                int check_y = clamp( child.pos.y + dy );
                int fieldContents = field[ check_x ][ check_y ];
                if ( fieldContents < 0 || fieldContents >= 6 )
                {
                    // something to pickup
                    cout << "pickup " << check_x << " " << check_y << endl;
                    return;
                }
            }
        }
        doMoveAction( child );
    }
    else if ( action < 62 )
    {
        // launch
        Point p1 = getRandomPoint( child.pos, MAX_LAUNCH_DISTANCE );
        // ignore whether we have rapid fire launcher
        cout << "launch " << p1.x << " " << p1.y << endl;
    }
    else if ( action < 67 )
    {
        // defend
        if ( isHoldingShield( child.holding ) )
        {
            cout << "defend" << endl;
        }
        else
        {
            doMoveAction( child );
        }
    }
    else if ( action < 72 )
    {
        // undefend
        if ( child.defending )
        {
            cout << "undefend" << endl;
        }
        else
        {
            doMoveAction( child );
        }
    }
    else if ( action < 80 )
    {
        // drop
        // we just drop some paintballs
        if ( child.paintballs > 1 )
        {
            int numToDrop = ( rand() % ( child.paintballs / 2 ) ) + 1;
            Point p = getRandomPoint( child.pos, 1 );
            cout << "drop " << p.x << " " << p.y << " " << numToDrop << endl;
        }
        else
        {
            doMoveAction( child );
        }
    }
    else if ( action < 82 )
    {
        // crouch
        if ( child.standing )
        {
            cout << "crouch" << endl;
        }
        else
        {
            doMoveAction( child );
        }
    }
    else if ( action < 92 )
    {
        // stand
        if ( !child.standing )
        {
            cout << "stand" << endl;
        }
        else
        {
            doMoveAction( child );
        }
    }
    else
    {
        // plant a flag
        for ( int dx = -1; dx <= 1; dx++ )
        {
            for ( int dy = -1; dy <= 1; dy++ )
            {
                if ( !( dx == 0 && dy == 0 ) )
                {
                    int check_x = clamp( child.pos.x + dx );
                    int check_y = clamp( child.pos.y + dy );
                    if ( field[ check_x ][ check_y ] == EMPTY )
                    {
                        // found empty space
                        cout << "plant " << check_x << " " << check_y << endl;
                        return;
                    }
                }
            }
        }
        cout << "idle" << endl;
    }
    return;
}

int decodeFieldSymbol( string symbol )
{
    if ( symbol[ 0 ] == '*' || symbol[ 0 ] == '.' )
    {
        return 0;
    }

    if ( symbol[ 0 ] == 'P' )
    {
        // this is a pile of paintballs
        // we encode paintballs a the negative of the number on the space
        return -atoi( symbol.substr( 1 ).c_str() );
    }

    // otherwise, let's just look up the encoding from a constant array
    int index = symbol[ 0 ] - 'A';
    return field_symbol_map[ index ];
}

int main()
{
    int turnNum;

    // read the turn number
    cin >> turnNum;

    while ( turnNum >= 0 )
    {
        // Read the current score.
        cin >> score[ RED ] >> score[ BLUE ];
    
        // Parse the current map
        for ( int i = 0; i < FIELD_DIMENSION; i++ )
        {
            for ( int j = 0; j < FIELD_DIMENSION; j++ )
            {
                string fieldSym;
                cin >> fieldSym;
                field[ i ][ j ] = decodeFieldSymbol( fieldSym );
            }
        }

        string token;
        for ( int i = 0; i < children.size(); i++ )
        {
            cin >> token;
            if ( token == "*" )
            {
                // Can't see this child
                children[ i ].pos.x = -1;
                children[ i ].pos.y = -1;
            }
            else
            {
                // Can see this child, read the description.
                children[ i ].pos.x = atoi( token.c_str() );
                cin >> children[ i ].pos.y;

                // Fill in the child's team color.
                children[ i ].color = i / NUM_CHILDREN_PER_TEAM;

                char ch;
                // Is child standing? ( S == standing, C == crouching )
                cin >> ch;
                children[ i ].standing = ( ch == 'S' );

                // Is child defending? ( D == defending, U == not defending )
                cin >> ch;
                children[ i ].defending = ( ch == 'D' );

                // Get child's inventory
                cin >> ch;
                children[ i ].holding = ( ch - 'a' );

                // Process number of paintballs child is carrying
                cin >> children[ i ].paintballs;
            }
        }

        // Choose an action for each child.
        for ( int i = 0; i < NUM_CHILDREN_PER_TEAM; i++ )
        {
            // choose and write the action
            chooseAction( i );
        }
    
        cin >> turnNum;
    }
}
