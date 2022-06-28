package orm.exception;

public class UnsupportedFieldTypeException extends Exception
{
    public UnsupportedFieldTypeException(String message)
    {
        super(message);
    }

    public UnsupportedFieldTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
