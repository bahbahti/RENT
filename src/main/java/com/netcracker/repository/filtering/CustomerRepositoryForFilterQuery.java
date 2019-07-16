package com.netcracker.repository.filtering;

import com.netcracker.entity.Customer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepositoryForFilterQuery {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Customer> queryFunction(List<Integer> id, List<String> firstName, List<String> lastName, List<String> areaOfLiving,
                                        List<Integer> discount, List<Integer> passportNumber, List<Integer> phoneNumber){
        Integer checkIfIdListIsEmpty = 0;
        Integer checkIfDiscountListIsEmpty = 0;
        Integer checkIfPassportNumberListIsEmpty = 0;
        Integer checkIfPhoneNumberListIsEmpty = 0;
        String checkIfFirstNameListIsEmpty = "";
        String checkIfLastNameListIsEmpty = "";
        String checkIfAreaOfLivingListIsEmpty = "";

        if(firstName.isEmpty()) {
            firstName.add(null);
            checkIfFirstNameListIsEmpty = firstName.iterator().next();
        }
        if(lastName.isEmpty()) {
            lastName.add(null);
            checkIfLastNameListIsEmpty = lastName.iterator().next();
        }
        if(areaOfLiving.isEmpty()) {
            areaOfLiving.add(null);
            checkIfAreaOfLivingListIsEmpty = areaOfLiving.iterator().next();
        }
        if(id == null) {
            id = new ArrayList<>();
            id.add(null);
            checkIfIdListIsEmpty = id.iterator().next();
        }
        if(discount == null) {
            discount = new ArrayList<>();
            discount.add(null);
            checkIfDiscountListIsEmpty = discount.iterator().next();
        }
        if(passportNumber == null) {
            passportNumber = new ArrayList<>();
            passportNumber.add(null);
            checkIfPassportNumberListIsEmpty = passportNumber.iterator().next();
        }
        if(phoneNumber == null) {
            phoneNumber = new ArrayList<>();
            phoneNumber.add(null);
            checkIfPhoneNumberListIsEmpty = phoneNumber.iterator().next();
        }

        StringBuilder nativeSqlString = new StringBuilder("SELECT * FROM customers ");
        nativeSqlString.append("WHERE (:checkIfIdListIsEmpty is null OR id IN :id) "
                + " AND (:checkIfFirstNameListIsEmpty is null OR first_name IN :firstName)"
                + " AND (:checkIfLastNameListIsEmpty is null OR last_name IN :lastName)"
                + " AND (:checkIfAreaOfLivingListIsEmpty is null OR area_of_living IN :areaOfLiving)"
                + " AND (:checkIfDiscountListIsEmpty is null OR discount IN :discount)"
                + " AND (:checkIfPassportNumberListIsEmpty is null OR passport_number IN :passportNumber)"
                + " AND (:checkIfPhoneNumberListIsEmpty is null OR phone_number IN :phoneNumber)");

        Session session = entityManager.unwrap(Session.class);
        Query<Customer> query = session.createNativeQuery(nativeSqlString.toString(), Customer.class);
        query.setParameterList("id", id, IntegerType.INSTANCE);
        query.setParameterList("firstName", firstName, StringType.INSTANCE);
        query.setParameterList("lastName", lastName, StringType.INSTANCE);
        query.setParameterList("areaOfLiving", areaOfLiving, StringType.INSTANCE);
        query.setParameterList("discount", discount, IntegerType.INSTANCE);
        query.setParameterList("passportNumber", passportNumber, IntegerType.INSTANCE);
        query.setParameterList("phoneNumber", phoneNumber, IntegerType.INSTANCE);

        query.setParameter("checkIfFirstNameListIsEmpty", checkIfFirstNameListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfLastNameListIsEmpty", checkIfLastNameListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfAreaOfLivingListIsEmpty", checkIfAreaOfLivingListIsEmpty, StringType.INSTANCE);
        query.setParameter("checkIfIdListIsEmpty", checkIfIdListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfDiscountListIsEmpty", checkIfDiscountListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfPassportNumberListIsEmpty", checkIfPassportNumberListIsEmpty, IntegerType.INSTANCE);
        query.setParameter("checkIfPhoneNumberListIsEmpty", checkIfPhoneNumberListIsEmpty, IntegerType.INSTANCE);

        List<Customer> results =(List<Customer>) query.list();
        return results;
    }
}
