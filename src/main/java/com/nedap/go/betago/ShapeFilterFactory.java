package com.nedap.go.betago;

import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.exceptions.InvalidBoardException;

import java.util.ArrayList;
import java.util.List;

public class ShapeFilterFactory {
    // X target
    // Y other
    // 0 empty


    public static void main (String[] args) throws InvalidBoardException {
        String testBoard =  "000111000" + // 9 x 9
                            "000111000" +
                            "000101000" +
                            "000111000" +
                            "000000000" +
                            "001110000" +
                            "001010000" +
                            "001010000" +
                            "001110000";

        Board original = new Board(testBoard, null);

        List<ShapeFilter> filters = ShapeFilterFactory.all(9, '1');

        for (ShapeFilter filter: filters) {
            testBoard = filter.filter(testBoard);
        }

        Board filtered = new Board(testBoard, null);

        System.out.println("ORIGINAL: ");
        original.printCurrentState();
        System.out.println("FILTERED");
        filtered.printCurrentState();
    }

    public static List<ShapeFilter> all(int dim, char colour) {
        List<ShapeFilter> filters = new ArrayList<>();

        filters.add(square(dim, colour));
        filters.add(tallSquare(dim, colour));
        filters.add(wideSquare(dim, colour));

        return filters;
    }

    // Killable

    // TODO: https://senseis.xmp.net/?KillingShapes

    // Dead shapes

    /**
     * X X X
     * X . X
     * X X X
     */
    public static ShapeFilter square(int dim, char colour) {
        String expr = ("^(.*)XXX(" + repeat(".", dim - 3)  + ")X0X(" + repeat(".", dim - 3) + ")XXX(.*)$")
                .replaceAll("X", Character.toString(colour));

        String replaceString = "$1XXX$2XXX$3XXX$4";

        return new ShapeFilter(dim, expr, replaceString);
    }

    /**
     * X X X X
     * X . . X
     * X X X X
     */
    public static ShapeFilter wideSquare(int dim, char colour) {
        String expr = ("^.*(XXXX)" + repeat(".", dim - 4)  + "(X0X)" + repeat(".", dim - 4) + "(XXXX).*$").replaceAll("X", Character.toString(colour));

        String replaceString = "$1XXXX$2XXXX$3XXXX$4";

        return new ShapeFilter(dim, expr, replaceString);
    }
    /**
     * X X X
     * X . X
     * X . X
     * X X X
     */
    public static ShapeFilter tallSquare(int dim, char colour) {
        String expr = ("^(.*)XXX(" + repeat(".", dim - 3) + ")X0X(" + repeat(".", dim - 3)
                + ")X0X(" + repeat(".", dim - 3) + ")XXX(.*)$")
                .replaceAll("X", Character.toString(colour));

        String replaceString = "$1XXX$2XXX$3XXX$4XXX$5";

        return new ShapeFilter(dim, expr, replaceString);
    }


    private static String repeat(String token, int times) {
        return token + "{" + (times + 1) + "}"; // add the one to acount for the padding
    }
}
