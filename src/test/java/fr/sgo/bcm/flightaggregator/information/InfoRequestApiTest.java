package fr.sgo.bcm.flightaggregator.information;

import fr.sgo.bcm.flightaggregator.information.api.InfoRequestApi;
import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.information.service.AggregatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(InfoRequestApi.class)
class InfoRequestApiTest {
    @Autowired
    protected MockMvc mockMvc;
    @MockBean
    protected AggregatorService aggregatorService;

    @Test
    void should_accept_with_full_params() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&return_date=2020-12-25&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_error_when_missing_param_departure_airport() throws Exception {
        String url = "/api/flights?arrival_airport=LHR&departure_date=2020-12-15&return_date=2020-12-25&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_error_when_missing_param_arrival_airport() throws Exception {
        String url = "/api/flights?departure_airport=CDG&departure_date=2020-12-15&return_date=2020-12-25&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_error_when_missing_param_departure_date() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&return_date=2020-12-25&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_error_when_missing_param_tripType() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&return_date=2020-12-25";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_accept_one_way_with_no_return_date() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&tripType=OW";
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_error_for_return_with_no_return_date() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_call_aggregator_service_with_input() throws Exception {
        String url = "/api/flights?departure_airport=CDG&arrival_airport=LHR&departure_date=2020-12-15&return_date=2020-12-25&tripType=R";
        mockMvc.perform(get(url))
                .andExpect(status().isOk());
        Mockito.verify(aggregatorService, Mockito.times(1)).listFlightInformation(
                FlightRequest.builder()
                        .departureAirport("CDG")
                        .arrivalAirport("LHR")
                        .departureDate(LocalDate.of(2020, 12, 15))
                        .returnDate(LocalDate.of(2020, 12, 25))
                        .tripType(TripType.R)
                        .build()
        );
    }
}