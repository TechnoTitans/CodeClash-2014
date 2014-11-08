package SampleJavaPlayer;

public class Scout extends Child{
	private int scoutNumber;
	private Navigation nav;
	Point flag1 = new Point(11,11);
	Point flag2 = new Point(8,23);
	Point flag3 = new Point(23,8);
	Point flag4 = new Point(23,23);
	boolean flag1placed = false;
	boolean flag2placed = false;
	boolean flag3placed = false;
	boolean flag4placed = false;
	int[][] field; //needs to receive field data
	private int flagColor; //needs to receive team color data
	
	public Scout (int scoutNumber){
		super(Constants.RED);
		this.scoutNumber = scoutNumber;
	}
	
	@Override
	public Action chooseAction(int [][] field){
		if (scoutNumber == 1)
		{
			//if (!flag1placed)
			if (!isPlanted(flag1, field, flagColor))
				return seekAndPlant(fPos, flag1);
			else if (!isPlanted(flag3, field, flagColor))
				return seekAndPlant(fPos, flag3);
			else
				return Action.makeIdleAction(); //placeholder
		}
		else
		{
			if (!isPlanted(flag2, field, flagColor))
				return seekAndPlant(fPos, flag2);
			else if (!isPlanted(flag4, field, flagColor))
				return seekAndPlant(fPos, flag4);
			else
				return Action.makeIdleAction(); //placeholder
		}
		
	}
	

	
	public Action seekFlag(Point cpos, Point flag)
	{
		if (nav.isOn(cpos, flag))
		{
			Point newTarget = new Point(cpos.getX(), cpos.getY()+1);
			return Action.makeMoveAction(newTarget);
		}
		else
			return Action.makeMoveAction(flag);
	}
	//flagCol is flag color assigned to a number
	public boolean isPlanted(Point flag, int[][] field, int flagCol){
		return (field[flag.getX()][flag.getY()] == flagCol);
	}
	
	public Action seekAndPlant(Point cpos, Point flag)
	{
		if (!nav.isAdjacent(cpos, flag))
			return seekFlag(cpos, flag);
		else
			return Action.makePlantAction(flag.getX(), flag.getY());
	}
}
