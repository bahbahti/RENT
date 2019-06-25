package com.netcracker.controller.tableController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.converter.CarMapper;
import com.netcracker.dto.CarDTO;
import com.netcracker.entity.Car;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.repository.CarRepository;
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
public class CarController {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    ObjectMapper includeTransientObjectMapper;

    @GetMapping("/cars")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<Car> cars = carRepository.findAll();
        List<CarDTO> carsDTO= carMapper.toCarDTOs(cars);
        return ResponseEntity.ok().body(carsDTO);
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        CarDTO carDTO = carMapper.toCarDTO(car);
        return ResponseEntity.ok().body(carDTO);
    }

    @PostMapping("/cars")
    public ResponseEntity<CarDTO> createCar(@Valid @RequestBody CarDTO carDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            DataIntegrityViolationException, HibernateException {
        Car carToSave = carMapper.toCar(carDTOToSave);
        final Car createdCar = carRepository.save(carToSave);
        final CarDTO createdCarDTO = carMapper.toCarDTO(createdCar);
        return ResponseEntity.ok().body(createdCarDTO);
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<CarDTO> deleteCar(@PathVariable(value = "id") Integer carId)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        carRepository.delete(car);
        return new ResponseEntity<CarDTO>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable(value = "id") Integer carId, @Valid @RequestBody CarDTO newCarDTO)
            throws ResourceNotFoundException, MethodArgumentNotValidException, HttpMessageNotReadableException,
            HibernateException, DataIntegrityViolationException{
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + carId));
        Car newCar = carMapper.toCar(newCarDTO);

        car.setCost(newCar.getCost());
        car.setName(newCar.getName());
        car.setStorage(newCar.getStorage());
        car.setRegistrarionNumber(newCar.getRegistrarionNumber());
        car.setColor(newCar.getColor());
        final Car updatedCar = carRepository.save(car);
        final CarDTO updaredCarDTO = carMapper.toCarDTO(updatedCar);
        return ResponseEntity.ok().body(updaredCarDTO);
    }

}
