package model.initialization;

import java.util.List;
import java.util.Random;

import model.chromosome.Chromosome;
import model.chromosome.FSM;
import model.chromosome.FSMTest;

public abstract class Initialization {
	protected int initialization;
	protected int boundSize;
	protected Random rnd = new Random();

	public abstract Chromosome[] initialize(int size_pob, List<FSMTest> allTests, int[][] testsVSmutants);
	public void setSize(int size) {
		this.boundSize = size;
	}
	public int getInitialization() {
		return initialization;
	}
	
}
