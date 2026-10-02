package objects;

import java.util.ArrayList;
import java.util.List;

import enums.SquareType;
import objects.squares.BlankSquare;
import objects.squares.CheckpointSquare;
import objects.squares.EventSquare;
import objects.squares.QuizSquare;
import objects.squares.ResourceSquare;

public class Board {

    private List<BoardSquare> squares;

    public Board()
    {
        this.squares = new ArrayList<>();
        initialiseBoard();
    }

    private void initialiseBoard()
    {
        // create all squares
        //Main path
        squares.add(new BlankSquare(0)); //Branch 2 end
        squares.add(new ResourceSquare(1));
        squares.add(new BlankSquare(2)); //Branch 1
        squares.add(new BlankSquare(3));
        squares.add(new BlankSquare(4));
        squares.add(new ResourceSquare(5));
        squares.add(new BlankSquare(6));
        squares.add(new QuizSquare(7));
        squares.add(new EventSquare(8));
        squares.add(new EventSquare(9));
        squares.add(new BlankSquare(10));
        squares.add(new QuizSquare(11));
        squares.add(new ResourceSquare(12));
        squares.add(new CheckpointSquare(13)); //Branch 1 End
        squares.add(new BlankSquare(14));
        squares.add(new BlankSquare(15));
        squares.add(new QuizSquare(16));
        squares.add(new EventSquare(17));
        squares.add(new BlankSquare(18));
        squares.add(new QuizSquare(19));
        squares.add(new ResourceSquare(20));
        squares.add(new CheckpointSquare(21)); //Branch 3 End
        squares.add(new BlankSquare(22));
        squares.add(new EventSquare(23));
        squares.add(new BlankSquare(24)); //Branch 2
        squares.add(new BlankSquare(25));
        squares.add(new BlankSquare(26));
        squares.add(new ResourceSquare(27));
        squares.add(new EventSquare(28));
        squares.add(new BlankSquare(29));
        squares.add(new CheckpointSquare(30));
        squares.add(new EventSquare(31));
        squares.add(new BlankSquare(32));
        squares.add(new BlankSquare(33));// End of Main path
        
        //Branch 1
        squares.add(new EventSquare(34));
        squares.add(new BlankSquare(35));
        squares.add(new QuizSquare(36));
        squares.add(new BlankSquare(37)); //Branch 3
        squares.add(new ResourceSquare(38));
        squares.add(new EventSquare(39));
        squares.add(new BlankSquare(40));

        //Branch 2
        squares.add(new BlankSquare(41));
        squares.add(new QuizSquare(42));
        squares.add(new EventSquare(43));
        squares.add(new QuizSquare(44));
        squares.add(new ResourceSquare(45));
        squares.add(new EventSquare(46));

        //Branch 3
        squares.add(new ResourceSquare(47));
        squares.add(new QuizSquare(48));
        squares.add(new EventSquare(49));
        squares.add(new EventSquare(50));
        squares.add(new QuizSquare(51));

        //Path connections

        for(int i = 0; i < squares.size(); i++) {
        //Branch 1
            if(i == 3) {
                squares.get(i).setNextSquares(4, 34);
                continue;
            }
            if(i == 40) {
                squares.get(i).setNextSquares(11);
                continue;
            }
        //Branch 2
            if(i == 21) {
                squares.get(i).setNextSquares(22, 41);
                continue;
            }
            if(i == 47) {
                squares.get(i).setNextSquares(33);
                continue;
            }
        //Branch 3
            if(i == 38) {
                squares.get(i).setNextSquares(39, 48);
                continue;
            }
            if(i == 51) {
                squares.get(i).setNextSquares(19);
                continue;
            }
        //Main Path
            if(i == 33) {
                squares.get(i).setNextSquares(0);
            }
            else {
            squares.get(i).setNextSquares(i+1);
            }
        }
    }

    public SquareType getSquareType(int position)
    {
        if (position >= 0 && position < squares.size())
        {
            return squares.get(position).getType();
        }
        return SquareType.BLANK; // blank here in case of  errors xoxo
    }

    public BoardSquare getSquare(int position)
    {
        if (position >= 0 && position < squares.size())
        {
            return squares.get(position);
        }
        return squares.get(0); // start square here in case of errors xoxoxo
    }

    public int getSquareCount()
    {
        return squares.size();
    }
    

}
