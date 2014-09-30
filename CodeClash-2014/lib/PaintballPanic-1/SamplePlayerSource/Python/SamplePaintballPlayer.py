# A sample Python player for Paintball Panic.  This player just chooses
# random actions for each child.
#
# You can use this as a starting point for your own Python player.  Feel free
# to copy any parts of this code that you want to use.
#

import random
import sys
import string

# Useful constants for Paintball Panic

# Team colors
RED = 0;
BLUE = 1;

NUM_CHILDREN_PER_TEAM = 4;

# Length of each side of the square field
FIELD_DIMENSION = 31;

# Encoding of what can be on the field
EMPTY               = 0;
TREE                = 1;
LOW_WALL            = 2;
HIGH_WALL           = 3;
RED_FLAG            = 4;
BLUE_FLAG           = 5;
ADAPTER             = 6;
SHIELD              = 7;
BASIC_LAUNCHER      = 8;
RAPID_FIRE_LAUNCHER = 9;

# Number of action choices that can be taken by a child on one turn
NUM_ACTIONS = 10;

# Maximum Euclidean distance a paintball can travel
MAX_LAUNCH_DISTANCE = 24;

# Constants for what a child can be holding (inventory)
NOTHING                        = 0;
ONE_BASIC_LAUNCHER             = 1;
ONE_SHIELD                     = 2;
SHIELD_AND_BASIC_LAUNCHER      = 3;
ONE_RAPID_FIRE_LAUNCHER        = 4;
SHIELD_AND_RAPID_FIRE_LAUNCHER = 5;

# Height for a standing child.
STANDING_HEIGHT = 9

# Height for a crouching child.
CROUCHING_HEIGHT = 3

# Map of character codes (letters of the alphabet) to their field encodings
# Unrecognized codes (which should never happen) map to EMPTY spaces
FIELD_SYMBOL_MAP = [
    ADAPTER, BLUE_FLAG, EMPTY, EMPTY, EMPTY,
    RAPID_FIRE_LAUNCHER, EMPTY, EMPTY, EMPTY, EMPTY,
    EMPTY, BASIC_LAUNCHER, EMPTY, EMPTY, EMPTY,
    EMPTY, EMPTY, RED_FLAG, SHIELD, TREE,
    EMPTY, LOW_WALL, HIGH_WALL, EMPTY, EMPTY, EMPTY
]

# Return the value of x, clamped to the [ a, b ] range.
def clamp( x ):
    if x < 0:
        return 0
    if x > 30:
        return 30
    return x

# Representation of a 2D point, used for playing field locations.
class Point:
    def __init__( self, x_arg, y_arg ):
        self.x = x_arg
        self.y = y_arg

# Simple representation for a child's action
class Action:
    # Action the child is making.
    action = "idle"

    def __init__( self, command ):
        self.action = command

# Simple representation for a child in the game with default values.
# Assumes that various attributes will be changed when a child is created
class Child:
    def __init__( self, color_arg ):
        # Location of the child.
        self.pos = Point( 0, 0 )

        # Child's team
        self.color = color_arg
    
        # True if  the child is standing.
        self.standing = True

        # True if the child is defending (defaults to false - need a shield to defend)
        self.defending = False
    
        # Child's inventory
        self.holding = NOTHING
    
        # Number of paintballs this child is carrying
        self.paintballs = 0

    # Computes a distance based on the child's current posture
    def getDistance( self ):
        if self.standing:
            if self.defending:
                return 2
            else:
                return 3
        else:
            return 1

    # Computes a random point that is a certain distance from where the child is currently positioned
    def getRandomPoint( self, distance ):
        # randomly choose a delta-x
        dx = rng.randint( 0, distance )

        # the remaining Euclidean distance is assigned to the delta-y magnitude
        dy = distance - dx

        # choose a random direction ( left or right )
        dx = dx * ( -1 if rng.randint( 0, 1 ) == 0 else 1 )

        # choose a random direction ( up or down )
        dy = dy * ( -1 if rng.randint( 0, 1 ) == 0 else 1 )

        return Point( clamp( self.pos.x + dx ), clamp( self.pos.y + dy ) )

    # Convenience function to create a move action
    def doMoveAction( self ):
        distance = self.getDistance()
        point    = self.getRandomPoint( distance )
        return Action( "move {0} {1}".format( point.x, point.y ) )

    # Determines if a child is holding a shield
    def isHoldingShield( self ):
        return self.holding in ( ONE_SHIELD, SHIELD_AND_BASIC_LAUNCHER, SHIELD_AND_RAPID_FIRE_LAUNCHER )

    # Determines if a child is holding a rapid fire launcher
    def isHoldingRapidFireLauncher( self ):
        return self.holding in ( RAPID_FIRE_LAUNCHER, SHIELD_AND_RAPID_FIRE_LAUNCHER )

    # This function chooses a random action to perform
    # You should replace this function with your own game logic
    def chooseAction( self ):
        # choose a random action.  we will do it based on an uneven distribution to keep things interesting
        rn = rng.randint(0, 99)
        actionType = 9
        if rn < 2:
            # IDLE
            return Action( "idle" )
        elif rn < 27:
            # MOVE
            return self.doMoveAction()
        elif rn < 45:
            # PICKUP
            # look around the current location to see if there is something to pickup
            for dx in range(-1, 2):
                for dy in range(-1, 2):
                    check_x = clamp( self.pos.x + dx )
                    check_y = clamp( self.pos.y + dy )
                    fieldContents = field[ check_x ][ check_y ]
                    if fieldContents < 0 or fieldContents >= 6:
                        # there is something to pickup at that space
                        return Action( "pickup {0} {1}".format( check_x, check_y ) )
            # there is nothing to pickup, so just move instead
            return self.doMoveAction()
        elif rn < 62:
            # LAUNCH
            p1 = self.getRandomPoint( MAX_LAUNCH_DISTANCE )
            # if we have a rapid fire weapon, then use it
            if self.isHoldingRapidFireLauncher():
                p2 = self.getRandomPoint( MAX_LAUNCH_DISTANCE )
                return Action( "launch {0} {1} {2} {3}".format( p1.x, p1.y, p2.x, p2.y ) )
            else:
                return Action( "launch {0} {1}".format( p1.x, p1.y ) )
        elif rn < 67:
            # DEFEND (if we are holding a shield)
            if self.isHoldingShield():
                return Action( "defend" )
            else:
                # just move instead
                return self.doMoveAction()
        elif rn < 72:
            # UNDEFEND (if we are defending)
            if self.defending:
                return Action( "undefend" )
            else:
                # just move instead
                return self.doMoveAction()
        elif rn < 80:
            # DROP
            # we will just drop some paintballs, but no more than half, just to demonstrate
            if self.paintballs > 1:
                numToDrop = rng.randint( 1, self.paintballs / 2 )
                p = self.getRandomPoint( 1 )
                return Action( "drop {0} {1} {2}".format( p.x, p.y, numToDrop ) )
            else:
                # just move instead
                return self.doMoveAction()
        elif rn < 82:
            # CROUCH (if we are standing)
            if self.standing:
                return Action( "crouch" )
            else:
                # just move instead
                return self.doMoveAction()
        elif rn < 92:
            # STAND (if we are crouched)
            if not self.standing:
                return Action( "stand" )
            else:
                # just move instead
                return self.doMoveAction()
        else:
            # PLANT a flag
            # we need to find an empty, adjacent space in which to plant the flag
            for dx in range(-1, 2):
                for dy in range(-1, 2):
                    if not ( dx == 0 and dy == 0 ):
                        # can't plant the flag where we are standing
                        check_x = clamp( self.pos.x + dx )
                        check_y = clamp( self.pos.y + dy )
                        if field[ check_x ][ check_y ] == EMPTY:
                            return Action( "plant {0} {1}".format( check_x, check_y ) )
            # no empty spaces.  just remain idle
            return Action( "idle" )

# Decodes a field element
def decodeFieldSymbol( encoding ):
    # a space we can't see is treated the same an empty space for now
    if encoding[ 0 ] in ( '*', '.' ):
        return 0

    # we store a pile of paintballs as a negative number in our field map
    if encoding[ 0 ] == 'P':
        return -string.atoi( encoding[ 1: ] )

    # just do the mapping lookup
    return FIELD_SYMBOL_MAP[ string.find( string.ascii_uppercase, encoding[ 0 ] ) ]

########################################################################################

# Source of randomness
rng = random.Random()

# Current game score for self (red) and opponent (blue).
score = [ 0, 0 ]

# Contents of each cell.
field = []

# Allocate the whole field.  Is there a better way to do this?
for i in range( FIELD_DIMENSION ):
    field.append( [ 0 ] * FIELD_DIMENSION )

# List of children on the field (0-3 are red team, 4-7 are blue team)
redChildren = [ Child( RED ) for i in range( NUM_CHILDREN_PER_TEAM ) ]
blueChildren = [ Child( BLUE ) for i in range( NUM_CHILDREN_PER_TEAM ) ]
children = redChildren + blueChildren

turnNum = string.atoi( sys.stdin.readline() )
while turnNum >= 0:

    # read the scores of the two sides.
    tokens = string.split( sys.stdin.readline() )
    score[ RED ] = tokens[ 0 ]
    score[ BLUE ] = tokens[ 1 ]
    
    # Parse the current map.
    for i in range( FIELD_DIMENSION ):
        fieldElts = string.split( sys.stdin.readline() )
        for j in range( FIELD_DIMENSION ):
            # Decode the field element
            field[ i ][ j ] = decodeFieldSymbol( fieldElts[ j ] )
    

    # Read the states of all the children.
    for i in range( NUM_CHILDREN_PER_TEAM * 2 ):
        child = children[ i ]
        
        # Can we see this child?        
        descriptor = string.split( sys.stdin.readline() )
        if descriptor[ 0 ] == "*":
            child.pos.x = -1
            child.pos.y = -1
        else:
            # Record the child's location.
            child.pos.x = string.atoi( descriptor[ 0 ] )
            child.pos.y = string.atoi( descriptor[ 1 ] )

            # Is child standing?  (S == standing, C == crouching)
            child.standing = ( descriptor[ 2 ] == "S" )
            
            # Is child defending? (D == defending, U == not defending)
            child.defending = ( descriptor[ 3 ] == "D" )

            # Process child's inventory
            child.holding = string.find( string.ascii_lowercase, descriptor[ 4 ] ) 

            # Process number of paintballs child is carrying
            child.paintballs = string.atoi( descriptor[ 5 ] )

    # Decide what each child should do
    for i in range( NUM_CHILDREN_PER_TEAM ):
        child = children[ i ]
        command = child.chooseAction()

        # Write out the child's action
        sys.stdout.write( "{0}\n".format( command.action ) )

    sys.stdout.flush()
    turnNum = string.atoi( sys.stdin.readline() )
