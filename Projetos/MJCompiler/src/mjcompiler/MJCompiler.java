/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mjcompiler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bianca
 */
public class MJCompiler extends javax.swing.JFrame
{
    public static void main(String[] args) 
    {       
        /*globalST = new SymbolTable<STEntry>();
        initSymbolTable();
        */
        Parser parser = new Parser("C:\\Users\\CakesPC\\Desktop\\MJCompiler\\src\\mjcompiler\\teste1.mj");
        
        Token tok;
        List<Token> listTokens = new ArrayList<>();
        
        
        //double var = 2.e+10;
        
        parser.execute();
     
    }

}
