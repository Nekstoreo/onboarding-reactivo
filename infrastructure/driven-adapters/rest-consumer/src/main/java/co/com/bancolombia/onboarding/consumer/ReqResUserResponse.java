package co.com.bancolombia.onboarding.consumer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqResUserResponse {
    private ReqResUserData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReqResUserData {
        private String id;
        private String email;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        private String avatar;
    }
}
