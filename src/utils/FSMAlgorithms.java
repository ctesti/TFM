package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.chromosome.FSM;
import model.chromosome.Node;
import model.chromosome.Transition;

public interface FSMAlgorithms {
	
	public static boolean different(FSM original, FSM mutant) {
		original = minimizeFSM(new FSM(original));
		mutant = minimizeFSM(new FSM(mutant));
		List<Node> nodesOriginal = original.getNodes();
		List<Node> nodesMutant = mutant.getNodes();
		if (nodesOriginal.size() != nodesMutant.size())
			return true;
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.addAll(original.getTransitions());
		transitions.addAll(mutant.getTransitions());
		Set<String> inputs = original.getInputs();
		if(inputs.size() != mutant.getInputs().size() || !inputs.containsAll(mutant.getInputs()))
			return true;
		Set<String> outputs = new HashSet<String>();
		outputs.addAll(original.getOutputs());
		outputs.addAll(mutant.getOutputs());
		Node initialNode = new Node(-1);
		Iterator<String> inputIterator = inputs.iterator();
		Iterator<String> outputIterator = outputs.iterator();
		Transition tran2original = new Transition(initialNode, original.getInitialNode(), inputIterator.next(), outputIterator.next());
		Transition tran2mutant = new Transition(initialNode, mutant.getInitialNode(), "0", outputIterator.next());
		initialNode.addTransition(tran2mutant);
		initialNode.addTransition(tran2original);
		while(inputIterator.hasNext()) {
			Transition t = new Transition(initialNode, initialNode, inputIterator.next(), "reset");
			initialNode.addTransition(t);
		}
		
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(initialNode);
		nodes.addAll(nodesOriginal);
		nodes.addAll(nodesMutant);
		FSM reducedFSM = minimizeFSM(new FSM(nodes, transitions, inputs, outputs, initialNode));
		return reducedFSM.getNodes().size() != original.getNodes().size() + 1;
	}
	
	
	public static FSM minimizeFSM(FSM machine) {
		List<Node> reachableNodes = removeUnreachableNodes(machine);
		boolean[][] unrelatedIndex = new boolean[reachableNodes.size()-1][reachableNodes.size()-1];//default is False

		boolean hasExtended = true;
		while(hasExtended) {
			hasExtended = false;
			for(int x = 0; x < reachableNodes.size() - 1; x++) {
				for(int y = x; y < reachableNodes.size() - 1; y++) {
					if(!unrelatedIndex[x][y]) {
						Iterator<String> inputIterator = machine.getInputs().iterator();
						while(!unrelatedIndex[x][y] && inputIterator.hasNext()) {
							String input = inputIterator.next();
							String outputX = reachableNodes.get(x).getTransition(input).getOutput();
							String outputY = reachableNodes.get(y+1).getTransition(input).getOutput();
							Node targetX = reachableNodes.get(x).getTransition(input).getTarget();
							Node targetY = reachableNodes.get(y+1).getTransition(input).getTarget();
							int newX = reachableNodes.indexOf(targetX);
							int newY = reachableNodes.indexOf(targetY);
							if(newX < newY) {
								if(unrelatedIndex[newX][newY-1]) {
									hasExtended = true;
									unrelatedIndex[x][y] = true;
								}
							}
							else if(newY < newX) {
								if(unrelatedIndex[newY][newX-1]) {
									hasExtended = true;
									unrelatedIndex[x][y] = true;
								}
							}
							if(!outputX.equals(outputY)) {
								hasExtended = true;
								unrelatedIndex[x][y] = true;
							}
						}
					}
				}
			}
		}
		
		List<Node> reducedNodes = new ArrayList<Node>(reachableNodes);
		for(int x = 0; x < reachableNodes.size() - 1; x++) {
			for(int y = x; y < reachableNodes.size() - 1; y++) {
				if(!unrelatedIndex[x][y]) {
					reducedNodes.remove(reachableNodes.get(y+1));
				}
			}
		}
		
		for(Node node : reachableNodes) {
			for(Transition transition : node.getTransitions()) {
				Node target = transition.getTarget();
				if(!reducedNodes.contains(target)) {
					int y = reachableNodes.indexOf(target) - 1;
					int x = 0;
					while (unrelatedIndex[x][y])
						x++;
					transition.setTarget(reachableNodes.get(x));
				}
			}
		}
		
		List<Transition> newTransitions = new ArrayList<Transition>();
		for(Node node : reducedNodes)
			newTransitions.addAll(node.getTransitions());
		return new FSM(reducedNodes, newTransitions, machine.getInputs(), machine.getOutputs(), reducedNodes.get(0));
	}
	
	public static List<Node> removeUnreachableNodes(FSM machine) {
		List<Node> unreachableNodes = new ArrayList<Node>(machine.getNodes());
		List<Node> reachableNodes = new ArrayList<Node>();
		unreachableNodes.remove(machine.getInitialNode());
		reachableNodes.add(machine.getInitialNode());
		boolean hasExtended = true;

		while(!unreachableNodes.isEmpty() && hasExtended) {
			hasExtended = false;
			for(int i = 0; i < reachableNodes.size(); i++) {
				Node reachableNode = reachableNodes.get(i);
				for (Transition t : reachableNode.getTransitions()) {
					if (unreachableNodes.contains(t.getTarget())) {
						unreachableNodes.remove(t.getTarget());
						reachableNodes.add(t.getTarget());
						hasExtended = true;
					}					
				}
			}
		}
		return reachableNodes;
	}
	
	public static FSM generateRandomSpecification(int numNodes, int numInputs, int numOutputs) {
		Random rnd = new Random();
		List<Node> nodes = new ArrayList<Node>();
		List<Transition> transitions = new ArrayList<Transition>();
		Set<String> inputset = new HashSet<String>();
		Set<String> outputset = new HashSet<String>();
		
		for(int i = 0; i < numNodes; i++)
			nodes.add(new Node(i));
		for(int i = 0; i < numInputs; i++)
			inputset.add("" + i);
		for(int i = 0; i < numOutputs; i++)
			outputset.add("" + i);
		
		for(int node = 0; node < numNodes; node++) {
			for(String input : inputset) {
				Transition t = new Transition(nodes.get(node),
					nodes.get(rnd.nextInt(numNodes)),
					input,
					"" + rnd.nextInt(numOutputs));
				nodes.get(node).addTransition(t);
				transitions.add(t);
			}
		}
		
		return new FSM(nodes,transitions,inputset,outputset,nodes.get(0));
	}
	
	
	public static List<FSM> generateSepcifications(int numSpec, int minNodes, int maxNodes, int minInputs, int maxInputs, int minOutputs, int maxOutputs) {
		List<FSM> specifications = new ArrayList<FSM>();
		Random rnd = new Random();
		
		for(int i = 0; i < numSpec; i++) {
			specifications.add(generateRandomSpecification(
					rnd.nextInt(maxNodes - minNodes + 1) + minNodes,
					rnd.nextInt(maxInputs - minInputs + 1) + minInputs,
					rnd.nextInt(maxOutputs - minOutputs + 1) + minOutputs
					));
		}
		return specifications;
	}
}
