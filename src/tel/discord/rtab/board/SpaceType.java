package tel.discord.rtab.board;

public enum SpaceType implements WeightedSpace
{
	CASH	(58)
	{
		@Override
		public int getWeight(int playerCount)
		{
			//Boost cash frequency in large games
			//And even more in extra large games
			if(playerCount >= 8)
				return weight + 10;
			else if(playerCount >= 6)
				return weight + 5;
			//Normal occurence in moderately-sized games
			else return weight;
		}
	},
	BOOSTER	(13),
	GAME	(1300)
	{
		@Override
		public int getWeight(int playerCount)
		{
			//Boost or reduce minigame frequency depending on playercount
			//More minigames in small games
			if(playerCount < 4)
				return weight + 5;
			//Very few minigames in extra large games
			else if(playerCount >= 8)
				return weight - 10;
			//Few minigames in large games
			else if(playerCount >= 6)
				return weight - 5;
			//Normal occurence in moderately-sized games
			else return weight;
		}
	},
	EVENT	(13)
	{
		@Override
		public int getWeight(int playerCount)
		{
			//Fewer events in small games
			if(playerCount < 4)
				return weight - 5;
			else return weight;
		}
	},
	GRAB_BAG( 2),
	BLAMMO  ( 1),
	BOMB	( 0); //Never generated, but tends to end up on the board anyway
	
	int weight;
	SpaceType(int spaceWeight)
	{
		weight = spaceWeight;
	}
	@Override
	public int getWeight(int playerCount)
	{
		//Hoo boy. Lots of things will override this.
		return weight;
	}
}
