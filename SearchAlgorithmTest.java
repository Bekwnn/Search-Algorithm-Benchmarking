import java.util.Random;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;

public class SearchAlgorithmTest
{
	static final int NUMBEROFCITIES = 26;
	static final float ADJACENCYCHANCE = 0.4f;
	
	static City[] cities = new City[NUMBEROFCITIES];
	static boolean[][] simpleAdjacencyMat = new boolean[NUMBEROFCITIES][NUMBEROFCITIES];
	static double[][] weightedAdjacencyMat = new double[NUMBEROFCITIES][NUMBEROFCITIES];
	
	static Random randomGenerator;
	
	public static void main(String[] args)
	{
		long[] dfsTimes = new long[100];
		long[] idsTimes = new long[100];
		long[] bfsTimes = new long[100];
		long[] aStarTimes = new long[100];
		long[] aStarAltTimes = new long[100];
		
		for (int i = 0; i < 100; i++)
		{
			GenerateCities();
			
			//use only one of the two below:
			GenerateLocalSimpleAdjacencyMatrix();
			//GenerateRandomSimpleAdjacencyMatrix();
			
			GenerateWeightedAdjacencyMatrix();
			
			//PrintMap();
			//PrintSimpleAdjacencyMatrix();
			//PrintWeightedAdjacencyMatrix();
			
			City startCity = cities[randomGenerator.nextInt(26)];
			City goalCity = cities[randomGenerator.nextInt(26)];
			
			//depth-first search:
			long dfsStartTime = System.nanoTime();
			Stack<City> dfsRoute = DepthFirstToDepth(startCity, goalCity, 0);
			long dfsEndTime = System.nanoTime();
			dfsTimes[i] = dfsEndTime - dfsStartTime;
			
			System.out.print("DFS:\n\t");
			PrintRoute(dfsRoute);
			System.out.println("\tTime: " + dfsTimes[i]);
			
			
			//iterative deepening search:
			long idsStartTime = System.nanoTime();
			Stack<City> iterativeRoute = IterativeDeepeningSearch(startCity, goalCity);
			long idsEndTime = System.nanoTime();
			idsTimes[i] = idsEndTime - idsStartTime;
			
			System.out.print("IDS:\n\t");
			PrintRoute(iterativeRoute);
			System.out.println("\tTime: " + idsTimes[i]);
			
			
			//breadth-first search:
			long bfsStartTime = System.nanoTime();
			String result = (BreadthFirstSearch(startCity, goalCity))? "Found a path." : "No path exists.";
			long bfsEndTime = System.nanoTime();
			bfsTimes[i] = bfsEndTime - bfsStartTime;
			
			System.out.println("BFS:\n\t" + result);
			System.out.println("\tTime: " + bfsTimes[i]);
			
			
			//A STAR, MOTHERFUCKERS, WOOO
			long aStarStartTime = System.nanoTime();
			String result2 = (AStar(startCity, goalCity, false))? "Found a path." : "No path exists.";
			long aStarEndTime = System.nanoTime();
			aStarTimes[i] = aStarEndTime - aStarStartTime;
			
			System.out.println("A*:\n\t" + result2);
			System.out.println("\tTime: " + aStarTimes[i]);
			
			//A* alternate heuristic, basically euclidean, but peaks ahead 1 step
			long aStarAltStartTime = System.nanoTime();
			String result3 = (AStar(startCity, goalCity, true))? "Found a path." : "No path exists.";
			long aStarAltEndTime = System.nanoTime();
			aStarAltTimes[i] = aStarAltEndTime - aStarAltStartTime;
			
			System.out.println("Al*:\n\t" + result3);
			System.out.println("\tTime: " + aStarAltTimes[i]);
			
		}
		System.out.println("Min for DFS: " + FindMin(dfsTimes));
		System.out.println("Min for BFS: " + FindMin(bfsTimes));
		System.out.println("Min for IDS: " + FindMin(idsTimes));
		System.out.println("Min for  A*: " + FindMin(aStarTimes));
		System.out.println("Min for AL*: " + FindMin(aStarAltTimes));
		
		System.out.println("Max for DFS: " + FindMax(dfsTimes));
		System.out.println("Max for BFS: " + FindMax(bfsTimes));
		System.out.println("Max for IDS: " + FindMax(idsTimes));
		System.out.println("Max for  A*: " + FindMax(aStarTimes));
		System.out.println("Max for AL*: " + FindMax(aStarAltTimes));
		
		System.out.println("Average for DFS: " + ComputeLongAverage(dfsTimes));
		System.out.println("Average for BFS: " + ComputeLongAverage(bfsTimes));
		System.out.println("Average for IDS: " + ComputeLongAverage(idsTimes));
		System.out.println("Average for  A*: " + ComputeLongAverage(aStarTimes));
		System.out.println("Average for AL*: " + ComputeLongAverage(aStarAltTimes));

		
	}
	
	public static double ComputeLongAverage(long[] array)
	{
		double avg = 0;
		for (int i = 0; i < 100; i++)
		{
			avg += array[i];
		}
		return avg/100;
	}
	
	public static long FindMin(long[] array)
	{
		long min = Long.MAX_VALUE;
		for (long l : array)
		{
			if (l < min)
				min = l;
		}
		
		return min;
	}
	
	public static long FindMax(long[] array)
	{
		long max = Long.MIN_VALUE;
		for (long l : array)
		{
			if (l > max)
				max = l;
		}
		
		return max;
	}
	
	public static boolean AStar(City startCity, City goalCity, boolean altHeuristic)
	{
		boolean[] visited = new boolean[NUMBEROFCITIES];
		ArrayList<City> openSet = new ArrayList<City>();
		openSet.add(startCity);
		//cameFrom
		
		double[] gScore = new double[NUMBEROFCITIES];
		double[] fScore = new double[NUMBEROFCITIES];
		//initiate scores to infinity
		for (int i = 0; i < NUMBEROFCITIES; i++)
		{
			gScore[i] = Double.MAX_VALUE;
			fScore[i] = Double.MAX_VALUE;
		}
		
		gScore[startCity.cityIndex] = 0;
		fScore[startCity.cityIndex] = gScore[startCity.cityIndex] +
			((altHeuristic)? BestNeighborCost(startCity, goalCity) : EuclidianCost(startCity, goalCity));
		
		while (!openSet.isEmpty())
		{
			//get city with smallest fScore
			int minIndex = openSet.get(0).cityIndex;
			for (City aCity : openSet)
			{
				if (fScore[aCity.cityIndex] < fScore[minIndex])
					minIndex = aCity.cityIndex;
			}
			City curCity = cities[minIndex];
			
			if (curCity == goalCity) return true; //change later
			
			//remove curCity from openSet, mark as visited
			openSet.remove(curCity);
			visited[curCity.cityIndex] = true;
			
			//get neighbors of current
			ArrayList<City> neighbors = GetUnvisitedNeighbors(curCity, visited, false);
			for (City neighbor : neighbors)
			{
				double tentativeGScore = gScore[curCity.cityIndex] + curCity.DistanceTo(neighbor);
				
				if (!openSet.contains(neighbor) || tentativeGScore < gScore[neighbor.cityIndex])
				{
					gScore[neighbor.cityIndex] = tentativeGScore;
					fScore[neighbor.cityIndex] = gScore[neighbor.cityIndex] +
						((altHeuristic)? BestNeighborCost(startCity, goalCity) : EuclidianCost(startCity, goalCity));
					
					if (!openSet.contains(neighbor))
						openSet.add(neighbor);
				}
			}
		}
		
		return false;
	}
	
	public static double EuclidianCost(City start, City goal)
	{
		return start.DistanceTo(goal);
	}
	
	public static double BestNeighborCost(City start, City goal)
	{
		boolean[] visited = new boolean[NUMBEROFCITIES]; //all false
		ArrayList<City> neighbors = GetUnvisitedNeighbors(start, visited, false);
		if (neighbors.isEmpty()) return Double.MAX_VALUE; //node has no neighbors
		City closest = neighbors.get(0);
		
		for (City neighbor : neighbors)
		{
			if (neighbor.DistanceTo(goal) < closest.DistanceTo(goal))
			{
					closest = neighbor;
			}
		}
		
		return closest.DistanceTo(goal);
	}
	
	//merely returns success or failure
	public static boolean BreadthFirstSearch(City startCity, City goalCity)
	{
		boolean[] visited = new boolean[NUMBEROFCITIES];
		LinkedList<City> searchQueue = new LinkedList<City>();
		
		//breadth search from first city
		visited[startCity.cityIndex] = true;
		searchQueue.addLast(startCity);
		
		while (!searchQueue.isEmpty())
		{
			City curCity = searchQueue.pop();
			
			ArrayList<City> neighbors = GetUnvisitedNeighbors(curCity, visited, false);
			
			for (City someNeighbor : neighbors)
			{
				searchQueue.addLast(someNeighbor);
				visited[someNeighbor.cityIndex] = true;
				
				//if we found our goal, return true
				if (someNeighbor == goalCity) return true;
			}
		}
		
		return false;
	}
	
	//calls DepthFirstToDepth repeatedly, incrementing the depth
	public static Stack<City> IterativeDeepeningSearch(City startCity, City goalCity)
	{
		Stack<City> retStack;
		int depth = 1;
		do
		{
			retStack = DepthFirstToDepth(startCity, goalCity, depth);
			depth++;
		} while (retStack == null);
		return retStack;
	}
	
	//returns route to goal if success
	//returns empty stack if no path failure
	//returns null if depth limit reached
	//- toDepth of 0 means regular depth first search
	public static Stack<City> DepthFirstToDepth(City startCity, City goalCity, int toDepth)
	{
		boolean[] visited = new boolean[NUMBEROFCITIES];
		if (toDepth < 1) toDepth = Integer.MAX_VALUE;
		Stack<City> searchStack = new Stack<City>();
		
		//depth search from first city
		int currentDepth = 0;
		boolean depthLimitReached = false;
		visited[startCity.cityIndex] = true;
		searchStack.push(startCity);
		
		while (!searchStack.empty())
		{
			//PrintRoute(searchStack);
			City curCity = searchStack.peek();
			
			//if the current city is the goal city, we've reached a success
			if (curCity == goalCity)
			{
				return searchStack;
			}
			
			//if we're not at max depth, look for nearest neighbor
			ArrayList<City> neighbor = new ArrayList<City>();
			if (currentDepth < toDepth)
			{
				neighbor = GetUnvisitedNeighbors(curCity, visited, false);
			}
			else
			{
				depthLimitReached = true;
			}
			
			//if there is a neighbor push it on the stack and add 1 to current depth
			if (!neighbor.isEmpty())
			{
				//TODO: currently fast for A to B and really slow for A to Z,
				//	since it prioritizes which neighbor to visit 'alphabetically'
				
				//NOTE: may perform worse as has to get all neighbors, test against alphabetical
				int neighborToVisit = randomGenerator.nextInt(neighbor.size());
				searchStack.push(neighbor.get(neighborToVisit));
				visited[neighbor.get(neighborToVisit).cityIndex] = true;
				
				//searchStack.push(neighbor.get(0));
				//visited[neighbor.get(0).cityIndex] = true;
				
				currentDepth++;
			}
			//otherwise pop the stack
			else
			{
				searchStack.pop();
				currentDepth--;
			}
		}
		
		if (depthLimitReached)
		{
			return null;
		}
		else
		{
			return searchStack;
		}
	}
	
	public static ArrayList<City> GetUnvisitedNeighbors(City city, boolean[] visited, boolean singular)
	{
		ArrayList<City> retList = new ArrayList<City>();
		
		for (int i = 0; i < simpleAdjacencyMat.length; i++)
		{
			if (simpleAdjacencyMat[city.cityIndex][i] == true && visited[i] == false)
			{
				retList.add(cities[i]);
				if (singular) break;
			}
		}
		
		return retList;
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
				if (randomGenerator.nextFloat() < ADJACENCYCHANCE && i != j)
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
				
			for (int i = 0; i < closestFive.length; i++)
			{
				if (i <= 3)
				{
					simpleAdjacencyMat[closestFive[i].cityIndex][curCity.cityIndex] = true;
				}
				else
				{
					simpleAdjacencyMat[closestFive[i].cityIndex][curCity.cityIndex] = false;
				}
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
	public static <T> void ShuffleArray(T[] ar)
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
	
	public static void PrintRoute(Stack<City> stack)
	{
		if (stack == null)
		{
			System.out.println("Stack is null.");
			return;
		}
		
		ArrayList<City> copy = new ArrayList<City>(stack);
		if (copy.isEmpty())
		{
			System.out.print("No route.");
		}
		
		for (City curCity : copy)
		{
			System.out.print(curCity.name + " ");
		}
		
		System.out.println();
	}
}