
public class Ships extends Main{

	public int shipType; // Set up variables
	public int shipHp;
	public boolean shipIsVertical = true; // True for vertical, false for horizontal

		
		// SHIP TYPES
		// 1 does not exist
		// 2 = Patrol boat, size 2
		// 3 = Destroyer, size 3
		// 4 = Battleship, size 4
		// 5 = Aircraft carrier, size 5
		//boolean shipIsVertical = true;
		
	public Ships(int shipType, boolean shipIsVertical)
	{
		this.shipType = shipType; // Sets unique variable for each ship
		this.shipIsVertical = shipIsVertical;
		shipHp = shipType; // Hp based on type
		
	}
	
	public void shipIsHit()
	{
		int shipHp = shipType;
		shipHp = shipHp - 1;
	}
	
	public boolean shipIsNotSunk() // Changed from shipSunk
	{
		return shipHp >= 0; // Fires if ship Hp reaches 0 (or below somehow)
	}
}
