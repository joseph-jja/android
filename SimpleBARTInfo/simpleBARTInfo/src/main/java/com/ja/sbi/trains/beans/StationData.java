package com.ja.sbi.trains.beans;

public static class StationData {

        private String stationName;
        private String stationCode;
        private String fare;

        /**
         * @param stationName the stationName to set
         */
        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        /**
         * @return the stationName
         */
        public String getStationName() {
            return stationName;
        }

        /**
         * @param stationCode the stationCode to set
         */
        public void setStationCode(String stationCode) {
            this.stationCode = stationCode;
        }

        /**
         * @return the stationCode
         */
        public String getStationCode() {
            return stationCode;
        }

        /**
         * @param fare the fare to set
         */
        public void setFare(String fare) {
            this.fare = fare;
        }

        /**
         * @return the fare
         */
        public String getFare() {
            return fare;
        }
    }
