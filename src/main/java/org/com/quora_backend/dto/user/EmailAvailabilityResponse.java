package org.com.quora_backend.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailAvailabilityResponse {

    private String email;

    private boolean available;

}
