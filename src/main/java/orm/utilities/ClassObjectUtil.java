package orm.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This util class provides some useful class operation using reflection.
 */
public class ClassObjectUtil
{
    private static final Logger logger = Logger.getLogger(ClassObjectUtil.class.getSimpleName());
    /**
     * Private constructor to prevent object creation of this util class.
     */
    private ClassObjectUtil()
    {

    }

    /**
     * This methods finds starter(main) classes of application.
     * Methods finds defined packages using system class loader,
     * then scans all classes that have main method under the all
     * defined packages.
     *
     * @return the main classes list of application
     */
    public static List<Class<?>> findMainClass()
    {
        List<Class<?>> mainClasses = new ArrayList<>();
        Package[] packages = ClassLoader.getSystemClassLoader().getDefinedPackages();

        for(Package packageInApp : packages)
        {
            Set<Class> classesOfPackage = findAllClassesOfPackage(packageInApp.getName());

            for(Class tClass :  classesOfPackage)
            {
                if(hasClassMainMethod(tClass))
                {
                    mainClasses.add(tClass);
                }
            }
        }

        return mainClasses;
    }

    /**
     * Checks whether given class has main method using reflection.
     *
     * @param pClassObject the class object
     * @return             list of found main classes
     */
    public static boolean hasClassMainMethod(Class pClassObject)
    {
        boolean isMainMethod = false;
        Method[] methods  =  pClassObject.getMethods();

        for (Method method  :  methods)
        {
            int mod =  method.getModifiers();

            if (method.getName().equals("main") && Modifier.isStatic(mod) &&
                    Modifier.isPublic(mod) && !Modifier.isSynchronized(mod))
            {
                isMainMethod = true;
            }
        }

        return isMainMethod;
    }

    /**
     * Finds all classes of given package using system class loader and reflection.
     *
     * @param packageName the package name
     * @return            all classes of given package
     */
    public static Set<Class> findAllClassesOfPackage(String packageName)
    {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    /**
     * Creates Class of given class name and package name.
     *
     * @param className   the class name
     * @param packageName the package name
     * @return            the class object
     */
    public static Class getClass(String className, String packageName)
    {
        Class classObject = null;

        try
        {
            classObject = Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.SEVERE,"ERROR : "+className+" not found !");
        }

        return classObject;
    }
}
