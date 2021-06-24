package model.mutation;

import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class ExtraTestMutation extends Mutation{

	public ExtraTestMutation(double prob, List<FSMTest> tests) {
		this.prob = prob;
		this.mutacion = 1;
		this.tests = tests;
	}
	
	public ExtraTestMutation() {
		this.mutacion = 1;
	}
	public Chromosome[] mutate(Chromosome[] pob, String metReplace) {
		for(int i = 0; i < pob.length; i++)
			if(rnd.nextDouble() <= prob || (metReplace == "Elitist replacement" && !pob[i].isModified())) {
				FSMTest t = new FSMTest(tests.get(rnd.nextInt(tests.size())));
				pob[i].setGene(pob[i].getGenotype().size(), t);
			}
		return pob;
	}
	
	@Override
	public String toString() {
		return "Extra test mutation";
	}
}
