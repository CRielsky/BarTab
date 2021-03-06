package rielc.bartab;
/**
 * Class to hold all constants needed
 * for the project.
 */
public final class Constants
{
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "rielc.bartab";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String SEARCH_DIST_URL = "http://69.204.137.215:3000/api/Locations/byDistance?";
    public static final String SEARCH_RATE_URL = "http://69.204.137.215:3000/api/Locations/byRating?";
    public static final String SEARCH_WT_URL = "http://69.204.137.215:3000/api/Locations/byWait?";
    public static final String POST_REVIEW_URL = "http://69.204.137.215:3000/api/Reviews";
}
