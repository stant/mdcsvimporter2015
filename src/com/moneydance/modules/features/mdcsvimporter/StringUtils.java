/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moneydance.modules.features.mdcsvimporter;

import java.util.ArrayList;

/**
 * Unescapes a string that contains standard Java escape sequences.
 * <ul>
 * <li><strong>\b \f \n \r \t \" \'</strong> :
 * BS, FF, NL, CR, TAB, double and single quote.</li>
 * <li><strong>\X \XX \XXX</strong> : Octal character
 * specification (0 - 377, 0x00 - 0xFF).</li>
 * <li><strong>\ uXXXX</strong> : Hexadecimal based Unicode character.</li>
 * </ul>
 * 
 */

public class StringUtils
{
public static ArrayList<Character> UnescapeJavaString(String st) {
 
    StringBuilder sb = new StringBuilder(st.length());
    ArrayList<Character> charList = new ArrayList<Character>();
    String fsb = "";
    Util.logTerminal( "in string =" + st + "=" );
    Util.logTerminal( "in string length =" + st.length() + "=" );
 
    Character ch = ' ';
    
    for (int i = 0; i < st.length(); i++) {
        Util.logTerminal( "at char index =" + i + "=" );

        //char ch = st.charAt(i);
        ch = st.charAt(i);
        if (ch == '\\') {
            Util.logTerminal( "found char \\\\" );
            char nextChar = (i == st.length() - 1) ? '\\' : st
                    .charAt(i + 1);
            // Octal escape?
            if (nextChar >= '0' && nextChar <= '7') {
            Util.logTerminal( "found octal" );
                String code = "" + nextChar;
                i++;
                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                        && st.charAt(i + 1) <= '7') {
                    code += st.charAt(i + 1);
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                    }
                }
                //sb.append((char) Integer.parseInt(code, 8));
                ch = (char) Integer.parseInt(code, 8);
                charList.add( ch );
                //Util.logTerminal( "at octal char index =" + i + "=" );
                continue;
            }
            //Util.logTerminal( "do switch" );

            switch (nextChar) {
            case '\\':
                ch = '\\';
                break;
            case 'b':
                ch = '\b';
                break;
            case 'f':
                ch = '\f';
                break;
            case 'n':
                ch = '\n';
                break;
            case 'r':
                ch = '\r';
                break;
            case 't':
                //Util.logTerminal( "found \\  t" );
                ch = '\t';
                break;
            case '\"':
                ch = '\"';
                break;
            case '\'':
                ch = '\'';
                break;
            // Hex Unicode: u????
            case 'u':
            Util.logTerminal( "found unicode u" );
                if (i >= st.length() - 5) {
                    ch = 'u';
                    break;
                }
                int code = Integer.parseInt(
                        "" + st.charAt(i + 2) + st.charAt(i + 3)
                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
                //sb.append(Character.toChars(code));
                Util.logTerminal( "Character.toChars(code) =" + Character.toChars(code) + "<" );
                ch = Character.toChars(code)[0];
                charList.add( ch );
                i += 5;
                continue;
            }
            Util.logTerminal( "after switch" );
            i++;
        }
        Util.logTerminal( "append char as int >" + (int) ch + "<   index i =" + i);
        Util.logTerminal( "character ch =" + ch + "<" );
        Util.logTerminal( "String.valueOf(ch) =" + String.valueOf(ch) + "<" );
         
        //sb.append( ch );
//        sb.append( String.valueOf(ch) );
        //fsb = String.valueOf(ch);
        charList.add( ch );
    }
    //return sb.toString();
    //return ch;
    return charList;
//    return String.valueOf(ch);
} // end of method

}
