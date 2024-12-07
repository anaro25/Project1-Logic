package machineProject;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Define the different token types
enum tokenCategory {
	Keyword, Identifier, Constant, SpecialSymbol, OperatorSymbol, Invalid
}

// Specify each token with its category and corresponding value
class Token {
	tokenCategory category;
	String value;
	
	Token(tokenCategory category, String value) {
		this.category = category;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Token{" + "type = '" + category + ", value = '" + value + '\'' + '}';
		
	}	
}

// Process and classify the input string based on its value
class Scanner {
	
	private tokenCategory gettokenCategory(String token) {
		if(token.matches("\\b(TRUE|FALSE|NOT|AND|OR|IMPLIES|EQUIVALENT)\\b")) {
			return tokenCategory.Keyword;
		} else if(token.matches("\\b(P|Q|S)\\b")) {
			return tokenCategory.Identifier;
		} else if(token.matches("\\b(TRUE|FALSE)\\b")) {
			return tokenCategory.Constant;
		} else if(token.matches("[()]")) {
			return tokenCategory.SpecialSymbol;
		} else if(token.matches("[¬^∨⇒⇔]")) {
			return tokenCategory.OperatorSymbol;			
		} else {
			return tokenCategory.Invalid;
		}	
	}
	
	// Scan the input string and tokenize into list of tokens
	public List<Token> scan(String input) {
		List<Token> tokens = new ArrayList<>();

		// Match valid tokens based on grammar
		String regex = "\\(|\\)|\\bP\\b|\\bQ\\b|\\bS\\b|\\bTRUE\\b|\\bFALSE\\b|\\bNOT\\b|\\bAND\\b|\\bOR\\b|\\bIMPLIES\\b|\\bEQUIVALENT\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		// Process each matched token
		while (matcher.find()) {
			String currentToken = matcher.group();
			tokenCategory category = gettokenCategory(currentToken);
			tokens.add(new Token(category, currentToken));
		}
		
		return tokens;
						
	}
}


