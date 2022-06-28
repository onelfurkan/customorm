package orm.utilities;


import java.util.concurrent.atomic.AtomicLong;

/**
 * Id generator util class to generate unique id for insert operation.
 * This id is used as primary key in insert operation.
 * This class is used only for this example orm framework.
 * An enhanced id generator cna be used instead of this class.
 */
public class IdGenerator
{
    private static final AtomicLong id =  new AtomicLong(0);

    /**
     * Private constructor to prevent object creation of thşs class.
     */
    private IdGenerator()
    {

    }

    /**
     * Generates atomic id.
     *
     * @return the id
     */
    public static long  getId()
    {
        return id.incrementAndGet();
    }
}
