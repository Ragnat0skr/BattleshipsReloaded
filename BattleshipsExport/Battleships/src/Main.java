import java.awt.Button;
import java.util.Random;
import javafx.application.*;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application{
	
		//private Boolean running = false;
		private int shipsToPlace = 5; // Limit on ships per player
		private boolean enemyTurn = false; // Defines whose turn it is
	    private Random random = new Random(); // Needed to use rng
	    
	    boolean ready = true; // Sets states for the game
	    boolean gameOn = false; // ready is a flag for the game in the preparation phase, gameon is a flag for the game being playable
	    int length = 5; // used for length of players ships
	    int lengthCPU = 5; // above, but for computer
	    int shipsPlaced = 0; // used to track the limit of the ships placed by the player
	    int shipsPlacedCom = 0; // above, but for com
	    int currentX; // used to hold the coordinates of a specific tile in the sea
	    int currentY;
	    int[][] board = new int[10][10]; // array to manage the players board
		int[][] cpuBoard = new int[10][10]; // array for coms board
		int[][] cpuShotsBoard = new int[10][10]; // array for com to keep track of where its already shot
		
		int carriersPlayer = 1; // Sets up number of ships for each player
		int battleshipsPlayer = 2; // Player values used in placement stage
		int destroyersPlayer = 2;
		int patrolsPlayer = 3;
		int carriersCom = 1;
		int battleshipsCom = 2;
		int destroyersCom = 2;
		int patrolsCom = 3;
		int totalShips = carriersPlayer + battleshipsPlayer + destroyersPlayer + patrolsPlayer;
		int[] boatHPPlayer = new int[totalShips + 1];
		int[] boatHPCom = new int[totalShips + 1];
		
		
		public static void main(String[] args)
		{
			launch(); // starts the game
		}
		
		/* @param vertical true if ship is vertical, false if horizontal
		 * @param length length of the ship
		 * @param board board to work on
		 * @param startX start x coordinate of ship
		 * @param startY start y coordinate of ship
		 * @return true if it's ok to place this ship, false if not
		 */
		private boolean canPlace(boolean vertical, int length, int[][] board, int startX, int startY)
		{
			boolean withinBounds = false; // flags for two parts of method
			boolean spaceEmpty = true;
			boolean passed = false; // flag to return
			
			if (vertical == true)
			{
				if (startY + length <= 10) // checks if extending length vertically would push out of bounds (over 10)
				{
					withinBounds = true;
				}
		}
			else
			{
				if (startX + length <= 10) // checks if extending length horizontally would push out of bounds
				{
					withinBounds = true;
				}
			}
			
			
			if (withinBounds == true) // part 2 of checking
			{
				for (int l = 0; l < length; l++) // loops through each space potential ship will occupy
				{
					if (vertical == true)
					{
						if (board[startX][startY+l] != 0) //checks if space is non 0, ie already filled (by existing ship)
						{
							spaceEmpty = false;
						}
					}
					else
					{
						if (board[startX + l][startY] != 0) //checks if space is non 0, ie already filled (by existing ship)
						{
							spaceEmpty = false;
						}
					}
				}
			}
			
			if (withinBounds == true && spaceEmpty == true) // passes if both tests are true
			{
				passed = true;
			}
			return passed;
		}
		@Override
		public void start(Stage game) throws Exception // begins immediately
		{
			
			game.setTitle("Battleships");
			game.setScene(new Scene(runGame(10), 1536, 864)); // creates the window, 10 being grid size, 1536x864 being a standard scaled down resolution
			game.show();
						
		}
		public Group runGame(int gSize)
		{
			Group group = new Group();
			
			for (int i = 0; i < gSize; i++) // loops through x and y to create the grid
			{
				for (int j = 0; j < gSize; j++)
				{
					Sea thisSea = new Sea(); // thisSea will be used to refer to individual tiles
					thisSea.setX((50*i)+50); // draws the sea tiles
					thisSea.setY((50*j)+50);
					thisSea.x = i;
					thisSea.y = j;
					thisSea.setWidth(45); // sets properties of sea
					thisSea.setHeight(45);
					thisSea.setCursor(Cursor.HAND); // cursor will be a hand when hovered to show that its clickable
					thisSea.setFill(Color.DARKBLUE);
					
					thisSea.setOnMouseClicked(event-> // triggers when sea tile is clicked
					{
						if (ready == true) // runs if in prep phase
						{
							if (carriersPlayer > 0) // checks if the player has any ships of any type to place
							{ 
								length = 5;
								carriersPlayer = carriersPlayer - 1; // reduces the amount of this ship left to place
								boatHPPlayer[shipsPlaced+1] = 5; // adds hp to the players total hits to lose
							}
							else if(battleshipsPlayer > 0) // repeats for each type of ship
							{
								length = 4;
								battleshipsPlayer = battleshipsPlayer - 1;
								boatHPPlayer[shipsPlaced+1] = 4;
							}
							else if(destroyersPlayer > 0)
							{
								length = 3;
								destroyersPlayer = destroyersPlayer - 1;
								boatHPPlayer[shipsPlaced+1] = 3;
							}
							else if(patrolsPlayer > 0)
							{
								length = 2;
								patrolsPlayer = patrolsPlayer - 1;
								boatHPPlayer[shipsPlaced+1] = 2;
							}
							
							currentX = thisSea.x; // converts thisSea coordinates to variables for easier use (resets with every click)
							currentY = thisSea.y;
							
							// vertical/horizontal
							// length of ship to place
							// board to work with
							// x/y of where new ship starts
							boolean legalSpace = canPlace(event.isShiftDown(), length, board, thisSea.x, thisSea.y);
							
							if (legalSpace == true)
							{
								thisSea.setX((50*currentX) + 50);
								thisSea.setY((50 * currentY) + 50);
								
								if (!event.isShiftDown()) // creates horizontal ships
								{
									thisSea.setWidth((50 * length)-5); // sets up tiles accordingly
									thisSea.setHeight(45); 							
									for (int k = 0; k < length; k++) {						
										board[thisSea.x+k][thisSea.y] = shipsPlaced+1;				
									}							
								}
								else // creates vertical ships
								{
									thisSea.setWidth(45);
									thisSea.setHeight((50*length)-5);
									for (int k = 0; k < length; k++) {						
										board[thisSea.x][thisSea.y+k] = shipsPlaced+1;				
									}
							}
								
								thisSea.setCursor(Cursor.HAND);
								thisSea.setFill(Color.GREEN); // ship tiles are coloured to stand out, green to show they're allied
								thisSea.toFront(); // makes ships visible
								shipsPlaced++;
								
								
								if (shipsPlaced == totalShips) // signifies the end of the prep phase
								{
									length = 0;
									ready = false; // prep phase ends
									gameOn = true; // game phase begins
								}
						}
					}
				});

					group.getChildren().add(thisSea);
				}						
			}
			for (int i = 0; i < gSize; i++) // loops through x and y to create opposing sea grid
			{ 
				for (int j = 0; j < gSize; j++) 
				{		
					Sea thisSeaCom = new Sea();
					thisSeaCom.setX((50*i)+850);
					thisSeaCom.setY((50*j)+50);
					thisSeaCom.x = i;
					thisSeaCom.y = j;
					thisSeaCom.setWidth(45);
					thisSeaCom.setHeight(45);
					thisSeaCom.setCursor(Cursor.HAND);
					thisSeaCom.setFill(Color.GREY);	// different colour to allied, otherwise the same creation
					
					
					thisSeaCom.setOnMouseClicked(event->{ // when the user shoots at a com ship
						if (gameOn == true && cpuBoard[thisSeaCom.x][thisSeaCom.y] != 0) // triggers if sea is not empty (due to com ship)
						{
							thisSeaCom.setX((50*thisSeaCom.x)+850); // sets tile accordingly
							thisSeaCom.setY((50*thisSeaCom.y)+50);
							thisSeaCom.setWidth(45);
							thisSeaCom.setHeight(45);							
							thisSeaCom.setCursor(Cursor.HAND);
							thisSeaCom.setFill(Color.RED); // colours red to indicate a hit
							thisSeaCom.toFront();
							
							
																			
						}
						else if (gameOn == true && cpuBoard[thisSeaCom.x][thisSeaCom.y] == 0) // triggers if sea is empty
						{
							thisSeaCom.setX((50*thisSeaCom.x)+850);
							thisSeaCom.setY((50*thisSeaCom.y)+50);
							thisSeaCom.setWidth(45);
							thisSeaCom.setHeight(45);							
							thisSeaCom.setCursor(Cursor.HAND);
							thisSeaCom.setFill(Color.DARKGREY); // colours to show the tile has been shot, and missed
							thisSeaCom.toFront();
						}
						
						if (gameOn == true) {
							Boolean shotCheck = false;
							Random rng = new Random();
							while (shotCheck == false) 
							{
								currentX = rng.nextInt(10); // generate random sea for cpu to shoot for x and y
								currentY = rng.nextInt(10);
								if (cpuShotsBoard[currentX][currentY] == 0) // checks if hasn't been shot before, with array holding previous shots
								{
									shotCheck = true; // flag for legal space to shoot
								}
							}
							cpuShotsBoard[currentX][currentY] = 1; // fills in array so sea wont get shot again
							Sea shotCpuSquare = new Sea();
							if (board[currentX][currentY] != 0)  // triggers if the com has a hit
							{						
								shotCpuSquare.setX((50*currentX)+50); // keeps setting up sea tiles accordingly
								shotCpuSquare.setY((50*currentY)+50);
								shotCpuSquare.setWidth(45);
								shotCpuSquare.setHeight(45);							
								shotCpuSquare.setCursor(Cursor.HAND);
								shotCpuSquare.setFill(Color.RED); // sets colour to red to tell the user a ship has been hit
								shotCpuSquare.toFront();
								group.getChildren().add(shotCpuSquare);
							}
							else if (board[currentX][currentY] == 0) 
							{
								shotCpuSquare.setX((50*currentX)+50);
								shotCpuSquare.setY((50*currentY)+50);
								shotCpuSquare.setWidth(45);
								shotCpuSquare.setHeight(45);							
								shotCpuSquare.setCursor(Cursor.HAND);
								shotCpuSquare.setFill(Color.DARKGREY); // sets colour to tell the user the com missed
								shotCpuSquare.toFront();
								group.getChildren().add(shotCpuSquare);
							}
							
						}
					});
					group.getChildren().add(thisSeaCom);
				}
			}
			
			//back in the prep phase
			while (shipsPlacedCom < 8) // runs until com has placed its max number of ships
			{	// the ships placed will be random	
				Random rng = new Random(); // sets up rng
				int x = rng.nextInt(10); // generates random coordinates
				int y = rng.nextInt(10);
				Boolean cpuHorizontal = rng.nextBoolean(); // randomly decides if current ship being placed is  vertical or horizontal
				
				if (carriersCom > 0) // variant of code above, checks each ship type and places accordingly
				{
					lengthCPU = 5;
					carriersCom = carriersCom -1;
					boatHPCom[shipsPlacedCom+1] = 5;
					
				}
				else if(battleshipsCom > 0) 
				{
					lengthCPU = 4;
					battleshipsCom = battleshipsCom -1;
					boatHPCom[shipsPlacedCom+1] = 4;
				}
				else if(destroyersCom > 0) 
				{
					lengthCPU = 3;
					destroyersCom = destroyersCom -1;
					boatHPCom[shipsPlacedCom+1] = 3;
				}
				else if(patrolsCom > 0) 
				{
					lengthCPU = 2;
					patrolsCom = patrolsCom -1;
					boatHPCom[shipsPlacedCom+1] = 2;
				}
				
				boolean legalSpace = canPlace(!cpuHorizontal, lengthCPU, cpuBoard, x, y);
					
					//validation complete, last thing is to create the coms ships
					if (legalSpace == true) 
					{
						for (int k = 0; k < lengthCPU; k++) 
						{
							if (cpuHorizontal == true) {
								cpuBoard[x+k][y] = shipsPlacedCom+1;
							}
							else {
								cpuBoard[x][y+k] = shipsPlacedCom+1;
							}
						}
						shipsPlacedCom++;
						
					}
				}
				
		


			
			
			return group;
			

		}

	}