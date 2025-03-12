package cz.syntaxbro.live.observer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebServerResponse {
    private String type;
    private String message;
    private LocalDateTime timestamp;
    private String clientId;
    private String serverSessionId;
}
