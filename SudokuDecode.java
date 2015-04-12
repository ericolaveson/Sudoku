/*
/******************************************************************************
 * Name            : Eric Olaveson
 * Date            : November 27, 2013
 *
 * This program will fun a java application that will read cnf variables from
 *   a file and decode it into a readable matrix. This matrix represents
 *   the solution to a sudoku puzzle.
 *****************************************************************************/

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;

/* - CLASS -
/******************************************************************************
 * Name    : SudokuDecode
 * Purpose : This class encapsulates methods that will decode a matrix of
 *              CNF variables into a sudoku matrix and output this to a file.
 *****************************************************************************/
public class SudokuDecode
{
   int size;   // CALC - size of sukoku puzzle
   int nWidth; // CALC - width of size variable for sudoku puzzle
   ArrayList<String> myElements; // LIST - holds all CNF variables

   /* - CONSTRUCTOR -
   /***************************************************************************
    * Name       : SudokuDecode
    * Purpose    : Create an instance of SudokuDecode which opens a file to
    *                 write the matriz to.
    * Parameters : file -> file to write to
    **************************************************************************/
   public SudokuDecode(String file)
   {
      // INITIALIZE
      myElements = new ArrayList<String>();

      // TRY - to read file of CNF variables
      try
      {
         readFile(file);
      }
      catch(Exception ex)
      {
         System.out.println("ERROR READING FILE");
      }

      // INITIALIZE
      this.size   = (int)Math.sqrt((double)myElements.size());
      this.nWidth = getWidth(size);
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : readFile
    * Purpose    : This method will read a file of CNF variables and store
    *                 them into a list.
    * Parameters : file -> file to read
    * Return     : void
    **************************************************************************/
   public void readFile(String file) throws Exception
   {
      boolean        skip;   // BOOL - tells to skip variable or not
      char[]         lineAr; // ARRY - holds all sudoku variables
      String         line;   // TMP  - holds line in file being read
      String         tmp;    // TMP  - hold tmp variabl being built
      BufferedReader reader; // READ - reads the file

      // INITIALIZE
      skip   = false;
      line   = null;
      tmp    = new String();
      reader = new BufferedReader(new FileReader(file));
      
      // WHILE - read file line by line
      while((line = reader.readLine()) != null)
      {
         // RE - INITIALIZE
         lineAr = line.toCharArray();

         // FOR - iterates the tmp line of file
         for(int index = 0; index < lineAr.length; ++index)
         {
            if(lineAr[index] == '-')
            {
               skip = true;
            }
            else if(lineAr[index] == ' ')
            {  
               if(!skip)
               {
                  myElements.add(tmp);
               }

               // RE-INITIALIZE
               tmp  = new String();
               skip = false;
            }

            if(!skip && lineAr[index] != ' ')
            {
               tmp += lineAr[index];
            }
         }
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : displayElements
    * Purpose    : This method outputs the sudoku matrix to the file
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void displayElements() throws Exception
   {
      int         tmpIndex;  // CALC - current index
      int         row;       // CALC - current row
      int         col;       // CALC - current column
      int         tmpLength; // TMP  - temporary length of variable
      String[][]  sudoku;    // ARRY - holds all elements of sudoku puzzle
      String      tmp;       // TMP  - temp string to read into
      String      current;   // TMP  - current line to look at
      PrintWriter out;       // OUT  - outputs to file

      // INIIALIZE
      row    = 0;
      col    = 0;
      tmp    = new String();
      sudoku = new String[this.size][this.size];
      out    = new PrintWriter(new FileWriter("nxnsolution"), true);

      // FOR - traverse each CNF variable
      for(int index = 0; index < myElements.size(); ++index)
      {
         // RE-INITIALIZE
         current = myElements.get(index);
         tmp     = current.substring(current.length() - nWidth);
         
         // TRY - parse variable into row/col/elements
         try
         {
            tmpIndex = current.length() - (2 * nWidth);
            col      = Integer.parseInt((current.substring(tmpIndex,
                                         tmpIndex + nWidth)));
            tmpIndex = current.length() - (3 * nWidth);
            row      = Integer.parseInt((current.substring(0,
                                         tmpIndex + nWidth)));
         }
         catch(Exception ex)
         {
            System.out.println("Invalid parse Int");
         }

         // ADD - element to sudoku board
         sudoku[row - 1][col - 1] = tmp;
      }

      // DOUBLE FOR - print all elements of sudoku
      for(int i = 0; i < sudoku.length; ++i)
      {
         for(int j = 0; j < sudoku[i].length; ++j)
         {
            out.write(" " + sudoku[i][j]);
         }

         out.write("\n");
      }

      out.close();
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : getWidth
    * Purpose    : This method finds the width of a number using base 10
    *                 logarithm. Efficient and easy!
    * Parameters : number -> number to find width of
    * Return     : width  -> width of number passed in
    **************************************************************************/
   public int getWidth(int number)
   {
      int width; // CALC - width of number

      // INITIALIZE
      width = 0;

      if(number > 0)
      {
         width = (int)Math.log10(number);
      }
      else if(number < 0)
      {
         width = (int)Math.log10(Math.abs(number));
      }

      return(width + 1);
   }

   /* - MAIN -
    ***************************************************************************
    * Name       : main
    * Purpose    : This method encapulates methods that will allow us to 
    *                 decode a CNF form of a Sudoku solution
    * Parameters : args -> command line parameters if desired
    * Return     : void
    **************************************************************************/
   public static void main(String[] args)
   {
      SudokuDecode decoder = new SudokuDecode("nxnout");

      try
      {
         decoder.displayElements();
      }
      catch(Exception ex)
      {
         System.out.println("Error cannot open file");
      }
   }
}
