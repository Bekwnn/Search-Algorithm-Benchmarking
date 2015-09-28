public class City
{
	public String name;
	public Vector2 position;
	public int cityIndex;
	
	public City(String cityName, int index, int xPos, int yPos)
	{
		position = new Vector2(xPos, yPos);
		name = cityName;
		cityIndex = index;
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