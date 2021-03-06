package model.replacement;

import java.util.List;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class BasicElitistReplacement extends Replacement{
	

	public BasicElitistReplacement(int[][] testsVSmutants) {
		this.replacement = 5;
		this.testsVSmutants = testsVSmutants;
	}
	
	public BasicElitistReplacement() {
		this(null);
	}
	
	@Override
	public Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob, List<FSMTest> allTests) {
		for (int i = 0; i < new_pob.length; i++) {
			new_pob[i].evaluateDistinguishingMutationScore(testsVSmutants);
		}
		
		quicksort(pob, 0, pob.length - 1);
		quicksort(new_pob, 0, pob.length - 1);	//esto sustituye los elit peores. si quitamos esto sustituye los elit aleatorios

		for(int i = 0; i < this.replacement; i++) {
			new_pob[pob.length-i-1] = ChromosomeFactory.copyChromosome(pob[i]);
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
    	return "Basic elitist replacement";
    }

}
