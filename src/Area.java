import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;



public class Area extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int[][] board={
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,4,4,4,4,4,0,0,0,4,0,4,0,0,0,0,0,0},
			{0,0,0,0,4,4,4,4,4,4,4,4,0,0,0,0,3,0,4,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,4,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,0,0,0,0,0},
			{0,0,0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,0},
			{0,0,0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0},
			{0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,0},
			{0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,4,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,4,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,0,0,0,0,0,0,0,0,0},
			{0,4,4,4,4,4,4,4,0,0,0,0,4,4,4,4,4,4,4,4,4,4,0,0,0},
			{0,5,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
	  };
	
	private int columns = board[0].length;
	private int rows = board.length;
	
	
	
	public void render(Graphics g)
	{
		 for(int i=0; i<rows; i++)
		  {
			  for(int j=0; j<columns;j++)
			  {
				  
				  switch(board[i][j])
				  {
				  
				  	case 0:
				  		g.drawImage(Magazyn.floor,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  
				  	case 4:
				  		g.drawImage(Magazyn.wall,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  
				  	case 1:
				  		g.drawImage(Magazyn.shelf1,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  		
				  	case 2:
				  		g.drawImage(Magazyn.shelf2,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  		
				  	case 3:
				  		g.drawImage(Magazyn.shelf3,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  	case 5:
				  		g.drawImage(Magazyn.shelf4,j*Magazyn.WIDTH/columns,i*Magazyn.HEIGHT/rows, Magazyn.WIDTH/columns+1, Magazyn.HEIGHT/rows+1,null);
				  		break;
				  
				  		
				  }
					 
			  }
		  }
		 }

}
