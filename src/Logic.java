package machineProject;

import java.util.List;

public class Logic {

	public static void main(String[] args) {
		Scanner1 scanner = new Scanner1();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		
		while (true) { // run indefinitely	
			String input = scanner.getUserInput();
			
			List<Token> tokens = scanner.scan(input);
			if (!scanner.isScanPass) continue; // if fail, restart loop
			
			ParseTree parseTree = parser.getParseTree(tokens);
			parser.additionalAmbiguityCheck(tokens);
			
			evaluator.getTruthTable(parseTree);
		}
	}
}
