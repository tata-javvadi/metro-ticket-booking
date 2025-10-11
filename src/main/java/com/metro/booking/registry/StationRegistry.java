package com.metro.booking.registry;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class StationRegistry {

    // Hyderabad Metro – Sample Stations (Red, Blue & Green Lines)
    private static final Map<String, String> STATIONS = Map.ofEntries(
            // 🚇 Red Line (Miyapur ↔ LB Nagar)
            Map.entry("ST01", "Miyapur"),
            Map.entry("ST02", "JNTU College"),
            Map.entry("ST03", "KPHB Colony"),
            Map.entry("ST04", "Kukatpally"),
            Map.entry("ST05", "Balanagar"),
            Map.entry("ST06", "Moosapet"),
            Map.entry("ST07", "Bharatnagar"),
            Map.entry("ST08", "Erragadda"),
            Map.entry("ST09", "ESI Hospital"),
            Map.entry("ST10", "S R Nagar"),
            Map.entry("ST11", "Ameerpet"),
            Map.entry("ST12", "Panjagutta"),
            Map.entry("ST13", "Irrum Manzil"),
            Map.entry("ST14", "Khairatabad"),
            Map.entry("ST15", "Lakdi-ka-pul"),
            Map.entry("ST16", "Assembly"),
            Map.entry("ST17", "Nampally"),
            Map.entry("ST18", "Malakpet"),
            Map.entry("ST19", "Dilsukhnagar"),
            Map.entry("ST20", "Chaitanyapuri"),
            Map.entry("ST21", "L B Nagar"),

            // 🚇 Blue Line (Nagole ↔ Raidurg)
            Map.entry("ST22", "Nagole"),
            Map.entry("ST23", "Uppal"),
            Map.entry("ST24", "NGRI"),
            Map.entry("ST25", "Habsiguda"),
            Map.entry("ST26", "Tarnaka"),
            Map.entry("ST27", "Mettuguda"),
            Map.entry("ST28", "Secunderabad East"),
            Map.entry("ST29", "Parade Ground"),  // interchange with Green Line
            Map.entry("ST30", "Paradise"),
            Map.entry("ST31", "Rasoolpura"),
            Map.entry("ST32", "Prakash Nagar"),
            Map.entry("ST33", "Begumpet"),
            Map.entry("ST34", "Ameerpet"), // interchange with Red Line
            Map.entry("ST35", "Madhura Nagar"),
            Map.entry("ST36", "Yusufguda"),
            Map.entry("ST37", "Jubilee Hills Check Post"),
            Map.entry("ST38", "Peddamma Gudi"),
            Map.entry("ST39", "Madhapur"),
            Map.entry("ST40", "Durgam Cheruvu"),
            Map.entry("ST41", "Hitech City"),
            Map.entry("ST42", "Raidurg"),

            // 🚇 Green Line (JBS Parade Ground ↔ MG Bus Station)
            Map.entry("ST43", "JBS Parade Ground"),
            Map.entry("ST44", "Secunderabad West"),
            Map.entry("ST45", "Gandhi Hospital"),
            Map.entry("ST46", "Musheerabad"),
            Map.entry("ST47", "RTC Cross Roads"),
            Map.entry("ST48", "Chikkadpally"),
            Map.entry("ST49", "Narayanguda"),
            Map.entry("ST50", "Sultan Bazaar"),
            Map.entry("ST51", "MG Bus Station") // interchange with Red Line
    );

    public String getStationName(String code) {
        return STATIONS.get(code);
    }

    public Set<String> getAllStationCodes() {
        return STATIONS.keySet();
    }

    public boolean isValidStation(String code) {
        return STATIONS.containsKey(code);
    }
}
