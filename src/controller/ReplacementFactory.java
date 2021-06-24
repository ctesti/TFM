package controller;

import model.replacement.Replacement;
import model.replacement.DirectReplacement;
import model.replacement.ElitistReplacement;

public class ReplacementFactory {

	public static Replacement createReplacement(Replacement r, int[][] testsVSmutants) {
		switch(r.getReplacement()) {
		case 1:
			return new DirectReplacement(testsVSmutants);
		case 2:
			return new ElitistReplacement(testsVSmutants);
		default:
			System.err.println("Error creando reemplazo");
			return null;
		}
	}
}
