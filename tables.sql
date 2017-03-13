DROP TABLE LeaseAgreement;
DROP TABLE Property;
DROP TABLE Employee;
DROP TABLE Owner;
DROP TABLE Branch;

CREATE TABLE Branch (
    branch_id INTEGER,
    phone VARCHAR(12),
    street VARCHAR(30),
    city VARCHAR(30),
    zip INTEGER
);
  
  
CREATE TABLE Owner (
    owner_id INTEGER PRIMARY KEY,
    name VARCHAR(30),
    phone VARCHAR(12),
    street VARCHAR(30),
    city VARCHAR(30),
    zip INTEGER
);


CREATE TABLE Employee (
    emp_id INTEGER PRIMARY KEY,
    branch_id INTEGER REFERENCES Branch(branch_id),
    name VARCHAR(30),
    phone VARCHAR(12),
    job VARCHAR(10) CHECK (job IN ('manager','supervisor')),
    start_date DATE
);


CREATE TABLE Property (
    rental_id INTEGER PRIMARY KEY,
    owner_id INTEGER REFERENCES Owner(owner_id),
    branch_id INTEGER REFERENCES Branch(branch_id),
    num_rooms INTEGER,
    monthly_rent NUMBER(6,2),
    status VARCHAR(9) CHECK (status IN ('available','leased')),
    street VARCHAR(30),
    city VARCHAR(30),
    zip INTEGER
);


CREATE TABLE LeaseAgreement (
    lease_id INTEGER PRIMARY KEY,
    rental_id INTEGER REFERENCES Property(rental_id),
    renter_name VARCHAR(30),
    phone_work VARCHAR(12),
    phone_home VARCHAR(12),
    friend_name VARCHAR(30),
    friend_phone VARCHAR(12),
    date_start DATE,
    date_end DATE,
    monthly_rent NUMBER(6,2),
    deposit NUMBER(6,2),
    emp_id INTEGER REFERENCES Employee(emp_id)
);
