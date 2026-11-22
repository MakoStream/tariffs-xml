package ua.lab2.tariffs.parser;

/** Signals that a tariffs XML document could not be read or parsed. */
public class TariffParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public TariffParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
