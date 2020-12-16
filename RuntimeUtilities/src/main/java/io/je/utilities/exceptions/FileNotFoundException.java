package io.je.utilities.exceptions;

public class FileNotFoundException extends JEException {

    private static final long serialVersionUID = 3893986508618978394L;

    public FileNotFoundException(String code, String message) {
        super(code, message);
    }

}
