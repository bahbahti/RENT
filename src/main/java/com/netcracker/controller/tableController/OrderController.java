package com.netcracker.controller.tableController;

import com.netcracker.converter.OrderMapper;
import com.netcracker.dto.OrderDTO;
import com.netcracker.entity.Car;
import com.netcracker.entity.Customer;
import com.netcracker.entity.Order;
import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.repository.CarRepository;
import com.netcracker.repository.CustomerRepository;
import com.netcracker.repository.OrderRepository;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderMapper orderMapper;

    boolean isUpdateMethod;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> ordersDTO = orderMapper.toOrderDTOs(orders);
        return ResponseEntity.ok().body(ordersDTO);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable(value = "id") Integer orderId)
            throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        OrderDTO orderDTO = orderMapper.toOrderDTO(order);
        return ResponseEntity.ok().body(orderDTO);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            BadRequestException, ResourceNotFoundException {
        isUpdateMethod = false;
        if(orderDTOToSave.getId() != null && orderRepository.findById(orderDTOToSave.getId()).isPresent()) {
            throw new BadRequestException("Such id already exists");
        }
        Order orderToSave = orderMapper.toOrder(orderDTOToSave);
        checkAvailabilityOfCar(orderToSave, isUpdateMethod);
        final Order createdOrder = orderRepository.save(orderToSave);
        final OrderDTO createdOrderDTO = orderMapper.toOrderDTO(createdOrder);
        return ResponseEntity.ok().body(createdOrderDTO);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> deleteOrder(@PathVariable(value = "id") Integer orderId)
            throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        orderRepository.delete(order);
        return new ResponseEntity<OrderDTO>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable(value = "id") Integer orderId, @Valid @RequestBody OrderDTO newOrderDTO)
            throws ResourceNotFoundException, MethodArgumentNotValidException, HttpMessageNotReadableException,
            HibernateException, DataIntegrityViolationException, BadRequestException {
        isUpdateMethod = true;
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        Order newOrder = orderMapper.toOrder(newOrderDTO);
        newOrder.setId(orderId);
        checkAvailabilityOfCar(newOrder, isUpdateMethod);
        order.setStartDay(newOrder.getStartDay());
        order.setCarId(newOrder.getCarId());
        order.setCustomerId(newOrder.getCustomerId());
        order.setEndDay(newOrder.getEndDay());
        final Order updatedOrder = orderRepository.save(order);
        final OrderDTO updatedOrderDTO = orderMapper.toOrderDTO(updatedOrder);
        return ResponseEntity.ok().body(updatedOrderDTO);
    }

    public void checkAvailabilityOfCar(Order orderToSave, boolean updateMethod) throws BadRequestException, ResourceNotFoundException {
        Car car = carRepository.findById(orderToSave.getCarId()).orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + orderToSave.getCarId()));
        Customer customer = customerRepository.findById(orderToSave.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderToSave.getCustomerId()));
        if (!updateMethod) {
            if (!car.getIsAvailable()) {
                throw new BadRequestException("Car is unavailable");
            } else if (car.getLastOrderDay() != null) {
                if (orderToSave.getStartDay().before(car.getLastOrderDay()) || orderToSave.getStartDay().equals(car.getLastOrderDay())) {
                    throw new BadRequestException("You can rent this car only after " + new SimpleDateFormat("dd.MM.yyyy").format(car.getLastOrderDay()));
                }
            }
        } else {
            if (!car.getIsAvailable() && !(orderToSave.getId().equals(car.getOrderIdThatIsUnavailable()))) {
                throw new BadRequestException("Car is unavailable");
            } else if (car.getLastOrderDay() != null) {
                if (orderToSave.getStartDay().before(car.getLastOrderDay()) || orderToSave.getStartDay().equals(car.getLastOrderDay())) {
                    throw new BadRequestException("You can rent this car only after " + new SimpleDateFormat("dd.MM.yyyy").format(car.getLastOrderDay()));
                }
            }
        }
    }

}