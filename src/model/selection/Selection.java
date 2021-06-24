package model.selection;

import model.chromosome.Chromosome;

public interface Selection {
	public Chromosome[] select(Chromosome[] pob, int tam_pob);
	public int getSelection();
}