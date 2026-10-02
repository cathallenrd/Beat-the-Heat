package utils;

public class InputValidator {
    
    public static boolean isValidInt(String input, int min, int max)
    {
        if (input == null || input.trim().isEmpty())
        {
            return false;
        }
       
        try
        {
            int value = Integer.parseInt(input.trim());
            return value >= min && value <= max;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static boolean isValidString(String input)
    {
        return input != null && !input.trim().isEmpty();
    }

}
