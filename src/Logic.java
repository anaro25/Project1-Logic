package machineProject;

import java.util.List;

public class Logic {
	
	public static void main(String[] args) {
		mainLoop();
	}
	
	private static void mainLoop() {
		while (true) {
			
			Scanner1 scanner = new Scanner1();
			Parser parser = new Parser();
			Evaluator evaluator = new Evaluator();
		
			String input = scanner.getUserInput();
			List<Token> tokens = scanner.scan(input);
				
			ParseTree parseTree = parser.getParseTree(tokens);
			parser.additionalAmbiguityCheck(tokens);
					
			evaluator.getTruthTable(parseTree);
		}
	}
	
	public static void restartProgram() {    
		mainLoop();
	}
}
