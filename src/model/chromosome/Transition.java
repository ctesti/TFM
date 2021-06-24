package model.chromosome;

import java.util.List;

public class Transition {
	private String input;
	private String output;
	private Node source;
	private Node target;
	
	public Transition(Node source, Node target, String input, String output) {
		this.input = input;
		this.output = output;
		this.source = source;
		this.target = target;
	}
	
	public Transition(Transition t, List<Node> nodes) {
		this.input = t.getInput();
		this.output = t.getOutput();
		
		
		for(Node candidate : nodes) {
			if (t.getSource().equals(candidate))
				this.source = candidate;
			if (t.getTarget().equals(candidate))
				this.target = candidate;
		}
	}
	
	public String getInput() {
		return input;
	}
	
	public String getOutput() {
		return output;
	}
	
	public Node getSource() {
		return source;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public void setTarget(Node target) {
		this.target = target;
	}
	
	@Override
	public String toString() {
		return source.getId() +" " + target.getId() + " " + input + " " + output;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Transition) {
			if (input.equals( ((Transition) o).getInput() ) && output.equals( ((Transition) o).getOutput() ) &&
					source.equals( ((Transition) o).getSource() ) && target.equals( ((Transition) o).getTarget() ))
				return true;
		}
		return false;
	}
}
