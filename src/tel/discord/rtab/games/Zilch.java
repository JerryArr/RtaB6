package tel.discord.rtab.games;

import java.util.LinkedList;

import tel.discord.rtab.Achievement;
import tel.discord.rtab.games.objs.Dice;

public class Zilch extends MiniGameWrapper {
	static final String NAME = "Zilch";
	static final String SHORT_NAME = "Zilch";
	static final boolean BONUS = false;
	static final int NUM_DICE = 6;
	static final int WINNING_SCORE = 10_000;
	static final int MONEY_PER_POINT = 100;

	static final int[] SINGLE_DICE_SCORE = new int[] {100, 0, 0, 0, 50, 0};
	static final int[] TRIPLE_DICE_SCORE = new int[] {1000, 200, 300, 400, 500, 600};
	static final int[] BASE_TRIPLE_MULTIPLIER = new int[] {1, 2, 3, 4};
	static final int[] ENHANCED_TRIPLE_MULTIPLIER = new int[] {1, 2, 4, 8};
	
	static final int NO_SCORING_DICE_SCORE = 500;
	static final int THREE_PAIRS_SCORE = 1500;
	static final int STRAIGHT_SCORE = 2500;
	
	Dice dice;
	boolean isAlive;
	int score;
	int diceToRoll;

	@Override
	void startGame() {
		LinkedList<String> output = new LinkedList<>();
		isAlive = true;
		score = 0;
		diceToRoll = NUM_DICE;
		
		output.add("In Zilch, you will be given six six-sided dice with " +
				"which you can win over " + String.format("$%,d",
				convertToDollars(WINNING_SCORE)) + " by scoring dice combinations.");
		output.add("For each three-of-a-kind, you will earn 100 points times " +
				"the tripled die face. For example, three twos are worth " +
				String.format("%,d", TRIPLE_DICE_SCORE[1]) + " points, " + 
				"three threes are worth " + 
				String.format("%,d", TRIPLE_DICE_SCORE[2]) + " points, " +
				"and so on. The exception is that three ones are worth " +
				String.format("%,d", applyBaseMultiplier(TRIPLE_DICE_SCORE[0])) +
				" points.");	
		output.add("A four-, five-, or six-of-a-kind is respectively worth " +
				"two, three, or four times the corresponding three-of-a-kind " +
				"score.");
		if(enhanced)
			output.add("ENHANCE BONUS: A five- or six-of-a-kind is " +
					"respectively worth four or eight times the corresponding" +
					" three-of-a-kind score.");
		output.add("Three pairs are worth " + String.format("%,d", THREE_PAIRS_SCORE) +
				" points, and a straight from one to six is worth " +
				String.format("%,d", STRAIGHT_SCORE) + " points.");
		output.add("In addition, each one not part of one of the above " +
				"scoring combinations is worth " +
				String.format("%,d", SINGLE_DICE_SCORE[0]) + " points, and " +
				"each five not part of one of the above scoring combinations " +
				"is worth " + String.format("%,d", SINGLE_DICE_SCORE[4]) +
				" points.");
		output.add("Each combination must be scored in a single throw. Each " +
				"scored die will be taken away. If you score all your " +
				"remaining dice, you get **HOT DICE**, which means a fresh " +
				"set of six dice.");
		output.add("You may stop at any time. If you wouldn't otherwise have " +
				"any scoring dice from the first roll of the game, you get " +
				String.format("%,d", NO_SCORING_DICE_SCORE) + " points and " +
				"hot dice. If you cannot score any of your dice after that, " +
				"however, you get **ZILCH** and lose everything.");
		output.add(String.format("Each point is worth $%,d upon cashing out.",
				applyBaseMultiplier(MONEY_PER_POINT)));
		output.add("You will keep rolling until you choose to stop, zilch out, " +
				"or win the game by accumulating a total of " +
				String.format("%,d", WINNING_SCORE) + " points or more. " +
				"If you exceed " + String.format("%,d", WINNING_SCORE) +
				" points, you still get to keep the money from the excess points.");
		output.add("Good luck! Type ROLL when you're ready.");
		sendSkippableMessages(output);
		getInput();
	}

	@Override
	void playNextTurn(String input) {
		LinkedList<String> output = new LinkedList<>();

		if (input.equalsIgnoreCase("STOP")) {
			if (score == 0) {
				String message = "There's no risk yet, so ROLL!";
				output.add(message);
			} else {
				isAlive = false;
				output.add("Very well!");
				dice = new Dice(diceToRoll);
				dice.rollDice();
				output.add("You would have rolled: " + dice.toString());
				int pointsMissedOutOn = scoreDice(dice.getDice());
				output.add("... and that would have been worth " + (pointsMissedOutOn == 0
						? "**ZILCH**! Good move!"
						: String.format("%,d", pointsMissedOutOn) + " points."));
			}
		} else if (input.equalsIgnoreCase("ROLL")) {
			dice = new Dice(diceToRoll);
			dice.rollDice();
			output.add("You rolled: " + dice.toString());
			int rollValue = scoreDice(dice.getDice());

			String s1 = "...which scores ";
			if (rollValue == 0)
			{
				s1 += "**ZILCH**. Sorry.";
				score = 0;
				isAlive = false;
			} else {
				s1 += String.format("worth %,d points", rollValue);
				s1 += diceToRoll == 0 ? "and **HOT DICE**!" : "!";
				score += rollValue;
			}
			output.add(s1);

			if (isAlive)
			{
				String s2 = String.format("You now have %,d points (worth $%,d)",
						score, convertToDollars(score));

				if (score >= WINNING_SCORE) {
					s2 += "... which is enough to win! Congratulations! :smile:";
					output.add(s2);
					isAlive = false;
					Achievement.ZILCH_JACKPOT.check(getCurrentPlayer());
				} else {
					if(diceToRoll == 0) diceToRoll = 6;
					s2 += (" and " + diceToRoll + " di" + (diceToRoll == 1 ? "" : "c") + "e to roll.");
					output.add(s2);
					output.add("ROLL again if you dare, or type STOP to stop with your total.");
				}
			}
		}
		
		sendMessages(output);
		if(!isAlive)
			awardMoneyWon(convertToDollars(score));
		else
			getInput();
	}

	int convertToDollars(int points) {
		return points * applyBaseMultiplier(MONEY_PER_POINT);
	}

	int scoreDice(int[] diceFaces) {
		int diceScore = 0;
		/*
		 * As with the columns on the scoring table, diceCount's index
		 * is one less than the number of pips on the face represented.
		 */
		int[] diceCount = new int[dice.getNumFaces()];

		for (int i = 0; i < diceFaces.length; i++) {
			diceCount[diceFaces[i] - 1]++;
		}

		/*
		 * Combinations that only score with a fresh set of six dice go
		 * in this outer if block.
		 */
		if (diceToRoll == NUM_DICE) {
			if (isStraight(diceCount)) {
				diceToRoll = 0;
				return STRAIGHT_SCORE;
			}
		
			if (countPairs(diceCount) == 3) {
				diceToRoll = 0;
				return THREE_PAIRS_SCORE;
			}
		}

		for (int i = 0; i < diceCount.length; i++)
		{
			int faceScore = 0;
			if(diceCount[i] >= 3)
				faceScore = TRIPLE_DICE_SCORE[i] * (enhanced ? ENHANCED_TRIPLE_MULTIPLIER[diceCount[i]-3] : BASE_TRIPLE_MULTIPLIER[diceCount[i]-3]);
			else
				faceScore = SINGLE_DICE_SCORE[i] * diceCount[i];
			if(faceScore != 0)
			{
				diceScore += faceScore;
				diceToRoll -= diceCount[i];
			}
		}
		
		if(diceScore == 0 && score == 0) //first roll and no score, that's kind of impressive actually
		{
			diceToRoll = 0;
			return NO_SCORING_DICE_SCORE;
		}

		return diceScore;
	}

	boolean isStraight(int[] frequencyCount) {
		if (frequencyCount.length != dice.getNumFaces()) {
			throw new IllegalArgumentException("Each die has " +
					dice.getNumFaces() + " faces, but frequencyCount is of " +
					"length " + frequencyCount.length + ".");
		}

		int minFrequency = Integer.MAX_VALUE;
		int maxFrequency = Integer.MIN_VALUE;

		for (int i = 0; i < frequencyCount.length; i++) {
			if (frequencyCount[i] < minFrequency) {
				minFrequency = frequencyCount[i];
			}
			if (frequencyCount[i] > maxFrequency) {
				maxFrequency = frequencyCount[i];
			}
		}

		return minFrequency == 1 && maxFrequency == 1;
	}

	int countPairs(int[] frequencyCount) {
		int pairs = 0;
		for (int i = 0; i < frequencyCount.length; i++) {
			if (frequencyCount[i] == 2) {
				pairs++;
			}
		}

		return pairs;
	}

	@Override
	String getBotPick() {
		// The bot will always try to get at least the no-scoring-dice total
		if (score < NO_SCORING_DICE_SCORE) {
			return "ROLL";
		}

		Dice testDice = new Dice(diceToRoll);
		if (scoreDice(testDice.getDice()) == 0) {
			return "STOP";
		} else {
			return "ROLL";
		}
	}

	@Override
	void abortGame() {
		//Auto-stop, as it is a push-your-luck style game.
		awardMoneyWon(convertToDollars(score));
	}

	@Override public String getName() { return NAME; }
	@Override public String getShortName() { return SHORT_NAME; }
	@Override public boolean isBonus() { return BONUS; }
	@Override public String getEnhanceText() {
		return "Five-of-a-kind is worth four instead of three times the " +
				"corresponding three-of-a-kind, and six-of-a-kind is worth " +
				"eight instead of four times the corresponding " +
				"three-of-a-kind.";
	}
	
}
