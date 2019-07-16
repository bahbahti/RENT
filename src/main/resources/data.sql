INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('BMW', 21000, 'Sovetskiy', 'N522UA', 'green');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('AUDI', 2342, 'Nizhegorodskiy','A333AA','white');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('VOLVO', 231, 'Leninskiy', 'O756OO', 'red');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('RENO', 21, 'Nizhegorodskiy', 'M654SK', 'black');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('FERRARI', 342,'Sormovskiy', 'D001RR', 'blue');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('FORD', 876, 'Sormovskiy', 'N123UN', 'green');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('LADA', 211, 'Kanavinsky', 'A634SK', 'black');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('SHELBY', 342,'Sormovskiy', 'L101RR', 'blue');
INSERT INTO cars (name, cost, storage, registration_number, color) VALUES ('KIA', 876, 'Moskovsky', 'Z173UN', 'green');

INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number) VALUES ('Ivan','Ivanov', 'Nizhegorodskiy',15, 321311);
INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number, phone_number) VALUES ('Stanislav','Petrov', 'Sormovskiy',10, 111112, 987112233);
INSERT INTO customers (first_name, last_name, area_of_living, passport_number, phone_number) VALUES ('Gleb', 'Sidorov', 'Sovetskiy', 321312, 987112232);
INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number) VALUES ('Oleg','Malinin', 'Leninskiy',5, 321414);
INSERT INTO customers (first_name, last_name, area_of_living, passport_number, phone_number) VALUES ('Artem', 'Myakishev', 'Nizhegorodskiy', 321315, 987112255);
INSERT INTO customers (first_name, last_name, area_of_living, discount, passport_number) VALUES ('Egor','Erovenko', 'Autozavodskiy',13, 327316);

INSERT INTO orders(start_day, end_day, customer_id, car_id) VALUES ('2018-02-23', '2018-02-23',3,1);
INSERT INTO orders(start_day, end_day, customer_id, car_id) VALUES ('2018-08-12', '2018-08-27',6,2);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-08-21', 2,6);
INSERT INTO orders(start_day, end_day, customer_id, car_id) VALUES ('2018-01-02', '2018-02-03',4,3);
INSERT INTO orders(start_day, end_day, customer_id, car_id) VALUES ('2018-06-14', '2018-07-24', 5,4);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-05-17',5,5);
INSERT INTO orders(start_day, customer_id, car_id) VALUES ('2018-07-25', 5,4);
INSERT INTO orders(start_day, end_day, customer_id, car_id) VALUES ('2018-02-17', '2018-04-19',1,3);
