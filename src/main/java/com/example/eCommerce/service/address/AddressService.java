package com.example.eCommerce.service.address;

import com.example.eCommerce.DTO.address.AddressDTO;
import com.example.eCommerce.model.User;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO address, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressesById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
