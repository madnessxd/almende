package alm.motiv.AlmendeMotivator;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by AsterLaptop on 4/5/14.
 */
public class Validation {
    // Regular Expression
    private static final String NUMERIC_REGEX = "^[1-8][0-9]$";
    private static final String NUMERIC_MSG_NO_LIMITATIONS = "^[0-9]*$";
    private static final String CHAR_REGEX = "^[^\n]$";
    private static final String TITLE_REGEX = "^[^\n]{3,30}$";

    // Error Messages
    private static final String REQUIRED_MSG = "This field cannot be empty";
    private static final String NUMERIC_MSG_AGE = "Only numbers are allowed, age between 10 and 89";
    private static final String CHAR_MSG = "Don't use enters";
    private static final String NUMERIC_MSG = "Please enter 2 numbers. Example: 08 hours per week";
    private static final String TITLE_MSG = "Please enter 3 - 30 characters";

    // call this method when you need to check email validation
    public static boolean isNumeric(EditText editText, boolean required) {
        return isValid(editText, NUMERIC_REGEX, NUMERIC_MSG_AGE, required);
    }

    public static boolean isTitle(EditText editText, boolean required) {
        if(editText.getText().toString().contains("\n")){
            editText.setError(CHAR_MSG);
            return false;
        }
        return isValid(editText, TITLE_REGEX, TITLE_MSG, required);
    }

    public static boolean isNumericWithoutLimitations(EditText editText, boolean required) {
        return isValid(editText, NUMERIC_MSG_NO_LIMITATIONS, NUMERIC_MSG, required);
    }

    public static boolean isLetters(EditText editText, boolean required){
        if(editText.getText().toString().contains("\n")){
            editText.setError(CHAR_MSG);
            return false;
        }
        return isValid(editText, CHAR_REGEX, CHAR_MSG, required);
    }
    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            System.out.println(Pattern.matches(regex, text));
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }

}
