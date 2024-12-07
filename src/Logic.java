package machineProject;

import java.util.List;

public class Logic {

	public static void main(String[] args) {
		Scanner1 scanner = new Scanner1();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		
		String input = scanner.getUserInput();
		List<Token> tokens = scanner.scan(input);

		/*
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			System.out.println(token.toString());
		}*/
		
		ParseTree parseTree = parser.getParseTree(tokens);
		parser.additionalAmbiguityCheck(tokens);
		evaluator.getTruthTable(parseTree);
	}
}
