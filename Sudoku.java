/*
/******************************************************************************
 * Name            : Eric Olaveson
 * Date            : November 27, 2013
 *
 * This program will encode the logic for a sudoku solver into conjunctive
 *    normal form (CNF) boolean statements. These will then be used as an
 *    input file for the minisat solver.
 *****************************************************************************/
//derp
import java.io.*;
import java.util.ArrayList;

/* - CLASS -
/******************************************************************************
 * Name    : Sudoku
 * Purpose : This class encapsulates methods that will be able to encode a
 *           CNF formatted boolean representation of a sudoku solution. Any
 *           size of sudoku puzzle will be able to be solved by this class,
 *           using the technique of representing the sudoku puzzle as
 *           comunctive normal form boolean functions.
 *****************************************************************************/
public class Sudoku
{
   /* -CONSTANTS
   /***************************************************************************
    * N_BY_N_SIZE : size of each side of sudoku puzzle
    **************************************************************************/
   public static final int N_BY_N_SIZE = 9;

   /* -VARIABLES- */
   private int         nWidth;    // CALC - width of nth term
   private int         totalCNF;  // CALC - total number of cnf lines
   private int         size;      // CALC - size of sudoku puzzle
   private int         blockSize; // CALC - size of each sudoku block
   private PrintWriter out;       // WRIT - write to a file
   private ArrayList<String> myCNF; // LISt - holds all lines to print

   /* - CONSTRUCTOR -
   /***************************************************************************
    * Name       : Sudoku
    * Purpose    : Create an instance of sudoku solver to use on any size 
    *                 puzzle.
    * Parameters : size -> size of the sudoku puzzle
    **************************************************************************/
   public Sudoku(int size)
   {
      // INITIALIZE
      this.size      = size;
      this.nWidth    = getWidth(size);
      this.blockSize = (int)Math.sqrt(size);
      this.totalCNF  = 0;
      this.myCNF     = new ArrayList<String>();

      // TRY - open file to write to
      try
      {
         this.out = new PrintWriter(new FileWriter("nxnin"), true);
      }
      catch(Exception EX)
      {
         System.out.println("~~~~~~~~~~ ERROR WRITING TO FILE ~~~~~~~~~~");
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : addCNF
    * Purpose    : This method adds a line to the list of cnf lines
    * Parameters : tmp -> cnf line to add to list
    * Return     : void
    **************************************************************************/
   public void addCNF(String tmp)
   {
      if(tmp.charAt(0) != 'c')
      {
         ++this.totalCNF;
      }

      this.myCNF.add(tmp);
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : buff
    * Purpose    : buffers this number with 0's
    * Parameters : tmp -> cnf line to add to list
    * Return     : void
    **************************************************************************/
   public String buff(int buffMe)
   {
      int    diff;  // CALC - difference in widths
      int    width; // CALC - width of number to buff
      String pad;   // STRN - zeros to pad with

      // INITIALIZE
      width = getWidth(buffMe);
      diff  = nWidth - width;
      pad   = new String();

      // FOR - pads amnt of zero's to number
      for(int index = 0; index < diff; ++index)
      {
         pad += "0";
      }

      return (pad + buffMe);
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : getWidth
    * Purpose    : returns the width of the number
    * Parameters : number -> number to find the width of
    * Return     : width  -> width of number
    **************************************************************************/
   public int getWidth(int number)
   {
      int width; // CALC - width of the number

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

   /* - METHOD -
    ***************************************************************************
    * Name       : closeFile
    * Purpose    : This method closes the file being written to.
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void closeFile()
   {
      out.close();
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : block
    * Purpose    : This method traverses each block in the sudoku puzzle
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void block()
   {
      int startX; // CALC - starting x coordinate entry
      int startY; // CALC - starting y coordinate entry

      // INITIALIZE
      startX = 1;
      startY = 1;

      // TRY - checks each block and writes data to file
      try
      {
         // FOR - traverses each x coord. block
         for(int xBlock = 1; xBlock <= blockSize; ++xBlock)
         {
            // FOR - traverses each y coord block
            for(int yBlock = 1; yBlock <= blockSize; ++yBlock)
            {
               // FOR - traverses each element of each entry
               for(int z = 1; z <= size; ++z)
               {
                  // CALL checkBlock - which checks each entry of block for
                  //                   uniqueness
                  checkBlock(xBlock,yBlock,z);
               }
            }          
         }
      }
      catch(Exception ex)
      {
         System.out.println("~~~~~~~~~~ ERROR WRITING TO FILE ~~~~~~~~~~");
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : checkBlock
    * Purpose    : This method checks each individual block for unique entries
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void checkBlock(int rBlock, int cBlock, int element)
   {
      /* -VARIABLES- */
      int    startR;  // CALC - start row to check
      int    startC;  // CALC - start column to check
      int    row;     // CALC - current row checking
      int    col;     // CALC - current column checking
      String tmpLine; // TEMP - line to concat. to send to CNF list
      ArrayList<String> blockElements; // LIST - all CNF in block

      // INITIALIZE
      startR  = 1 + ((rBlock - 1) * blockSize);
      startC  = 1 + ((cBlock - 1) * blockSize);
      row     = startR;
      col     = startC;
      tmpLine = "";
      blockElements = new ArrayList<String>();
     
      this.addCNF("c check for unique entry in block <"
                  + buff(cBlock) + "," + buff(rBlock) + "> for \'"
                  + buff(element) + "\'\n");

      // WHILE - traverse each element of block
      while(row < startR + blockSize)
      {
         tmpLine += " " + buff(row) + ""
                    + buff(col) + "" + buff(element);
         blockElements.add(buff(row) + "" + buff(col)
                           + "" + buff(element));

         // IF - starts column to beginning of block
         if((col + 1) == startC + blockSize)
         {
            col = startC;
            ++row;
         }
         else
         {
            ++col;
         }
      }

      addCNF(tmpLine + " 0\n\n");

      // INITIALIZE - element to start at 1
      int index = 0;

      // 2nd TO-DO - ensure each square has only 1 value
      for(int block = (size - 1); block > 0; --block)
      {
         // CALC - increment current element checker
         int tmp = index + 1;

         // FOR - checks that there are only 1 element at each tile
         for(int iter = block; iter > 0; --iter)
         {
            String first  = "-" + blockElements.get(index);
            String second = "-" + blockElements.get(tmp);
            addCNF(first + " " + second + " 0\n");

            // INCREMENT - to next element to check against
            ++tmp;
         }

         // INCREMENT - next level of equality checking
         ++index;
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : column
    * Purpose    : This method checks each column for unique entries
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void column()
   {
      String tmpLine; // CALC - tmp line to add to CNF list
 
      // TRY - to write to a file column unique-ness cnf equation lines
      try
      {
         // FOR - traverses each column for unique-ness
         for(int col = 1; col <= size; ++col)
         {
            // FOR - check that each element occures only once in the column
            for(int z = 1; z <= size; ++z)
            {
               addCNF("c check for unique entries in column " + buff(col)
                    + " for \'" + buff(z) + "\'\n");

               // RE-INITIALIZE
               tmpLine = new String();

               // FOR - set column to hold 'z' in any row
               for(int element = 1; element <= size; ++element)
               {
                  tmpLine += (" " + buff(element) + ""
                             + buff(col) + "" + buff(z));
               }

               addCNF(tmpLine + " " + 0 + "\n\n");
               
               // INITIALIZE - element to start at 1
               int x = 1;

               // 2nd TO-DO - ensure each square has only 1 value
               for(int block = (size - 1); block > 0; --block)
               {
                  // CALC - increment current element checker
                  int tmpX = x + 1;

                  // FOR - checks that there are only 1 element at each tile
                  for(int iter = block; iter > 0; --iter)
                  {
                     String first  = "-" + buff(x)    + ""
                                     + buff(col) + "" + buff(z);
                     String second = "-" + buff(tmpX) + ""
                                     + buff(col) + "" + buff(z);
                     addCNF(first + " " + second + " 0\n");

                     // INCREMENT - to next element to check against
                     ++tmpX;
                  }

                  // INCREMENT - next level of equality checking
                  ++x;
               }
            }
         }
      }
      catch(Exception ex)
      {
         System.out.println("~~~~~~~~~~ ERROR WRITING TO FILE ~~~~~~~~~~");
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : row
    * Purpose    : This method checks that there are no similar elements on
    *                 each individual row.
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void row()
   {
      String tmpLine;
      
      // TRY - to write to a file the row unique-ness cnf equation lines
      try
      {
         // FOR - traverse each row for uniqueness
         for(int row = 1; row <= size; ++row)
         {
            // FOR - check that each element only ocurrs once each row
            for(int z = 1; z <= size; ++z)
            {
               addCNF("c check for unique entries in row " + buff(row)
                    + " for \'" + buff(z) + "\'\n");

               // RE-INITIALIZE
               tmpLine = new String();

               // FOR - set row able to contain 'z' in any column
               for(int element = 1; element <= size; ++element)
               {
                  tmpLine += (" " + buff(row) + "" 
                             + buff(element) + "" + buff(z));
               }

               addCNF(tmpLine + " " + 0 + "\n\n");

               // INITIALIZE - element to start at 1
               int y = 1;

               // 2nd TO-DO - ensure each square has only 1 value
               for(int block = (size - 1); block > 0; --block)
               {
                  // CALC - increment current element checker
                  int tmpY = y + 1;

                  // FOR - checks that there are only 1 element at each tile
                  for(int iter = block; iter > 0; --iter)
                  {
                     String first  = "-" + buff(row) + ""
                                     + buff(y) + "" + buff(z);
                     String second = "-" + buff(row) + "" 
                                     + buff(tmpY) + "" + buff(z);

                     addCNF(first + " " + second + " 0\n");

                     // INCREMENT - to next element to check against
                     ++tmpY;
                  }

                  // INCREMENT - next level of equality checking
                  ++y;
               }
            }
         }
         
      }
      catch(Exception Ex)
      {
         System.out.println("~~~~~~~~~~ ERROR WRITING TO FILE ~~~~~~~~~~");
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : unique
    * Purpose    : This method checks that there is only one value for each
    *                 tile of the total number (n squared) of tiles.
    * Parameters : none
    * Return     : void
    **************************************************************************/
   public void unique()
   {
      String tmpLine; // TEMP - tmp line to add to CNF list

      // TRY - to write to a file the unique CNF equation
      try
      {
         // DOUBLE FOR - These 2 outter loops check that each tile of the
         //              sudoku puzzle are a single value, unique to themselves
         for(int x = 1; x <= size; ++x)
         {
            for(int y = 1; y <= size; ++y)
            {
               addCNF("c check for unique entry at <" 
                      + buff(x) + "," + buff(y)  + ">\n");

               // INITIALIZE
               tmpLine = new String();

               // 1st TO-DO - set tile's domain to 1 - 9 possibility
               for(int z = 1; z <= size; ++z)
               {
                  tmpLine += (" " + buff(x) + "" + buff(y) + "" + buff(z));
               }

               addCNF(tmpLine + " " + 0 + "\n\n");

               // INITIALIZE - element to start at 1
               int z = 1;

               // 2nd TO-DO - ensure each square has only 1 value
               for(int block = (size - 1); block > 0; --block)
               {
                  // CALC - increment current element checker
                  int tmpZ = z +1;

                  // FOR - checks that there are only 1 element at each tile
                  for(int iter = block; iter > 0; --iter)
                  {
                     String first  = "-" + buff(x) + "" + buff(y) + "" + buff(z);
                     String second = "-" + buff(x) + "" + buff(y) + "" + buff(tmpZ);
                     
                     addCNF(first + " " + second + " 0\n");

                     // INCREMENT - to next element to check against
                     ++tmpZ;
                  }

                  // INCREMENT - next level of equality checking
                  ++z;
               }
            }
         }
      }
      catch(Exception EX)
      {
         System.out.println("~~~~~~~~~~ ERROR WRITING TO FILE ~~~~~~~~~~");
      }
   }

   /* - METHOD -
    ***************************************************************************
    * Name       : printCNF
    * Purpose    : This method will print all of the CNF lines to the file
    * Parameters : none
    * Return     : void  
    **************************************************************************/
   public void printCNF()
   {
      out.write("p cnf " + size + "" + size
                + "" + size + " " + this.totalCNF + "\n");

      for(int index = 0; index < myCNF.size(); ++index)
      {
         out.write(myCNF.get(index));
         
      }
   }

   /* - MAIN -
    ***************************************************************************
    * Name       : main 
    * Purpose    : This method will call methods of Sudoku to solve a sudoku
    *                 puzzle of n x n size.
    * Parameters : args -> command line parameters (if desired)
    * Return     : void  
    **************************************************************************/
   public static void main(String[] args)
   {
      Sudoku mySudoku = new Sudoku(N_BY_N_SIZE);

      mySudoku.unique();
      mySudoku.row();
      mySudoku.column();
      mySudoku.block();
      mySudoku.printCNF();
      mySudoku.closeFile();
   }
}
