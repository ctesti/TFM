package model.replacement;

import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public abstract class Replacement {
	protected int replacement;
	protected int[][] testsVSmutants;
	
    public abstract Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob, List<FSMTest> allTests);
    
    public int getReplacement() {
    	return replacement;
    }
}
