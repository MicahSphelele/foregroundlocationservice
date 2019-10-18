package sphelele.getuserlocation.apputils;

public class CommonData {

    public enum AppEnum{
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        ADDRESS("address"),
        LOCATION_BROADCAST_START_ACTION("LocationBroadcastStart"),
        LOCATION_BROADCAST_STOP_ACTION("LocationBroadcastStop"),
        LOCATION_BROADCAST_DONE_ACTION("LocationBroadcastDone");


        private final String text;


        AppEnum(final String text) {
            this.text = text;
        }


        @SuppressWarnings("NullableProblems")
        @Override
        public String toString() {
            return text;
        }

    }
}
