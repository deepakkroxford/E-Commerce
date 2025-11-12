package com.example.eCommerce.controller;

import com.example.eCommerce.DTO.address.AddressDTO;
import com.example.eCommerce.model.User;
import com.example.eCommerce.service.address.AddressService;
import com.example.eCommerce.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress( @Valid @RequestBody AddressDTO address) {
        User user = authUtil.loggedInUser();
        AddressDTO addressDTO = addressService.createAddress(address,user);
        return new  ResponseEntity<>(addressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity< List<AddressDTO>> getAddress() {
       List<AddressDTO> addressDTOList = addressService.getAddresses();
        return new  ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressesById(addressId);
        return new  ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOList = addressService.getUserAddresses(user);
        return new  ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId, @RequestBody  AddressDTO addressDTO) {
        AddressDTO updatedAddress = addressService.updateAddress(addressId,addressDTO);
        return new  ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId) {
        String deleteStatus = addressService.deleteAddress(addressId);
        return new  ResponseEntity<>(deleteStatus, HttpStatus.OK);
    }


}
