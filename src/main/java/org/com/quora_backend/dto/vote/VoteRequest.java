package org.com.quora_backend.dto.vote;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.com.quora_backend.model.VoteType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {

    @NotNull(message = "voteType cannot be null")
    private VoteType voteType;
}
