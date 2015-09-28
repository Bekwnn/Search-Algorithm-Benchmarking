import java.util.Random;

public class SearchAlgorithmTest
{
	static City[] cities = new City[26];
	static boolean[][] simpleAdjacencyMat = new boolean[26][26];
	static float[][] weightedAdjacencyMat = new float[26][26];
	
	static Random randomGenerator;
	
	public static void main(String[] args)
	{
		GenerateCities();
		GenerateSimpleAdjacencyMatrix();
		
		PrintMap();
	}
	
	public static void GenerateCities()
	{
		if (randomGenerator == null) randomGenerator = new Random();
		
		char startChar = 'A';
		
		for (int i = 0; i < cities.length; i++)
		{
			cities[i] = new City(Character.toString((char)(startChar + i)), randomGenerator.nextInt(100), randomGenerator.nextInt(100));
		}
	}
	
	public static void GenerateSimpleAdjacencyMatrix()
	{
		
	}
	
	public static void PrintMap()
	{
		char[][] map = new char[50][50];
		
		//initialize
		for (int i = 0; i < map.length; i++)
		{
			for (int j = 0; j < map[i].length; j++)
			{
				map[i][j] = '.';
			}
		}
		
		//change city characters
		for (City curCity : cities)
		{
			//if there's no city there, change the icon to first letter of city name
			if (map[curCity.position.y/2][curCity.position.x/2] == '.')
			{
				map[curCity.position.y/2][curCity.position.x/2] = curCity.name.charAt(0);
			}
			
			//if there is another city there, change icon to '&'
			else
			{
				map[curCity.position.y/2][curCity.position.x/2] = '&';
			}
		}
		
		//print cities
		for (char[] row : map) {
			for (char character : row) {
				System.out.print(character);
			}
			System.out.println();
		}
	}
}