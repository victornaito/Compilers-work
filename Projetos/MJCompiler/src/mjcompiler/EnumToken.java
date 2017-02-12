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
public enum EnumToken 
{
    /**
     * Token usado para palavras reservadas.
     */    
    W_RESERVED, 
    /**
     * Token usado para abertura de comentário de bloco /*
     */ 
    OPEN_COMMENT_BLOCK,
    /**
     * Token usado para abertura de comentário de bloco * /
     */ 
    CLOSE_COMMENT_BLOCK, // new token to close comment block;
    /**
     * Token usado para comentário de linha;
     */ 
    LINE_COMMENT, 
    UNDEF,
    CLASS,
    PUBLIC,
    STATIC,
    VOID,
    MAIN,
    ID,
    IF,
    WHILE,
    /**
     * Token usado para System.out.println * /
     */ 
    SOPRINTLN,
    THIS,
    STRING,
    INT,
    BOOLEAN,
    NEW,
    EXTENDS,
    SEP,
    /**
     * Token usado para { 
     */ 
    LBRACKET,
    /**
     * Token usado para } 
     */ 
    RBRACKET,
    /**
     * Token usado para ( 
     */ 
    LPARENTHESE,
    /**
     * Token usado para )
     */ 
    RPARENTHESE,
    /**
     * Token usado para [
     */ 
    LBRACE,
    /**
     * Token usado para ]
     */
    RBRACE,
    /**
     * Token usado para .
     */ 
    PERIOD,
    COMMA,
    /**
     * Token usado para ;
     */ 
    SEMICOLON,
    ELSE,
    ATTRIB,
    NOT,
    ARITHOP,
    PLUS,
    MINUS,
    MULT,
    DIV,
    RELOP,
    EQ,
    NE,
    GT,
    LT,
    NUMBER,
    INTEGER_LITERAL,
    TRUE,
    FALSE,
    LOGOP,
    AND,
    RETURN,
    LENGTH,
    EOF
}
