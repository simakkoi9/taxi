package io.simakkoi9.ridesservice.util;

import java.math.BigDecimal;

public class FareTestDataUtil {
    public static final String PICKUP_ADDRESS = "53.929086, 27.587694";
    public static final String DESTINATION_ADDRESS = "53.933624, 27.652157";
    public static final BigDecimal FARE_START = BigDecimal.valueOf(3);
    public static final BigDecimal FARE_PER_KM = BigDecimal.valueOf(2.5);
    public static final Double DISTANCE = 6.328;
    public static final BigDecimal COST = BigDecimal.valueOf(18.82);
    public static final String OSRM_EXPECTED_PATH = 
        "/route/v1/driving/27.587694,53.929086;27.652157,53.933624?overview=false";



}
