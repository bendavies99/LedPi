package net.bdavies.app;

import java.lang.reflect.InvocationTargetException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.IApplication;
import net.bdavies.api.IDisplay;


/**
 * Display factory for making debug displays
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class DisplayFactory
{
    /**
     * Make a debug display
     *
     * @param application The application instance
     */
    public static void makeDisplay(IApplication application) {
        try
        {
            @SuppressWarnings("unchecked")
            Class<? extends IDisplay> displayClass =
                (Class<? extends IDisplay>) Class.forName("net.bdavies.display.NativeWindow");
            IDisplay display = displayClass.getConstructor(IApplication.class)
                .newInstance(application);
            display.init();
        }
        catch (ClassNotFoundException e)
        {
            log.error("Could not find net.bdavies.display.NativeWindow on your classpath please make sure the " +
                "library is available on the classpath", e);
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e)
        {
            log.error(e.getMessage(), e);
        }

    }
}
