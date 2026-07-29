package org.com.quora_backend.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsernameAvailabilityResponse {

    private String username;

    private boolean available;

}
