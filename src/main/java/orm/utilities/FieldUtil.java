package orm.utilities;


import java.lang.reflect.Field;

/**
 * This class provides util methods get info of field of class using reflection.
 */
public class FieldUtil
{
    /**
     * Private constructor to prevent object creation of this class.
     */
    private FieldUtil()
    {

    }

    /**
     * Finds data type of given field of class.
     *
     * @param pField the field
     * @return       the data type of field
     */
    // TODO other data types should be added
    public static String findDataTypeOfField(Field pField)
    {
        String dataTye;

        if(pField.getType() == int.class)
        {
            dataTye = "int";
        }
        else if(pField.getType() == String.class)
        {
            // TODO :  varchar value can be obtained from annotation max value , future work :)
            dataTye = "varchar(50)";
        }
        else if(pField.getType() == Long.class)
        {
            dataTye = "int";
        }
        else
        {
            dataTye = null;
        }

        return dataTye;
    }
}
