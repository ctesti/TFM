package model.initialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSM;
import model.chromosome.FSMTest;

public class IncrementalInitialization extends Initialization {
	
	public IncrementalInitialization(int size) {
		this.initialization = 1;
	}
	
	public IncrementalInitialization() {
		this.initialization = 1;
	}
	
	
	@Override
	public Chromosome[] initialize(int size_pob, List<FSMTest> allTests, int[][] testsVSmutants) {
		Chromosome[] pop = new Chromosome[size_pob];
		int size = 0;
		for(FSMTest t : allTests)
			size += t.getSize();
		
		for(int i = 0; i < size_pob; i++) {
			List<FSMTest> tests = new ArrayList<FSMTest>();
			tests.add(allTests.get(rnd.nextInt(allTests.size())));
			int actualSize = tests.get(0).getSize();
			for(int j = 1; actualSize < size && j < allTests.size() && rnd.nextInt(allTests.size())>j/10; j++) {
				FSMTest test = allTests.get(rnd.nextInt(allTests.size()));
				if(actualSize + test.getSize() < size && !tests.contains(test)) {
					tests.add(test);
					actualSize += test.getSize();
				}
			}
			pop[i] = ChromosomeFactory.createChromosome(tests);
			pop[i].evaluateDistinguishingMutationScore(testsVSmutants);
		}
		
		return pop;
		
	}

	@Override
	public String toString() {
		return "Incremental initialization";
	}
}



