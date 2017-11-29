package utils;

import java.util.regex.Pattern;

public class RMIStringUtils {
    public static final Pattern RMI_URL_PATTERN = Pattern.compile("^\\/\\/([^:]+):([0-9]+)\\/(.*)$");
}
