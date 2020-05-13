package fr.sgo.bcm.flightaggregator.information.service;

import fr.sgo.bcm.flightaggregator.information.model.FlightInformation;
import fr.sgo.bcm.flightaggregator.information.model.FlightRequest;
import fr.sgo.bcm.flightaggregator.information.model.TripType;
import fr.sgo.bcm.flightaggregator.partner.PartnerFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<FlightInformation> listFlightInformation(FlightRequest flightRequest) {
        return partnerFlightServices.parallelStream()
                .flatMap(service -> {
                    final Stream<FlightInformation> forwardWay = service.call(
                            flightRequest.getDepartureAirport(),
                            flightRequest.getArrivalAirport(),
                            flightRequest.getDepartureDate());
                    final Supplier<Stream<FlightInformation>> returnWay = !TripType.R.equals(flightRequest.getTripType())
                            ? Stream::empty
                            : () -> service.call(
                            flightRequest.getArrivalAirport(),
                            flightRequest.getDepartureAirport(),
                            flightRequest.getReturnDate());

                    return Stream.concat(forwardWay, returnWay.get());
                })
                .collect(Collectors.toList());
    }
}
