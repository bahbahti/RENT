package com.netcracker.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@Table(name = "cars")
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrarionNumber;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "storage", nullable = false)
    private String storage;

    @OneToMany(mappedBy = "carId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Order> orders;

    @Transient
    private Boolean isAvailable = true;

    //для возможности изменения заказа с параметром isAnavailable = false
    @Transient
    private Integer orderIdThatIsUnavailable;

    //для сравнения старта нового заказа и окончания последнего заказа
    @Transient
    private Date lastOrderDay;

    @JsonSetter(value = "isAvailable")
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    @JsonSetter(value = "isAvailable")
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @PostLoad
    private void postLoad() {
        //на данную машину заказов нет
        if (orders.isEmpty()) {
            isAvailable = true;
        }

        //на данную машину заказ только один
        else if (orders.size() == 1){
            if (orders.iterator().next().getEndDay() == null) {
                orderIdThatIsUnavailable = orders.iterator().next().getId();
                isAvailable = false;
            }
            else {
                isAvailable = true;
                lastOrderDay = orders.iterator().next().getEndDay();
            }
        }

        //на данную машину заказов много
        else {
            for (int i = 0; i < orders.size() - 1; i++) {
                for (int k = i + 1; k < orders.size(); k++) {
                    if (orders.get(i).getEndDay() == null || orders.get(k).getEndDay() == null) {
                        if(orders.get(i).getEndDay() == null) {
                            orderIdThatIsUnavailable = orders.get(i).getId();
                        }
                        else {
                            orderIdThatIsUnavailable = orders.get(k).getId();
                        }
                        lastOrderDay = null;
                        break;
                    } else if (orders.get(i).getEndDay().after(orders.get(k).getEndDay())) {
                        lastOrderDay = orders.get(i).getEndDay();
                    } else if (orders.get(i).getEndDay().equals(orders.get(k).getEndDay())) {
                        lastOrderDay = orders.get(i).getEndDay();
                    } else {
                        lastOrderDay = orders.get(k).getEndDay();
                    }
                }
            }

            if (lastOrderDay == null) {
                isAvailable = false;
            }
            else {
                isAvailable = true;
            }
        }
    }

}
