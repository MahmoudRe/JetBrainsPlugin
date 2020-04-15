package exampleClasses;

/**
 * This example class can be used for testing the plugin
 */
public class ExampleClassJava {
    /**
     * Empty method with camel case naming
     */
    public void thisMethodIsCamelCase() {

    }

    /**
     * method with a simple return statement
     * @return always returns true.
     */
    public boolean singleReturnStatement() {
        return true;
    }

    /**
     * Method with multiple return statements.
     * @param value Holds a simple boolean value to switch the return value.
     * @return simple return value to test different return statements.
     */
    public boolean multipleReturnStatement(boolean value) {
        return value;
    }

    /**
     * Overload for the multipleReturnStatement() method. Adds a second parameter required to switch the return statement.
     * @param value Holds a simple boolean value to switch the return value.
     * @param secondParam Holds a simple boolean value to switch the return value.
     * @return simple return value to test different return statements.
     */
    public boolean multipleReturnStatementOverloading(boolean value, boolean secondParam) {
        return value && secondParam;
    }

    /**
     * Dummy method to test the Cyclomatic Complexity.
     * The CC of this method should be 5.
     * @param a dummy param
     * @param b dummy param
     * @return not important
     */
    public int cyclomaticComplexityIs5(int a, int b) {
        int cc = 5;

        if (a == b) {
            if (b == cc)
                return cc;
            else
                return b;
        } else {
            if(a == cc) {
                return a;
            } else {
                if (b == cc)
                    return cc;
                else
                    return a;
            }
        }
    }

    /**
     * Dummy method to test the Cyclomatic Complexity.
     * The CC of this method should be 7.
     * @param a dummy param
     * @param b dummy param
     * @return not important
     */
    public int cyclomaticComplexityIs7(int a, int b) {
        int cc = 7;

        if (a == b) {
            if (b == cc)
                return cc;
            else if (b == 5)
                return b;
            else
                return 5;
        } else {
            if(a == cc) {
                return a;
            } else {
                if (b == cc)
                    return cc;
            }
        }

        if(a == 5)
            return a;

        return 5;
    }

    /**
     * Dummy method to test the Cyclomatic Complexity
     * with {for} and {while} loops as Decision Points.
     * The CC of this method should be 7.
     * @param a dummy param
     * @param b dummy param
     * @return not important
     */
    public int cyclomaticComplexityTest(int a, int b) {
        int cc = 7;

        if (a == b) {                           //--> 1

            while (a > b)                       //--> 2
                for(int i = 0; a > b; i++)      //--> 3
                    if (a > Integer.MIN_VALUE)  //--> 4
                        a--;


        } else if (a == 5) {                    //--> 5

            if(a != cc) {                       //--> 6
                return a;
            } else {

                for(int i = 0; i < b; i++) {    //--> 7
                    if(i != 0) {                //--> 8
                        b--;
                    }

                    while (i < 0)               //--> 9
                        i++;
                }
            }
        }

        if (b == cc) {                          //--> 10
            return a;
        }


        return 5;
    }

    public int loopMethodWithoutDescription(int numb) {
        // This methods features a while-loop and is missing JavaDoc.
        int result = 0;
        int i = 0;
        while (i < numb) {
            result +=1;
            i++;
        }
        return result;
    }
}


