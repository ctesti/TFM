package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.chromosome.Chromosome;

public class NSGAIIAlgorithms {
	
	public static List<HashSet<Chromosome>> fastNonDominatedSort(Chromosome[] pop) {
			HashMap<Chromosome, HashSet<Chromosome>> dominated_set = new HashMap<Chromosome, HashSet<Chromosome>>(); // hashmap with a list of all the other elements dominated by each element
			HashMap<Chromosome, Integer> domination_count = new HashMap<Chromosome, Integer>(); // hashmap to store the number of elements that dominate each element
			List<HashSet<Chromosome>> fronts = new ArrayList<HashSet<Chromosome>>(); // array that stores in each position the set of keys that form the each front
			
			fronts.add(new HashSet<Chromosome>()); // initialize the first front
			
			for (int p = 0; p < pop.length; p++) { // for each element p in the population
				HashSet<Chromosome> dominates = new HashSet<Chromosome>(); // create a set to store the elements that it dominates
				int nDominatedBy = 0; // counter of elements that dominate the current element
				for (int q = 0; q < pop.length; q++) { // for each element q in the population
					if (q != p) { // if it is different from p
						double p_obj_x = pop[p].getMutationScore(); // extract the values of the objectives
						double p_obj_y = pop[p].getNumInputs();
						double q_obj_x = pop[q].getMutationScore();
						double q_obj_y = pop[q].getNumInputs();
						if (XdominatesY(p_obj_x, p_obj_y, q_obj_x, q_obj_y)) { // if p dominates q
							dominates.add(pop[q]); // we add q to the set of elements dominated by p
						}
						else if (XdominatesY(q_obj_x, q_obj_y, p_obj_x, p_obj_y)) { // otherwise
							nDominatedBy++; // increase the counter of solutions that dominate p
						}
					}
				}
				dominated_set.put(pop[p], dominates); // add the set of elements dominated by p to the hashmap
				if (nDominatedBy == 0) { // if no element dominates p
					fronts.get(0).add(pop[p]); // it is part of the first front
					pop[p].setFront(0); // assign frontid = 0
				}
				domination_count.put(pop[p], nDominatedBy); // include the counter to the set of counters
			}
			
			int f = 0;
			while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f
				HashSet<Chromosome> next_front = new HashSet<Chromosome>(); // initialize the next front
				for(Chromosome p : fronts.get(f)) { // for each element p in f
					for (Chromosome q : dominated_set.get(p)) { // for each element q dominated by p
						Integer qDominated = domination_count.get(q); // take the number of elements that dominate q
						domination_count.put(q, qDominated-1); // decrease the domination counter
						if (qDominated-1 == 0) { // if the updated counter is 0
							next_front.add(q); // it means that is is in the next front
							q.setFront(f+1);
						}
					}
				}

				if (!next_front.isEmpty()) {
					fronts.add(next_front); // add the next front to the list of fronts
				}
				f++;
			}
						
			return fronts;
	  }
	  
	  public static List<HashSet<Chromosome>> fastNonDominatedSort(Chromosome[] pop, int max_elems) {
			HashMap<Chromosome, HashSet<Chromosome>> dominated_set = new HashMap<Chromosome, HashSet<Chromosome>>(); // hashmap with a list of all the other elements dominated by each element
			HashMap<Chromosome, Integer> domination_count = new HashMap<Chromosome, Integer>(); // hashmap to store the number of elements that dominate each element
			List<HashSet<Chromosome>> fronts = new ArrayList<HashSet<Chromosome>>(); // array that stores in each position the set of keys that form the each front
			
			fronts.add(new HashSet<Chromosome>()); // initialize the first front
			
			for (int p = 0; p < pop.length; p++) { // for each element p in the population
				HashSet<Chromosome> dominates = new HashSet<Chromosome>(); // create a set to store the elements that it dominates
				int nDominatedBy = 0; // counter of elements that dominate the current element
				for (int q = 0; q < pop.length; q++) { // for each element q in the population
					if (q != p) { // if it is different from p
						double p_obj_x = pop[p].getMutationScore(); // extract the values of the objectives
						double p_obj_y = pop[p].getNumInputs();
						double q_obj_x = pop[q].getMutationScore();
						double q_obj_y = pop[q].getNumInputs();
						if (XdominatesY(p_obj_x, p_obj_y, q_obj_x, q_obj_y)) { // if p dominates q
							dominates.add(pop[q]); // we add q to the set of elements dominated by p
						}
						else if (XdominatesY(q_obj_x, q_obj_y, p_obj_x, p_obj_y)) { // otherwise
							nDominatedBy++; // increase the counter of solutions that dominate p
						}
					}
				}
				dominated_set.put(pop[p], dominates); // add the set of elements dominated by p to the hashmap
				if (nDominatedBy == 0) { // if no element dominates p
					fronts.get(0).add(pop[p]); // it is part of the first front
					pop[p].setFront(0); // assign frontid = 0
				}
				domination_count.put(pop[p], nDominatedBy); // include the counter to the set of counters
			}
			
			int f = 0;
			int selected = 0;
			while (f < fronts.size() && !fronts.get(f).isEmpty() && selected < max_elems) { // for each front f
				HashSet<Chromosome> next_front = new HashSet<Chromosome>(); // initialize the next front
				HashSet<Chromosome> current_front = fronts.get(f);
				for(Chromosome p : current_front) { // for each element p in f
					HashSet<Chromosome> dominates = dominated_set.get(p);
					if(!dominates.isEmpty()) {
						for (Chromosome q : dominates) { // for each element q dominated by p
							Integer qDominated = domination_count.get(q); // take the number of elements that dominate q
							domination_count.put(q, qDominated-1); // decrease the domination counter
							if (domination_count.get(q) == 0) { // if the updated counter is 0
								next_front.add(q); // it means that is is in the next front
								q.setFront(f+1);
								selected++;
							}
						}
					}
				}

				if (!next_front.isEmpty()) {
					fronts.add(next_front); // add the next front to the list of fronts
				}
				f++;
			}
			
			return fronts;
	  }

	  public static List<HashSet<Chromosome>> slowNonDominatedSort(Chromosome[] pop, int max_elems) {
		  List<HashSet<Chromosome>> fronts = new ArrayList<HashSet<Chromosome>>();
		  HashSet<Chromosome> discarded = new HashSet<Chromosome>();
		  HashSet<Chromosome> selected = new HashSet<Chromosome>();
		  HashSet<Chromosome> pool = new HashSet<Chromosome>();

		  for(int i = 0; i < pop.length; i++)
			  pool.add(pop[i]);
		  
		  int numChosen = 0;

		  int level = 0;
		  while(numChosen < max_elems) {
			  for(Chromosome candidato : pool) {
				  for(Chromosome casifrontera : selected) {
					  if(XdominatesY(candidato.getMutationScore(), candidato.getNumInputs(), casifrontera.getMutationScore(), casifrontera.getNumInputs())) {
						 discarded.add(casifrontera);
						 numChosen--;
					  }
					  else if (XdominatesY(casifrontera.getMutationScore(), casifrontera.getNumInputs(), candidato.getMutationScore(), candidato.getNumInputs())) {
						  discarded.add(candidato);
					  }
				  }
				  if(!discarded.contains(candidato)) {
					  selected.add(candidato);
					  selected.removeAll(discarded);
					  numChosen++;
				  }
			  }
			  for(Chromosome winner : selected)
				  winner.setFront(level);
			  pool = discarded;
			  discarded = new HashSet<Chromosome>();
			  fronts.add(selected);
			  selected = new HashSet<Chromosome>();
			  level++;
		  }
		  return fronts;
	  }
	  
	  public static boolean XdominatesY(double p_x, double p_y, double q_x, double q_y) {
		  boolean dominates = false;
		  
		  if (p_x < q_x) {
			  if (p_y <= q_y) {
				  dominates = true;
			  }
		  }
		  else if (p_x == q_x) {
			  if (p_y < q_y) {
				  dominates = true;
			  }
		  }

		  return dominates;
	  }

	  public static void crowdingDistanceAssignment(Chromosome[] pop, HashSet<Chromosome> front){
		  HashMap<Chromosome, Double> distances = new HashMap<Chromosome, Double>(); // hashmap to store each element's crowding distance
		  int length = front.size();
		  
		  for (Chromosome item : front) {
			  distances.put(item, 0.0); // initialize the hashmap
		  }
		  
		  int num_objectives = 2;
		  
		  for (int i = 0; i < num_objectives; i++) { // for every objective
			  HashMap<Chromosome, Double> sol_aux = new HashMap<Chromosome, Double>(); //create a hashmap only with the front items and the objective
			  for (int e = 0; e < pop.length; e++) {
				  if (distances.containsKey(pop[e])) {
					  if (i == 0) {
						  sol_aux.put(pop[e], pop[e].getMutationScore()); // fill in the hashmap
					  }
					  else if (i == 1) {
						  sol_aux.put(pop[e], pop[e].getNumInputs()); // fill in the hashmap
					  }
					 
				  }
			  }

			  HashMap<Chromosome, Double> sol = sortByValue(sol_aux); //sort the hashmap
			  List<Chromosome> lKeys = new ArrayList<Chromosome>(sol.keySet()); // auxiliary array to keep the order of the hashmap
			  
			  double fmin = sol.get(lKeys.get(0));
			  double fmax = sol.get(lKeys.get(lKeys.size()-1));
			  double norm = fmax-fmin;
			  
			  int item = 0;
			  for (Chromosome k : sol.keySet()) { // for every element in the front
				  if (item == 0 || item == length-1 || norm == 0) { // set to infinity the first and last element
					  distances.put(k, Double.MAX_VALUE);
				  }
				  if (distances.get(k) != Double.MAX_VALUE){
					  distances.put(k, distances.get(k) + ((Math.abs(sol.get(lKeys.get(item+1))-sol.get(lKeys.get(item-1)))) / norm)); // compute distance to its neighbours
				  }
				  k.setCrowdingDistance(distances.get(k));
				  item++;
			  }
		  }
		  
	  }

	//function to sort hashmap by values 
	  public static HashMap<Chromosome, Double> sortByValue(HashMap<Chromosome, Double> input) { 
	    // Create a list from elements of HashMap 
	    List<Map.Entry<Chromosome, Double>> list_aux = new LinkedList<Map.Entry<Chromosome, Double>>(input.entrySet()); 

	    // Sort the list 
	    Collections.sort(list_aux, new Comparator<Map.Entry<Chromosome, Double>>() { 
	        public int compare(Map.Entry<Chromosome, Double> o1,  
	                           Map.Entry<Chromosome, Double> o2) 
	        { 
	            return (o1.getValue()).compareTo(o2.getValue()); 
	        } 
	    }); 
	      
	    // put data from sorted list to hashmap  
	    HashMap<Chromosome, Double> sorted = new LinkedHashMap<Chromosome, Double>(); 
	    for (Map.Entry<Chromosome, Double> i : list_aux) { 
	        sorted.put(i.getKey(), i.getValue()); 
	    } 
	    return sorted; 
	  } 
	  
	  public static List<HashSet<String>> OLD_fastNonDominatedSort(HashMap<String, double[]> elems) {
			HashMap<String, HashSet<String>> dominated_set = new HashMap<String, HashSet<String>>(); // hashmap with a list of all the other elements dominated by each element
			HashMap<String, Integer> domination_count = new HashMap<String, Integer>(); // hashmap to store the number of elements that dominate each element
			List<HashSet<String>> fronts = new ArrayList<HashSet<String>>(); // array that stores in each position the set of keys that form the each fron
			
			fronts.add(new HashSet<String>()); // initialize the first front
			
			for (String p : elems.keySet()) { // for each element p in the population
				HashSet<String> dominates = new HashSet<String>(); // create a set to store the elements that it dominates
				int nDominatedBy = 0; // counter of elements that dominate the current element
				for (String q : elems.keySet()) { // for each element q in the population
					if (!q.equals(p)) { // if it is different from p
						double[] p_pos = elems.get(p); // extract the values of the objectives
						double[] q_pos = elems.get(q);
						if (OLD_XdominatesY(p_pos, q_pos)) { // if p dominates q
							dominates.add(q); // we add q to the set of elements dominated by p
						}
						else if (OLD_XdominatesY(q_pos, p_pos)) { // otherwise
							nDominatedBy++; // increase the counter of solutions that dominate p
						}
					}
				}
				dominated_set.put(p, dominates); // add the set of elements dominated by p to the hashmap
				if (nDominatedBy == 0) { // if no element dominates p
					fronts.get(0).add(p); // it is part of the first front
				}
				domination_count.put(p, nDominatedBy); // include the counter to the set of counters
			}
			
			int f = 0;
			while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f
				HashSet<String> next_front = new HashSet<String>(); // initialize the next front
				for(String p : fronts.get(f)) { // for each element p in f
					for (String q : dominated_set.get(p)) { // for each element q dominated by p
						Integer qDominated = domination_count.get(q); // take the number of elements that dominate q
						domination_count.put(q, qDominated-1); // decrease the domination counter
						if (qDominated-1 == 0) { // if the updated counter is 0
							next_front.add(q); // it means that is is in the next front
						}
					}
				}
				System.out.println(next_front);

				fronts.add(next_front); // add the next front to the list of fronts
				f++;
			}
			
			System.out.println(dominated_set);
			System.out.println(fronts);
			
			return fronts;
		}
	  
	  public static boolean OLD_XdominatesY(double[] x_pos, double[] y_pos) {
		  boolean dominates = false;
		  
		  if (x_pos[0] < y_pos[0]) {
			  if (x_pos[1] <= y_pos[1]) {
				  dominates = true;
			  }
		  }
		  else if (x_pos[0] == y_pos[0]) {
			  if (x_pos[1] < y_pos[1]) {
				  dominates = true;
			  }
		  }

		  return dominates;
	  }
	  
	  public static HashMap<String, Double> OLD_crowdingDistanceAssignment(HashMap<String, double[]> elems, HashSet<String> front){
		  HashMap<String, Double> distances = new HashMap<String, Double>(); // hashmap to store each element's crowding distance
		  int length = front.size();
		  
		  for (String item : front) {
			  distances.put(item, 0.0); // initialize the hashmap
		  }
		  
		  int num_objectives = elems.get(elems.keySet().iterator().next()).length; // get the number of objectives to be evaluated
		  
		  for (int i = 0; i < num_objectives; i++) { // for every objective
			  HashMap<String, Double> sol_aux = new HashMap<String, Double>(); //create a hashmap only with the front items and the objective
			  for (String e : elems.keySet()) {
				  if (distances.containsKey(e)) {
					  sol_aux.put(e, elems.get(e)[i]); // fill in the hashmap
				  }
			  }

			  HashMap<String, Double> sol = OLD_sortByValue(sol_aux); //sort the hashmap
			  List<String> lKeys = new ArrayList<String>(sol.keySet()); // auxiliary array to keep the order of the hashmap
			  
			  double fmin = sol.get(lKeys.get(0));
			  double fmax = sol.get(lKeys.get(lKeys.size()-1));
			  double norm = fmax-fmin;
			  
			  int item = 0;
			  for (String k : sol.keySet()) { // for every element in the front
				  if (item == 0 || item == length-1) { // set to infinity the first and last element
					  distances.put(k, Double.MAX_VALUE);
				  }
				  else {
					  distances.put(k, distances.get(k) + (sol.get(lKeys.get(item+1))-sol.get(lKeys.get(item-1)) / norm)); // compute distanceto its neighbours
				  }
				  item++;
			  }
		  }
		  
		return distances;
	  }
	  
	  //function to sort hashmap by values 
	  public static HashMap<String, Double> OLD_sortByValue(HashMap<String, Double> input) 
	  { 
	      // Create a list from elements of HashMap 
	      List<Map.Entry<String, Double>> list_aux = new LinkedList<Map.Entry<String, Double>>(input.entrySet()); 

	      // Sort the list 
	      Collections.sort(list_aux, new Comparator<Map.Entry<String, Double>>() { 
	          public int compare(Map.Entry<String, Double> o1,  
	                             Map.Entry<String, Double> o2) 
	          { 
	              return (o1.getValue()).compareTo(o2.getValue()); 
	          } 
	      }); 
	        
	      // put data from sorted list to hashmap  
	      HashMap<String, Double> sorted = new LinkedHashMap<String, Double>(); 
	      for (Map.Entry<String, Double> i : list_aux) { 
	          sorted.put(i.getKey(), i.getValue()); 
	      } 
	      return sorted; 
	  } 

}
