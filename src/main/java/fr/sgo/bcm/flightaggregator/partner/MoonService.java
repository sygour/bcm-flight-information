package fr.sgo.bcm.flightaggregator.partner;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class MoonService implements PartnerFlightService {

    private final String urlTemplate;
    private RestTemplate restTemplate;

    @Autowired
    public MoonService() {
        this.restTemplate = new RestTemplate();
        urlTemplate = "https://flights.beta.bcmenergy.fr/moon/flights?departure_airport=%s&arrival_airport=%s&departure_date=%s";
    }

    @Override
    public Stream<FlightInformation> call(String departureAirport, String arrivalAirport, LocalDate departureDate) {
        final String url = String.format(urlTemplate, departureAirport, arrivalAirport, departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        final MoonOutputData[] results = restTemplate.getForObject(url, MoonOutputData[].class);

        return results == null
                ? Stream.empty()
                : Arrays.stream(results).map(MoonOutputData::convert);
    }
}
