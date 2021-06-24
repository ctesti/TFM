package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.chromosome.FSM;
import model.chromosome.FSMTest;

public class Main {
	public static void main(String[] args) {
		
/*		for(int i = 0; i <= 8; i++) {
			FSM spec = ChromosomeFactory.readSpecification("files/Spec_"+i+".txt");
			List<FSMTest> tests = spec.generateTests(spec.getNodes().size(), spec.getTransitions().size());
			
			try {
				FileWriter p = new FileWriter("Tests_"+i+".txt");
				for(FSMTest t : tests) {
					p.write(t.toFile());
				}
				p.close();
			}
			catch(IOException e){
				
			}
		}*/
		
		for(int i = 0; i < 9; i++) {
			//if(i==0||i==2) {//||i==6||i==8) {
			if(i==0) {
				Controller c = new Controller();
				c.run(75, 15, //tamaño poblacion, iteraciones, cota de inputs, cota tests
					3, 0.8, //numero participantes cruce, prob victoria participante
					0.75, 0.1, //prob cruce, prob mutacion
					0.2,//elitism_ratio
					"files/Tests_"+i+".txt", "files/Muts_"+i+".txt");
			}
		}
	}
}
