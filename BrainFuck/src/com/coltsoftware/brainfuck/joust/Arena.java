package com.coltsoftware.brainfuck.joust;

import com.coltsoftware.brainfuck.BrainFuck;
import com.coltsoftware.brainfuck.Program;
import com.coltsoftware.brainfuck.ReverseTapePointer;
import com.coltsoftware.brainfuck.StandardTapePointer;
import com.coltsoftware.brainfuck.Tape;
import com.coltsoftware.brainfuck.TapeException;
import com.coltsoftware.brainfuck.TapePointer;

public final class Arena {

	public static class Builder {

		private Program program1;
		private Program program2;
		private int tapeLength = 10;

		public Builder programStrings(String program1, String program2) {
			return program1String(program1).program2String(program2);
		}

		public Builder program1String(String program1) {
			return program1(Program.compile(program1));
		}

		public Builder program2String(String program2) {
			return program2(Program.compile(program2));
		}

		public Builder program1(Program program1) {
			this.program1 = program1;
			return this;
		}

		public Builder program2(Program program2) {
			this.program2 = program2;
			return this;
		}

		public Builder tapeLength(int tapeLength) {
			this.tapeLength = tapeLength;
			return this;
		}

		public Arena build() {
			return new Arena(tapeLength, program1, program2);
		}

		public AllLengthScore allLengthScore() {
			AllLengthScore score = new AllLengthScore();
			for (int i = 10; i <= 30; i++) {
				Arena arena = tapeLength(i).build();
				JoustResult joust = arena.joust(10000);
				score.add(joust);
				Tape tape = arena.getTape();
				out(tape.toString());
				out("\n");
			}
			return score;
		}
	}

	public static class AllLengthScore {
		private int lengthsWon;
		private int lengthsLost;
		private int lengthsDrawn;
		private int wonMoves;
		private int lostMoves;
		private int drawnMoves;

		public void add(JoustResult joust) {
			switch (joust.winner) {
			case 1:
				lengthsWon++;
				wonMoves += joust.moves;
				break;
			case -1:
				lengthsLost++;
				lostMoves += joust.moves;
				break;
			default:
				lengthsDrawn++;
				drawnMoves += joust.moves;
				break;
			}
		}

		public int getLengthsWon() {
			return lengthsWon;
		}

		public int getLengthsLost() {
			return lengthsLost;
		}

		public int getLengthsDrawn() {
			return lengthsDrawn;
		}

		public int getWonMoves() {
			return wonMoves;
		}

		public int getLostMoves() {
			return lostMoves;
		}

		public int getDrawnMoves() {
			return drawnMoves;
		}

		public int basicScore() {
			return lengthsWon - lengthsLost;
		}

	}

	public class JoustResult {
		private final int winner;
		private final int moves;

		private JoustResult(int winner, int moves) {
			this.winner = winner;
			this.moves = moves;
		}

		public int getWinner() {
			return winner;
		}

		public int getMoves() {
			return moves;
		}
	}

	private static final String setTo128 = "(+)*128";

	private final Tape tape;
	private final TapePointer pointer;
	private final TapePointer pointerreversed;
	private final BrainFuck engine1;
	private final BrainFuck engine2;
	private int zeroCount1 = 0;
	private int zeroCount2 = 0;

	public Arena(int tapeLength, Program program1, Program program2) {
		tape = new Tape(tapeLength);
		pointer = new StandardTapePointer(tape);
		pointerreversed = ReverseTapePointer.reverse(tape);
		TapePointer reg = new StandardTapePointer(tape);
		TapePointer rev = ReverseTapePointer.reverse(tape);
		new BrainFuck(reg, setTo128).execute();
		new BrainFuck(rev, setTo128).execute();
		engine1 = new BrainFuck(pointer, program1);
		engine2 = new BrainFuck(pointerreversed, program2);
	}

	public JoustResult joust(int moves) {
		for (int i = 1; i <= moves; i++) {
			boolean out1 = false;
			boolean out2 = false;
			try {
				engine1.executeSingleStep();
			} catch (TapeException ex) {
				out("Prog 1 end of tape:\n");
				out1 = true;
			}
			try {
				engine2.executeSingleStep();
			} catch (TapeException ex) {
				out("Prog 2 end of tape:\n");
				out2 = true;
			}
			if (out1 && out2) {
				out("Draw both out:\n");
				return new JoustResult(0, i);
			}
			if (out1) {
				return new JoustResult(-1, i);
			}
			if (out2) {
				return new JoustResult(1, i);
			}
			if (tape.getAt(0) == 0)
				zeroCount1++;
			else
				zeroCount1 = 0;
			if (tape.getAt(tape.length() - 1) == 0)
				zeroCount2++;
			else
				zeroCount2 = 0;
			if (zeroCount1 == 2 && zeroCount2 == 2) {
				out("Draw by death:\n");
				return new JoustResult(0, i);
			}
			if (zeroCount1 == 2) {
				out("Prog 2 clasic win:\n");
				return new JoustResult(-1, i);
			}
			if (zeroCount2 == 2) {
				out("Prog 1 clasic win:\n");
				return new JoustResult(1, i);
			}
		}
		out("Draw:\n");
		return new JoustResult(0, moves);
	}

	private static void out(String string) {
		// System.out.print(string);
	}

	public Tape getTape() {
		return tape;
	}

}
