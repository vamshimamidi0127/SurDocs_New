package gov.dc.surdocs.service;

import gov.dc.surdocs.model.dto.BootstrapResponse;
import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class BootstrapService {

    public BootstrapResponse getBootstrapData() {
        BootstrapResponse response = new BootstrapResponse();
        response.setQueryTypes(Arrays.asList("Square", "Parcel", "Reservation", "Appropriation"));
        return response;
    }
}
