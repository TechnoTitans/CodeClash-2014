package SampleJavaPlayer;

public class Navigation {
	
	public boolean isAdjacent(Point cpos, Point target)
	{
		boolean answer = true;
		if (Math.abs(cpos.getX()-target.getX())>1)
			answer = false;
		if (Math.abs(cpos.getY()-target.getY())>1)
			answer = false;
		if (isOn(cpos, target))
			answer = false;
		return answer;
	}
	
	public boolean isOn(Point cpos, Point target)
	{
		return (cpos.getX()==target.getX() && cpos.getY()==target.getY());
	}
	
	
}
