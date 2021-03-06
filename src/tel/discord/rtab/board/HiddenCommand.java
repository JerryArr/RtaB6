package tel.discord.rtab.board;

public enum HiddenCommand
{
	NONE(	"**Absolutely Nothing!**\n"
			+ "(You should never see this message)",
			"You do not currently possess any hidden command."),
	FOLD(	"A **FOLD**!\n"
			+ "The fold allows you to drop out of the round at any time by typing **!fold**.\n"
			+ "If you use it, you will keep your mulipliers and minigames, "
			+ "so consider it a free escape from a dangerous board!",
			"You currently possess a **FOLD**.\n"
			+ "You may use it at any time by typing **!fold**."),
	REPEL(	"**BLAMMO REPELLENT**!\n"
			+ "You may use this by typing **!repel** whenever any player is facing a blammo to automatically block it.\n"
			+ "The person affected will then need to choose a different space from the board.",
			"You currently possess **BLAMMO REPELLENT**.\n"
			+ "You may use it when a blammo is in play by typing **!repel**."),
	BLAMMO(	"A **BLAMMO SUMMONER**!\n"
			+ "You may use this by typing **!blammo** at any time to give the next player a blammo!\n"
			+ "This will activate on the NEXT turn (not the current one), and will replace that player's normal turn.",
			"You currently possess a **BLAMMO SUMMONER**.\n"
			+ "You may use it at any time by typing **!blammo**."),
	DEFUSE(	"A **SHUFFLER**!\n"
			+ "You may use this at any time by typing **!shuffle 13**, replacing '13' with the space you wish to shuffle.\n"
			+ "This will replace the contents of the space with a newly-generated space, removing any bomb there. Use this wisely!\n",
			"You currently possess a **SHUFFLER**.\n"
			+ "You may use it at any time by typing **!shuffle** followed by the space you wish to shuffle."),
	WAGER(	"A **WAGERER**!\n"
			+ "The wager allows you to force all living players to add a portion of their total bank to a prize pool, "
			+ "which the winner(s) of the round will claim.\n"
			+ "The amount is equal to 1% of the last-place player's total bank, "
			+ "and you can activate this at any time by typing **!wager**.",
			"You currently possess a **WAGERER**.\n"
			+ "You may use it at any time by typing **!wager**."),
	BONUS(	"A **BONUS BAG**!\n"
			+ "The bonus bag contains many things, "
			+ "and you can use this command to pass your turn and draw from the bag instead.\n"
			+ "To do so, type !bonus followed by either 'cash', 'boost', 'game', or 'event', depending on what you want.",
			"You currently possess a **BONUS BAG**.\n"
			+ "You may use it at any time by typing **!bonus** followed by 'cash', 'boost', 'game', or 'event'.");
	
	public String pickupText, carryoverText;
	
	HiddenCommand(String pickupText, String carryoverText)
	{
		this.pickupText = pickupText;
		this.carryoverText = carryoverText;
	}
}
