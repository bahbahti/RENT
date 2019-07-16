package com.netcracker.controller.tableController;

import com.netcracker.converter.RepairOrderMapper;
import com.netcracker.dto.RepairOrderDTO;
import com.netcracker.entity.Car;
import com.netcracker.entity.Customer;
import com.netcracker.entity.Order;
import com.netcracker.entity.RepairOrder;
import com.netcracker.entity.repairStatusEnum.RepairStatus;
import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.pojoServices.pojoForSecondService.HttpDtoToAcceptRepair;
import com.netcracker.pojoServices.pojoForSecondService.HttpDtoToFinishRepair;
import com.netcracker.repository.CarRepository;
import com.netcracker.repository.CustomerRepository;
import com.netcracker.repository.OrderRepository;
import com.netcracker.repository.RepairOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class RepairOrderController {

    @Autowired
    RepairOrderRepository repairOrderRepository;

    @Autowired
    RepairOrderMapper repairOrderMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders headers;

    private final static String URL_FOR_SERVICE_2 = "http://192.168.99.100:7777/secondService";

    @GetMapping("/repairOrders")
    public ResponseEntity<List<RepairOrderDTO>> getAllRepairOrders() {
        List<RepairOrder> repairOrders = repairOrderRepository.findAll();
        List<RepairOrderDTO> repairOrdersDTO = repairOrderMapper.toRepairOrderDTOs(repairOrders);
        return ResponseEntity.ok().body(repairOrdersDTO);
    }

    @GetMapping("/repairOrders/{id}")
    public ResponseEntity<RepairOrderDTO> getRepairOrderById(@PathVariable(value = "id") Integer repairOrderId)
            throws ResourceNotFoundException {
        RepairOrder repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + repairOrderId));
        RepairOrderDTO repairOrderDTO = repairOrderMapper.toRepairOrderDTO(repairOrder);
        return ResponseEntity.ok().body(repairOrderDTO);
    }

    @PostMapping("/repairOrders")
    public ResponseEntity<RepairOrderDTO> createRepairOrder(@Valid @RequestBody RepairOrderDTO repairOrderDTOToSave)
            throws MethodArgumentNotValidException, HttpMessageNotReadableException,
            BadRequestException, ResourceNotFoundException {
        if(repairOrderDTOToSave.getId() != null && repairOrderRepository.findById(repairOrderDTOToSave.getId()).isPresent()) {
            throw new BadRequestException("Such id already exists");
        }
        //сохраняем во внутреннюю БД
        RepairOrder repairOrderToSave = repairOrderMapper.toRepairOrder(repairOrderDTOToSave);
        checkIfThisCarAlreadyInRepair(repairOrderToSave);
        checkIfThisCarHasUnfinishedRentOrder(repairOrderToSave);
        final RepairOrder createdRepairOrder = repairOrderRepository.save(repairOrderToSave);

        //создание DTO и отправка на второй сервис
        HttpDtoToAcceptRepair postDTOFromService1 = HttpDtoToAcceptRepair.create(createdRepairOrder.getId(), createdRepairOrder.getStartRepairDay());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpDtoToAcceptRepair> request = new HttpEntity<>(postDTOFromService1, headers);
        restTemplate.exchange(URL_FOR_SERVICE_2, HttpMethod.POST, request, HttpDtoToAcceptRepair.class);

        final RepairOrderDTO createdRepairOrderDTO = repairOrderMapper.toRepairOrderDTO(createdRepairOrder);
        return ResponseEntity.ok().body(createdRepairOrderDTO);
    }

    @PutMapping("/repairOrders/{id}/changeStatus")
    public ResponseEntity<RepairOrderDTO> changeStatusOfRepairOrder(@PathVariable(value = "id") Integer orderRepairId, @Valid @RequestBody HttpDtoToAcceptRepair httpDtoFromService2)
            throws ResourceNotFoundException, ResourceAccessException {
        RepairOrder repairOrder = repairOrderRepository.findById(orderRepairId).orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + orderRepairId));
        repairOrder.setRepairIdExternal(httpDtoFromService2.getRowId());
        repairOrder.setEndRepairDay(httpDtoFromService2.getDay());
        repairOrder.setRepairStatus(RepairStatus.IN_PROGRESS);
        final RepairOrder updatedRepairOrder = repairOrderRepository.save(repairOrder);
        final RepairOrderDTO updatedrepairOrderDTO = repairOrderMapper.toRepairOrderDTO(repairOrder);
        return ResponseEntity.ok().body(updatedrepairOrderDTO);
    }

    @PutMapping("/repairOrders/{id}/finished")
    public ResponseEntity<RepairOrderDTO> finishStatusOfRepairOrder(@PathVariable(value = "id") Integer orderRepairId, @Valid @RequestBody HttpDtoToFinishRepair httpDtoFromService2)
            throws ResourceNotFoundException {
        RepairOrder repairOrder = repairOrderRepository.findById(orderRepairId).orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + orderRepairId));
        if(httpDtoFromService2.getPrice() != null) {
            repairOrder.setPrice(httpDtoFromService2.getPrice());
            repairOrder.setRepairStatus(RepairStatus.FINISHED);
        }
        final RepairOrder updatedRepairOrder = repairOrderRepository.save(repairOrder);
        final RepairOrderDTO updatedrepairOrderDTO = repairOrderMapper.toRepairOrderDTO(repairOrder);
        return ResponseEntity.ok().body(updatedrepairOrderDTO);
    }

    @PutMapping("/repairOrders/{id}/update")
    public ResponseEntity<RepairOrderDTO> updateStatusOfRepairOrder(@PathVariable(value = "id") Integer orderRepairId)
            throws ResourceNotFoundException, BadRequestException {
        RepairOrder repairOrder = repairOrderRepository.findById(orderRepairId).orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + orderRepairId));
        if(repairOrder.getRepairIdExternal() == null) {
            throw new BadRequestException("Server 2 have not accepted this repair order yet");
        }

        //получение DTO из второго сервиса
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HttpDtoToFinishRepair> entity = new HttpEntity<>(headers);
        ResponseEntity<HttpDtoToFinishRepair> httpDtoFromService2 = restTemplate.exchange(URL_FOR_SERVICE_2 + "/" + repairOrder.getRepairIdExternal(), HttpMethod.GET, entity, HttpDtoToFinishRepair.class);
        if(httpDtoFromService2.getBody().getPrice() != null) {
            repairOrder.setPrice(httpDtoFromService2.getBody().getPrice());
            repairOrder.setRepairStatus(RepairStatus.FINISHED);
        }
        final RepairOrder updatedRepairOrder = repairOrderRepository.save(repairOrder);
        final RepairOrderDTO updatedrepairOrderDTO = repairOrderMapper.toRepairOrderDTO(repairOrder);
        return ResponseEntity.ok().body(updatedrepairOrderDTO);
    }



    public void checkIfThisCarAlreadyInRepair(RepairOrder repairOrderToSave) throws BadRequestException, ResourceNotFoundException {
        //проверка на то, что машина уже сломалась и в сервисе
        Car car = carRepository.findById(repairOrderToSave.getCarId()).orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + repairOrderToSave.getCarId()));
        if(repairOrderToSave.getCustomerId() != null) {
            Customer customer = customerRepository.findById(repairOrderToSave.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + repairOrderToSave.getCustomerId()));
        }
        if (!car.getRepairOrders().isEmpty()) {
            for (RepairOrder repairOrder : car.getRepairOrders()) {
                if(!repairOrder.getRepairStatus().equals(RepairStatus.FINISHED)) {
                    throw new BadRequestException("This car is already in repair");
                }
            }
        }
    }

    public void checkIfThisCarHasUnfinishedRentOrder(RepairOrder repairOrderToSave) throws BadRequestException, ResourceNotFoundException {
        Car car = carRepository.findById(repairOrderToSave.getCarId()).orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + repairOrderToSave.getCarId()));
        //если заказ на аренду этой машины существует, то мы заканчиваем заказ датой поломки
        if(!car.getOrders().isEmpty() && car.getOrderIdThatIsUnavailable() != null) {
            Order order = orderRepository.findById(car.getOrderIdThatIsUnavailable()).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + car.getOrderIdThatIsUnavailable()));
            if(repairOrderToSave.getStartRepairDay().before(order.getStartDay())) {
                throw new BadRequestException("Start day of repair cannot be earlier then start day of rent: " + new SimpleDateFormat("dd.MM.yyyy").format(order.getStartDay()));
            }
            order.setEndDay(repairOrderToSave.getStartRepairDay());
            repairOrderToSave.setCustomerId(order.getCustomerId());
        }
    }

}
