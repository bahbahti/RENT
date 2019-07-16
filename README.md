Car rent service
================

	Database consists of 3 tables:
1. cars
2. customers
3. orders


Table "cars":
--------------

Column | Type | Nullable | Type of key | Constraints 
--- | --- | --- | --- | ---
id | integer |  NO | primary key | unique
name | character varying |  NO |  - |  -
cost | integer |  NO |  - |  -
storage | character varying |  NO |  - |  -
registration_number | character varying | NO | - |  unique
color | character varying | NO | - |  -



Table "customers":
--------------

Column | Type | Nullable | Type of key | Constraints 
--- | --- | --- | --- | ---
id | integer |  NO |  primary key |  unique 
first_name | character varying |  NO |  composite key |  unique 
last_name | character varying |  NO |  composite key |  unique 
area_of_living | character varying |  NO |  - |  - 
discount | integer |  YES |  - |  - 
passport_number | integer |  NO |  - |  unique 
phone_number | integer |  YES |  - |  - 



Table "orders":
--------------

Column | Type | Nullable | Type of key | Constraints 
--- | --- | --- | --- | ---
id | integer |  NO |  primary key |  unique 
start_day | date |  NO |  - |  - 
end_day | date |  YES |  - |  - 
car_id | integer |  NO |  foreign key ( car_id(FK) -> cars.id(PK) ) |  - 
customer_id | integer | NO | foreign key ( customer_id(FK) -> customers.id(PK) ) |  - 
