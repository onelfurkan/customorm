package orm.exception;

public class EntityOperationException extends Exception
{
    public EntityOperationException(String message)
    {
        super(message);
    }

    public EntityOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
