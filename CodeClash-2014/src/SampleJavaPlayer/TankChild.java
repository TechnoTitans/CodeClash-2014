package SampleJavaPlayer;

import java.util.Random;

public class TankChild extends Child{

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
	
	public TankChild (int scoutNumber){
		super(Constants.RED);
		this.scoutNumber = scoutNumber;
	}
	
	@Override
	public Action chooseAction(int [][] field){
		return fireAtNearestPerson();
		
	}
	
	public Action fireAtNearestPerson(){
		Point nearestPerson = findNearestPerson(fPos, field);
		if (nearestPerson != null){
			return Action.makeLaunchAction(findNearestPerson(fPos, field));
		}else{
			return Action.makeMoveAction(new Point 
					(fPos.getX()+ (new Random().nextInt(4)), 
								fPos.getY()+ (new Random().nextInt(4))));
		}
	}
	

	
	private Point findNearestPerson(Point cpos, int [][] field){
		for (int i = 0; i<14; i++){
			int row;
			if (i%2 ==0){
				row = cpos.getY() -i;
			}else{
				row = cpos.getY() + (i-1);
			}
			int column;
			if (row >= 0){
				for (column = 0; column < field[row].length; i++ ){
					int pointOnMap = field [row][column];
					if (pointOnMap != super.fColor){
						return new Point(row, column);
					}
						
				}
			}
		}
		return null;
		
	}
}
