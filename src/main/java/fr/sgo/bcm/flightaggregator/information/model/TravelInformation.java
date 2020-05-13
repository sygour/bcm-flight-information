package fr.sgo.bcm.flightaggregator.information.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TravelInformation {
    private FlightInformation forwardWay;
    private FlightInformation returnWay;

    public Float getTotalPrice() {
        return returnWay == null
                ? forwardWay.getPrice()
                : forwardWay.getPrice() + returnWay.getPrice();
    }
}
