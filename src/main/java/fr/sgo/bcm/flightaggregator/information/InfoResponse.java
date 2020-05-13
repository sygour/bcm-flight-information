package fr.sgo.bcm.flightaggregator.information;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class InfoResponse {
    private HttpStatus status;
    private InfoRequest request;
}
