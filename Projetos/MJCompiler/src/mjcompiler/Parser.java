/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mjcompiler;

/**
 *
 * @author bianca
 */
public class Parser 
{
    private final Scanner scan;
//    private SymbolTable globalST;
//    private SymbolTable currentST;
    private Token lToken;
    
    
    public Parser(String inputFile)    
    {
        //Instancia a tabela de símbolos global e a inicializa
  //      globalST = new SymbolTable<STEntry>();
  //      initSymbolTable();
     
        //Faz o ponteiro para a tabela do escopo atual apontar para a tabela global
    //    currentST = globalST;
        
        //Instancia o analisador léxico
        scan = new Scanner(/*globalST, */inputFile);
    }
    
    /*
     * Método que inicia o processo de análise sintática do compilador
     */
    public void execute()
    {
        advance();
        
        try
        {
            program();
        }
        catch(CompilerException e)
        {
            System.err.println(e);
        }
    }
    
    private void advance()
    {
        lToken = scan.nextToken();
        
        System.out.print(lToken.name + "(" + lToken.lineNumber + ")" + " " );
    }
    
    private void match(EnumToken cTokenName) throws CompilerException
    {
        if (lToken.name == cTokenName)
            advance();
        else
        {            //Erro
            throw new CompilerException("Token inesperado: " + lToken.name + " na linha: " + lToken.lineNumber);
        }
    }
    
    
    /*
     * Método para o símbolo inicial da gramática
     */    
    private void program() throws CompilerException
    {
        //  MainClass ( ClassDeclaration )*
        mainClass();
        
        while (lToken.name == EnumToken.CLASS) 
            classDeclaration();
        
        match(EnumToken.EOF);
        
        System.out.println("\nCompilação encerrada com sucesso");
        
    }    
    
    private void mainClass() throws CompilerException
    {
        // class ID { public static void main (String[ ] ID){ Statement } }
        match(EnumToken.CLASS);
        match(EnumToken.ID);
        match(EnumToken.LBRACE);
        match(EnumToken.PUBLIC);
        match(EnumToken.STATIC);
        match(EnumToken.VOID);
        match(EnumToken.MAIN);
        match(EnumToken.LPARENTHESE);
        match(EnumToken.STRING);
        match(EnumToken.LBRACKET);
        match(EnumToken.RBRACKET);
        match(EnumToken.ID);
        match(EnumToken.RPARENTHESE);
        match(EnumToken.LBRACE);
        Statement();
        match(EnumToken.RBRACE);
        match(EnumToken.RBRACE);
    }
    
    private void classDeclaration() throws CompilerException
    {
        //class ID ( extends ID )? { ( VarDeclaration )*( MethodDeclaration)*}
        match(EnumToken.CLASS);
        match(EnumToken.ID);
        if(lToken.name == EnumToken.EXTENDS){
            advance();
            match(EnumToken.ID);
        }
        match(EnumToken.LBRACE);
        while (    lToken.name == EnumToken.INT 
                || lToken.name == EnumToken.BOOLEAN 
                || lToken.name == EnumToken.ID) {
            VarDeclaration();
        }
        while (    lToken.name == EnumToken.PUBLIC)
        {
            MethodDeclaration();
        }
        match(EnumToken.RBRACE);
    }

    private void Statement() throws CompilerException{
        if(null != lToken.name)switch (lToken.name) {
            //  { ( Statement )* }
            case LBRACE:
                advance();
                while(     lToken.name == EnumToken.LBRACE
                        || lToken.name == EnumToken.IF
                        || lToken.name == EnumToken.WHILE
                        || lToken.name == EnumToken.SOPRINTLN
                        || lToken.name == EnumToken.ID){
                    Statement();
                }   match(EnumToken.RBRACE);
                break;
            
            // if ( Expression ) Statement else Statement
            case IF:
                advance();
                match(EnumToken.LPARENTHESE);
                Expression();
                match(EnumToken.RPARENTHESE);
                Statement();
                match(EnumToken.ELSE);
                Statement();
                break;
            
            // while ( Expression ) Statement
            case WHILE:
                advance();
                match(EnumToken.LPARENTHESE);
                Expression();
                match(EnumToken.RPARENTHESE);
                Statement();
                break;
            
            // System.out.println ( Expression ) ;
            case SOPRINTLN:
                advance();
                match(EnumToken.LPARENTHESE);
                Expression();
                match(EnumToken.RPARENTHESE);
                match(EnumToken.SEMICOLON);
                break;
            
            //ID Statement'
            case ID:
                advance();
                Statement_linha();
                break;
            
            default:
                break;
        }
    }

    private void VarDeclaration() throws CompilerException{
        // Type ID;
        Type();
        match(EnumToken.ID);
    }

    private void MethodDeclaration() throws CompilerException{
        // public Type ID ( ( Type ID ( , Type ID )* )? ) { ( VarDeclaration )* ( Statement )* return Expression ; }
        match(EnumToken.PUBLIC);
        Type();
        match(EnumToken.ID);
        match(EnumToken.LPARENTHESE);
        if(    lToken.name == EnumToken.INT 
                || lToken.name == EnumToken.BOOLEAN 
                || lToken.name == EnumToken.ID){
            Type();
            match(EnumToken.ID);
            while(lToken.name == EnumToken.COMMA){
                advance();
                Type();
                match(EnumToken.ID);
            }
        }
        match(EnumToken.RPARENTHESE);
        match(EnumToken.LBRACE);
        while (    lToken.name == EnumToken.INT 
                || lToken.name == EnumToken.BOOLEAN 
                || lToken.name == EnumToken.ID) {
            VarDeclaration();
        }
        while(     lToken.name == EnumToken.LBRACE 
                    || lToken.name == EnumToken.IF
                    || lToken.name == EnumToken.WHILE
                    || lToken.name == EnumToken.SOPRINTLN
                    || lToken.name == EnumToken.ID){
                Statement();
        }
        match(EnumToken.RETURN);
        Expression();
        match(EnumToken.SEMICOLON);
        match(EnumToken.RBRACE);
    }

    private void Expression() throws CompilerException{
        if(null != lToken.name)switch (lToken.name) {
            
            // Op Expression Expression'
            case AND:
            case LT:
            case GT:
            case EQ:
            case NE:
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
                Op();
                Expression();
                Expression_linha();
                break;
            
            // [ Expression ] Expression'
            case LBRACE:
                advance();
                Expression();
                match(EnumToken.RBRACE);
                Expression_linha();
                break;
                
            // .Ponto'    
            case PERIOD:
                advance();
                Ponto_linha();
                break;
                
            // INTEGER_LITERAL    
            case INTEGER_LITERAL:
                advance();
                break;
            
            // true    
            case TRUE:
                advance();
                break;
            
            // false
            case FALSE:
                advance();
                break;
            
            //ID
            case ID:
                advance();
                break;
            
            // this
            case THIS:
                advance();
                break;
            
            // new New'
            case NEW:
                advance();
                New_linha();
                break;
            
            // ! Expression
            case NOT:
                advance();
                Expression();
                break;
            
            // ( Expression )
            case LPARENTHESE:
                advance();
                Expression();
                match(EnumToken.RPARENTHESE);
                break;
                
            default:
                break;
        }
    }

    private void Statement_linha() throws CompilerException {
        // [ Expression ] = Expression;
        if(lToken.name == EnumToken.LBRACE){
            advance();
            Expression();
            match(EnumToken.RBRACE);
            match(EnumToken.ATTRIB);
            Expression();
            match(EnumToken.SEMICOLON);
            
        }
        // = Expression;
        else if( lToken.name == EnumToken.ATTRIB){
            advance();
            Expression();
            match(EnumToken.SEMICOLON);
        }
    }

    private void Type() throws CompilerException {
        if(null != lToken.name)switch (lToken.name) {
            // int Type'
            case INT:
                advance();
                Type_linha();
                break;
            // boolean
            case BOOLEAN:
                advance();
                break;
            // ID    
            case ID:
                advance();
                break;
            default:
                break;
        }
        
    }

    @SuppressWarnings("empty-statement")
    private void Expression_linha() throws CompilerException {
        // Expression Expression'
        if(     lToken.name == EnumToken.AND
            ||  lToken.name == EnumToken.LT
            ||  lToken.name == EnumToken.GT
            ||  lToken.name == EnumToken.EQ
            ||  lToken.name == EnumToken.NE
            ||  lToken.name == EnumToken.PLUS
            ||  lToken.name == EnumToken.MINUS
            ||  lToken.name == EnumToken.MULT
            ||  lToken.name == EnumToken.DIV
            ||  lToken.name == EnumToken.PERIOD
            ||  lToken.name == EnumToken.INTEGER_LITERAL
            ||  lToken.name == EnumToken.TRUE
            ||  lToken.name == EnumToken.FALSE
            ||  lToken.name == EnumToken.ID
            ||  lToken.name == EnumToken.THIS
            ||  lToken.name == EnumToken.NEW
            ||  lToken.name == EnumToken.NOT
            ||  lToken.name == EnumToken.LPARENTHESE
            ){
            Expression();
            Expression_linha();
        }
        // Epslon
        else{
        ;
        }
    }

    private void Ponto_linha() throws CompilerException {
        // length Expression
        if(lToken.name == EnumToken.LENGTH){
            advance();
            Expression();
        }
        // ID ( ( Expression ( , Expression ) * )? ) Expression
        else if(lToken.name == EnumToken.ID){
           match(EnumToken.LPARENTHESE);
           if( lToken.name == EnumToken.AND
            ||  lToken.name == EnumToken.LT
            ||  lToken.name == EnumToken.GT
            ||  lToken.name == EnumToken.EQ
            ||  lToken.name == EnumToken.NE
            ||  lToken.name == EnumToken.PLUS
            ||  lToken.name == EnumToken.MINUS
            ||  lToken.name == EnumToken.MULT
            ||  lToken.name == EnumToken.DIV
            ||  lToken.name == EnumToken.PERIOD
            ||  lToken.name == EnumToken.INTEGER_LITERAL
            ||  lToken.name == EnumToken.TRUE
            ||  lToken.name == EnumToken.FALSE
            ||  lToken.name == EnumToken.ID
            ||  lToken.name == EnumToken.THIS
            ||  lToken.name == EnumToken.NEW
            ||  lToken.name == EnumToken.NOT
            ||  lToken.name == EnumToken.LPARENTHESE )
           {
               Expression();
               while(lToken.name == EnumToken.COMMA){
                   advance();
                   Expression();
               }
           }
           match(EnumToken.RPARENTHESE);
        }     
    }

    private void New_linha() throws CompilerException{
        // int [ Expression ]
        if(lToken.name == EnumToken.INT){
            advance();
            match(EnumToken.LBRACE);
            Expression();
            match(EnumToken.RBRACE);
        }
        // ID ( )
        else if(lToken.name == EnumToken.ID){
            advance();
            match(EnumToken.LPARENTHESE);
            match(EnumToken.RPARENTHESE);
        }
    }
    
    private void Op() throws CompilerException{
        if(null != lToken.name)switch (lToken.name) {
            // &&
            case AND:
                advance();
                break;
            // <
            case LT:
                advance();
                break;
            // >
            case GT:
                advance();
                break;
            // ==
            case EQ:
                advance();
                break;
            // !=
            case NE:
                advance();
                break;
            // +
            case PLUS:
                advance();
                break;
            // -
            case MINUS:
                advance();
                break;
            // *
            case MULT:
                advance();
                break;
            // /
            case DIV:
                advance();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("empty-statement")
    private void Type_linha() throws CompilerException {
       // []
        if(lToken.name == EnumToken.LBRACE){
           advance();
           match(EnumToken.RBRACE);
       }
        // Epslon
       else{
           ;
       }
    }
}























