/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compl.etc;

import java.util.List;

/**
 * represents  syntax within brackets {}
 * has a recursive call to statements or more blocks of syntax within the 
 * syntax.
 * @author kieda
 */
public class Syntax extends compl.etc.Thing{
    //this is a recursive call to a list of code things to execute.
    List<compl.etc.Thing> code;
}

