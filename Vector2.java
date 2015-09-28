public class Vector2
{
	public int x;
	public int y;
	
	public Vector2(int xPos, int yPos)
	{
		x = xPos;
		y = yPos;
	}
	
	public double DistanceTo(Vector2 other)
	{
		int xdiff = this.x - other.x;
		int ydiff = this.y - other.y;
		return Math.sqrt(xdiff*xdiff + ydiff*ydiff);
	}

	public String ToString()
	{
		return "(" + x + ", " + y + ")";
	}
}