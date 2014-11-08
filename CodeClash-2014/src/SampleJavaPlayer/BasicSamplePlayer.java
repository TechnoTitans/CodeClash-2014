package SampleJavaPlayer;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class implements a rather simple, basic Paintball Panic player.
 *
 * Feel free to use the code in this class as a starting point for your own player.  In particular,
 * you can use the code to read the game state and write your move to the game engine exactly as given here.
 */
public class BasicSamplePlayer{
	// flag to say whether we are running in tournament mode or not.  this is based on parameter passed into main()
	protected static boolean fisTournament = false;

	// a very simple log (i.e. file) interface.  cannot be used in tournament mode.
	private static PrintWriter sLog;

	// holds information about all the children on the field
	protected Child[] fChildren;

	// scores for each player
	protected int[] fScores;

	// a basic representation of a field
	protected int[][] fField;

	private static final int[] sFieldMapping = {
		Constants.ADAPTER, Constants.BLUE_FLAG, Constants.EMPTY, Constants.EMPTY, Constants.EMPTY,
		Constants.RAPID_FIRE_LAUNCHER, Constants.EMPTY, Constants.EMPTY, Constants.EMPTY, Constants.EMPTY,
		Constants.EMPTY, Constants.BASIC_LAUNCHER, Constants.EMPTY, Constants.EMPTY, Constants.EMPTY,
		Constants.EMPTY, Constants.EMPTY, Constants.RED_FLAG, Constants.SHIELD, Constants.TREE,
		Constants.EMPTY, Constants.LOW_WALL, Constants.HIGH_WALL, Constants.EMPTY, Constants.EMPTY, Constants.EMPTY
	};

	/**
	 * Construct a basic player object.
	 */
	public BasicSamplePlayer()
	{
		fChildren = new Child[ Constants.NUM_CHILDREN_PER_TEAM * 2 ];

		setfScores(new int[ 2 ]);

		fField = new int[ Constants.FIELD_DIMENSION ][ Constants.FIELD_DIMENSION ];

		for ( int i = 0; i < fChildren.length; i++ )
		{
			if (i%2 == 0){
				fChildren[ i ] = new Scout( i < Constants.NUM_CHILDREN_PER_TEAM ? Constants.RED : Constants.BLUE );
			}else{
				fChildren[i] = new TankChild(i < Constants.NUM_CHILDREN_PER_TEAM ? Constants.RED : Constants.BLUE );
			}
			
		}
	}

	/**
	 * The main player run loop.
	 */
	public void run()
	{
		// Scanner to parse input from the game engine.
		Scanner in = new Scanner( System.in );

		// Keep reading states until the game ends.
		int turnNumber = in.nextInt();

		// the game engine sends a -1 for a turn number when the game is over
		while ( turnNumber >= 0 )
		{
			// Read current game score.
			getfScores()[ Constants.RED ]  = in.nextInt();
			getfScores()[ Constants.BLUE ] = in.nextInt();

			// Read the current field configuration and store in the field contents array
			for ( int i = 0; i < Constants.FIELD_DIMENSION; i++ )
			{
				for ( int j = 0; j < Constants.FIELD_DIMENSION; j++ )
				{
					// Decode the field space encoding
					String fieldEncoding = in.next();
					fField[ i ][ j ] = decodeFieldSymbol( fieldEncoding );
				}
			}

			// Read the states of all the children.
			for ( Child child : fChildren )
			{
				String encoding = in.next();
				if ( encoding.equals( "*" ) )
				{
					// if the encoding is just a *, then it means we can't see this child.
					// this could only happen for children on the other team
					child.setPosition( new Point() );
				}
				else
				{
					// we can see this child.

					// Record the child's location.
					int x = Integer.parseInt( encoding );
					int y = in.nextInt();

					child.setPosition( new Point( x, y  ) );

					// read posture ( standing = S, crouching = C )
					encoding = in.next();
					child.setIsStanding( encoding.equals( "S" ) );

					// read defending mode ( defending = D, not defending = U )
					encoding = in.next();
					child.setIsDefending( encoding.equals( "D" ) );

					// read inventory
					encoding = in.next();
					child.setHolding( encoding.charAt( 0 ) - 'a' );

					// read the number of paintballs the child is holding
					child.setPaintballCount( in.nextInt() );
				}
			}

			// Decide what each child should do
			for ( int i = 0; i < Constants.NUM_CHILDREN_PER_TEAM; i++ )
			{
				Action action = fChildren[ i ].chooseAction( fField );

				if ( !fisTournament )
				{
					sLog.println( action.toString() );
				}

				// write the child's action to the game engine
				System.out.println( action.toString() );
			}

			turnNumber = in.nextInt();
		}
	}

	/**
	 * This function maps a field encoding string to an integer constant value to store in the field map.
	 *
	 * @param fieldEncoding the encoding read from the game engine
	 *
	 * @return the integer code representing the space's contents
	 */
	protected static int decodeFieldSymbol( String fieldEncoding )
	{
		char code = fieldEncoding.charAt( 0 );

		if ( code == '*' || code == '.' )
		{
			// a * means that we cannot see what is at that field location
			// a . means the space is empty
			return 0;
		}
		else if ( code == 'P' )
		{
			// an encoding that starts with P represents a pile of paintballs
			// we store the number of paintballs in the space as a negative value (the value is the number
			// of paintballs) to distinguish from the other space encodings
			return -Integer.parseInt( fieldEncoding.substring( 1 ) );
		}
		else
		{
			// use a simple array to map the code to an integer encoding
			return sFieldMapping[ code - 'A' ];
		}
	}

	/**
	 * This is the main entry point to the player.
	 *
	 * @param args command-line arguments
	 *             tournament: this argument is passed when the player is run as part of the official tournament website
	 *                         no file or network I/O is allowed
	 */
	public static void main( String[] args )
	{
		try
		{
			if ( args.length > 0 )
			{
				fisTournament = args[ 0 ].equals( "tournament" );
			}

			if ( !fisTournament )
			{
				sLog = new PrintWriter( new FileWriter( String.format( "sp-%d.log", (int) ( Math.random() * 1000 ) ) ) );
			}

			BasicSamplePlayer player = new BasicSamplePlayer();

			player.run();
		}
		catch ( Throwable t )
		{
			System.err.format( "Unhandled exception %s\n", t.getMessage() );
			if ( !fisTournament )
			{
				t.printStackTrace( sLog );
			}
			else
			{
				t.printStackTrace();
			}
		}

		if ( !fisTournament )
		{
			sLog.close();
		}

		// explicitly exit with a success status
		System.exit( 0 );
	}

	public int[] getfScores() {
		return fScores;
	}

	public void setfScores(int[] fScores) {
		this.fScores = fScores;
	}
}
