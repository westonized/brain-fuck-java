package com.coltsoftware.brainfuck;

import static org.junit.Assert.*;

import org.junit.Test;

public final class OptimizeTests {

	@Test
	public void can_optomise() {
		assertEquals(">", Optomizer.optomize(">+-"));
	}

	@Test
	public void can_optomise_2() {
		assertEquals(">", Optomizer.optomize(">-+"));
	}

	@Test
	public void can_optomise_left_right() {
		assertEquals("+", Optomizer.optomize("+><"));
	}

	@Test
	public void can_optomise_right_left() {
		assertEquals("+", Optomizer.optomize("+<>"));
	}

	@Test
	public void can_optomise_both_left_right_and_minus_plus() {
		assertEquals("", Optomizer.optomize("-<>+"));
	}

	@Test
	public void can_optomise_whole_set() {
		assertEquals("-", Optomizer.optomize(">>><<<-"));
	}

	@Test
	public void doesnt_optimize() {
		assertEquals(">>>-<<<", Optomizer.optomize(">>>-<<<"));
	}

	@Test
	public void does_optimize() {
		assertEquals(">>>-<<<", Optomizer.optomize(">><>>-<<<"));
	}

	@Test
	public void does_optimize_2() {
		assertEquals("?+", Optomizer.optomize("?>><>>+-<<<+"));
	}

}
