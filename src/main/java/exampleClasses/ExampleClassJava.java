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
