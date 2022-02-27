package fr.bobinho.steams.utils.format;

public class BDurationFormat {

    /**
     * Gets a duration string in seconds format
     *
     * @param durationInSecond the duration in second to format
     * @return the formatted string in seconds format
     */
    public static String getAsSecondFormat(long durationInSecond) {
        return durationInSecond + "s ";
    }

}
