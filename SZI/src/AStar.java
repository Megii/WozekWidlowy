
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.*;
 

 
public class AStar {
 
    public static JFrame mazeFrame;  // The main form of the program
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int width  = 993;
        int height = 845;
        mazeFrame = new JFrame("Wózek widłowy");
        mazeFrame.setContentPane(new MazePanel(width,height));
        mazeFrame.pack();
        mazeFrame.setResizable(false);
 
        // the form is located in the center of the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = screenSize.getWidth();
        double ScreenHeight = screenSize.getHeight();
        int x = ((int)screenWidth-width)/2;
        int y = ((int)ScreenHeight-height)/2;
 
        mazeFrame.setLocation(x,y);
        mazeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mazeFrame.setVisible(true);
    } // end main()
     
    /**
      * This class defines the contents of the main form
      * and contains all the functionality of the program.
      */
    public static class MazePanel extends JPanel {
         
        /*
         **********************************************************
         *          Nested classes in MazePanel
         **********************************************************
         */
         
        /**
         * Helper class that represents the cell of the grid
         */
        private class Cell {
            int row;   // the row number of the cell(row 0 is the top)
            int col;   // the column number of the cell (Column 0 is the left)
            int g;     // the value of the function g of A* and Greedy algorithms
            int h;     // the value of the function h of A* and Greedy algorithms
            int f;     // the value of the function h of A* and Greedy algorithms
            int dist;  // the distance of the cell from the initial position of the robot
                       // Ie the label that updates the Dijkstra's algorithm
            Cell prev; // Each state corresponds to a cell
                       // and each state has a predecessor which
                       // is stored in this variable
             
            public Cell(int row, int col){
               this.row = row;
               this.col = col;
            }
        } // end nested class Cell
       
        /**
         * Auxiliary class that specifies that the cells will be sorted
         * according their 'f' field
         */
        private class CellComparatorByF implements Comparator<Cell>{
            @Override
            public int compare(Cell cell1, Cell cell2){
                return cell1.f-cell2.f;
            }
        } // end nested class CellComparatorByF
       
        /**
         * Auxiliary class that specifies that the cells will be sorted
         * according their 'dist' field
         */
        private class CellComparatorByDist implements Comparator<Cell>{
            @Override
            public int compare(Cell cell1, Cell cell2){
                return cell1.dist-cell2.dist;
            }
        } // end nested class CellComparatorByDist
       
        /**
         * Class that handles mouse movements as we "paint"
         * obstacles or move the robot and/or target.
         */
        private class MouseHandler implements MouseListener, MouseMotionListener {
            private int cur_row, cur_col, cur_val;
            @Override
            public void mousePressed(MouseEvent evt) {
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                if (row >= 0 && row < rows && col >= 0 && col < columns) {
                    if (realTime ? true : !found && !searching){
 
                        if (realTime) {
                            searching = true;
                            fillGrid();
                        }
                        cur_row = row;
                        cur_col = col;
                        cur_val = grid[row][col];
                        if (cur_val == EMPTY){
                            grid[row][col] = OBST;
                        }
                        if (cur_val == OBST){
                            grid[row][col] = EMPTY;
                        }
                        if (realTime) {
                            if (dijkstra.isSelected()) {
                               initializeDijkstra();
                            }
                        }
                    }
                }
                if (realTime) {
                    timer.setDelay(0);
                    timer.start();
                    checkTermination();
                } else {
                    repaint();
                }
            }
 
            @Override
            public void mouseDragged(MouseEvent evt) {
                int row = (evt.getY() - 10) / squareSize;
                int col = (evt.getX() - 10) / squareSize;
                if (row >= 0 && row < rows && col >= 0 && col < columns){
                    if (realTime ? true : !found && !searching){
                        if (realTime) {
                            searching = true;
                            fillGrid();
                        }
                        if ((row*columns+col != cur_row*columns+cur_col) && (cur_val == ROBOT || cur_val == TARGET)){
                            int new_val = grid[row][col];
                            if (new_val == EMPTY){
                                grid[row][col] = cur_val;
                                if (cur_val == ROBOT) {
                                    robotStart.row = row;
                                    robotStart.col = col;
                                } else {
                                    targetPos.row = row;
                                    targetPos.col = col;
                                }
                                grid[cur_row][cur_col] = new_val;
                                cur_row = row;
                                cur_col = col;
                                if (cur_val == ROBOT) {
                                    robotStart.row = cur_row;
                                    robotStart.col = cur_col;
                                } else {
                                    targetPos.row = cur_row;
                                    targetPos.col = cur_col;
                                }
                                cur_val = grid[row][col];
                            }
                        } else if (grid[row][col] != ROBOT && grid[row][col] != TARGET){
                            grid[row][col] = OBST;
                        }
                        if (realTime) {
                            if (dijkstra.isSelected()) {
                               initializeDijkstra();
                            }
                        }
                    }
                }
                if (realTime) {
                    timer.setDelay(0);
                    timer.start();
                    checkTermination();
                } else {
                    repaint();
                }
            }
 
            @Override
            public void mouseReleased(MouseEvent evt) { }
            @Override
            public void mouseEntered(MouseEvent evt) { }
            @Override
            public void mouseExited(MouseEvent evt) { }
            @Override
            public void mouseMoved(MouseEvent evt) { }
            @Override
            public void mouseClicked(MouseEvent evt) { }
             
        } // end nested class MouseHandler
         
        /**
         * When the user presses a button performs the corresponding functionality
         */
        private class ActionHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String cmd = evt.getActionCommand();
                if (cmd.equals("Clear")) {
                    fillGrid();
                    realTime = false;
                    realTimeButton.setEnabled(true);
                    realTimeButton.setForeground(Color.black);
                    stepButton.setEnabled(true);
                    animationButton.setEnabled(true);
                    slider.setEnabled(true);
                    aStar.setEnabled(true);
                } else if (cmd.equals("Poukładaj paczki") && !realTime) {
                    realTime = true;
                    searching = true;
                    realTimeButton.setForeground(Color.red);
                    stepButton.setEnabled(false);
                    animationButton.setEnabled(false);
                    slider.setEnabled(false);
                    aStar.setEnabled(false);
                    timer.setDelay(0);
                    timer.start();
                    if (dijkstra.isSelected()) {
                       initializeDijkstra();
                    }
                    checkTermination();
                } else if (cmd.equals("Step-by-Step") && !found && !endOfSearch) {
                    realTime = false;
                    // The Dijkstra's initialization should be done just before the
                    // start of search, because obstacles must be in place.
                    if (!searching && dijkstra.isSelected()) {
                        initializeDijkstra();
                    }
                    searching = true;
                    message.setText(msgSelectStepByStepEtc);
                    realTimeButton.setEnabled(false);
                    aStar.setEnabled(false);
                    timer.stop();
                    // Here we decide whether we can continue the
                    // 'Step-by-Step' search or not.
                    // In the case of DFS, BFS, A* and Greedy algorithms
                    // here we have the second step:
                    // 2. If OPEN SET = [], then terminate. There is no solution.
                    checkTermination();
                    repaint();
                } else if (cmd.equals("Animacja ruchu") && !endOfSearch) {
                    realTime = false;
                    if (!searching && dijkstra.isSelected()) {
                        initializeDijkstra();
                    }
                    searching = true;
                    message.setText(msgSelectStepByStepEtc);
                    realTimeButton.setEnabled(false);
                    aStar.setEnabled(false);
                    timer.setDelay(delay);
                    timer.start();
                }
            }
        } // end nested class ActionHandler
    
        /**
         * The class that is responsible for the animation
         */
        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // Here we decide whether we can continue or not
                // the search with 'Animation'.
                // In the case of DFS, BFS, A* and Greedy algorithms
                // here we have the second step:
                // 2. If OPEN SET = [], then terminate. There is no solution.
                checkTermination();
                if (found) {
                    timer.stop();
                }
                if (!realTime) {
                    repaint();
                }
            }
        } // end nested class RepaintAction
       
        public void checkTermination() {
            if ((dijkstra.isSelected() && graph.isEmpty()) ||
                          (!dijkstra.isSelected() && openSet.isEmpty()) ) {
                endOfSearch = true;
                grid[robotStart.row][robotStart.col]=ROBOT;
                message.setText(msgNoSolution);
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
                slider.setEnabled(false);
                repaint();
            } else {
                expandNode();
                if (found) {
                    endOfSearch = true;
                    plotRoute();
                    stepButton.setEnabled(false);
                    animationButton.setEnabled(false);
                    slider.setEnabled(false);
                    repaint();
                }
            }
        }

 
        /**
         * Creates a random, perfect (without cycles) maze
         * 
         * The code of the class is an adaptation, with the original commentary, of the answer given
         * by user DoubleMx2 on August 25 to a question posted by user nazar_art at stackoverflow.com:
         * http://stackoverflow.com/questions/18396364/maze-generation-arrayindexoutofboundsexception
         */
        private class MyMaze {
            private int dimensionX, dimensionY; // dimension of maze
            private int gridDimensionX, gridDimensionY; // dimension of output grid
            private char[][] mazeGrid; // output grid
            private Cell[][] cells; // 2d array of Cells
            private Random random = new Random(); // The random object
 
            // initialize with x and y the same
            public MyMaze(int aDimension) {
                // Initialize
                this(aDimension, aDimension);
            }
            // constructor
            public MyMaze(int xDimension, int yDimension) {
                dimensionX = xDimension;
                dimensionY = yDimension;
                gridDimensionX = xDimension * 2 + 1;
                gridDimensionY = yDimension * 2 + 1;
                mazeGrid = new char[gridDimensionX][gridDimensionY];
                init();
                generateMaze();
            }
 
            private void init() {
                // create cells
                cells = new Cell[dimensionX][dimensionY];
                for (int x = 0; x < dimensionX; x++) {
                    for (int y = 0; y < dimensionY; y++) {
                        cells[x][y] = new Cell(x, y, false); // create cell (see Cell constructor)
                    }
                }
            }
 
            // inner class to represent a cell
            private class Cell {
                int x, y; // coordinates
                // cells this cell is connected to
                ArrayList<Cell> neighbors = new ArrayList<>();
                // impassable cell
                boolean wall = true;
                // if true, has yet to be used in generation
                boolean open = true;
                // construct Cell at x, y
                Cell(int x, int y) {
                    this(x, y, true);
                }
                // construct Cell at x, y and with whether it isWall
                Cell(int x, int y, boolean isWall) {
                    this.x = x;
                    this.y = y;
                    this.wall = isWall;
                }
                // add a neighbor to this cell, and this cell as a neighbor to the other
                void addNeighbor(Cell other) {
                    if (!this.neighbors.contains(other)) { // avoid duplicates
                        this.neighbors.add(other);
                    }
                    if (!other.neighbors.contains(this)) { // avoid duplicates
                        other.neighbors.add(this);
                    }
                }
                // used in updateGrid()
                boolean isCellBelowNeighbor() {
                    return this.neighbors.contains(new Cell(this.x, this.y + 1));
                }
                // used in updateGrid()
                boolean isCellRightNeighbor() {
                    return this.neighbors.contains(new Cell(this.x + 1, this.y));
                }
                // useful Cell equivalence
                @Override
                public boolean equals(Object other) {
                    if (!(other instanceof Cell)) return false;
                    Cell otherCell = (Cell) other;
                    return (this.x == otherCell.x && this.y == otherCell.y);
                }
 
                // should be overridden with equals
                @Override
                public int hashCode() {
                    // random hash code method designed to be usually unique
                    return this.x + this.y * 256;
                }
 
            }
            // generate from upper left (In computing the y increases down often)
            private void generateMaze() {
                generateMaze(0, 0);
            }
            // generate the maze from coordinates x, y
            private void generateMaze(int x, int y) {
                generateMaze(getCell(x, y)); // generate from Cell
            }
            private void generateMaze(Cell startAt) {
                // don't generate from cell not there
                if (startAt == null) return;
                startAt.open = false; // indicate cell closed for generation
                ArrayList<Cell> cellsList = new ArrayList<>();
                cellsList.add(startAt);
 
                while (!cellsList.isEmpty()) {
                    Cell cell;
                    // this is to reduce but not completely eliminate the number
                    // of long twisting halls with short easy to detect branches
                    // which results in easy mazes
                    if (random.nextInt(10)==0)
                        cell = cellsList.remove(random.nextInt(cellsList.size()));
                    else cell = cellsList.remove(cellsList.size() - 1);
                    // for collection
                    ArrayList<Cell> neighbors = new ArrayList<>();
                    // cells that could potentially be neighbors
                    Cell[] potentialNeighbors = new Cell[]{
                        getCell(cell.x + 1, cell.y),
                        getCell(cell.x, cell.y + 1),
                        getCell(cell.x - 1, cell.y),
                        getCell(cell.x, cell.y - 1)
                    };
                    for (Cell other : potentialNeighbors) {
                        // skip if outside, is a wall or is not opened
                        if (other==null || other.wall || !other.open) continue;
                        neighbors.add(other);
                    }
                    if (neighbors.isEmpty()) continue;
                    // get random cell
                    Cell selected = neighbors.get(random.nextInt(neighbors.size()));
                    // add as neighbor
                    selected.open = false; // indicate cell closed for generation
                    cell.addNeighbor(selected);
                    cellsList.add(cell);
                    cellsList.add(selected);
                }
                updateGrid();
            }
            // used to get a Cell at x, y; returns null out of bounds
            public Cell getCell(int x, int y) {
                try {
                    return cells[x][y];
                } catch (ArrayIndexOutOfBoundsException e) { // catch out of bounds
                    return null;
                }
            }
            // draw the maze
            public void updateGrid() {
                char backChar = ' ', wallChar = 'X', cellChar = ' ';
                // fill background
                for (int x = 0; x < gridDimensionX; x ++) {
                    for (int y = 0; y < gridDimensionY; y ++) {
                        mazeGrid[x][y] = backChar;
                    }
                }
                // build walls
                for (int x = 0; x < gridDimensionX; x ++) {
                    for (int y = 0; y < gridDimensionY; y ++) {
                      
                            mazeGrid[0][1] = wallChar;
                            mazeGrid[0][2] = wallChar;
                            mazeGrid[0][3] = wallChar;
                            mazeGrid[0][4] = wallChar;
                            mazeGrid[0][6] = wallChar;
                            mazeGrid[0][7] = wallChar;
                            mazeGrid[0][8] = wallChar;
                            mazeGrid[0][10] = wallChar;
                            mazeGrid[0][11] = wallChar;
                            mazeGrid[0][12] = wallChar;
                            mazeGrid[0][14] = wallChar;
                            mazeGrid[0][15] = wallChar;
                            mazeGrid[0][16] = wallChar;
                            mazeGrid[0][17] = wallChar;
                            
                            mazeGrid[1][2] = wallChar;
                            mazeGrid[1][3] = wallChar;
                            mazeGrid[1][12] = wallChar;
                            mazeGrid[1][14] = wallChar;
                            
                            mazeGrid[2][1] = wallChar;
                            mazeGrid[2][2] = wallChar;
                            mazeGrid[2][3] = wallChar;
                            mazeGrid[2][4] = wallChar;
                            mazeGrid[2][6] = wallChar;
                            mazeGrid[2][7] = wallChar;
                            mazeGrid[2][8] = wallChar;
                            mazeGrid[2][10] = wallChar;
                            mazeGrid[2][11] = wallChar;
                            mazeGrid[2][12] = wallChar;
                            mazeGrid[2][14] = wallChar;
                            mazeGrid[2][15] = wallChar;
                            mazeGrid[2][16] = wallChar;
                            mazeGrid[2][17] = wallChar;
                            
                            mazeGrid[3][2] = wallChar;
                            mazeGrid[3][3] = wallChar;
                            mazeGrid[3][12] = wallChar;
                            mazeGrid[3][14] = wallChar;
                            
                            
                            mazeGrid[4][1] = wallChar;
                            mazeGrid[4][2] = wallChar;
                            mazeGrid[4][3] = wallChar;
                            mazeGrid[4][4] = wallChar;
                            mazeGrid[4][6] = wallChar;
                            mazeGrid[4][7] = wallChar;
                            mazeGrid[4][8] = wallChar;
                            mazeGrid[4][10] = wallChar;
                            mazeGrid[4][11] = wallChar;
                            mazeGrid[4][12] = wallChar;
                            mazeGrid[4][14] = wallChar;
                            mazeGrid[4][15] = wallChar;
                            mazeGrid[4][16] = wallChar;
                            mazeGrid[4][17] = wallChar;
                            
                            mazeGrid[5][2] = wallChar;
                            mazeGrid[5][3] = wallChar;
                            mazeGrid[5][12] = wallChar;
                            mazeGrid[5][14] = wallChar;
                            
                            mazeGrid[6][1] = wallChar;
                            mazeGrid[6][2] = wallChar;
                            mazeGrid[6][3] = wallChar;
                            mazeGrid[6][4] = wallChar;
                            mazeGrid[6][6] = wallChar;
                            mazeGrid[6][7] = wallChar;
                            mazeGrid[6][8] = wallChar;
                            mazeGrid[6][10] = wallChar;
                            mazeGrid[6][11] = wallChar;
                            mazeGrid[6][12] = wallChar;
                            mazeGrid[6][14] = wallChar;
                            mazeGrid[6][15] = wallChar;
                            mazeGrid[6][16] = wallChar;
                            mazeGrid[6][17] = wallChar;
                            
                            mazeGrid[8][1] = wallChar;
                            mazeGrid[8][4] = wallChar;
                            mazeGrid[8][6] = wallChar;
                            mazeGrid[8][8] = wallChar;
                            mazeGrid[8][10] = wallChar;
                            mazeGrid[8][12] = wallChar;
                            mazeGrid[8][14] = wallChar;
                            mazeGrid[8][16] = wallChar;
                            mazeGrid[8][17] = wallChar;
                            
                            mazeGrid[9][1] = wallChar;
                            mazeGrid[9][2] = wallChar;
                            mazeGrid[9][3] = wallChar;
                            mazeGrid[9][4] = wallChar;
                            mazeGrid[9][6] = wallChar;
                            mazeGrid[9][8] = wallChar;
                            mazeGrid[9][10] = wallChar;
                            mazeGrid[9][11] = wallChar;
                            mazeGrid[9][12] = wallChar;
                            mazeGrid[9][14] = wallChar;
                            mazeGrid[9][16] = wallChar;
                            mazeGrid[9][17] = wallChar;
                            
                            mazeGrid[11][1] = wallChar;
                            mazeGrid[11][2] = wallChar;
                            mazeGrid[11][4] = wallChar;
                            mazeGrid[11][6] = wallChar;
                            mazeGrid[11][7] = wallChar;
                            mazeGrid[11][8] = wallChar;
                            mazeGrid[11][10] = wallChar;
                            mazeGrid[11][11] = wallChar;
                            mazeGrid[11][12] = wallChar;
                            mazeGrid[11][14] = wallChar;
                            mazeGrid[11][16] = wallChar;
                            mazeGrid[11][17] = wallChar;
                            
                            mazeGrid[12][1] = wallChar;
                            mazeGrid[12][4] = wallChar;
                            mazeGrid[12][6] = wallChar;
                            mazeGrid[12][14] = wallChar;
                            mazeGrid[12][17] = wallChar;
                            
                            mazeGrid[13][1] = wallChar;
                            mazeGrid[13][2] = wallChar;
                            mazeGrid[13][4] = wallChar;
                            mazeGrid[13][6] = wallChar;
                            mazeGrid[13][7] = wallChar;
                            mazeGrid[13][8] = wallChar;
                            mazeGrid[13][10] = wallChar;
                            mazeGrid[13][11] = wallChar;
                            mazeGrid[13][12] = wallChar;
                            mazeGrid[13][14] = wallChar;
                            mazeGrid[13][15] = wallChar;
                            mazeGrid[13][17] = wallChar;
                            
                            mazeGrid[14][1] = wallChar;
                            mazeGrid[14][4] = wallChar;
                            mazeGrid[14][6] = wallChar;
                            mazeGrid[14][14] = wallChar;
                            mazeGrid[14][15] = wallChar;
                            mazeGrid[14][17] = wallChar;
                            
                            mazeGrid[15][1] = wallChar;
                            mazeGrid[15][2] = wallChar;
                            mazeGrid[15][3] = wallChar;
                            mazeGrid[15][4] = wallChar;
                            mazeGrid[15][6] = wallChar;
                            mazeGrid[15][7] = wallChar;
                            mazeGrid[15][8] = wallChar;
                            mazeGrid[15][10] = wallChar;
                            mazeGrid[15][11] = wallChar;
                            mazeGrid[15][12] = wallChar;
                            mazeGrid[15][14] = wallChar;
                            mazeGrid[15][15] = wallChar;
                            mazeGrid[15][17] = wallChar;
                            
                            mazeGrid[17][1] = wallChar;
                            mazeGrid[17][4] = wallChar;
                            mazeGrid[17][10] = wallChar;
                            mazeGrid[17][11] = wallChar;
                            mazeGrid[17][12] = wallChar;
                            mazeGrid[17][14] = wallChar;
                            mazeGrid[17][17] = wallChar;
                            
                            mazeGrid[18][2] = wallChar;
                            mazeGrid[18][3] = wallChar;
                            mazeGrid[18][4] = wallChar;
                            mazeGrid[18][6] = wallChar;
                            mazeGrid[18][7] = wallChar;
                            mazeGrid[18][8] = wallChar;
                            mazeGrid[18][10] = wallChar;
                            mazeGrid[18][11] = wallChar;
                            mazeGrid[18][12] = wallChar;
                            mazeGrid[18][14] = wallChar;
                            mazeGrid[18][16] = wallChar;
                            mazeGrid[18][17] = wallChar;
                            
                    }
                }
          
                 
                // We create a clean grid ...
                searching = false;
                endOfSearch = false;
                fillGrid();
                // ... and copy into it the positions of obstacles
                // created by the maze construction algorithm
                for (int x = 0; x < gridDimensionX; x++) {
                    for (int y = 0; y < gridDimensionY; y++) {
                        if (mazeGrid[x][y] == wallChar && grid[x][y] != ROBOT && grid[x][y] != TARGET){
                            grid[x][y] = OBST;
                        }
                    }
                }
            }
        } // end nested class MyMaze
         
        /*
         **********************************************************
         *          Constants of class MazePanel
         **********************************************************
         */
         
        private final static int
            INFINITY = Integer.MAX_VALUE, // The representation of the infinite
            EMPTY    = 0,  // empty cell
            OBST     = 1,  // cell with obstacle
            ROBOT    = 2,  // the position of the robot
            TARGET   = 3,  // the position of the target
            FRONTIER = 4,  // cells that form the frontier (OPEN SET)
            CLOSED   = 5,  // cells that form the CLOSED SET
            ROUTE    = 6;  // cells that form the robot-to-target path
         
        // Messages to the user
        private final static String
            msgDrawAndSelect =
                "\"Paint\" obstacles, then click 'Real-Time' or 'Step-by-Step' or 'Animation'",
            msgSelectStepByStepEtc =
                "Click 'Step-by-Step' or 'Animation' or 'Clear'",
            msgNoSolution =
                "There is no path to the target !!!";
 
        /*
         **********************************************************
         *          Variables of class MazePanel
         **********************************************************
         */
         
        JSpinner rowsSpinner, columnsSpinner; // Spinners for entering # of rows and columns
         
        int rows    = 20,           // the number of rows of the grid
            columns = 20,           // the number of columns of the grid
            squareSize = 800/rows;  // the cell size in pixels
         
 
        int arrowSize = squareSize/2; // the size of the tip of the arrow
                                      // pointing the predecessor cell
        ArrayList<Cell> openSet   = new ArrayList();// the OPEN SET
        ArrayList<Cell> closedSet = new ArrayList();// the CLOSED SET
        ArrayList<Cell> graph     = new ArrayList();// the set of vertices of the graph
                                                    // to be explored by Dijkstra's algorithm
          
        Cell robotStart; // the initial position of the robot
        Cell targetPos;  // the position of the target
       
        JLabel message;  // message to the user
         
        // basic buttons
        JButton resetButton, mazeButton, clearButton, realTimeButton, stepButton, animationButton;
         
        // buttons for selecting the algorithm
        JRadioButton dfs, bfs, aStar, greedy, dijkstra;
         
        // the slider for adjusting the speed of the animation
        JSlider slider;
         
        // Diagonal movements allowed?
        JCheckBox diagonal;
        // Draw arrows to predecessors
        JCheckBox drawArrows;
 
        int[][] grid;        // the grid
        boolean realTime;    // Solution is displayed instantly
        boolean found;       // flag that the goal was found
        boolean searching;   // flag that the search is in progress
        boolean endOfSearch; // flag that the search came to an end
        int delay;           // time delay of animation (in msec)
        int expanded;        // the number of nodes that have been expanded
         
        // the object that controls the animation
        RepaintAction action = new RepaintAction();
         
        // the Timer which governs the execution speed of the animation
        Timer timer;
       
        /**
         * The creator of the panel
         * @param width  the width of the panel.
         * @param height the height of the panel.
         */
        public MazePanel(int width, int height) {
       
            setLayout(null);
             
            MouseHandler listener = new MouseHandler();
            addMouseListener(listener);
            addMouseMotionListener(listener);
 
 
            setPreferredSize( new Dimension(width,height) );
 
            grid = new int[rows][columns];
 
            // We create the contents of the panel
 
            message = new JLabel(msgDrawAndSelect, JLabel.CENTER);
            message.setForeground(Color.blue);
            message.setFont(new Font("Helvetica",Font.PLAIN,16));
 
      
 
            resetButton = new JButton("New grid");
            resetButton.addActionListener(new ActionHandler());
            resetButton.setBackground(Color.lightGray);
            resetButton.addActionListener(this::resetButtonActionPerformed);
 
            mazeButton = new JButton("Maze");
            mazeButton.addActionListener(new ActionHandler());
            mazeButton.setBackground(Color.lightGray);
            mazeButton.addActionListener(this::mazeButtonActionPerformed);
 
            clearButton = new JButton("Rozrzuć paczki");
            clearButton.addActionListener(new ActionHandler());
            clearButton.setBackground(Color.lightGray);
           
 
            realTimeButton = new JButton("Poukładaj paczki");
            realTimeButton.addActionListener(new ActionHandler());
            realTimeButton.setBackground(Color.lightGray);
          
            stepButton = new JButton("Śledz każdy ruch");
            stepButton.addActionListener(new ActionHandler());
            stepButton.setBackground(Color.lightGray);
          
 
            animationButton = new JButton("Animacja układania");
            animationButton.addActionListener(new ActionHandler());
            animationButton.setBackground(Color.lightGray);
          
 
            JLabel velocity = new JLabel("Szybkość ruchów", JLabel.CENTER);
            velocity.setFont(new Font("Helvetica",Font.PLAIN,10));
             
            slider = new JSlider(0,1000,500); // initial value of delay 500 msec

             
            delay = 1000-slider.getValue();
            slider.addChangeListener((ChangeEvent evt) -> {
                JSlider source = (JSlider)evt.getSource();
                if (!source.getValueIsAdjusting()) {
                    delay = 1000-source.getValue();
                }
            });
             
            // ButtonGroup that synchronizes the five RadioButtons
            // choosing the algorithm, so that only one
            // can be selected anytime
            ButtonGroup algoGroup = new ButtonGroup();
 
            dfs = new JRadioButton("DFS");
            dfs.setToolTipText("Depth First Search algorithm");
            algoGroup.add(dfs);
            dfs.addActionListener(new ActionHandler());
 
            bfs = new JRadioButton("BFS");
            bfs.setToolTipText("Breadth First Search algorithm");
            algoGroup.add(bfs);
            bfs.addActionListener(new ActionHandler());
 
            aStar = new JRadioButton("A*");
            aStar.setToolTipText("A* algorithm");
            algoGroup.add(aStar);
            aStar.addActionListener(new ActionHandler());
 
            greedy = new JRadioButton("Greedy");
            greedy.setToolTipText("Greedy search algorithm");
            algoGroup.add(greedy);
            greedy.addActionListener(new ActionHandler());
 
            dijkstra = new JRadioButton("Dijkstra");
            dijkstra.setToolTipText("Dijkstra's algorithm");
            algoGroup.add(dijkstra);
            dijkstra.addActionListener(new ActionHandler());
 
            JPanel algoPanel = new JPanel();
            algoPanel.setBorder(javax.swing.BorderFactory.
                    createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                    "Algorytm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 0, 14)));
             
            dfs.setSelected(true);  // DFS is initially selected 
             
            diagonal = new
                    JCheckBox("Diagonal movements");
            diagonal.setToolTipText("Diagonal movements are also allowed");
 
            drawArrows = new
                    JCheckBox("Arrows to predecessors");
            drawArrows.setToolTipText("Draw arrows to predecessors");
 
            JLabel robot = new JLabel("Robot", JLabel.CENTER);
            robot.setForeground(Color.red);
            robot.setFont(new Font("Helvetica",Font.PLAIN,14));
 
            JLabel target = new JLabel("Target", JLabel.CENTER);
            target.setForeground(Color.GREEN);
            target.setFont(new Font("Helvetica",Font.PLAIN,14));
          
            JLabel frontier = new JLabel("Frontier", JLabel.CENTER);
            frontier.setForeground(Color.blue);
            frontier.setFont(new Font("Helvetica",Font.PLAIN,14));
 
            JLabel closed = new JLabel("Closed set", JLabel.CENTER);
            closed.setForeground(Color.CYAN);
            closed.setFont(new Font("Helvetica",Font.PLAIN,14));
 
     
 
            // we add the contents of the panel
            add(message);
            add(resetButton);
            add(mazeButton);
            add(clearButton);
            add(realTimeButton);
            add(stepButton);
            add(animationButton);
            add(velocity);
            add(slider);
            add(aStar);
            add(algoPanel);
     
 
            // we regulate the sizes and positions
            resetButton.setBounds(820, 65, 170, 25);
            mazeButton.setBounds(820, 95, 170, 25);
            clearButton.setBounds(820, 125, 170, 25);
            realTimeButton.setBounds(820, 155, 170, 25);
            stepButton.setBounds(820, 185, 170, 25);
            animationButton.setBounds(820, 215, 170, 25);
            velocity.setBounds(820, 245, 170, 10);
            slider.setBounds(820, 255, 170, 25);
            aStar.setBounds(830, 325, 70, 25);
            algoPanel.setLocation(820,280);
            algoPanel.setSize(170, 100);
        
    
       
 
            // we create the timer
            timer = new Timer(delay, action);
             
            // We attach to cells in the grid initial values.
            // Here is the first step of the algorithms
            fillGrid();
 
        } // end constructor
 
    static protected JSpinner addLabeledSpinner(Container c,
                                                String label,
                                                SpinnerModel model) {
        JLabel l = new JLabel(label);
        c.add(l);
  
        JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
  
        return spinner;
    }
 
        /**
         * Function executed if the user presses the button "New Grid"
         */
        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            slider.setEnabled(true);
            initializeGrid(false);
        } // end resetButtonActionPerformed()
     
        /**
         * Function executed if the user presses the button "Maze"
         */
        private void mazeButtonActionPerformed(java.awt.event.ActionEvent evt) {
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            slider.setEnabled(true);
            initializeGrid(true);
        } // end mazeButtonActionPerformed()
     
        /**
         * Creates a new clean grid or a new maze
         */
        private void initializeGrid(Boolean makeMaze) {                                           
            squareSize = 800/(rows > columns ? rows : columns);
            arrowSize = squareSize/2;
            // the maze must have an odd number of rows and columns
            if (makeMaze && rows % 2 == 0) {
                rows -= 1;
            }
            if (makeMaze && columns % 2 == 0) {
                columns -= 1;
            }
            grid = new int[rows][columns];
            robotStart = new Cell(rows-2,1);
            targetPos = new Cell(1,columns-2);
            dfs.setEnabled(true);
            dfs.setSelected(true);
            bfs.setEnabled(true);
            aStar.setEnabled(true);
            greedy.setEnabled(true);
            dijkstra.setEnabled(true);
            diagonal.setSelected(false);
            diagonal.setEnabled(true);
            drawArrows.setSelected(false);
            drawArrows.setEnabled(true);
            slider.setValue(800);
            if (makeMaze) {
                MyMaze maze = new MyMaze(rows/2,columns/2);
            } else {
                fillGrid();
            }
        } // end initializeGrid()
    
        /**
         * Expands a node and creates his successors
         */
        private void expandNode(){
            // Dijkstra's algorithm to handle separately
            if (dijkstra.isSelected()){
                Cell u;
                // 11: while Q is not empty:
                if (graph.isEmpty()){
                    return;
                }
                // 12:  u := vertex in Q (graph) with smallest distance in dist[] ;
                // 13:  remove u from Q (graph);
                u = graph.remove(0);
                // Add vertex u in closed set
                closedSet.add(u);
                // If target has been found ...
                if (u.row == targetPos.row && u.col == targetPos.col){
                    found = true;
                    return;
                }
                // Counts nodes that have expanded.
                expanded++;
                // Update the color of the cell
                grid[u.row][u.col] = CLOSED;
                // 14: if dist[u] = infinity:
                if (u.dist == INFINITY){
                    // ... then there is no solution.
                    // 15: break;
                    return;
                // 16: end if
                } 
                // Create the neighbors of u
                ArrayList<Cell> neighbors = createSuccesors(u, false);
                // 18: for each neighbor v of u:
                neighbors.stream().forEach((v) -> {
                    // 20: alt := dist[u] + dist_between(u, v) ;
                    int alt = u.dist + distBetween(u,v);
                    // 21: if alt < dist[v]:
                    if (alt < v.dist) {
                        // 22: dist[v] := alt ;
                        v.dist = alt;
                        // 23: previous[v] := u ;
                        v.prev = u;
                        // Update the color of the cell
                        grid[v.row][v.col] = FRONTIER;
                        // 24: decrease-key v in Q;
                        // (sort list of nodes with respect to dist)
                        Collections.sort(graph, new CellComparatorByDist());
                    }
                }); // The handling of the other four algorithms
            } else {
                Cell current;
                if (dfs.isSelected() || bfs.isSelected()) {
                    // Here is the 3rd step of the algorithms DFS and BFS
                    // 3. Remove the first state, Si, from OPEN SET ...
                    current = openSet.remove(0);
                } else {
                    // Here is the 3rd step of the algorithms A* and Greedy
                    // 3. Remove the first state, Si, from OPEN SET,
                    // for which f(Si) ≤ f(Sj) for all other
                    // open states Sj  ...
                    // (sort first OPEN SET list with respect to 'f')
                    Collections.sort(openSet, new CellComparatorByF());
                    current = openSet.remove(0);
                }
                // ... and add it to CLOSED SET.
                closedSet.add(0,current);
                // Update the color of the cell
                grid[current.row][current.col] = CLOSED;
                // If the selected node is the target ...
                if (current.row == targetPos.row && current.col == targetPos.col) {
                    // ... then terminate etc
                    Cell last = targetPos;
                    last.prev = current.prev;
                    closedSet.add(last);
                    found = true;
                    return;
                }
                // Count nodes that have been expanded.
                expanded++;
                // Here is the 4rd step of the algorithms
                // 4. Create the successors of Si, based on actions
                //    that can be implemented on Si.
                //    Each successor has a pointer to the Si, as its predecessor.
                //    In the case of DFS and BFS algorithms, successors should not
                //    belong neither to the OPEN SET nor the CLOSED SET.
                ArrayList<Cell> succesors;
                succesors = createSuccesors(current, false);
                // Here is the 5th step of the algorithms
                // 5. For each successor of Si, ...
                succesors.stream().forEach((cell) -> {
                    // ... if we are running DFS ...
                    if (dfs.isSelected()) {
                        // ... add the successor at the beginning of the list OPEN SET
                        openSet.add(0, cell);
                        // Update the color of the cell
                        grid[cell.row][cell.col] = FRONTIER;
                        // ... if we are runnig BFS ...
                    } else if (bfs.isSelected()){
                        // ... add the successor at the end of the list OPEN SET
                        openSet.add(cell);
                        // Update the color of the cell
                        grid[cell.row][cell.col] = FRONTIER;
                        // ... if we are running A* or Greedy algorithms (step 5 of A* algorithm) ...
                    } else if (aStar.isSelected() || greedy.isSelected()){
                        // ... calculate the value f(Sj) ...
                        int dxg = current.col-cell.col;
                        int dyg = current.row-cell.row;
                        int dxh = targetPos.col-cell.col;
                        int dyh = targetPos.row-cell.row;
                        if (diagonal.isSelected()){
                            // with diagonal movements 
                            // calculate 1000 times the Euclidean distance
                            if (greedy.isSelected()) {
                                // especially for the Greedy ...
                                cell.g = 0;
                            } else {
                                cell.g = current.g+(int)((double)1000*Math.sqrt(dxg*dxg + dyg*dyg));
                            }
                            cell.h = (int)((double)1000*Math.sqrt(dxh*dxh + dyh*dyh));
                        } else {
                            // without diagonal movements
                            // calculate Manhattan distances
                            if (greedy.isSelected()) {
                                // especially for the Greedy ...
                                cell.g = 0;
                            } else {
                                cell.g = current.g+Math.abs(dxg)+Math.abs(dyg);
                            }
                            cell.h = Math.abs(dxh)+Math.abs(dyh);
                        }
                        cell.f = cell.g+cell.h;
                        // ... If Sj is neither in the OPEN SET nor in the CLOSED SET states ...
                        int openIndex   = isInList(openSet,cell);
                        int closedIndex = isInList(closedSet,cell);
                        if (openIndex == -1 && closedIndex == -1) {
                            // ... then add Sj in the OPEN SET ...
                            // ... evaluated as f(Sj)
                            openSet.add(cell);
                            // Update the color of the cell
                            grid[cell.row][cell.col] = FRONTIER;
                            // Else ...
                        } else {
                            // ... if already belongs to the OPEN SET, then ...
                            if (openIndex > -1){
                                // ... compare the new value assessment with the old one. 
                                // If old <= new ...
                                if (openSet.get(openIndex).f <= cell.f) {
                                    // ... then eject the new node with state Sj.
                                    // (ie do nothing for this node).
                                    // Else, ...
                                } else {
                                    // ... remove the element (Sj, old) from the list
                                    // to which it belongs ...
                                    openSet.remove(openIndex);
                                    // ... and add the item (Sj, new) to the OPEN SET.
                                    openSet.add(cell);
                                    // Update the color of the cell
                                    grid[cell.row][cell.col] = FRONTIER;
                                }
                                // ... if already belongs to the CLOSED SET, then ...
                            } else {
                                // ... compare the new value assessment with the old one. 
                                // If old <= new ...
                                if (closedSet.get(closedIndex).f <= cell.f) {
                                    // ... then eject the new node with state Sj.
                                    // (ie do nothing for this node).
                                    // Else, ...
                                } else {
                                    // ... remove the element (Sj, old) from the list
                                    // to which it belongs ...
                                    closedSet.remove(closedIndex);
                                    // ... and add the item (Sj, new) to the OPEN SET.
                                    openSet.add(cell);
                                    // Update the color of the cell
                                    grid[cell.row][cell.col] = FRONTIER;
                                }
                            }
                        }
                    }
                });
            }
        } //end expandNode()
         
        /**
         * Creates the successors of a state/cell
         * 
         * @param current       the cell for which we ask successors
         * @param makeConnected flag that indicates that we are interested only on the coordinates
         *                      of cells and not on the label 'dist' (concerns only Dijkstra's)
         * @return              the successors of the cell as a list
         */
        private ArrayList<Cell> createSuccesors(Cell current, boolean makeConnected){
            int r = current.row;
            int c = current.col;
            // We create an empty list for the successors of the current cell.
            ArrayList<Cell> temp = new ArrayList<>();
            // With diagonal movements priority is:
            // 1: Up 2: Up-right 3: Right 4: Down-right
            // 5: Down 6: Down-left 7: Left 8: Up-left
             
            // Without diagonal movements the priority is:
            // 1: Up 2: Right 3: Down 4: Left
             
            // If not at the topmost limit of the grid
            // and the up-side cell is not an obstacle ...
            if (r > 0 && grid[r-1][c] != OBST &&
                    // ... and (only in the case are not running the A* or Greedy)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                          isInList(openSet,new Cell(r-1,c)) == -1 &&
                          isInList(closedSet,new Cell(r-1,c)) == -1)) {
                Cell cell = new Cell(r-1,c);
                // In the case of Dijkstra's algorithm we can not append to
                // the list of successors the "naked" cell we have just created.
                // The cell must be accompanied by the label 'dist',
                // so we need to track it down through the list 'graph'
                // and then copy it back to the list of successors.
                // The flag makeConnected is necessary to be able
                // the present method createSuccesors() to collaborate
                // with the method findConnectedComponent(), which creates
                // the connected component when Dijkstra's initializes.
                if (dijkstra.isSelected()){
                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1) {
                            temp.add(graph.get(graphIndex));
                        }
                    }
                } else {
                    // ... update the pointer of the up-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the up-side cell to the successors of the current one. 
                    temp.add(cell);
                 }
            }
            if (diagonal.isSelected()){
                // If we are not even at the topmost nor at the rightmost border of the grid
                // and the up-right-side cell is not an obstacle ...
                if (r > 0 && c < columns-1 && grid[r-1][c+1] != OBST &&
                        // ... and one of the upper side or right side cells are not obstacles ...
                        // (because it is not reasonable to allow 
                        // the robot to pass through a "slot")                        
                        (grid[r-1][c] != OBST || grid[r][c+1] != OBST) &&
                        // ... and (only in the case are not running the A* or Greedy)
                        // not already belongs neither to the OPEN SET nor CLOSED SET ...
                        ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                              isInList(openSet,new Cell(r-1,c+1)) == -1 &&
                              isInList(closedSet,new Cell(r-1,c+1)) == -1)) {
                    Cell cell = new Cell(r-1,c+1);
                    if (dijkstra.isSelected()){
                        if (makeConnected) {
                            temp.add(cell);
                        } else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1) {
                                temp.add(graph.get(graphIndex));
                            }
                        }
                    } else {
                        // ... update the pointer of the up-right-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the up-right-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the rightmost limit of the grid
            // and the right-side cell is not an obstacle ...
            if (c < columns-1 && grid[r][c+1] != OBST &&
                    // ... and (only in the case are not running the A* or Greedy)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected())? true :
                          isInList(openSet,new Cell(r,c+1)) == -1 &&
                          isInList(closedSet,new Cell(r,c+1)) == -1)) {
                Cell cell = new Cell(r,c+1);
                if (dijkstra.isSelected()){
                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1) {
                            temp.add(graph.get(graphIndex));
                        }
                    }
                } else {
                    // ... update the pointer of the right-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the right-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (diagonal.isSelected()){
                // If we are not even at the lowermost nor at the rightmost border of the grid
                // and the down-right-side cell is not an obstacle ...
                if (r < rows-1 && c < columns-1 && grid[r+1][c+1] != OBST &&
                        // ... and one of the down-side or right-side cells are not obstacles ...
                        (grid[r+1][c] != OBST || grid[r][c+1] != OBST) &&
                        // ... and (only in the case are not running the A* or Greedy)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                              isInList(openSet,new Cell(r+1,c+1)) == -1 &&
                              isInList(closedSet,new Cell(r+1,c+1)) == -1)) {
                    Cell cell = new Cell(r+1,c+1);
                    if (dijkstra.isSelected()){
                        if (makeConnected) {
                            temp.add(cell);
                        } else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1) {
                                temp.add(graph.get(graphIndex));
                            }
                        }
                    } else {
                        // ... update the pointer of the downr-right-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the down-right-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the lowermost limit of the grid
            // and the down-side cell is not an obstacle ...
            if (r < rows-1 && grid[r+1][c] != OBST &&
                    // ... and (only in the case are not running the A* or Greedy)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                          isInList(openSet,new Cell(r+1,c)) == -1 &&
                          isInList(closedSet,new Cell(r+1,c)) == -1)) {
                Cell cell = new Cell(r+1,c);
                if (dijkstra.isSelected()){
                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1) {
                            temp.add(graph.get(graphIndex));
                        }
                    }
                } else {
                   // ... update the pointer of the down-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the down-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (diagonal.isSelected()){
                // If we are not even at the lowermost nor at the leftmost border of the grid
                // and the down-left-side cell is not an obstacle ...
                if (r < rows-1 && c > 0 && grid[r+1][c-1] != OBST &&
                        // ... and one of the down-side or left-side cells are not obstacles ...
                        (grid[r+1][c] != OBST || grid[r][c-1] != OBST) &&
                        // ... and (only in the case are not running the A* or Greedy)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                              isInList(openSet,new Cell(r+1,c-1)) == -1 &&
                              isInList(closedSet,new Cell(r+1,c-1)) == -1)) {
                    Cell cell = new Cell(r+1,c-1);
                    if (dijkstra.isSelected()){
                        if (makeConnected) {
                            temp.add(cell);
                        } else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1) {
                                temp.add(graph.get(graphIndex));
                            }
                        }
                    } else {
                        // ... update the pointer of the down-left-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the down-left-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the leftmost limit of the grid
            // and the left-side cell is not an obstacle ...
            if (c > 0 && grid[r][c-1] != OBST && 
                    // ... and (only in the case are not running the A* or Greedy)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                          isInList(openSet,new Cell(r,c-1)) == -1 &&
                          isInList(closedSet,new Cell(r,c-1)) == -1)) {
                Cell cell = new Cell(r,c-1);
                if (dijkstra.isSelected()){
                    if (makeConnected) {
                        temp.add(cell);
                    } else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1) {
                            temp.add(graph.get(graphIndex));
                        }
                    }
                } else {
                   // ... update the pointer of the left-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the left-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (diagonal.isSelected()){
                // If we are not even at the topmost nor at the leftmost border of the grid
                // and the up-left-side cell is not an obstacle ...
                if (r > 0 && c > 0 && grid[r-1][c-1] != OBST &&
                        // ... and one of the up-side or left-side cells are not obstacles ...
                        (grid[r-1][c] != OBST || grid[r][c-1] != OBST) &&
                        // ... and (only in the case are not running the A* or Greedy)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((aStar.isSelected() || greedy.isSelected() || dijkstra.isSelected()) ? true :
                              isInList(openSet,new Cell(r-1,c-1)) == -1 &&
                              isInList(closedSet,new Cell(r-1,c-1)) == -1)) {
                    Cell cell = new Cell(r-1,c-1);
                    if (dijkstra.isSelected()){
                        if (makeConnected) {
                            temp.add(cell);
                        } else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1) {
                                temp.add(graph.get(graphIndex));
                            }
                        }
                    } else {
                        // ... update the pointer of the up-left-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the up-left-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // When DFS algorithm is in use, cells are added one by one at the beginning of the
            // OPEN SET list. Because of this, we must reverse the order of successors formed,
            // so the successor corresponding to the highest priority, to be placed
            // the first in the list.
            // For the Greedy, A* and Dijkstra's no issue, because the list is sorted
            // according to 'f' or 'dist' before extracting the first element of.
            if (dfs.isSelected()){
                Collections.reverse(temp);
            }
            return temp;
        } // end createSuccesors()
         
        /**
         * Returns the index of the cell 'current' in the list 'list'
         *
         * @param list    the list in which we seek
         * @param current the cell we are looking for
         * @return        the index of the cell in the list
         *                if the cell is not found returns -1
         */
        private int isInList(ArrayList<Cell> list, Cell current){
            int index = -1;
            for (int i = 0 ; i < list.size(); i++) {
                if (current.row == list.get(i).row && current.col == list.get(i).col) {
                    index = i;
                    break;
                }
            }
            return index;
        } // end isInList()
         
        /**
         * Returns the predecessor of cell 'current' in list 'list'
         *
         * @param list      the list in which we seek
         * @param current   the cell we are looking for
         * @return          the predecessor of cell 'current'
         */
        private Cell findPrev(ArrayList<Cell> list, Cell current){
            int index = isInList(list, current);
            return list.get(index).prev;
        } // end findPrev()
         
        /**
         * Returns the distance between two cells
         *
         * @param u the first cell
         * @param v the other cell
         * @return  the distance between the cells u and v
         */
        private int distBetween(Cell u, Cell v){
            int dist;
            int dx = u.col-v.col;
            int dy = u.row-v.row;
            if (diagonal.isSelected()){
                // with diagonal movements 
                // calculate 1000 times the Euclidean distance
                dist = (int)((double)1000*Math.sqrt(dx*dx + dy*dy));
            } else {
                // without diagonal movements
                // calculate Manhattan distances
                dist = Math.abs(dx)+Math.abs(dy);
            }
            return dist;
        } // end distBetween()
         
        /**
         * Calculates the path from the target to the initial position
         * of the robot, counts the corresponding steps
         * and measures the distance traveled.
         */
        private void plotRoute(){
            searching = false;
            endOfSearch = true;
            int steps = 0;
            double distance = 0;
            int index = isInList(closedSet,targetPos);
            Cell cur = closedSet.get(index);
            grid[cur.row][cur.col]= TARGET;
            do {
                steps++;
                if (diagonal.isSelected()) {
                    int dx = cur.col-cur.prev.col;
                    int dy = cur.row-cur.prev.row;
                    distance += Math.sqrt(dx*dx + dy*dy);
                } else { 
                    distance++;
                }
                cur = cur.prev;
                grid[cur.row][cur.col] = ROUTE;
            } while (!(cur.row == robotStart.row && cur.col == robotStart.col));
            grid[robotStart.row][robotStart.col]=ROBOT;
            String msg;
            msg = String.format("Nodes expanded: %d, Steps: %d, Distance: %.3f",
                     expanded,steps,distance); 
            message.setText(msg);
           
        } // end plotRoute()
         
        /**
         * Gives initial values ​​for the cells in the grid.
         * With the first click on button 'Clear' clears the data
         * of any search was performed (Frontier, Closed Set, Route)
         * and leaves intact the obstacles and the robot and target positions
         * in order to be able to run another algorithm
         * with the same data.
         * With the second click removes any obstacles also.
         */
        private void fillGrid() {
            if (searching || endOfSearch){ 
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE) {
                            grid[r][c] = EMPTY;
                        }
                        if (grid[r][c] == ROBOT){
                            robotStart = new Cell(r,c);
                        }
                        if (grid[r][c] == TARGET){
                            targetPos = new Cell(r,c);
                        }
                    }
                }
                searching = false;
            } else {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        grid[r][c] = EMPTY;
                    }
                }
                robotStart = new Cell(rows-2,1);
                targetPos = new Cell(1,columns-2);
            }
            if (aStar.isSelected() || greedy.isSelected()){
                robotStart.g = 0;
                robotStart.h = 0;
                robotStart.f = 0;
            }
            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;
          
            // The first step of the other four algorithms is here
            // 1. OPEN SET: = [So], CLOSED SET: = []
            openSet.removeAll(openSet);
            openSet.add(robotStart);
            closedSet.removeAll(closedSet);
          
            grid[targetPos.row][targetPos.col] = TARGET; 
            grid[robotStart.row][robotStart.col] = ROBOT;
            message.setText(msgDrawAndSelect);
            timer.stop();
            repaint();
             
        } // end fillGrid()
 
        /**
          * Appends to the list containing the nodes of the graph only
          * the cells belonging to the same connected component with node v.
          * This is a Breadth First Search of the graph starting from node v.
          *
          * @param v    the starting node
          */
        private void findConnectedComponent(Cell v){
            Stack<Cell> stack;
            stack = new Stack();
            ArrayList<Cell> succesors;
            stack.push(v);
            graph.add(v);
            while(!stack.isEmpty()){
                v = stack.pop();
                succesors = createSuccesors(v, true);
                for (Cell c: succesors) {
                    if (isInList(graph, c) == -1){
                        stack.push(c);
                        graph.add(c);
                    }
                }
            }
        } // end findConnectedComponent()
         
        /**
         * Initialization of Dijkstra's algorithm
         * 
         * When one thinks of Wikipedia pseudocode, observe that the
         * algorithm is still looking for his target while there are still
         * nodes in the queue Q.
         * Only when we run out of queue and the target has not been found,
         * can answer that there is no solution .
         * As is known, the algorithm models the problem as a connected graph.
         * It is obvious that no solution exists only when the graph is not
         * connected and the target is in a different connected component
         * of this initial position of the robot.
         * To be thus possible negative response from the algorithm,
         * should search be made ONLY in the coherent component to which the
         * initial position of the robot belongs.
         */
        private void initializeDijkstra() {
            // First create the connected component
            // to which the initial position of the robot belongs.
            graph.removeAll(graph);
            findConnectedComponent(robotStart);
            // Here is the initialization of Dijkstra's algorithm 
            // 2: for each vertex v in Graph;
            for (Cell v: graph) {
                // 3: dist[v] := infinity ;
                v.dist = INFINITY;
                // 5: previous[v] := undefined ;
                v.prev = null;
            }
            // 8: dist[source] := 0;
            graph.get(isInList(graph,robotStart)).dist = 0;
            // 9: Q := the set of all nodes in Graph;
            // Instead of the variable Q we will use the list
            // 'graph' itself, which has already been initialised.            
 
            // Sorts the list of nodes with respect to 'dist'.
            Collections.sort(graph, new CellComparatorByDist());
            // Initializes the list of closed nodes
            closedSet.removeAll(closedSet);
        } // end initializeDijkstra()
 
        /**
         * paints the grid
         */
        @Override
        public void paintComponent(Graphics g) {
 
            super.paintComponent(g);  // Fills the background color.
 
            g.setColor(Color.DARK_GRAY);
            g.fillRect(10, 10, columns*squareSize+1, rows*squareSize+1);
 
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    if (grid[r][c] == EMPTY) {
                        g.setColor(Color.WHITE);
                    } else if (grid[r][c] == ROBOT) {
                        g.setColor(Color.RED);
                    } else if (grid[r][c] == TARGET) {
                        g.setColor(Color.GREEN);
                    } else if (grid[r][c] == OBST) {
                        g.setColor(Color.BLACK);
                    } else if (grid[r][c] == FRONTIER) {
                        g.setColor(Color.BLUE);
                    } else if (grid[r][c] == CLOSED) {
                        g.setColor(Color.CYAN);
                    } else if (grid[r][c] == ROUTE) {
                        g.setColor(Color.YELLOW);
                    }
                    g.fillRect(11 + c*squareSize, 11 + r*squareSize, squareSize - 1, squareSize - 1);
                }
            }
            
             
            if (drawArrows.isSelected()) {
                // We draw all arrows from each open or closed state
                // to its predecessor.
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < columns; c++) {
                        // If the current cell is the goal and the solution has been found,
                        // or belongs in the route to the target,
                        // or is an open state,
                        // or is a closed state but not the initial position of the robot
                        if ((grid[r][c] == TARGET && found)  || grid[r][c] == ROUTE  || 
                                grid[r][c] == FRONTIER || (grid[r][c] == CLOSED &&
                                !(r == robotStart.row && c == robotStart.col))){
                            // The tail of the arrow is the current cell, while
                            // the arrowhead is the predecessor cell.
                            Cell head;
                            if (grid[r][c] == FRONTIER){
                                if (dijkstra.isSelected()){
                                    head = findPrev(graph,new Cell(r,c));
                                } else {
                                    head = findPrev(openSet,new Cell(r,c));
                                }
                            } else {
                                head = findPrev(closedSet,new Cell(r,c));
                            }
                            // The coordinates of the center of the current cell
                            int tailX = 11+c*squareSize+squareSize/2;
                            int tailY = 11+r*squareSize+squareSize/2;
                            // The coordinates of the center of the predecessor cell
                            int headX = 11+head.col*squareSize+squareSize/2;
                            int headY = 11+head.row*squareSize+squareSize/2;
                            // If the current cell is the target
                            // or belongs to the path to the target ...
                            if (grid[r][c] == TARGET  || grid[r][c] == ROUTE){
                                // ... draw a red arrow directing to the target.
                                g.setColor(Color.RED);
                                drawArrow(g,tailX,tailY,headX,headY);
                            // Else ...
                            } else {
                                // ... draw a black arrow to the predecessor cell.
                                g.setColor(Color.BLACK);
                                drawArrow(g,headX,headY,tailX,tailY);
                            }
                        }
                    }
                }
            }
        } // end paintComponent()
         
        /**
         * Draws an arrow from point (x2,y2) to point (x1,y1)
         */
        private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
            Graphics2D g = (Graphics2D) g1.create();
 
            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.sqrt(dx*dx + dy*dy);
            AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
            at.concatenate(AffineTransform.getRotateInstance(angle));
            g.transform(at);
 
            // We design an horizontal arrow 'len' in length
            // that ends at the point (0,0) with two tips 'arrowSize' in length
            // which form 20 degrees angles with the axis of the arrow ...
            g.drawLine(0, 0, len, 0);
            g.drawLine(0, 0, (int)(arrowSize*Math.sin(70*Math.PI/180)) , (int)(arrowSize*Math.cos(70*Math.PI/180)));
            g.drawLine(0, 0, (int)(arrowSize*Math.sin(70*Math.PI/180)) , -(int)(arrowSize*Math.cos(70*Math.PI/180)));
            // ... and class AffineTransform handles the rest !!!!!!
            // Java is admirable!!! Isn't it ?
        } // end drawArrow()
         
    } // end nested classs MazePanel
   
} // end class Maze