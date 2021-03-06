package model.mutation;

import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class ReplaceMutation extends Mutation {

	public ReplaceMutation(double prob, List<FSMTest> tests) {
		this.prob = prob;
		this.mutacion = 2;
		this.tests = tests;
	}
	
	public ReplaceMutation() {
		this.mutacion = 2;
	}
	
	public Chromosome[] mutate(Chromosome[] pob, String metReplace) {
		int pos;
		for(int i = 0; i < pob.length; i++)
			if(rnd.nextDouble() <= prob || (metReplace == "Elitist Replacement" && !pob[i].isModified())) {
				pos = rnd.nextInt(pob[i].getGenotype().size());
				FSMTest t = new FSMTest(tests.get(rnd.nextInt(tests.size())));
				pob[i].setGene(pos, t);				
			}
		return pob;
	}
	
	@Override
	public String toString() {
		return "Replace mutation";
	}
}