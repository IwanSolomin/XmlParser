package ru.oiteb.xmlparser.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductData {
    private Integer indexNum;
    private String unit;
    private String okpd2;
    private String nkmi;
    private String manufacturer;
    private String country;
    private String certNumber;
    private String fullName;
    private String tradeMark;
}



