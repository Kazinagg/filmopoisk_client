package org.example.filmopoisk_client.entity.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String email;
    private String password;
    private String username;
}