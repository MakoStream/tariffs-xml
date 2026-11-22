package ua.lab2.tariffs.xml;

/** Signals a failed XSD validation or XSL transformation. */
public class XmlProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    public XmlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlProcessingException(String message) {
        super(message);
    }
}
