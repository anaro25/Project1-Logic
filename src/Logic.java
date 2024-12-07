package machineProject;

import java.util.List;

public class Logic {

	public static void main(String[] args) {
		Scanner scanner = new Scanner();
		Parser parser = new Parser();
		Evaluator evaluator = new Evaluator();
		List<Token> tokens = scanner.scan("(P OR Q) AND NOT Q");
		/*
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			System.out.println(token.toString());
		}*/
		
		ParseTree parseTree = parser.getParseTree(tokens);
		evaluator.getTruthTable(parseTree);
		

	}

}
