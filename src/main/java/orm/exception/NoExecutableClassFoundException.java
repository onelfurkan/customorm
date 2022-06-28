package orm.exception;

public class NoExecutableClassFoundException extends Exception
{
    public NoExecutableClassFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public NoExecutableClassFoundException(String message)
    {
        super(message);
    }
}
