package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import model.chromosome.Chromosome;
import model.chromosome.FSM;
import model.chromosome.FSMTest;
import model.crossover.ContinuousCrossover;
import model.crossover.Crossover;
import model.crossover.StandardCrossover;
import model.initialization.IncrementalInitialization;
import model.initialization.Initialization;
import model.mutation.ExtraTestMutation;
import model.mutation.Mutation;
import model.mutation.ReplaceMutation;
import model.replacement.ElitistReplacement;
import model.replacement.Replacement;
import model.replacement.BasicElitistReplacement;
import model.replacement.DirectReplacement;
import model.selection.Selection;
import model.selection.Tournament;
import model.selection.Truncation;
import utils.NSGAIIAlgorithms;

public class Controller {
	private Chromosome[] mejores;
	private Chromosome[] peores;
	private double[] medias;
	private double[] generaciones;
	
	private List<HashSet<Chromosome>> bestFronts;
	private List<HashSet<Chromosome>> firstFronts;


	public double run(int size_pop, int iters,
					int participantes, double victoria,
					double prob_cruce,
					double prob_mut,
					double elitism_ratio,
					String testsFile, String mutantsFile) {
		

		long startTime = System.currentTimeMillis();

		List<FSMTest> totalTests = ChromosomeFactory.readTests(testsFile);
		List<FSM> mutants = ChromosomeFactory.readMutants(mutantsFile);
		
		String methods = "";
		Initialization metInic= new IncrementalInitialization();
		Selection metSelec = new Tournament(participantes, victoria);
		methods += "_sTournament";
		//Selection metSelec = new Truncation(victoria);
		//methods += "_sTruncation";
		Crossover metCruce = new ContinuousCrossover(prob_cruce);
		methods += "_cContinuous";
		//Crossover metCruce = new StandardCrossover(prob_cruce);
		//methods += "_cStandard";
		Mutation metMut = new ExtraTestMutation(prob_mut, totalTests);
		methods += "_mExtra";
		//Mutation metMut = new ReplaceMutation(prob_mut, totalTests);
		//methods += "_mReplace";

		int[][] testsVSmutants = new int[totalTests.size()][mutants.size()];
		for(int test = 0; test < totalTests.size(); test++)
			for(int mutant = 0; mutant < mutants.size(); mutant++)
					testsVSmutants[test][mutant] = totalTests.get(test).killMutant(mutants.get(mutant));
		
		Chromosome[] poblacion = metInic.initialize(size_pop, totalTests, testsVSmutants);

		Replacement metReempl = new ElitistReplacement(testsVSmutants);
		methods += "_rElitist_";
		Replacement metReempl2 = new DirectReplacement(testsVSmutants);
		Replacement metReempl3 = new BasicElitistReplacement(testsVSmutants);
		
		mejores = new Chromosome[iters+1];
		peores = new Chromosome[iters+1];
		generaciones = new double[iters+1];
		
		Chromosome completeItem = new Chromosome(totalTests);
		completeItem.evaluateDistinguishingMutationScore(testsVSmutants);
		
		bestFronts = new ArrayList<HashSet<Chromosome>>();
		firstFronts = new ArrayList<HashSet<Chromosome>>();

				
		List<HashSet<Chromosome>> fronts = NSGAIIAlgorithms.fastNonDominatedSort(poblacion, poblacion.length);
		int f = 0;
		while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f that fits entirely
			NSGAIIAlgorithms.crowdingDistanceAssignment(poblacion, fronts.get(f));
			firstFronts.add(fronts.get(f));
			f++;
		}
		
		Chromosome mejor = mejorC(poblacion);

		mejores[0] = mejor;
		peores[0] = peorC(poblacion);

		bestFronts.add(fronts.get(0));
		
		generaciones[0] = 0;
		
		Chromosome[] poblacion2 = new Chromosome[size_pop];
		for(int i = 0; i < size_pop; i++)
			poblacion2[i] = new Chromosome(poblacion[i]);		
		ArrayList<HashSet<Chromosome>> bestFronts2 = new ArrayList<HashSet<Chromosome>>(bestFronts);
		
		Chromosome[] poblacion3 = new Chromosome[size_pop];
		for(int i = 0; i < size_pop; i++)
			poblacion3[i] = new Chromosome(poblacion[i]);		
		ArrayList<HashSet<Chromosome>> bestFronts3 = new ArrayList<HashSet<Chromosome>>(bestFronts);

		boolean lastTenEqual = false;
		boolean lastTenFrontsEqual = false;

		// For NSGA-II elitist replacement
        for(int i = 0; i < iters && !lastTenEqual && !lastTenFrontsEqual; i++) {
        	poblacion =
			metReempl.replace(
				poblacion,
				metMut.mutate(
					metCruce.cruza(
						metSelec.select(poblacion, size_pop)
					), metReempl.toString()
				), totalTests
			);
            mejores[i+1] = mejorC(poblacion);
            peores[i+1] = peorC(poblacion);
            generaciones[i+1] = i+1;
            
            fronts = NSGAIIAlgorithms.fastNonDominatedSort(poblacion, poblacion.length);
    		bestFronts.add(fronts.get(0));
    		
            if (i > 10) {
            	lastTenEqual = true;
            	for (int j = i+1; (j > i - 9) && lastTenEqual; j--) {
            		if (mejores[j] != mejores[j-1]) {
            			lastTenEqual = false;
            		}
            	}

            	lastTenFrontsEqual = true;
            	for (int j = i+1; (j > i - 9) && lastTenFrontsEqual; j--) {
            		lastTenFrontsEqual = bestFronts.get(j).equals(bestFronts.get(j-1));
            	}
            }
		}
        
        // For direct replacement
        boolean lastTenEqual2 = false;
		boolean lastTenFrontsEqual2 = false;
        for(int i = 0; i < iters && !lastTenEqual2 && !lastTenFrontsEqual2; i++) {
        	poblacion2 =
			metReempl2.replace(
				poblacion2,
				metMut.mutate(
					metCruce.cruza(
						metSelec.select(poblacion2, size_pop)
					), metReempl2.toString()
				), totalTests
			);
            
            fronts = NSGAIIAlgorithms.fastNonDominatedSort(poblacion2, poblacion2.length);
    		bestFronts2.add(fronts.get(0));
    		
            if (i > 10) {
            	lastTenEqual2 = true;
            	for (int j = i+1; (j > i - 9) && lastTenEqual2; j--) {
            		if (mejores[j] != mejores[j-1]) {
            			lastTenEqual2 = false;
            		}
            	}

            	lastTenFrontsEqual2 = true;
            	for (int j = i+1; (j > i - 9) && lastTenFrontsEqual2; j--) {
            		lastTenFrontsEqual2 = bestFronts2.get(j).equals(bestFronts2.get(j-1));
            	}
            }
		}
        
     // For basic elitist replacement
        boolean lastTenEqual3 = false;
		boolean lastTenFrontsEqual3 = false;
        for(int i = 0; i < iters && !lastTenEqual3 && !lastTenFrontsEqual3; i++) {
        	poblacion3 =
			metReempl3.replace(
				poblacion3,
				metMut.mutate(
					metCruce.cruza(
						metSelec.select(poblacion3, size_pop)
					), metReempl3.toString()
				), totalTests
			);
        	
            fronts = NSGAIIAlgorithms.fastNonDominatedSort(poblacion3, poblacion3.length);
    		bestFronts3.add(fronts.get(0));
    		
            if (i > 10) {
            	lastTenEqual3 = true;
            	for (int j = i+1; (j > i - 9) && lastTenEqual3; j--) {
            		if (mejores[j] != mejores[j-1]) {
            			lastTenEqual3 = false;
            		}
            	}

            	lastTenFrontsEqual3 = true;
            	for (int j = i+1; (j > i - 9) && lastTenFrontsEqual3; j--) {
            		lastTenFrontsEqual3 = bestFronts3.get(j).equals(bestFronts3.get(j-1));
            	}
            }
		}
        
        long endTime = System.currentTimeMillis();

		System.out.println(testsFile + " took " + (endTime - startTime) + " milliseconds");
        
        try {
			toFile(testsFile, size_pop, iters, methods, completeItem, firstFronts, bestFronts.get(0), bestFronts.get(bestFronts.size()-1), poblacion2, bestFronts2.get(bestFronts2.size()-1), poblacion3, bestFronts3.get(bestFronts3.size()-1));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return 0;
	}
	
	private Chromosome peorC(Chromosome[] pob) {
		Chromosome peor = pob[0];
		for (int i = 1; i < pob.length; i++)
			peor = pob[i].crowdedComparisonOperator(peor) == pob[i] ? peor : pob[i];
		return peor;
	}

	private Chromosome mejorC(Chromosome[] pob) {
		Chromosome mejor = pob[0];
		for (int i = 1; i < pob.length; i++)
			mejor = pob[i].crowdedComparisonOperator(mejor);
		return mejor;
	}
	
	public Chromosome[] getMejoresC() {
		return mejores;
	}
	
	public double[] getMedias() {
		return medias;
	}
	
	public Chromosome[] getPeoresC() {
		return peores;
	}
	
	public double[] getGeneraciones() {
		return generaciones;
	}
	
	public void toFile(String testFile, int size_pop, int iters, String methods, Chromosome completeItem, 
			List<HashSet<Chromosome>> firstFronts, HashSet<Chromosome> firstBest, 
			HashSet<Chromosome> bestElit,
			Chromosome[] pobDir, HashSet<Chromosome> bestDir,
			Chromosome[] pobBelit, HashSet<Chromosome> bestBelit) throws IOException {
		FileWriter csvWriter = new FileWriter("finalExperiments_v4/00Experiment2_"+size_pop+"_"+iters+"_"+methods+testFile.charAt(testFile.length()-5)+"_C.csv");
		csvWriter.append("Crowding Distance;");
		csvWriter.append("Front;");
		csvWriter.append("ID;");
		csvWriter.append("Maximized mutation Score;");
		csvWriter.append("Minimized mutation Score;");
		csvWriter.append("Num Inputs;");
		csvWriter.append("Num Tests;");
		csvWriter.append("Tests\n");
		
		csvWriter.append(completeItem.getCrowdingDistance() + ";");
		csvWriter.append(completeItem.getFront() + ";");
		csvWriter.append(completeItem.getId() + ";");
		csvWriter.append((1.0 - completeItem.getMutationScore()) + ";");
		csvWriter.append((completeItem.getMutationScore()) + ";");
		csvWriter.append(completeItem.getNumInputs() + ";");
		csvWriter.append(completeItem.getNumTests() + ";");
		csvWriter.append(completeItem.toString() + "\n");
		
		csvWriter.append("\n");
		int ff = 0;
		while (ff < firstFronts.size() && !firstFronts.get(ff).isEmpty()) { // for each front f that fits entirely
			for (Chromosome ffs: firstFronts.get(ff)) {
				csvWriter.append(ffs.getCrowdingDistance() + ";");
				csvWriter.append(ffs.getFront() + ";");
				csvWriter.append(ffs.getId() + ";");
				csvWriter.append((1.0 - ffs.getMutationScore()) + ";");
				csvWriter.append((ffs.getMutationScore()) + ";");
				csvWriter.append(ffs.getNumInputs() + ";");
				csvWriter.append(ffs.getNumTests() + ";");
				csvWriter.append(ffs.toString() + "\n");
			}
			ff++;
		}
		csvWriter.append("\n");
		
		for (Chromosome fb : firstBest) {
			csvWriter.append(fb.getCrowdingDistance() + ";");
			csvWriter.append(fb.getFront() + ";");
			csvWriter.append(fb.getId() + ";");
			csvWriter.append((1.0 - fb.getMutationScore()) + ";");
			csvWriter.append((fb.getMutationScore()) + ";");
			csvWriter.append(fb.getNumInputs() + ";");
			csvWriter.append(fb.getNumTests() + ";");
			csvWriter.append(fb.toString() + "\n");
		}
		for (int i = firstBest.size(); i < 75; i++) {
			csvWriter.append("\n");
		}
		csvWriter.append("\n");

		for (Chromosome f : bestElit) {
			csvWriter.append(f.getCrowdingDistance() + ";");
			csvWriter.append(f.getFront() + ";");
			csvWriter.append(f.getId() + ";");
			csvWriter.append((1.0 - f.getMutationScore()) + ";");
			csvWriter.append((f.getMutationScore()) + ";");
			csvWriter.append(f.getNumInputs() + ";");
			csvWriter.append(f.getNumTests() + ";");
			csvWriter.append(f.toString() + "\n");
		}
		for (int i = bestElit.size(); i < 75; i++) {
			csvWriter.append("\n");
		}
		
		csvWriter.append("\n");
		csvWriter.append("\n");

		for (Chromosome p : pobDir) {
			csvWriter.append(p.getCrowdingDistance() + ";");
			csvWriter.append(p.getFront() + ";");
			csvWriter.append(p.getId() + ";");
			csvWriter.append((1.0 - p.getMutationScore()) + ";");
			csvWriter.append((p.getMutationScore()) + ";");
			csvWriter.append(p.getNumInputs() + ";");
			csvWriter.append(p.getNumTests() + ";");
			csvWriter.append(p.toString() + "\n");
		}
		for (int i = pobDir.length; i < 75; i++) {
			csvWriter.append("\n");
		}
		
		csvWriter.append("\n");

		for (Chromosome fd : bestDir) {
			csvWriter.append(fd.getCrowdingDistance() + ";");
			csvWriter.append(fd.getFront() + ";");
			csvWriter.append(fd.getId() + ";");
			csvWriter.append((1.0 - fd.getMutationScore()) + ";");
			csvWriter.append((fd.getMutationScore()) + ";");
			csvWriter.append(fd.getNumInputs() + ";");
			csvWriter.append(fd.getNumTests() + ";");
			csvWriter.append(fd.toString() + "\n");
		}
		for (int i = bestDir.size(); i < 75; i++) {
			csvWriter.append("\n");
		}
		
		csvWriter.append("\n");
		csvWriter.append("\n");

		for (Chromosome pbe : pobBelit) {
			csvWriter.append(pbe.getCrowdingDistance() + ";");
			csvWriter.append(pbe.getFront() + ";");
			csvWriter.append(pbe.getId() + ";");
			csvWriter.append((1.0 - pbe.getMutationScore()) + ";");
			csvWriter.append((pbe.getMutationScore()) + ";");
			csvWriter.append(pbe.getNumInputs() + ";");
			csvWriter.append(pbe.getNumTests() + ";");
			csvWriter.append(pbe.toString() + "\n");
		}
		for (int i = pobDir.length; i < 75; i++) {
			csvWriter.append("\n");
		}
		
		csvWriter.append("\n");

		for (Chromosome fbe : bestBelit) {
			csvWriter.append(fbe.getCrowdingDistance() + ";");
			csvWriter.append(fbe.getFront() + ";");
			csvWriter.append(fbe.getId() + ";");
			csvWriter.append((1.0 - fbe.getMutationScore()) + ";");
			csvWriter.append((fbe.getMutationScore()) + ";");
			csvWriter.append(fbe.getNumInputs() + ";");
			csvWriter.append(fbe.getNumTests() + ";");
			csvWriter.append(fbe.toString() + "\n");
		}
		for (int i = bestBelit.size(); i < 75; i++) {
			csvWriter.append("\n");
		}
		csvWriter.flush();
		csvWriter.close();
	}
		
}