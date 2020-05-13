package fr.sgo.bcm.flightaggregator.information.service;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TravelInformation;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.partner.PartnerFlightService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;

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

    private LocalDateTime randomTimeIn(LocalDate date, Random random) {
        final int secondsPerDay = 60 * 60 * 24;
        return LocalDateTime.of(date, LocalTime.MIN)
                .plus(random.nextInt(secondsPerDay), ChronoUnit.SECONDS);
    }

    private Random createRandom() {
        final Random random = new Random();
        final long seed = random.nextLong();
        random.setSeed(seed);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Seed for test: " + seed);
        return random;
    }

    @Test
    void listFlightInformation_should_throw_exception_if_dates_impossible() {
        final String cdg = "CDG";
        final String lhr = "LHR";
        final LocalDate departureDate = LocalDate.of(2020, Month.APRIL, 24);
        final LocalDate returnDate = LocalDate.of(2020, Month.APRIL, 22);
        final FlightRequest request = FlightRequest.builder()
                .departureAirport(cdg)
                .arrivalAirport(lhr)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .tripType(TripType.R)
                .build();

        AggregatorService aggregatorService = new AggregatorService(List.of());

        try {
            aggregatorService.listFlightInformation(request);
            Assertions.fail("exception waited: return date should be later than departure date");
        } catch (Exception ignored) {
        }
    }

    @Test
    void listFlightInformation_should_combine_possible_return_flights() {
        final Random random = createRandom();

        final PartnerFlightService service_jazz = Mockito.mock(PartnerFlightService.class);
        final String cdg = "CDG";
        final String lhr = "LHR";
        final LocalDate departureDate = LocalDate.of(2020, Month.APRIL, 16);
        final LocalDate returnDate = LocalDate.of(2020, Month.APRIL, 16);
        final FlightRequest request = FlightRequest.builder()
                .departureAirport(cdg)
                .arrivalAirport(lhr)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .tripType(TripType.R)
                .build();

        Mockito.when(service_jazz.call(eq(cdg), eq(lhr), any()))
                .thenReturn(Stream.generate(() ->
                        FlightInformation.builder().departureAirport(cdg).arrivalAirport(lhr)
                                .arrivalTime(randomTimeIn(departureDate, random))
                                .build())
                        .limit(100));
        Mockito.when(service_jazz.call(anyString(), eq(cdg), any()))
                .thenReturn(Stream.generate(() ->
                        FlightInformation.builder().departureAirport(lhr).arrivalAirport(cdg)
                                .departureTime(randomTimeIn(returnDate, random))
                                .build())
                        .limit(100));
        AggregatorService aggregatorService = new AggregatorService(List.of(service_jazz));

        final List<TravelInformation> result = aggregatorService.listFlightInformation(request);

        // every travel info should have 2 flights
        Assertions.assertThat(result.stream()
                .anyMatch(travelInformation -> travelInformation.getForwardWay() == null && travelInformation.getReturnWay() != null))
                .isFalse();
        // every travel info should have return flight leaving after forward arrived
        Assertions.assertThat(result.stream()
                .anyMatch(travelInformation ->
                        travelInformation.getForwardWay().getArrivalTime().compareTo(travelInformation.getReturnWay().getDepartureTime()) > 0))
                .isFalse();
    }

    @Test
    void listFlightInformation_should_combine_nothing() {
        final PartnerFlightService service_jazz = Mockito.mock(PartnerFlightService.class);
        final String cdg = "CDG";
        final String lhr = "LHR";
        final LocalDate departureDate = LocalDate.of(2020, Month.APRIL, 16);
        final LocalDate returnDate = LocalDate.of(2020, Month.APRIL, 16);
        final FlightRequest request = FlightRequest.builder()
                .departureAirport(cdg)
                .arrivalAirport(lhr)
                .departureDate(departureDate)
                .returnDate(returnDate)
                .tripType(TripType.R)
                .build();

        Mockito.when(service_jazz.call(eq(cdg), eq(lhr), any()))
                .thenReturn(Stream.generate(() ->
                        FlightInformation.builder().departureAirport(cdg).arrivalAirport(lhr)
                                .arrivalTime(LocalDateTime.of(departureDate, LocalTime.MIN).plus(3000, ChronoUnit.SECONDS))
                                .build())
                        .limit(100));
        Mockito.when(service_jazz.call(anyString(), eq(cdg), any()))
                .thenReturn(Stream.generate(() ->
                        FlightInformation.builder().departureAirport(lhr).arrivalAirport(cdg)
                                .departureTime(LocalDateTime.of(departureDate, LocalTime.MIN).plus(1000, ChronoUnit.SECONDS))
                                .build())
                        .limit(100));
        AggregatorService aggregatorService = new AggregatorService(List.of(service_jazz));

        final List<TravelInformation> result = aggregatorService.listFlightInformation(request);

        Assertions.assertThat(result).isEmpty();
    }
}