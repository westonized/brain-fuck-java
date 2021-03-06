package com.coltsoftware.brainfuck.joust;

import static org.junit.Assert.*;

import org.junit.Test;

import com.coltsoftware.brainfuck.joust.Arena.AllLengthScore;
import com.coltsoftware.brainfuck.joust.Arena.JoustResult;

public final class ArenaTests {

	@Test
	public void inital_setup_length_10() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("[-]", "+[]").build();
		assertEquals(-128, arena.getTape().getAt(0));
		assertEquals(-128, arena.getTape().getAt(9));
	}

	@Test
	public void can_create_correct_length() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("(>)*9[-]", "+[]").build();
		JoustResult joustResult = arena.joust(11 + 257);
		System.out.print(arena.getTape());
		assertEquals(1, joustResult.getWinner());
		assertEquals(11 + 257, joustResult.getMoves());
	}

	@Test
	public void only_plays_number_of_moves_given_resulting_in_draw() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("+(>)*9[-]", "+[]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(0, joust.getWinner());
		assertEquals(10, joust.getMoves());
	}

	@Test
	public void no_double_joust() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("+(>)*9[-]", "+[]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(0, joust.getWinner());
		assertEquals(10, joust.getMoves());
	}

	@Test
	public void high_water_test() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("+[.]", ">+[-]<<[.]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(1, joust.getWinner());
		assertEquals(3, joust.getP1HighInstruction().getProgramOffset());
		assertEquals(6, joust.getP2HighInstruction().getProgramOffset());
	}

	@Test
	public void high_water_all_length_test() {
		AllLengthScore allLengthScore = new Arena.Builder().programStrings(
				"+[.]", ">+[-]<<[.]").allLengthScore();
		assertEquals(3, allLengthScore.getHighWater1().getProgramOffset());
		assertEquals(6, allLengthScore.getHighWater2().getProgramOffset());
	}

	@Test
	public void end_of_moves_draw_1() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("[.]", "-[.]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(1, joust.getWinner());
		assertEquals(10, joust.getMoves());
	}

	@Test
	public void end_of_moves_draw_2() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("-[.]", "[.]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(-1, joust.getWinner());
		assertEquals(10, joust.getMoves());
	}

	@Test
	public void end_of_moves_draw() {
		Arena arena = new Arena.Builder().tapeLength(10)
				.programStrings("-[.]", "-[.]").build();
		JoustResult joust = arena.joust(10);
		assertEquals(0, joust.getWinner());
		assertEquals(10, joust.getMoves());
	}

	@Test
	public void immediate_fail() {
		Arena arena = new Arena.Builder()
				.tapeLength(10)
				.programStrings("<-++-<><>>", ">>(+)*128<(+)*128(>)*8([-]>)*21")
				.build();
		JoustResult joust = arena.joust(10);
		assertEquals(-1, joust.getWinner());
		assertEquals(1, joust.getMoves());
		assertNotNull(joust.getP1HighInstruction());
		assertNotNull(joust.getP2HighInstruction());
	}

	@Test
	public void all_lengths_score() {
		AllLengthScore allLengthScore = new Arena.Builder()
				.tapeLength(10)
				.programStrings("<-++-<><>>", ">>(+)*128<(+)*128(>)*8([-]>)*21")
				.allLengthScore();
		assertNotNull(allLengthScore.getHighWater1());
		assertNotNull(allLengthScore.getHighWater2());
	}

	@Test
	public void all_lengths_score_empty_first_program() {
		AllLengthScore allLengthScore = new Arena.Builder().tapeLength(10)
				.programStrings("", "[.]").allLengthScore();
		assertNull(allLengthScore.getHighWater1());
		assertNotNull(allLengthScore.getHighWater2());
	}

	@Test
	public void all_lengths_score_empty_second_program() {
		AllLengthScore allLengthScore = new Arena.Builder().tapeLength(10)
				.programStrings("[.]", "").allLengthScore();
		assertNotNull(allLengthScore.getHighWater1());
		assertNull(allLengthScore.getHighWater2());
	}
}
