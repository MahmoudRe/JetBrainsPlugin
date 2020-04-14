class ExampleClassFourMethodsK {
    /**
     * Empty method with camel case naming
     */
    fun thisMethodIsCamelCase() {}

    /**
     * method with a simple return statement
     * @return always returns true.
     */
    fun singleReturnStatement(): Boolean {
        return true
    }

    /**
     * Method with multiple return statements.
     * @param value Holds a simple boolean value to switch the return value.
     * @return simple return value to test different return statements.
     */
    fun multipleReturnStatement(value: Boolean): Boolean {
        return value
    }

    /**
     * Overload for the multipleReturnStatement() method. Adds a second parameter required to switch the return statement.
     * @param value Holds a simple boolean value to switch the return value.
     * @param secondParam Holds a simple boolean value to switch the return value.
     * @return simple return value to test different return statements.
     */
    fun multipleReturnStatementOverloading(value: Boolean, secondParam: Boolean): Boolean {
        return value && secondParam
    }
}