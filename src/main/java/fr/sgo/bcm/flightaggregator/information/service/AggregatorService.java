package fr.sgo.bcm.flightaggregator.information.service;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TravelInformation;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.partner.PartnerFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AggregatorService {
    private final List<PartnerFlightService> partnerFlightServices;

    @Autowired
    public AggregatorService(List<PartnerFlightService> partnerFlightServices) {
        this.partnerFlightServices = partnerFlightServices;
    }

    public Map<Float, List<TravelInformation>> listFlightInformation(FlightRequest flightRequest) {
        if (TripType.R.equals(flightRequest.getTripType())
                && (flightRequest.getDepartureDate().compareTo(flightRequest.getReturnDate()) > 0)) {
            throw new IllegalArgumentException("Return date must be later than departure date");
        }
        return partnerFlightServices.parallelStream()
                .flatMap(service -> {
                    final Stream<FlightInformation> forwardWays = service.call(
                            flightRequest.getDepartureAirport(),
                            flightRequest.getArrivalAirport(),
                            flightRequest.getDepartureDate());
                    final Supplier<Stream<FlightInformation>> returnWaysSupplier = !TripType.R.equals(flightRequest.getTripType())
                            ? Stream::empty
                            : () -> service.call(
                            flightRequest.getArrivalAirport(),
                            flightRequest.getDepartureAirport(),
                            flightRequest.getReturnDate());

                    if (TripType.R.equals(flightRequest.getTripType())) {
                        return combineMatchingFlights(forwardWays, returnWaysSupplier.get());
                    } else {
                        return forwardWays.map(flight -> TravelInformation.builder().forwardWay(flight).build());
                    }
                })
                .collect(Collectors.toMap(
                        TravelInformation::getTotalPrice,
                        Arrays::asList,
                        (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())));
    }

    private Stream<TravelInformation> combineMatchingFlights(Stream<FlightInformation> forwardWays, Stream<FlightInformation> returnWays) {
        final List<FlightInformation> sortedReturns = returnWays.sorted(Comparator.comparing(FlightInformation::getDepartureTime))
                .collect(Collectors.toList());

        return forwardWays.flatMap(forward ->
                sortedReturns.stream()
                        .dropWhile(returnWay -> forward.getArrivalTime().compareTo(returnWay.getDepartureTime()) >= 0)
                        .map(returnWay -> TravelInformation.builder().forwardWay(forward).returnWay(returnWay).build()));
    }
}
