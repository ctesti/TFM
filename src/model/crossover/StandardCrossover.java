package model.crossover;

import java.util.List;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class StandardCrossover extends Crossover {

	public StandardCrossover(double prob) {
		this.prob = prob;
		this.crossover = 2;
	}

	public StandardCrossover() {
		this(0.6);
	}
	
	@Override
	public Chromosome[] cruza(Chromosome[] pob) {
		Chromosome ind1, ind2;
		List<FSMTest> t1, t2;
		int pos1, pos2;
		
		for(int i = 0; i + 1 < pob.length; i = i+2) {
			if (prob >= Rnd.nextDouble()) {
				ind1 = ChromosomeFactory.copyChromosome(pob[i]);
				ind2 = ChromosomeFactory.copyChromosome(pob[i+1]);
				
				pos1 =  ind1.getGenotype().size() > 0 ? Rnd.nextInt(ind1.getGenotype().size()) : 0; // Crossover position 1
				pos2 =  ind2.getGenotype().size() > 0 ? Rnd.nextInt(ind2.getGenotype().size()) : 0; // Crossover position 2


				t1 = ind1.getGenes(pos1);
				t2 = ind2.getGenes(pos2);

				ind1.setGenes(pos1, t2);
				ind2.setGenes(pos2, t1);
				
				pob[i] = ind1;
				pob[i+1] = ind2;
				pob[i].setModified(true);
				pob[i+1].setModified(true);
			}
		}
		return pob;
	}
	
	@Override
	public String toString() {
		return "Standard crossover";
	}
}