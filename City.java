public class City
{
	public String name;
	public Vector2 position;
	
	public City(String cityName, int xPos, int yPos)
	{
		position = new Vector2(xPos, yPos);
		name = cityName;
	}
	
	public double DistanceTo(City b)
	{
		return position.DistanceTo(b.position);
	}
	
	public String ToString()
	{
		return "City " + name + ": " + position.ToString();
	}
}