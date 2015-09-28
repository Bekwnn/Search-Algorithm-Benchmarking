import java.util.Random;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchAlgorithmTest
{
	static final int numberOfCities = 26;
	static final float adjacencyChance = 0.4f;
	
	static City[] cities = new City[numberOfCities];
	static boolean[][] simpleAdjacencyMat = new boolean[numberOfCities][numberOfCities];
	static double[][] weightedAdjacencyMat = new double[numberOfCities][numberOfCities];
	
	static Random randomGenerator;
	
	public static void main(String[] args)
	{
		GenerateCities();
		GenerateLocalSimpleAdjacencyMatrix();
		//GenerateRandomSimpleAdjacencyMatrix();
		GenerateWeightedAdjacencyMatrix();
		
		PrintMap();
		PrintSimpleAdjacencyMatrix();
		PrintWeightedAdjacencyMatrix();
	}
	
	public static void GenerateCities()
	{
		if (randomGenerator == null) randomGenerator = new Random();
		
		char startChar = 'A';
		
		for (int i = 0; i < cities.length; i++)
		{
			cities[i] = new City(Character.toString((char)(startChar + i)), i, randomGenerator.nextInt(100), randomGenerator.nextInt(100));
		}
	}
	
	public static void GenerateRandomSimpleAdjacencyMatrix()
	{
		for (int i = 0; i < simpleAdjacencyMat.length; i++)
		{
			for (int j = 0; j < simpleAdjacencyMat[i].length; j++)
			{
				if (randomGenerator.nextFloat() < adjacencyChance)
				{
					simpleAdjacencyMat[i][j] = true;
				}
				else
				{
					simpleAdjacencyMat[i][j] = false;
				}
			}
		}
	}
	
	public static void GenerateLocalSimpleAdjacencyMatrix()
	{
		for (City curCity : cities)
		{
			City[] closestFive = new City[5];
			int indexOfFarthestClosest = 0;
			
			for (City otherCity : cities)
			{
				if (curCity == otherCity) continue; //skips itself
				
				//if a value in closest five is null or this other city is closer than the farthest of the close five
				if (closestFive[indexOfFarthestClosest] == null ||
					curCity.DistanceTo(otherCity) < curCity.DistanceTo(closestFive[indexOfFarthestClosest]))
				{
					//replace farthest of close five with new closer city
					closestFive[indexOfFarthestClosest] = otherCity;
					
					//find the new farthest closest
					for (int i = 0; i < closestFive.length; i++)
					{
						if (closestFive[indexOfFarthestClosest] != null && (closestFive[i] == null ||
							curCity.DistanceTo(closestFive[i]) > curCity.DistanceTo(closestFive[indexOfFarthestClosest])))
						{
							indexOfFarthestClosest = i;
						}
					}
				}
			}
			
			ShuffleArray(closestFive);
				
			for (int i = 0; i < 3; i++)
			{
				simpleAdjacencyMat[closestFive[i].cityIndex][curCity.cityIndex] = true;
			}
		}
	}
	
	public static void GenerateWeightedAdjacencyMatrix()
	{
		for (int i = 0; i < simpleAdjacencyMat.length; i++)
		{
			for (int j = 0; j < simpleAdjacencyMat[i].length; j++)
			{
				if (simpleAdjacencyMat[i][j] == true)
				{
					weightedAdjacencyMat[i][j] = cities[i].DistanceTo(cities[j]);
				}
				else
				{
					weightedAdjacencyMat[i][j] = 0.0;
				}
			}
		}
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
	
	//mainly for debug purposes
	public static void PrintSimpleAdjacencyMatrix()
	{
		for (boolean[] row : simpleAdjacencyMat) {
			for (boolean bool : row) {
				System.out.print(((bool)? 1 : 0));
			}
			System.out.println();
		}
	}
	
	//mainly for debug purposes
	public static void PrintWeightedAdjacencyMatrix()
	{
		DecimalFormat df = new DecimalFormat("000");
		
		for (double[] row : weightedAdjacencyMat) {
			for (double val : row) {
				System.out.print(df.format(val) + " ");
			}
			System.out.println();
		}
	}
	
	//uses Fisher-Yates shuffle
	static <T> void ShuffleArray(T[] ar)
	{
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			T a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}
}