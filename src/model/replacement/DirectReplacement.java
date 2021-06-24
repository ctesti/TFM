package model.replacement;

import java.util.HashSet;
import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;
import utils.NSGAIIAlgorithms;

public class DirectReplacement extends Replacement {

	public DirectReplacement(int[][] testsVSmutants) {
		this.replacement = 1;
		this.testsVSmutants = testsVSmutants;
	}

	@Override
	public Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob, List<FSMTest> allTests) {
		for (int i = 0; i < new_pob.length; i++) {
			new_pob[i].evaluateDistinguishingMutationScore(testsVSmutants);
		}
		List<HashSet<Chromosome>> fronts = NSGAIIAlgorithms.fastNonDominatedSort(new_pob, new_pob.length);
		
		int f = 0;
		while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f that fits entirely
			NSGAIIAlgorithms.crowdingDistanceAssignment(new_pob, fronts.get(f));		
			f++;
		}
		return new_pob;
	}

	@Override
	public String toString() {
		return "Direct replacement";
	}
}
