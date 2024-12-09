package machineProject;

import java.util.*;

public class Parser {
	private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
	private static final Set<String> OPERATORS = new HashSet<>(Arrays.asList("NOT", "AND", "OR", "IMPLIES", "EQUIVALENT"));

	static {
		PRECEDENCE.put("NOT", 3);  
		PRECEDENCE.put("AND", 2);  
		PRECEDENCE.put("OR", 1);   
		PRECEDENCE.put("IMPLIES", 0);  
		PRECEDENCE.put("EQUIVALENT", 0);
	}

	private List<Token> tokens;
	private int index;

	public ParseTree getParseTree(List<Token> tokens) {
		this.tokens = tokens;
		this.index = 0;

		if (isAmbiguous(tokens)) {
			System.out.println("ERROR. Ambiguity detected in the input tokens.");
			Logic.restartProgram();
		}

		ParseTree tree = new ParseTree();
		tree.root = parse_S();

		if (this.index != tokens.size()) {
			System.out.println("ERROR. Unexpected token between " + tokens.get(this.index-1).value + " and " + tokens.get(this.index).value);
			Logic.restartProgram();
		}

		return tree;
	}

	private Node parse_S() {
		Node sNode = new Node("<S>");
		sNode.addChild(parse_CS());
		sNode.addChild(parse_SPrime());
		return sNode;
	}

	private Node parse_SPrime() {
		Node sPrimeNode = new Node("<S'>");

		if (this.index < tokens.size() && isConnective(tokens.get(this.index).value)) {
			sPrimeNode.addChild(parse_C());
			sPrimeNode.addChild(parse_S());
			sPrimeNode.addChild(parse_SPrime());
		} else {
			sPrimeNode.addChild(new Node("ε"));
		}

		return sPrimeNode;
	}

	private Node parse_CS() {
		Node csNode = new Node("<CS>");

		if (this.index < tokens.size() && tokens.get(this.index).value.equals("(")) {
			csNode.addChild(new Node("\"(\""));
			this.index++;
			csNode.addChild(parse_S());

			if (this.index < tokens.size() && tokens.get(this.index).value.equals(")")) {
				csNode.addChild(new Node("\")\""));
				this.index++;
			} else {
				System.out.println("ERROR. Expected ')'");
				Logic.restartProgram();
			}
		} else if (this.index < tokens.size() && tokens.get(this.index).value.equals("NOT")) {
			csNode.addChild(new Node("NOT"));
			this.index++;
			csNode.addChild(parse_S());
		} else {
			csNode.addChild(parse_A());
		}

		return csNode;
	}

	private Node parse_A() {
		if (this.index < tokens.size() && isAtomic(tokens.get(this.index).value)) {
			Node aNode = new Node("<A>");
			aNode.addChild(new Node("\"" + tokens.get(this.index).value + "\""));
			this.index++;
			return aNode;
		} else {
			System.out.println("ERROR. Missing atomic sentence.");
			Logic.restartProgram();
			
			Node placeHolder = new Node("");
			return placeHolder;
		}
	}

	private Node parse_C() {
		if (this.index < tokens.size() && isConnective(tokens.get(this.index).value)) {
			Node cNode = new Node("<C>");
			cNode.addChild(new Node("\"" + tokens.get(this.index).value + "\""));
			this.index++;
			return cNode;
		} else {
			System.out.println("ERROR. Missing connective.");
			Logic.restartProgram();
			
			Node placeHolder = new Node("");
			return placeHolder;
		}
	}

	private boolean isAtomic(String token) {
		return token.equals("TRUE") || token.equals("FALSE") ||
			   token.equals("P") || token.equals("Q") || token.equals("S");
	}

	private boolean isConnective(String token) {
		return OPERATORS.contains(token);
	}
	
	private boolean isAmbiguous(List<Token> tokens) {
		Stack<String> operatorStack = new Stack<>();
		int lastPrecedence = -1; // Keep track of the last operator precedence

		for (Token token : tokens) {
			String value = token.value;

			if (value.equals("(")) {
				operatorStack.push(value); // Push open parenthesis onto stack
			} else if (value.equals(")")) {
				// Pop operators until we reach a matching "("
				while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
					operatorStack.pop();
				}
				if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
					operatorStack.pop(); // Pop the matching "("
				}
			} else if (OPERATORS.contains(value)) {
				Integer currentPrecedence = PRECEDENCE.get(value);

				// Check if the operator exists in the precedence map
				if (currentPrecedence == null) {
					System.out.println("ERROR. Unknown operator: " + value);
					Logic.restartProgram();
					
					return false;  // Or handle it as an error case
				}

				// Check for ambiguity based on precedence and associativity
				if (!operatorStack.isEmpty()) {
					String topOperator = operatorStack.peek();
					Integer topPrecedence = PRECEDENCE.get(topOperator);

					// Check for ambiguity if precedence order is violated
					if (topPrecedence != null && currentPrecedence <= topPrecedence) {
						return true;
					}
				}

				// Push the current operator onto the stack
				operatorStack.push(value);
			}
		}

		return false; // No ambiguity detected
	}
	
	public void additionalAmbiguityCheck(List<Token> tokens) {
		String[] connectives = {"AND","OR","IMPLIES","EQUIVALENT"};
		int connectivesCtr = 0;
		int parenthesisPairCtr = 0;
		
		for (Token token : tokens) {
			if (Arrays.asList(connectives).contains(token.value)) {
				connectivesCtr++;
			}
			if (token.value.equals("(")) {
				parenthesisPairCtr++;
			}
		}
		
		if (connectivesCtr >= parenthesisPairCtr + 2) {
			System.out.println("ERROR. Ambiguity detected.");
			Logic.restartProgram();
		}
	}
}

class ParseTree {
	Node root;

	public void print() {
		if (root != null) {
			root.print("");
		}
	}
}

class Node {
	String value;
	List<Node> children;

	public Node(String value) {
		this.value = value;
		this.children = new ArrayList<>();
	}

	public void addChild(Node child) {
		children.add(child);
	}

	public void print(String indent) {
		System.out.println(indent + value);
		for (Node child : children) {
			child.print(indent + "  ");
		}
	}
}
