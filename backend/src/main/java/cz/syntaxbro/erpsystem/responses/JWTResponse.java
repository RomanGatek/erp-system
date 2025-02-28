package cz.syntaxbro.erpsystem.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JWTResponse {
    private String accessToken;
    private String refreshToken;
}
