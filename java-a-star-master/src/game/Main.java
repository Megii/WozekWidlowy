package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import gui.TrainGui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

public class Main
{

	public static final int WIDTH = 1260;
	public static final int HEIGHT = 1380;
	public static final String NAME = "Magazyn";
	
	public static TrainGui trainGui;

	private static BufferedImage image;
	private static Graphics2D g;
	private static boolean forceQuit;
	
	private static JButton button;

	private static Game game;

	private static void init()
	{
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setBackground(Color.BLACK);

		game = new Game();
		button = new JButton("Zmieñ symbol");
		button.setPreferredSize(new Dimension(200,1000));
		button.addActionListener(e -> {
           trainGui = new TrainGui();
        });
	}

	private static void start()
	{
		run();
	}

	public static void stop()
	{
		forceQuit = true;
	}

	public static void run()
	{
		@SuppressWarnings("unused")
		int frames = 0;

		double unprocessedSeconds = 0;
		long lastTime = System.nanoTime();
		double secondsPerTick = 1.0 / 60.0;
		int tickCount = 0;

		while (!forceQuit)
		{
			long now = System.nanoTime();
			long passedTime = now - lastTime;
			lastTime = now;
			if (passedTime < 0)
				passedTime = 0;
			if (passedTime > 100000000)
				passedTime = 100000000;

			unprocessedSeconds += passedTime / 1000000000.0;

			boolean ticked = false;
			while (unprocessedSeconds > secondsPerTick)
			{
				game.update();
				unprocessedSeconds -= secondsPerTick;
				ticked = true;

				tickCount++;
				if (tickCount % 60 == 0)
				{
					// System.out.println("FPS:" + frames);
					lastTime += 1000;
					frames = 0;
				}
			}

			if (ticked)
			{
				game.render(g);

				Graphics gg = game.getGraphics();
				gg.drawImage(image, 0, 0, null);
				gg.dispose();

				frames++;
			}
			else
			{
				try
				{
					Thread.sleep(1);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	


	public static void main(String[] args)
	{
		Main.init();

		JFrame frame = new JFrame(NAME);
	    frame.setPreferredSize(new Dimension(1366, 760));
		
		JPanel panel = new JPanel();
		panel.add(game);
		panel.add(button);

		
		frame.setContentPane(panel);
		
		//frame.add(button);
		frame.setDefaultCloseOperation(3);
		//frame.setContentPane(game);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.getTour().getPack(0);
	
		Main.start();
		
	}

}
