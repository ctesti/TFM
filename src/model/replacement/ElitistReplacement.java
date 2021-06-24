package model.replacement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;


import utils.NSGAIIAlgorithms;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class ElitistReplacement extends Replacement {
	protected Random rnd = new Random();

	
	public ElitistReplacement(int[][] testsVSmutants) {
		this.replacement = 2;
		this.testsVSmutants = testsVSmutants;
	}
	
	public ElitistReplacement() {
		this(null);
	}
	
	@Override
	public Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob, List<FSMTest> allTests) {
		for (int i = 0; i < new_pob.length; i++) {
			new_pob[i].evaluateDistinguishingMutationScore(testsVSmutants);
			pob[i].evaluateDistinguishingMutationScore(testsVSmutants);
		}

		Chromosome[] R_pob = new Chromosome[pob.length + new_pob.length];
		for(int i = 0; i < pob.length; i++)
			R_pob[i] = new Chromosome(pob[i]);
		for(int i = 0; i < new_pob.length; i++)
			R_pob[pob.length +i] = new Chromosome(new_pob[i]);
		
		// Remove x repeated elements in R_pob and add x random new elements
		int size = 0;
		for(FSMTest t : allTests)
			size += t.getSize();
		
		for (int i = 0; i < R_pob.length; i++) {
			for (int j = i + 1 ; j < R_pob.length; j++) {
				if (R_pob[i].equals(R_pob[j])) {
					List<FSMTest> tests = new ArrayList<FSMTest>();
					tests.add(allTests.get(rnd.nextInt(allTests.size())));
					int actualSize = tests.get(0).getSize();
					for(int k = 1; actualSize < size && k < allTests.size() && rnd.nextDouble() > actualSize/size; k++) {
						FSMTest test = allTests.get(rnd.nextInt(allTests.size()));
						if(actualSize + test.getSize() < size && !tests.contains(test)) {
							tests.add(test);
							actualSize += test.getSize();
						}
					}
					R_pob[j] = ChromosomeFactory.createChromosome(tests);
					R_pob[j].evaluateDistinguishingMutationScore(testsVSmutants);
					
				}
			}
		}
		
		List<HashSet<Chromosome>> fronts = NSGAIIAlgorithms.fastNonDominatedSort(R_pob, pob.length);
		
		Chromosome[] P_pob = new Chromosome[pob.length];
		
		int f = 0;
		int new_i = 0;
		while (f < fronts.size() && !fronts.get(f).isEmpty() && fronts.get(f).size() <= (P_pob.length - new_i)) { // for each front f that fits entirely
			NSGAIIAlgorithms.crowdingDistanceAssignment(R_pob, fronts.get(f));
			for (Chromosome elem : fronts.get(f)) {
				P_pob[new_i] = elem;
				new_i++;
			}			
			f++;
		}
		
		if(new_i != P_pob.length) {
			Chromosome[] aux_pob = new Chromosome[fronts.get(f).size()]; //to prepare the front that will be split
			int aux_i = 0;
			NSGAIIAlgorithms.crowdingDistanceAssignment(R_pob, fronts.get(f));
			for (Chromosome aux_elem : fronts.get(f)) {
				aux_pob[aux_i] = aux_elem;
				aux_i++;
			}
			
			quicksort(aux_pob, 0, aux_pob.length - 1);
			
			aux_i = 0;
			while (new_i < P_pob.length) { //add the remaining elements
				P_pob[new_i] = aux_pob[aux_i];
				aux_i++;
				new_i++;
			}
		}
		
		for(int i = 0; i < P_pob.length; i++) {
			new_pob[i] = ChromosomeFactory.copyChromosome(P_pob[i]);
		}

		return new_pob;
	}
	
	private void quicksort(Chromosome[] pob, int low, int high) {
		if (low < high) {
			int p = partition(pob, low, high);
			quicksort(pob, low, p - 1);
        	quicksort(pob, p + 1, high);
		}
	}
	
	private int partition(Chromosome[] pob, int low, int high) {
	    Chromosome pivot = pob[high];
	    Chromosome aux;
	    int i = low - 1;
	    for (int j = low; j < high; j++)
		    if (pob[j] == pob[j].crowdedComparisonOperator(pivot)) {
	            i++;
	            aux = ChromosomeFactory.copyChromosome(pob[i]);
	            pob[i] = ChromosomeFactory.copyChromosome(pob[j]);
	            pob[j] = aux;
	        }
        aux = ChromosomeFactory.copyChromosome(pob[i+1]);
        pob[i+1] = ChromosomeFactory.copyChromosome(pob[high]);
        pob[high] = aux;
        return i + 1;
	}
	
    	@Override
    	public String toString() {
    		return "Elitist replacement";
    	}
}