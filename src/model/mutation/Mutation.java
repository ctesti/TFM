package model.mutation;

import java.util.List;
import java.util.Random;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public abstract class Mutation {
	protected Random rnd = new Random();
	protected double prob;
	protected int mutacion;
	protected List<FSMTest> tests;
		
	public abstract Chromosome[] mutate(Chromosome[] pob, String metReplace);
	public int getMutation() {
		return mutacion;
	}
}
