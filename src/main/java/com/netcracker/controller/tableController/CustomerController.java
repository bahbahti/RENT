package com.netcracker.controller.tableController;

import com.netcracker.converter.CustomerMapper;
import com.netcracker.dto.CustomerDTO;
import com.netcracker.entity.Customer;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.repository.CustomerRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customersDTO = customerMapper.toCustomerDTOs(customers);
        return ResponseEntity.ok().body(customersDTO);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        CustomerDTO customerDTO = customerMapper.toCustomerDTO(customer);
        return ResponseEntity.ok().body(customerDTO);
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            DataIntegrityViolationException, HibernateException {
        Customer customerToSave = customerMapper.toCustomer(customerDTOToSave);
        final Customer createdCustomer = customerRepository.save(customerToSave);
        final CustomerDTO createdCustomerDTO = customerMapper.toCustomerDTO(createdCustomer);
        return ResponseEntity.ok().body(createdCustomerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> deleteCustomer(@PathVariable(value = "id") Integer customerId)
            throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        customerRepository.delete(customer);
        return new ResponseEntity<CustomerDTO>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable(value = "id") Integer customerId, @Valid @RequestBody CustomerDTO newCustomerDTO)
            throws ResourceNotFoundException, MethodArgumentNotValidException, HttpMessageNotReadableException,
            HibernateException, DataIntegrityViolationException{
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Customer newCustomer = customerMapper.toCustomer(newCustomerDTO);

        customer.setAreaOfLiving(newCustomer.getAreaOfLiving());
        customer.setDiscount(newCustomer.getDiscount());
        customer.setLastName(newCustomer.getLastName());
        customer.setFirstName(newCustomer.getFirstName());
        customer.setPassportNumber(newCustomer.getPassportNumber());
        customer.setPhoneNumber(newCustomer.getPhoneNumber());
        final Customer updatedCustomer = customerRepository.save(customer);
        final CustomerDTO updatedCustomerDTO = customerMapper.toCustomerDTO(updatedCustomer);
        return ResponseEntity.ok().body(updatedCustomerDTO);
    }

}