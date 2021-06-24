package model.chromosome;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Chromosome {
	private List<FSMTest> tests;
	
	private double mutationScore;
	private double numInputs;  //to be used as an objective
	private int id;
	private int front;
	private double crowdingDistance;
	private boolean modified;
	
	public Chromosome(List<FSMTest> tests) {	
		this.tests = tests;
		
		int size = 0;
		for(FSMTest t : tests)
			size += t.getSize();
		this.numInputs = size;
		this.modified = false;
	}

	public Chromosome(Chromosome c) {		//copy
		this.tests = c.copyGenotype();
		this.numInputs = c.getNumInputs();
		this.crowdingDistance = c.getCrowdingDistance();
		this.front = c.getFront();
		this.mutationScore = c.getMutationScore();
		this.modified = c.isModified();
	}
	
	public Chromosome(double mutationScore, double numInputs, int id, int front, double crowdingDistance) {	
		this.mutationScore = mutationScore;
		this.numInputs = numInputs;
		this.id = id;
		this.front = front;
		this.crowdingDistance = crowdingDistance;
		this.modified = false;
	}
	
	public double getMutationScore() {
		return this.mutationScore;
	}
	
	public double getNumInputs() {
		return this.numInputs;
	}
	
	public double getNumTests() {
		return this.tests.size();
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getFront() {
		return this.front;
	}
	
	public void setFront(int front) {
		this.front = front;
	}
	
	public double getCrowdingDistance() {
		return this.crowdingDistance;
	}
	
	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}
	
	public Chromosome crowdedComparisonOperator(Chromosome other) {
		if (other.getFront() < this.front) {
			return other;
		}
		else if (this.front == other.getFront()) {
			if (other.getCrowdingDistance() > this.crowdingDistance) {
				return other;
			}
		}
		return this;
	}

	
	public boolean isModified() {
		return modified;
	}
	
	public void setModified(boolean b) {
		modified = b;
	}
	
	public List<FSMTest> getGenotype() {
		return tests;
	}
	
	public List<FSMTest> copyGenotype() {
		List<FSMTest> copy = new ArrayList<FSMTest>();
		for(int i = 0; i < tests.size(); i++) {
			copy.add(new FSMTest(tests.get(i)));
		}
		return copy;
	}

	public FSMTest removeGene(int pos) {
		if (tests.size() > 1) 
			return tests.remove(pos);
		
		return tests.get(pos);
	}
	
	public FSMTest getGene(int pos) {
		return tests.get(pos);
	}

	public List<FSMTest> getGenes(int init, int end){
		List<FSMTest> l = new ArrayList<FSMTest>();
		for (int i = init; i < end; i++) {
			FSMTest t = tests.get(i);
			l.add(t);
		}
		return l;
	}
	
	public List<FSMTest> getGenes(int init) {
		return getGenes(init, tests.size());
	}
	
	public void setGene(int pos, FSMTest t) {
		if (!testInList(t)){
			int size = 0;
			if (pos < tests.size())
				tests.remove(pos);
			for(FSMTest test : tests)
				size += test.getSize();
			tests.add(pos, t);
			size += t.getSize();
			numInputs = size;
		}
	}	

	public void setGenes(int pos, List<FSMTest> ts) {
		int size = 0;
		FSMTest aTest = null;
		boolean eliminated = false;
		for(int i = tests.size() - 1; i >= pos; i--) {
			aTest = tests.remove(i);
			eliminated = true;
		}
		for(FSMTest test : tests)
			size += test.getSize();
		for(int i = 0; i < ts.size(); i++) {
			if (!testInList(ts.get(i))){
				tests.add(ts.get(i));
				size += ts.get(i).getSize();
			}
		}
		if(eliminated && tests.size() == 0) {
			tests.add(aTest);
			size += aTest.getSize();
		}
		
		numInputs = size;
	}
	
	public boolean testInList(FSMTest o) {
		for(FSMTest t : this.tests) {
			if (t.equals(o)) {
				return true;
			}
		}
		return false;
	}
	
	public int getSize() {
		int size = 0;
		for(FSMTest t : tests)
			size += t.getSize();
		return size;
	}
	
	public int getSizeTest(int i) {
		return tests.get(i).getSize();
	}
	
	public int getSizeTests(int init, int end) {
		int size = 0;
		for(int i = init; i < end; i++)
			size += tests.get(i).getSize();
		return size;
	}
		
	public void evaluateMutationScore(int[][] testsVSmutants) {
		mutationScore = 0;
		int accKilled = 0;
		int mutantsSize = testsVSmutants[0].length;
		for(int posMutant = 0; posMutant < mutantsSize; posMutant++) {
			int posTest = 0;
			boolean killed = false;
			while (posTest < tests.size() && !killed) {
				killed = testsVSmutants[tests.get(posTest).getId()][posMutant] != Integer.MAX_VALUE;
				
				posTest++;
			}
			accKilled += killed ? 1 : 0;
		}
		
		mutationScore = ((double) accKilled) / mutantsSize;		
	}
	
	public void evaluateDistinguishingMutationScore(int[][] testsVSmutants) {
		this.mutationScore = 0;

		int mutantsSize = testsVSmutants[0].length;
		ArrayList<ArrayList<Long>> binaryVectors = new ArrayList<ArrayList<Long>>();

		for(int posMutant = 0; posMutant < mutantsSize; posMutant++) {
			boolean distinctToSpec = false;
			ArrayList<Long> mutantBinary = new ArrayList<Long>(); //tests.size()/63
			for (int posTest = 0; posTest < this.tests.size(); posTest++) {
				int i = posTest;
				long toBinary = 0;
				while (i < this.tests.size() && i < posTest+60) {
					boolean killed = (testsVSmutants[this.tests.get(i).getId()][posMutant] != Integer.MAX_VALUE);
					toBinary += (killed ? Math.pow(2, i%60) : 0); // If it kills the mutant (!= max_value) then we add the value based on its position
					i++;
				}
				if(toBinary != 0)
					distinctToSpec = true;
				posTest = i -1;
				mutantBinary.add(toBinary);
			}
			if(distinctToSpec)
				binaryVectors.add(mutantBinary);
		}

		HashSet<ArrayList<Long>> distinctMutants = new HashSet<ArrayList<Long>>(); //Set to store the index of the different mutants

		distinctMutants.add(new ArrayList<Long>());//Add an index as the specification

		for(int posMutant = 0; posMutant < binaryVectors.size(); posMutant++) {
			distinctMutants.add(binaryVectors.get(posMutant));
			for (int posMutant2 = posMutant + 1; posMutant2 < binaryVectors.size(); posMutant2++) {
			    boolean distinguish = false;
				for(int posTestVec = 0; posTestVec < binaryVectors.get(posMutant).size() && !distinguish; posTestVec++) {
				     if(!distinguish && !binaryVectors.get(posMutant).get(posTestVec).equals(binaryVectors.get(posMutant2).get(posTestVec))){
				    	 distinguish = true;
				     }
				}
				if (!distinguish) { //The mutant vectors are the same
					if(distinctMutants.contains(binaryVectors.get(posMutant))) {
				   	distinctMutants.remove(binaryVectors.get(posMutant));
				   	}
				}
			}
		}

		this.mutationScore = distinctMutants.size();
		this.mutationScore = 1 - (this.mutationScore / (mutantsSize + 1));
		if (this.mutationScore > 1 || this.mutationScore < 0) {
		this.mutationScore = 1;
		}
		this.mutationScore = truncate(this.mutationScore, 4);
	}
	
	static double truncate(double value, int decimalpoint){  
        // Using the pow() method
        value = value * Math.pow(10, decimalpoint);
        value = Math.floor(value);
        value = value / Math.pow(10, decimalpoint);  
        return value;
    }

	public void showMaximizedMutationScore() {
		System.out.println("Mutation score (as maximized): " + (1-this.mutationScore));
	}

	
	public String toString() {
		return tests.toString();
	}


	@Override
	public boolean equals (Object c) {
		if (c == this) 
			return true;
		if(! (c instanceof Chromosome))
			return false;
		if (this.crowdingDistance != ((Chromosome)c).getCrowdingDistance()) return false;
		if (this.front != ((Chromosome)c).getFront()) return false;
		if (this.id != ((Chromosome)c).getId()) return false;
		if (this.mutationScore != ((Chromosome)c).getMutationScore()) return false;
		if (this.numInputs != ((Chromosome)c).getNumInputs()) return false;
		if (!this.tests.equals(((Chromosome)c).getGenotype())) return false;
		
		return true;
	}
	
}
