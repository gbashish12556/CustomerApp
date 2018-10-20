package in.co.theshipper.www.shipper_customer;

public final class Constants {

        public static final class Config{

        public static final String ROOT_PATH = "http://www.theshipper.co.in/loader_mobile/";
        public static final int UPDATE_CUSTOMER_LOCATION_DELAY = 0*10000;
        public static final int UPDATE_CUSTOMER_LOCATION_PERIOD = 30*1000;
        public static final int GET_DRIVER_LOCATION_DELAY = 0*10000;
        public static final int GET_DRIVER_LOCATION_PERIOD = 30*1000;
        public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
        public static final long MIN_TIME_BW_UPDATES = 0;
        public static final long MIN_DATE_DURATION = 1*1000;
        public static final long MAX_DATE_DURATION = 3*24*60*60*1000;
        public static final String SUPPORT_CONTACT = "08276097972";
        public static final int NAME_FIELD_LENGTH = 50;
        public static final int ADDRESS_FIELD_LENGTH = 50;
        public static final int DELAY_LOCATION_CHECK = 0*100;
        public static final int PERIOD_LOCATION_CHECK  = 2*100;
        public static final float MAP_HIGH_ZOOM_LEVEL = 17;
        public static final float MAP_SMALL_ZOOM_LEVEL = 13;
        public static final int IMAGE_WIDTH = 500;
        public static final int IMAGE_HEIGHT = 500;
        public static final String CURRENT_FRAG_TAG = "current_fragment";
        public static final int FLASH_TO_MAIN_DELAY = 3*1000;
        public static final int GPS_INTERVAL = 2*1000;
        public static final int GPS_FASTEST_INTERVAL = 1*1000;
        public static final int PROGRESSBAR_DELAY = 2*1000;
        public static final int BOOK_LATER_DELAY = 15*60*1000;

    }

    public static final class Message{

        public static final String NEW_USER_ENTER_DETAILS = "Please enter your details";
        public static final String NO_CURRENT_BOOKING = "No Current Booking";
        public static final String VEHICLE_ALLOCATION_PENDING = "Vehicle Allocation Pending";
        public static final String NETWORK_ERROR = "Unable to connect to server.Check your Internet Connection";
        public static final String SERVER_ERROR = "Server not responding to request";
        public static final String CONNECTING = "Connecting...";
        public static final String OTP_VERIFICATION_ERROR = "OTP could not be verified";
        public static final String FORM_ERROR = "Form contains error";
        public static final String TRACKING_ERROR = "Error while updating location";
        public static final String NO_SERVICE = "Sorry we do not provide service in your area !!";
        public static final String INVALID_DATETIME = "Form Contains Invalid Date Field";
        public static final String INVALID_ADDRESS = "Form Contains Invalid Address Field(s)";
        public static final String EMPTY_IMAGE = "Item Image Required";

    }

    public static final class Title{

        public static final String NETWORK_ERROR = "NETWORK ERROR";
        public static final String SERVER_ERROR = "SERVER ERROR";
        public static final String OTP_VERIFICATION_ERROR = "VERIFICATION ERROR";
        public static final String NO_SERVICE = "NO SERVICE AVAILABLE";
        public static final String BOOKING_DATETIME = "SELECT BOOKING DATE TIME";

    }

    public static final class Keys{

        public static final String CITY_ID = "city_id";
        public static final String LATER_BOOKING_DATETIME = "later_booking_datetime";
        public static final String MY_CURRENT_ADDRESS = "my_current_address";

    }

}
