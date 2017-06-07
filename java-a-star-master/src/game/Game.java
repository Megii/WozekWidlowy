package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.astar.Map;
import game.astar.Node;
import game.entity.Pack;
import game.entity.Player;
import weka.classifiers.trees.Id3;
import weka.core.Instance;
import weka.core.NoSupportForMissingValuesException;

public class Game extends JPanel implements MouseListener
{

	private Map map;
	private Player player;
	private Pack package1;
	private Pack package2;
	private Pack package3;
	private Pack package4;
	private List<Node> path;
	private DecisionTree decisionTree;
	private Id3 id3tree;
	private Image image;
	private Image pack;
	private Tour tour;
	
	//2 - czerwony
	//3 - zielony
	//4 - niebieski
	//5 - pomaranczonwy

	int[][] m0 =  {// 0	 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
					{ 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 3, 3, 3, 1, 3, 3, 3, 3, 1},//0
					{ 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1},//1
					{ 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 3, 3, 3, 1, 3, 3, 3, 3, 1},//2
					{ 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1},//3
					{ 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 3, 3, 3, 1, 3, 3, 3, 3, 1},//4
					{ 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1},//5
					{ 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 3, 3, 3, 1, 3, 3, 3, 3, 1},//6
					{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //7
					{ 1, 4, 1, 1, 4, 1, 5, 1, 5, 1, 3, 1, 3, 1, 2, 1, 2, 2, 1},//8
					{ 1, 4, 4, 4, 4, 1, 5, 1, 5, 1, 3, 3, 3, 1, 2, 1, 2, 2, 1},//9
					{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //10
					{ 1, 4, 4, 1, 4, 1, 4, 4, 4, 1, 5, 5, 5, 1, 5, 1, 5, 5, 1},//11
					{ 1, 4, 1, 1, 4, 1, 4, 1, 1, 1, 1, 1, 1, 1, 5, 1, 1, 5, 1},//12
					{ 1, 4, 4, 1, 4, 1, 4, 4, 4, 1, 5, 5, 5, 1, 5, 5, 1, 5, 1},//13
					{ 1, 4, 1, 1, 4, 1, 4, 1, 1, 1, 1, 1, 1, 1, 5, 5, 1, 5, 1},//14
					{ 1, 4, 4, 4, 4, 1, 4, 4, 4, 1, 5, 5, 5, 1, 5, 5, 1, 5, 1},//15
					 };	


	public Game()
	{

		int[][] m = m0;

		setPreferredSize(new Dimension(m[0].length * 60, m.length * 60));
		addMouseListener(this);
		
		decisionTree = new DecisionTree("input/dane.arff");		
		id3tree = decisionTree.trainTheTree();
		System.out.println(id3tree.toString());
		
		try {
			image = ImageIO.read(new File("input/wozek.png"));
			pack = ImageIO.read(new File("input/pack.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		map = new Map(m);
		player = new Player(9, 7);

		package1 = new Pack(4,1,"chemia","latwopalne","duza","mala","krotki","duza","duza","czerwony");
		package2 = new Pack(13,0,"zywnosc","nielatwopalne","mala","mala","dlugi","mala","mala","zielony");
		package3 = new Pack(2,14,"tekstylia","latwopalne","mala","mala","krotki","mala","mala","pomaranczowy");
		package4 = new Pack(10,14,"zywnosc","nielatwopalne","mala","duza","krotki","mala","mala","niebieski");

		package1 = new Pack(18,0,"zywnosc","nielatwopalne","mala","duza","krotki","mala","mala","niebieski");
		package2 = new Pack(2,14,"zywnosc","nielatwopalne","mala","mala","dlugi","mala","mala","zielony");
		package3 = new Pack(1,1,"tekstylia","latwopalne","mala","mala","krotki","mala","mala","pomaranczowy");
		package4 = new Pack(10,14,"chemia","latwopalne","duza","mala","krotki","duza","duza","czerwony");

		TourManager.addPack(package1);
		TourManager.addPack(package2);
		TourManager.addPack(package3);
		TourManager.addPack(package4);
		
		  Population pop = new Population(50, true);
	        System.out.println("Initial distance: " + pop.getFittest().getDistance());

	        // Evolve population for 100 generations
	        pop = GA.evolvePopulation(pop);
	        for (int i = 0; i < 100; i++) {
	            pop = GA.evolvePopulation(pop);
	        }
	        
	        tour = pop.getFittest();

	        // Print final results
	        System.out.println("Finished");
	        System.out.println("Final distance: " + tour.getDistance());
	        System.out.println("Solution:");
	        System.out.println(tour);
	        
	        
	    }
	

	public void update()
	{
		player.update();
		
		
		if(compareLocation(player.getX(),package1.getX()) && compareLocation(player.getY(),package1.getY())){
			deliverPackage(package1);
			
			
							
		}
		if(compareLocation(player.getX(),package2.getX()) && compareLocation(player.getY(),package2.getY())){
			deliverPackage(package2);
			
		
		}
		if(compareLocation(player.getX(),package3.getX()) && compareLocation(player.getY(),package3.getY())){
			deliverPackage(package3);
			
			
		}
		if(compareLocation(player.getX(),package4.getX()) && compareLocation(player.getY(),package4.getY())){
			deliverPackage(package4);
			
			
		}
		
	}

	public void render(Graphics2D g)
	{
		map.drawMap(g, path, m0);
		g.setColor(Color.GRAY);
		for (int x = 0; x < getWidth(); x += 60)
		{
			g.drawLine(x, 0, x, getHeight());
		}
		for (int y = 0; y < getHeight(); y += 60)
		{
			g.drawLine(0, y, getWidth(), y);
		}
		
		g.drawImage(pack, package1.getX()*60, package1.getY()*60, 60, 60, Color.RED, null);
		g.drawImage(pack, package2.getX()*60, package2.getY()*60, 60, 60, Color.RED, null);
		g.drawImage(pack, package3.getX()*60, package3.getY()*60, 60, 60, Color.RED, null);
		g.drawImage(pack, package4.getX()*60, package4.getY()*60, 60, 60, Color.RED, null);
		/*g.setColor(Color.PINK);
		g.fillRect(package1.getX()*60, package1.getY()*60, 60, 60);
		g.fillRect(package2.getX()*60, package2.getY()*60, 60, 60);
		g.fillRect(package3.getX()*60, package3.getY()*60, 60, 60);
		g.fillRect(package4.getX()*60, package4.getY()*60, 60, 60);*/
		
		g.drawImage(image, player.getX() * 60 + player.getSx(), player.getY() * 60 + player.getSy(), 60, 60, Color.RED, null);
		/*g.setColor(Color.RED);
		g.fillRect(player.getX() * 60 + player.getSx(), player.getY() * 60 + player.getSy(), 60, 60);*/
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@SuppressWarnings("static-access")
	@Override
	public void mousePressed(MouseEvent e)
	{
		
		
			path = map.findPath(player.getX(), player.getY(), tour.getPack(0).getX(),tour.getPack(0).getY());
			player.followPath(path);
		
		
		/*int mx = e.getX() /60;
		int my = e.getY() /60;
		if (map.getNode(mx, my).isWalkable())
		{
			path = map.findPath(player.getX(), player.getY(), mx, my);
			player.followPath(path);
			
		}
		else
		{
			System.out.println("Can't walk to that node!");
		}*/
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	
	public boolean compareLocation(int x , int y){
		if(x==y) return true;
		else return false;
	}
	
	
	public int [] nearestPoint(int px, int py,int color){
		
		int minDist = 1000;
		int tempX = 0,tempY = 0;
		int destX = 0, destY = 0;
		
		
		for(int y=0;y<m0.length;y++){
			for(int x=0; x<m0[0].length; x++){
				int dist = 0;
				if(m0[y][x]!=0 && m0[y][x] == color){
					if (x+1<m0[0].length && map.getNode(x+1, y).isWalkable())
					{
						dist = map.findPath(px, py, x+1,y).size();
						if(minDist>dist){
							tempX = x+1;
							tempY = y;
							destX = x;
							destY = y;
							minDist = dist;
						}	
					
					}
					else if (x-1>= 0 && map.getNode(x-1,y).isWalkable())
					{
						dist = map.findPath(px, py, x-1, y).size();
					
						if(minDist>dist){
							tempX = x-1;
							tempY = y;
							destX = x;
							destY = y;
							minDist = dist;
						}	
					}
					else if (y+1<m0.length && map.getNode(x, y+1).isWalkable() )
					{
						dist = map.findPath(px, py,x, y+1).size();
						if(minDist>dist){
							tempX = x;
							tempY = y+1;
							destX = x;
							destY = y;
							minDist = dist;
						}	
					
					}
					else if (y-1>=0 && map.getNode(x, y-1).isWalkable())
					{
						dist = map.findPath(px, py, x, y-1).size();
						if(minDist>dist){
							tempX = x;
							tempY = y-1;
							destX = x;
							destY = y;
							minDist = dist;
						}	
					
					}
								
			}
		}
		
		
		
		}
		int tab[] = {tempX,tempY,destX,destY};
		return tab;
	}
	
	public void deliverPackage(Pack pack) {
		

		Instance testInstance = decisionTree.prepareTestInstance(pack.getTyp(), pack.getPalnosc(),
				pack.getWrazliwosc(),pack.getOdpornosc(),pack.getTermin(),pack.getWaga(),pack.getWielkosc(), pack.getRezultat());



		int result = 0;
		try {
			result = (int) id3tree.classifyInstance(testInstance);
		} catch (NoSupportForMissingValuesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String readableResult = decisionTree.getTrainingData().attribute(7).value(result);
		System.out.println(" ----------------------------------------- ");
		System.out.println("Test data               : " + testInstance);
		System.out.println("Test data classification: " + readableResult);
		
		int resultP[] = null;
		
		switch(readableResult){
		//2 - czerwony
		//3 - zielony
		//4 - niebieski
		//5 - pomaranczonwy
		case "czerwony":
			resultP = nearestPoint(pack.getX(),pack.getY(),2);
			break;
		case "zielony":
			resultP = nearestPoint(pack.getX(),pack.getY(),3);
			break;
		case "niebieski":
			resultP = nearestPoint(pack.getX(),pack.getY(),4);
			break;
		case "pomaranczowy":
			resultP = nearestPoint(pack.getX(),pack.getY(),5);
			break;
		}
		System.out.println(resultP[0]+" "+resultP[1]);
		path = map.findPath(player.getX(), player.getY(), resultP[0], resultP[1]);
		player.followPath(path);
		
		tour.remove(0);
		pack.setX(resultP[2]);
		pack.setY(resultP[3]);
	}
	

}
