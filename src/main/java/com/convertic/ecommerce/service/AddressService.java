package com.convertic.ecommerce.service;

import com.convertic.ecommerce.domain.Address;
import com.convertic.ecommerce.web.dto.AddressDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) //define el nivel de acceso al constructor de direccion
public class AddressService {
    public static AddressDto mapToDto(Address address){
        return new AddressDto(
                address.getAddress1(),
                address.getAddress2(),
                address.getCity(),
                address.getPostcode(),
                address.getCountry() );

    }
    public static Address createFromDto(AddressDto addressDto){
        return new Address(
                addressDto.getAddress1(),
                addressDto.getAddress2(),
                addressDto.getCity(),
                addressDto.getPostcode(),
                addressDto.getCountry() );
    }
}
