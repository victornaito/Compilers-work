package mjcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import java.text.StringCharacterIterator;

/**
 *
 * @author Bianca
 */
public class Scanner 
{
    private static String input;
    private StringCharacterIterator inputIt;
    //private SymbolTable st;
    private int lineNumber;
    
    public Scanner(/*SymbolTable globalST, */String inputFileName)
    {
        File inputFile = new File(inputFileName);       
        
        //st = globalST;
        
        try
        {
            FileReader fr = new FileReader(inputFile);
            
            int size = (int)inputFile.length();            
            char[] buffer = new char[size];
        
            fr.read(buffer, 0, size);
            
            input = new String(buffer);
            
            inputIt = new StringCharacterIterator(input);
            
            lineNumber = 1;
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Arquivo não encontrado");
        }
        catch(IOException e)
        {
            System.err.println("Erro na leitura do arquivo");
        }
    }
    
    public Token nextToken()
    {
        Token tok = new Token(EnumToken.UNDEF);   
        
        int begin = 0, end = 0;
        String lexema;    
        char ch = inputIt.current();
         
        while (true)
        {
            //Verifica final de arquivo, finalizando execucao MJCompiler.
            if(Character.getType(inputIt.current()) == '\0'){
                tok.name = EnumToken.EOF;
                tok.attribute = EnumToken.EOF;
                tok.lineNumber = lineNumber;
                return tok;
            }
            //Ignorando comentarios Bloco e Linha;
            if (inputIt.current() == '/') 
            {
                switch(inputIt.next())
                {
                    // COMENTÁRIO DE BLOCO
                    case '*':
                        while(true)
                        {
                            if(inputIt.current() == '\n')
                            {
                                tok.lineNumber = lineNumber++;
                                inputIt.next();
                            }
                            else if(inputIt.current() == '*')
                            {
                                inputIt.next();
                                if(inputIt.current() == '/'){
                                    lineNumber++;
                                    inputIt.next();
                                    break;
                                }
                            }
                            else{
                                inputIt.next();
                            }
                        }
                        if(inputIt.current() == '\n'){
                            lineNumber++;
                        }
                        inputIt.next();
                        tok.lineNumber = lineNumber;
                    break;
                    
                    // COMENTÁRIO DE LINHA
                    case '/':
                        while(inputIt.current() != '\n'){
                            inputIt.next();
                        }
                        lineNumber++;
                        inputIt.next();
                    break;
                    default:
                        tok.lineNumber = lineNumber;
                        break;
                }
            }
            
            //Consome espaços em branco e volta para o estado inicial
            if(Character.isWhitespace(inputIt.current())){
               inputIt.next();
            }
            
            //Vê se é uma quebra de linha e incrementa o lineNumber
           if(inputIt.current() == '\n'){
                lineNumber++;
                inputIt.next();
           }

            //Operadores aritméticos
            else if (  inputIt.current() == '+' 
                    || inputIt.current() == '-' 
                    || inputIt.current() == '*' 
                    || inputIt.current() == '/'
                    || inputIt.current() == '%')
            {                

                switch(inputIt.current())
                {
                    case '+':
                        tok.attribute = EnumToken.ARITHOP;
                        tok.name = EnumToken.PLUS;
                        inputIt.next();
                        tok.lineNumber = lineNumber;
                        return tok;
                    case '-':
                        tok.attribute = EnumToken.ARITHOP;
                        tok.name = EnumToken.MINUS;                        
                        inputIt.next();
                        tok.lineNumber = lineNumber;
                        return tok;
                    case '*':
                        if(inputIt.next() != '/'){
                            tok.attribute = EnumToken.ARITHOP;
                            tok.name = EnumToken.MULT;
                            inputIt.next();
                            tok.lineNumber = lineNumber;
                            return tok;
                        }
                        else{
                            while(inputIt.current() != '\n'){
                                inputIt.next();
                            }
                            tok.lineNumber = lineNumber++;
                        }
                        break;
                    case '/':
                        switch (inputIt.next())
                        {
                            case '/':
                                while(inputIt.current() != '\n'){
                                    inputIt.next();
                                }
                                inputIt.next();
                                tok.lineNumber = lineNumber++;
                                break;
                            case '*':
                                do
                                {
                                    inputIt.next();
                                    if(inputIt.current() == '\n')
                                    {
                                        tok.lineNumber = lineNumber++;
                                    }
                                }while((inputIt.current() != '*' && inputIt.next() != '/'));
                                inputIt.next();
                                if(inputIt.current() == '\n'){
                                    lineNumber++;
                                    inputIt.next();
                                    tok.lineNumber = lineNumber;
                                }
                                else{
                                    inputIt.next();
                                    tok.lineNumber = lineNumber;
                                }
                                break;
                            default:
                                tok.attribute = EnumToken.ARITHOP;
                                tok.name = EnumToken.DIV;
                                inputIt.next();
                                tok.lineNumber = lineNumber;
                                return tok;
                        }
                        break;
                    }
            }
            
            //Operadores de igualdade
            else if( inputIt.current() == '=' ){
                tok.lineNumber = lineNumber;

                if(inputIt.next() == '='){
                    inputIt.next();
                    tok.attribute = EnumToken.RELOP;
                    tok.name = EnumToken.EQ;
                    return tok;
                }
               
                 else{
                    tok.attribute = EnumToken.LOGOP;
                    tok.name = EnumToken.ATTRIB;
                    return tok;
                }
                     
                }
            
            else if( inputIt.current() == '!' ){
                tok.lineNumber = lineNumber;
                
                if(inputIt.next() == '='){
                    inputIt.next();
                    tok.attribute = EnumToken.RELOP;
                    tok.name = EnumToken.NE;
                    return tok;
                }
                else{
                    tok.attribute = EnumToken.LOGOP;
                    tok.name = EnumToken.NOT;
                    return tok;
                }
            }
            
            else if( inputIt.current() == '&'){
                tok.lineNumber = lineNumber;
                if(inputIt.next() == '&'){
                    inputIt.next();
                    tok.attribute = EnumToken.LOGOP;
                    tok.name = EnumToken.AND;
                    return tok;
                }
                else{
                    tok.attribute = EnumToken.UNDEF;
                    tok.name = EnumToken.UNDEF;
                    return tok;
                }
            }
            
            else if( inputIt.current() == '>' ){
                tok.lineNumber = lineNumber;
                inputIt.next();
                tok.attribute = EnumToken.RELOP;
                tok.name = EnumToken.GT;
                return tok;
            }
            
            else if( inputIt.current() == '<' ){
                tok.lineNumber = lineNumber;
                 inputIt.next();
                tok.attribute = EnumToken.RELOP;
                tok.name = EnumToken.LT;
                return tok;
            }
            
            //Identificadores
            else if(isLetter(inputIt.current())){
                tok.lineNumber = lineNumber;
                tok.attribute = EnumToken.ID;
                tok.name = EnumToken.ID;
                while(true){
                    if(  isLetter(inputIt.current()) || isDigit(inputIt.current()) || inputIt.current() == '_' ){
                        switch (inputIt.current()) {
                            
                            //IDs reservadas começando com B
                            case 'b':
                                inputIt.next();
                                
                                //BOOLEAN
                                if( inputIt.current() == 'o'){
                                    inputIt.next();
                                    if(inputIt.current() == 'o'){
                                        inputIt.next();
                                        if(inputIt.current() == 'l'){
                                            inputIt.next();
                                            if(inputIt.current() == 'e'){
                                                inputIt.next();
                                                if(inputIt.current() == 'a'){
                                                    inputIt.next();
                                                    if(inputIt.current() == 'n' && (   Character.isWhitespace(inputIt.next()) 
                                                        || inputIt.current()  == ')'))
                                                    {
                                                        tok.name = EnumToken.BOOLEAN;
                                                        tok.attribute = EnumToken.W_RESERVED;
                                                        tok.lineNumber = lineNumber;
                                                        return tok;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }   break;
                                
                            // IDs reservadas começando com C
                            case 'c':
                                inputIt.next();
                                
                                //CLASS
                                if(inputIt.current() == 'l'){
                                    inputIt.next();
                                    if(inputIt.current() == 'a'){
                                        inputIt.next();
                                        if(inputIt.current() == 's'){
                                            inputIt.next();
                                            if(    inputIt.current() == 's' && Character.isWhitespace(inputIt.next()) ){
                                                tok.attribute = EnumToken.W_RESERVED;
                                                tok.name = EnumToken.CLASS;
                                                tok.lineNumber = lineNumber;
                                                return tok;
                                            }
                                        }
                                    }
                                }break;
                                
                            // IDs reservadas começando com E
                            case 'e':
                                inputIt.next();
                                
                                // ELSE
                                if(inputIt.current() == 'l'){
                                    inputIt.next();
                                    if(inputIt.current() == 's'){
                                        inputIt.next();
                                        if(inputIt.current() == 'e' && (    Character.isWhitespace(inputIt.next()) 
                                        || inputIt.current()  == '{'))
                                        {
                                            tok.name = EnumToken.ELSE;
                                            tok.attribute = EnumToken.W_RESERVED;
                                            tok.lineNumber = lineNumber;
                                            return tok;
                                        }
                                    }
                                }
                                
                                // EXTENDS
                                else if(inputIt.current() == 'x'){
                                    inputIt.next();
                                    if(inputIt.current() == 't'){
                                        inputIt.next();
                                        if(inputIt.current() == 'e'){
                                            inputIt.next();
                                            if(inputIt.current() == 'n'){
                                                inputIt.next();
                                                if(inputIt.current() == 'd'){
                                                    inputIt.next();
                                                    if( inputIt.current() == 's' && (Character.isWhitespace(inputIt.next()) 
                                                            || inputIt.current() == '{' ))
                                                    {
                                                        tok.name = EnumToken.EXTENDS;
                                                        tok.attribute = EnumToken.W_RESERVED;
                                                        tok.lineNumber = lineNumber;
                                                        return tok;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 'f':
                                inputIt.next();
                                //FALSE
                                if( inputIt.current() == 'a'){
                                    inputIt.next();
                                    if(inputIt.current() == 'l'){
                                        inputIt.next();
                                        if(inputIt.current() == 's'){
                                            inputIt.next();
                                            if(inputIt.current() == 'e' && (Character.isWhitespace(inputIt.next())
                                                    || inputIt.next() == ')' || inputIt.next() == ';' || inputIt.next() == '|'
                                                    || inputIt.next() == '&')){
                                                tok.name = EnumToken.FALSE;
                                                tok.attribute = EnumToken.W_RESERVED;
                                                tok.lineNumber = lineNumber;
                                                return tok;
                                            }
                                        }
                                    }
                                }break;
                                
                                
                            case 'i':
                                inputIt.next();
                                
                                // IF
                                if(inputIt.current() == 'f' && (Character.isWhitespace(inputIt.next())
                                    ||  inputIt.current()  == '(') )
                                {
                                    tok.name = EnumToken.IF;
                                    tok.attribute = EnumToken.W_RESERVED;
                                    tok.lineNumber = lineNumber;
                                    return tok;
                                }
                                
                                // INT
                                else if( inputIt.current() == 'n'){
                                    inputIt.next();
                                    if(    inputIt.current() == 't' && ( Character.isWhitespace(inputIt.next())
                                        || inputIt.current()  == ')' ))
                                    {
                                        tok.name = EnumToken.INT;
                                        tok.attribute = EnumToken.W_RESERVED;
                                        tok.lineNumber = lineNumber;
                                        return tok;  
                                    }
                                }break;
                            
                            case 'l':
                                inputIt.next();
                                
                                //LENGTH
                                if(inputIt.current() == 'e'){
                                    inputIt.next();
                                    if(inputIt.current() == 'n'){
                                        inputIt.next();
                                        if(inputIt.current() == 'g'){
                                            inputIt.next();
                                            if(inputIt.current() == 't'){
                                                inputIt.next();
                                                if(inputIt.current() == 'h' && inputIt.next() == '('){
                                                    tok.name = EnumToken.LENGTH;
                                                    tok.attribute = EnumToken.W_RESERVED;
                                                    tok.lineNumber = lineNumber;
                                                    return tok;
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 'm':
                                inputIt.next();
                                
                                //MAIN
                                if(inputIt.current() == 'a'){
                                    inputIt.next();
                                    if(inputIt.current() == 'i'){
                                        inputIt.next();
                                        if(inputIt.current() == 'n' && (Character.isWhitespace(inputIt.next())
                                           || inputIt.current()  == '('))
                                        {
                                           tok.name = EnumToken.MAIN;
                                           tok.attribute = EnumToken.W_RESERVED;
                                           tok.lineNumber = lineNumber;
                                           return tok;
                                        }
                                    }
                                }break;
                                
                            case 'n':
                                inputIt.next();
                                
                                //NEW
                                if(inputIt.current() == 'e'){
                                    inputIt.next();
                                    if(inputIt.current() == 'w' && Character.isWhitespace(inputIt.next()) ){
                                        tok.name = EnumToken.NEW;
                                        tok.attribute = EnumToken.W_RESERVED;
                                        tok.lineNumber = lineNumber;
                                        return tok;
                                    }
                                }break;
                                
                            case 'p':
                                inputIt.next();
                                
                                //PUBLIC
                                if(inputIt.current() == 'u'){
                                    inputIt.next();
                                    if(inputIt.current() == 'b'){
                                        inputIt.next();
                                        if(inputIt.current() == 'l'){
                                            inputIt.next();
                                            if(inputIt.current() == 'i'){
                                                inputIt.next();
                                                if(inputIt.current() == 'c' && Character.isWhitespace(inputIt.next()) ){
                                                    tok.attribute = EnumToken.W_RESERVED;
                                                    tok.name = EnumToken.PUBLIC;
                                                    tok.lineNumber = lineNumber;
                                                    return tok;
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 'r':
                                inputIt.next();
                                
                                //RETURN
                                if(inputIt.current() == 'e'){
                                    inputIt.next();
                                    if(inputIt.current() == 't'){
                                        inputIt.next();
                                        if(inputIt.current() == 'u'){
                                            inputIt.next();
                                            if(inputIt.current() == 'r'){
                                                inputIt.next();
                                                if(inputIt.current() == 'n' && Character.isWhitespace(inputIt.next())){
                                                    tok.name = EnumToken.RETURN;
                                                    tok.attribute = EnumToken.W_RESERVED;
                                                    tok.lineNumber = lineNumber;
                                                    return tok;
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 's':
                                inputIt.next();
                                
                                //STATIC
                                if(inputIt.current() == 't'){
                                    inputIt.next();
                                    if(inputIt.current() == 'a'){
                                        inputIt.next();
                                        if(inputIt.current() == 't'){
                                            inputIt.next();
                                            if(inputIt.current() == 'i'){
                                                inputIt.next();
                                                if(inputIt.current() == 'c' && Character.isWhitespace(inputIt.next())){
                                                    tok.name = EnumToken.STATIC;
                                                    tok.attribute = EnumToken.W_RESERVED;
                                                    tok.lineNumber = lineNumber;
                                                    return tok;
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 'S':
                                inputIt.next();
                                
                                // STRING
                                if(inputIt.current() == 't'){
                                    inputIt.next();
                                    if(inputIt.current() == 'r'){
                                        inputIt.next();
                                        if(inputIt.current() == 'i'){
                                            inputIt.next();
                                            if(inputIt.current() == 'n'){
                                                inputIt.next();
                                                if(inputIt.current() == 'g' && (Character.isWhitespace(inputIt.next())
                                                    || inputIt.current() == '['))
                                                {
                                                    tok.name = EnumToken.STRING;
                                                    tok.attribute = EnumToken.W_RESERVED;
                                                    tok.lineNumber = lineNumber;
                                                    return tok;
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                // SYSTEM.OUT.PRINTLN
                                else if(inputIt.current() == 'y'){
                                    inputIt.next();
                                    if(inputIt.current() == 's'){
                                        inputIt.next();
                                        if(inputIt.current() == 't'){
                                            inputIt.next();
                                            if(inputIt.current() == 'e'){
                                                inputIt.next();
                                                if(inputIt.current() == 'm'){
                                                    inputIt.next();
                                                    if(inputIt.current() == '.'){
                                                        inputIt.next();
                                                        if(inputIt.current() == 'o'){
                                                            inputIt.next();
                                                            if(inputIt.current() == 'u'){
                                                                inputIt.next();
                                                                if(inputIt.current() == 't'){
                                                                    inputIt.next();
                                                                    if(inputIt.current() == '.'){
                                                                        inputIt.next();
                                                                        if(inputIt.current() == 'p'){
                                                                            inputIt.next();
                                                                            if(inputIt.current() == 'r'){
                                                                                inputIt.next();
                                                                                if(inputIt.current() == 'i'){
                                                                                    inputIt.next();
                                                                                    if(inputIt.current() == 'n'){
                                                                                        inputIt.next();
                                                                                        if(inputIt.current() == 't'){
                                                                                            inputIt.next();
                                                                                            if(inputIt.current() == 'l'){
                                                                                                inputIt.next();
                                                                                                if(inputIt.current() == 'n' && (Character.isWhitespace(inputIt.next()) 
                                                                                                    || inputIt.current()  == '(' ))
                                                                                                {
                                                                                                    tok.name = EnumToken.SOPRINTLN;
                                                                                                    tok.attribute = EnumToken.W_RESERVED;
                                                                                                    tok.lineNumber = lineNumber;
                                                                                                    return tok;
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }break;
                                
                            case 't':
                                inputIt.next();
                                
                                //THIS
                                if(inputIt.current() == 'h'){
                                    inputIt.next();
                                    if(inputIt.current() == 'i'){
                                        inputIt.next();
                                        if(inputIt.current() == 's' && inputIt.next() == '.'){
                                            tok.name = EnumToken.THIS;
                                            tok.attribute = EnumToken.W_RESERVED;
                                            tok.lineNumber = lineNumber;
                                            return tok;
                                        }
                                    }
                                }
                             
                                //TRUE
                                else if(inputIt.current() == 'r'){
                                    inputIt.next();
                                    if(inputIt.current() == 'u'){
                                        inputIt.next();
                                        if(inputIt.current() == 'e' && (inputIt.next() == ')' ||
                                                inputIt.next() == ';')){
                                            tok.name = EnumToken.TRUE;
                                            tok.attribute = EnumToken.W_RESERVED;
                                            tok.lineNumber = lineNumber;
                                            return tok;
                                        }
                                    }
                                }break;
                                
                            case 'v':
                                inputIt.next();
                                
                                //VOID
                                if(inputIt.current() == 'o'){
                                    inputIt.next();
                                    if(inputIt.current() == 'i'){
                                        inputIt.next();
                                        if(inputIt.current() == 'd' && Character.isWhitespace(inputIt.next())){
                                            tok.name = EnumToken.VOID;
                                            tok.attribute = EnumToken.W_RESERVED;
                                            tok.lineNumber = lineNumber;
                                            return tok;
                                        }
                                    }
                                }break;
                                
                            case 'w':
                                inputIt.next();
                                
                                //WHILE
                                if(inputIt.current() == 'h'){
                                    inputIt.next();
                                    if(inputIt.current() == 'i'){
                                        inputIt.next();
                                        if(inputIt.current() == 'l'){
                                            inputIt.next();
                                            if(inputIt.current() == 'e' && (Character.isWhitespace(inputIt.next())
                                                || inputIt.current()  == '(' ))
                                            {
                                                tok.name = EnumToken.WHILE;
                                                tok.attribute = EnumToken.W_RESERVED;
                                                tok.lineNumber = lineNumber;
                                                return tok;
                                            }
                                        }
                                    }
                                }break;
                                
                                
                            default:
                                inputIt.next();
                                break;
                        }
                    }
                    else{
                        break;
                    }
                }
                return tok;
            }
            
            //Números Inteiros
            else if(isDigit(inputIt.current())){
                tok.lineNumber = lineNumber;
                tok.name = EnumToken.INTEGER_LITERAL;
                tok.attribute = EnumToken.INTEGER_LITERAL;
                inputIt.next();
                while(true){
                    if( isDigit(inputIt.current()) ){
                        inputIt.next();
                    }
                    else{
                        break;
                    }
                }
                return tok;
            }
           
           //Separadores
            else if(   inputIt.current() == '('
                    || inputIt.current() == ')'
                    || inputIt.current() == '{'
                    || inputIt.current() == '}'
                    || inputIt.current() == '['
                    || inputIt.current() == ']'
                    || inputIt.current() == ';'
                    || inputIt.current() == '.'
                    || inputIt.current() == ','){
                tok.attribute = EnumToken.SEP;
                tok.lineNumber = lineNumber;
                
                switch(inputIt.current()){
                    case '(':
                        tok.name = EnumToken.LPARENTHESE;
                        break;
                    case ')':
                        tok.name = EnumToken.RPARENTHESE;
                        break;
                    case '{':
                        tok.name = EnumToken.LBRACE;
                        break;
                    case '}':
                        tok.name = EnumToken.RBRACE;
                        break;
                    case '[':
                        tok.name = EnumToken.LBRACKET;
                        break;
                    case ']':
                        tok.name = EnumToken.RBRACKET;
                        break;    
                    case ';':
                        tok.name = EnumToken.SEMICOLON;
                        break;
                    case '.':
                        tok.name = EnumToken.PERIOD;
                        break;
                    case ',':
                        tok.name = EnumToken.COMMA;
                        break;
                }
                
                inputIt.next();
                return tok;
                                        
                
            }
        }
    }
        
        
        //Continua....
    }//nextToken
    
    

