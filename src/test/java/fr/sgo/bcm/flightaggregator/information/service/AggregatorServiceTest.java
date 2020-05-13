package fr.sgo.bcm.flightaggregator.information.service;

import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.partner.PartnerFlightService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

class AggregatorServiceTest {
    @Test
    void listFlightInformation_should_call_multiple_services_once_for_one_way() {
        PartnerFlightService service_jazz = Mockito.mock(PartnerFlightService.class);
        PartnerFlightService service_moon = Mockito.mock(PartnerFlightService.class);
        PartnerFlightService service_else = Mockito.mock(PartnerFlightService.class);

        AggregatorService aggregatorService = new AggregatorService(List.of(service_jazz, service_moon, service_else));

        FlightRequest request = FlightRequest.builder()
                .departureAirport("CDG")
                .arrivalAirport("LHR")
                .departureDate(LocalDate.of(2020, Month.APRIL, 16))
                .tripType(TripType.OW)
                .build();

        aggregatorService.listFlightInformation(request);

        Mockito.verify(service_jazz, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
        Mockito.verify(service_moon, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
        Mockito.verify(service_else, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
    }

    @Test
    void listFlightInformation_should_call_multiple_services_twice_for_return() {
        PartnerFlightService service_jazz = Mockito.mock(PartnerFlightService.class);
        PartnerFlightService service_moon = Mockito.mock(PartnerFlightService.class);
        PartnerFlightService service_else = Mockito.mock(PartnerFlightService.class);

        AggregatorService aggregatorService = new AggregatorService(List.of(service_jazz, service_moon, service_else));

        FlightRequest request = FlightRequest.builder()
                .departureAirport("CDG")
                .arrivalAirport("LHR")
                .departureDate(LocalDate.of(2020, Month.APRIL, 16))
                .returnDate(LocalDate.of(2020, Month.APRIL, 24))
                .tripType(TripType.R)
                .build();

        aggregatorService.listFlightInformation(request);

        Mockito.verify(service_jazz, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
        Mockito.verify(service_jazz, Mockito.times(1)).call(request.getArrivalAirport(), request.getDepartureAirport(), request.getReturnDate());
        Mockito.verify(service_moon, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
        Mockito.verify(service_moon, Mockito.times(1)).call(request.getArrivalAirport(), request.getDepartureAirport(), request.getReturnDate());
        Mockito.verify(service_else, Mockito.times(1)).call(request.getDepartureAirport(), request.getArrivalAirport(), request.getDepartureDate());
        Mockito.verify(service_else, Mockito.times(1)).call(request.getArrivalAirport(), request.getDepartureAirport(), request.getReturnDate());
    }

}