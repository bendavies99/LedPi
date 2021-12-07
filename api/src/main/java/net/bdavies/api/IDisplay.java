package net.bdavies.api;

/**
 * The interface for a debug display for the application
 *
 * @author ben.davies
 */
public interface IDisplay
{
   /**
    * Init the display and setup all background processes
    */
   void init();

   /**
    * Get the display width
    *
    * @return int
    */
   int getWidth();

   /**
    * Get the display height
    *
    * @return int
    */
   int getHeight();

   /**
    * Get the display position X
    *
    * @return int
    */
   int getPosX();

   /**
    * Get the display position Y
    *
    * @return int
    */
   int getPosY();
}
