package machineProject;

public class Evaluator {
	
	private int pvalue = 0;
	private int qvalue = 0;
	private int svalue = 0;
	private int hasP = 0;
	private int hasQ = 0;
	private int hasS = 0;
	
	public void getTruthTable(ParseTree parseTree) {
		
		evaluate(parseTree.root); //evaluate once muna, to check for the existence of p, q, and s
		
        if (hasP == 1 && hasQ == 1 && hasS == 1) {
    		System.out.println("|P     |Q     |S     |Result|");
    		System.out.println("-----------------------------");
        	for (int row = 0; row < 8; row++) {
            	this.pvalue = (row & 4) >> 2; //extract the 3rd bit
            	this.qvalue = (row & 2) >> 1; //extract the 2nd bit
            	this.svalue = (row & 1);      //extract the 1st bit
            	boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%-6s|", pvalue == 0, qvalue == 0, svalue == 0);
            	System.out.printf("%-6s|%n", result);
        		System.out.println("-----------------------------");
        	}
        }
        else if (hasP == 1 && hasQ == 1) {
    		System.out.println("|P     |Q     |Result|");
    		System.out.println("----------------------");
        	for (int row = 0; row < 4; row++) {
            	this.pvalue = (row & 2) >> 1; //extract the 2nd bit
            	this.qvalue = (row & 1);      //extract the 1st bit
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%-6s|%n", pvalue == 0, qvalue == 0, result.toString());
        		System.out.println("----------------------");
        	}
        }
        else if (hasP == 1 && hasS == 1) {
    		System.out.println("|P     |S     |Result|");
    		System.out.println("----------------------");
        	for (int row = 0; row < 4; row++) {
            	this.pvalue = (row & 2) >> 1; //extract the 2nd bit
            	this.svalue = (row & 1);      //extract the 1st bit
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%-6s|%n", pvalue == 0, svalue == 0, result.toString());
        		System.out.println("----------------------");
        	}
        }
        else if (hasQ == 1 && hasS == 1) {
    		System.out.println("|Q     |S     |Result|");
    		System.out.println("----------------------");
        	for (int row = 0; row < 4; row++) {
            	this.qvalue = (row & 2) >> 1; //extract the 2nd bit
            	this.svalue = (row & 1);      //extract the 1st bit
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%-6s|%n", qvalue == 0, svalue == 0, result.toString());
        		System.out.println("-----------------------");
        	}
        }
        else if (hasP == 1) {
    		System.out.println("|P     |Result|");
    		System.out.println("---------------");
        	for (int row = 0; row < 2; row++) {
            	this.pvalue = row;
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%n", pvalue == 0, result.toString());
        		System.out.println("---------------");
        	}
        }
        else if (hasQ == 1) {
    		System.out.println("|Q     |Result|");
    		System.out.println("---------------");
        	for (int row = 0; row < 2; row++) {
            	this.qvalue = row;
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%n", qvalue == 0, result.toString());
        		System.out.println("---------------");
        	}
        }
        else {
    		System.out.println("|S     |Result|");
    		System.out.println("---------------");
        	for (int row = 0; row < 2; row++) {
            	this.svalue = row;
            	Boolean result = evaluate(parseTree.root);
            	System.out.printf("|%-6s|%-6s|%n", svalue == 0, result.toString());
        		System.out.println("--------------");
        	}
        }

	}
	
	private boolean evaluate(Node node) {
		
		switch(node.value) {
		
			case "<S>":
				Node left = node.children.get(0); //<CS>
				Node right = node.children.get(1); //<S'>
				Node connector = right.children.get(0); //<C> of <S'>
				if (connector.value.equals("<C>")) {
					
					connector = connector.children.get(0);
					boolean left_result = evaluate(left);
					boolean right_result = evaluate(right);
					
					if (connector.value.equals("\"OR\"")) {
						return (left_result || right_result);
					}
					else if (connector.value.equals("\"AND\"")) {
						return (left_result && right_result);
					}
					else if (connector.value.equals("\"IMPLIES\"")) {
						return (!left_result || right_result);
					}
					else if (connector.value.equals("\"EQUIVALENT\"")) {
						return (left_result == right_result);
					}
					else {
						System.out.println("ERROR: Invalid grammar " + left.value + " " + connector.value + " " + right.value);
						Logic.restartProgram();
					}
				}
				else if (connector.value.equals("ε")){
					return evaluate(left);
				}
				else {
					//System.out.println("Error happens here at <S>");
					System.out.println("ERROR: Invalid grammar " + left.children.get(0).value + " " + right.children.get(0).value);
					Logic.restartProgram();
				}
				
				
			case "<CS>":
				if (node.children.get(0).value.equals("\"(\"")) {
					return evaluate(node.children.get(1)); 
				}
				else if (node.children.get(0).value.equals("NOT")) {
					return !evaluate(node.children.get(1));
				}
				else if (node.children.get(0).value.equals("<A>")){
					return evaluate(node.children.get(0));
				}
				else {
					//System.out.println("Error happens here at <CS>");
					System.out.println("ERROR: Invalid grammar " + node.children.get(0).value);
					Logic.restartProgram();
				}
					

			case "<S'>":
				Node left_prime = node.children.get(1); //<S>
				Node right_prime = node.children.get(2); //<S'>
				Node connector_prime = right_prime.children.get(0); //<C> of <S'>
				if (connector_prime.value.equals("<C>")) {

					boolean left_prime_result = evaluate(left_prime);
					boolean right_prime_result = evaluate(right_prime);
					connector_prime = connector_prime.children.get(0);
					
					if (connector_prime.value.equals("\"OR\"")) {
						return (left_prime_result || right_prime_result);
					}
					else if (connector_prime.value.equals("\"AND\"")) {
						return (left_prime_result && right_prime_result);
					}
					else if (connector_prime.value.equals("\"IMPLIES\"")) {
						return (!left_prime_result || right_prime_result);
					}
					else if (connector_prime.value.equals("\"EQUIVALENT\"")) {
						return (left_prime_result == right_prime_result);
					}
					else {
						System.out.println("ERROR: Invalid grammar " + left_prime.value + " " + connector_prime.value + " " + right_prime.value);
						Logic.restartProgram();
					}
				}
				else if (connector_prime.value.equals("ε")){
					return evaluate(left_prime);
				}
				else {
					//System.out.println("Error happens here at <S'>");
					System.out.println("ERROR: Invalid grammar " + left_prime.children.get(0).value + " " + right_prime.children.get(0).value);
					Logic.restartProgram();
				}
				
			case "<A>":
				Node atom = node.children.get(0); //<A>
				if (atom.value.equals("\"P\"")) {
					this.hasP = 1;
					if (this.pvalue == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (atom.value.equals("\"Q\"")) {
					this.hasQ = 1;
					if (this.qvalue == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (atom.value.equals("\"S\"")) {
					this.hasS = 1;
					
					if (this.svalue == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					//System.out.println("Error happens here at <A>");
					System.out.println("ERROR: Invalid identifier " + atom.children.get(0));
					Logic.restartProgram();
				}
		}
		
		return false;
	}

}