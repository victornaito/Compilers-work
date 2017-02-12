/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mjcompiler;

import java.util.List;

/**
 *
 * @author bianca
 */
public class Token 
{
    public EnumToken name;
    public EnumToken attribute;
    public String value;
    public int lineNumber;
    //public STEntry tsPtr;
    
    public Token(EnumToken name)
    {
        this.name = name;
        attribute = EnumToken.UNDEF;
        lineNumber = -1;
        //tsPtr = null;
    }
    
    public String print(List<Token> tok){
        int line = 0;
        StringBuilder str = new StringBuilder();
        for (Token object : tok) 
        {
            if(line != object.lineNumber && object.lineNumber != 0){
                line = object.lineNumber;
                str.append("\n").append("--------------------").append("\n").append("Linha [").append(object.lineNumber).append("]:");
            }            
            if(line == object.lineNumber){
                str.append("\n").append(object.name).append(" ").append(object.attribute);
            }
        }
        return str.toString();
    }
    
    public Token(EnumToken name, EnumToken attr)
    {
        this.name = name;
        attribute = attr;
        //tsPtr = null;
    }
    
}
