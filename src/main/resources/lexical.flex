
import java.util.*;
import java_cup.runtime.*;
import jflex.sym;
import top.coolzhang.simplecompiler.algorithm.lexical.Token;
import top.coolzhang.simplecompiler.utils.LexicalError;

%%

%class LexicalAnalyzer
%unicode
%cup
%line
%column

%{
  private static ArrayList<Token> token = new ArrayList<>();

  public ArrayList<Token> getToken() {
      ArrayList<Token> result = new ArrayList<>(token);
      token.clear();
      return result;
  }
%}

DIGIT = [0-9]
DIGIT1 = [1-9]
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Keyword = char|int|float|break|const|return|void|continue|do|while|if|else|for|main|read|write
Delimiter = "{"|"}"|","|";"
Integer = {DIGIT1}*[$0-9] | {DIGIT1}{DIGIT}*[$0-9]
Char = [\'][^\'][\']
String = [\"][^\"]*[\"]
Identifier = [a-zA-Z_][a-zA-Z0-9_]*
Decimal = {DIGIT}"."{DIGIT}*[$0-9] | {DIGIT1}{DIGIT}*"."{DIGIT}*[$0-9]
Operator = "("|")"|"["|"]"|"!"|"*"|"/"|"%"|"+"|"-"|"<"|"<="|">"|">="|"=="|"!="|"&&"|"||"|"="|"."

Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" [^\r\n]* {LineTerminator}?

Error = [^] | {Integer}+{Identifier}+ | {Decimal}+{Identifier}

%%

{Integer} {
  token.add(new Token("integer", yytext(), yyline+1, yycolumn+1));
}

{Decimal} {
  token.add(new Token("decimal", yytext(), yyline+1, yycolumn+1));
}

{Keyword} {
  token.add(new Token(yytext(), yytext(), yyline+1, yycolumn+1));
}

{Identifier} {
  token.add(new Token("identifier", yytext(), yyline+1, yycolumn+1));
}

{Operator} {
  token.add(new Token(yytext(), yytext(), yyline+1, yycolumn+1));
}

{Delimiter} {
  token.add(new Token(yytext(), yytext(), yyline+1, yycolumn+1));
}

{Char} {
  token.add(new Token("char", yytext(), yyline+1, yycolumn+1));
}

{String} {
  token.add(new Token("string", yytext(), yyline+1, yycolumn+1));
}

{WhiteSpace} {}

{Comment} {}

{Error} {
    token.clear();
    throw new LexicalError("在 (" + (yyline+1) + ", " + (yycolumn+1) + ") 处有词法错误");
}