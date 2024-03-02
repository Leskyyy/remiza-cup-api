package com.odazie.simpleblog.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table( name = "API_KEY" )
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long apiKeyId;
    private String apiKey;
}
