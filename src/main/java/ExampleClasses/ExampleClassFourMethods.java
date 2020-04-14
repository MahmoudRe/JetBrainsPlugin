package ExampleClasses;

public class ExampleClassFourMethods {
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
        if (value) {
            return true;
        } else  {
            return false;
        }
    }

    /**
     * Overload for the multipleReturnStatement() method. Adds a second parameter required to switch the return statement.
     * @param value Holds a simple boolean value to switch the return value.
     * @param secondParam Holds a simple boolean value to switch the return value.
     * @return simple return value to test different return statements.
     */
    public boolean multipleReturnStatementOverloading(boolean value, boolean secondParam) {
        if (value && secondParam) {
            return true;
        } else  {
            return false;
        }
    }

    /**
     * Dummy method to test the Cyclomatic Complexity
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

}
