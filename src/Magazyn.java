import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

public class Magazyn extends Canvas implements Runnable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String TITLE = "Magazyn";
	public static int WIDTH = 1200;
	public static int HEIGHT = 900;
	private boolean running = false;
	public static Thread thread;
	
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	static BufferedImage wall = null;
	static BufferedImage floor = null;
	static BufferedImage shelf1 = null;
	static BufferedImage shelf2 = null;
	static BufferedImage shelf3 = null;
	static BufferedImage shelf4 = null;
	
	
	private Area area;
	
	private synchronized void start()
	{
		if(running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop()
	{
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread.start();
		System.exit(1);
	}
	
	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	
		
		area.render(g);
		
	
	 g.dispose();
	 bs.show();
	 
	}
	
	private void init()
	{
		BufferedImageLoader loader = new BufferedImageLoader();
		try
		{
			
			wall = loader.loadImage("/wall.png");
			floor = loader.loadImage("/floor.png");
			shelf1 = loader.loadImage("/shelf1.png");
			shelf2 = loader.loadImage("/shelf2.png");
			shelf3 = loader.loadImage("/shelf3.png");
			shelf4 = loader.loadImage("/shelf4.png");
		
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		area = new Area();

	}
	
	
	private void tick()
	{
		
	}
	
	
	public void run()
	{
		init();
		
		while(running)
		{
		
			render();
			
			
		}
		
		stop();
	}
	
	
	public static void main(String[] args)
	{
		Magazyn magazyn = new Magazyn();
		
		magazyn.setPreferredSize(new Dimension(WIDTH , HEIGHT ));
		magazyn.setMaximumSize(new Dimension(WIDTH , HEIGHT ));
		magazyn.setMinimumSize(new Dimension(WIDTH , HEIGHT ));
		
		JFrame frame = new JFrame(Magazyn.TITLE);
		frame.add(magazyn);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		magazyn.start();
		
		
		
	}
	
	
}
