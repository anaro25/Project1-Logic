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
    public boolean isParsePass = true;

    public ParseTree getParseTree(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.isParsePass = true; 

        if (isAmbiguous(tokens)) {
            System.out.println("ERROR. Ambiguity detected in the input tokens.");
            this.isParsePass = false;
        }

        ParseTree tree = new ParseTree();
        tree.root = parse_S();

        if (this.index != tokens.size()) {
            System.out.println("ERROR. Unexpected token between " + tokens.get(this.index - 1).value + " and " + tokens.get(this.index).value);
            this.isParsePass = false;
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
                isParsePass = false;
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
            isParsePass = false;
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
            isParsePass = false;
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
        Stack<String> stack = new Stack<>();
        List<String> lastOperator = new ArrayList<>();
    
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
    
            if (token.value.equals("(")) {
                stack.push("(");
            } else if (token.value.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    stack.pop();
                }
                if (!stack.isEmpty() && stack.peek().equals("(")) {
                    stack.pop();
                }
            } else if (OPERATORS.contains(token.value)) {
                if (!stack.isEmpty() && OPERATORS.contains(stack.peek())) {
                    String prevOperator = stack.peek();
                    if (PRECEDENCE.get(prevOperator) < PRECEDENCE.get(token.value)) {
                        return true; 
                    }
                }
                lastOperator.add(token.value);
                stack.push(token.value);
            }
        }
    
        return false;
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

class Token {
    String value;

    public Token(String value) {
        this.value = value;
    }
}
