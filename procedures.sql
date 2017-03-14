-- 1)
CREATE OR REPLACE PROCEDURE available_rentals(arg_branch IN INTEGER) AS
    var_manager VARCHAR2(30);
    CURSOR cur_rentals IS
        SELECT rental_id, street, city, zip
        FROM Property
        WHERE supervisor_id IN (
            SELECT emp_id
            FROM Employee
            WHERE branch_id = arg_branch
        );
BEGIN
    -- get manager name
    SELECT name
    INTO var_manager
    FROM Employee
    WHERE branch_id = arg_branch
    AND job = 'manager';
    
    -- print branch and manager
    dbms_output.put_line('Branch ID: ' || arg_branch);
    dbms_output.put_line('Manager: ' || var_manager);

    -- print each rental
    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(
            v_rec.rental_id || ' ' ||
            v_rec.street || ' ' ||
            v_rec.city || ' ' ||
            v_rec.zip
        );
    END LOOP;
END available_rentals;
/
show errors;


-- 2)
CREATE OR REPLACE PROCEDURE list_supervisors AS
    CURSOR cur_supervisors IS
        SELECT
            Property.supervisor_id,
            Employee.name,
            Property.rental_id,
            Property.street,
            Property.city,
            Property.zip
        FROM Property JOIN Employee
        ON Property.supervisor_id = Employee.emp_id;
BEGIN
    FOR v_rec IN cur_supervisors
    LOOP
        dbms_output.put_line(
            v_rec.supervisor_id || ' ' ||
            v_rec.name || ' ' ||
            v_rec.rental_id || ' ' ||
            v_rec.street || ' ' ||
            v_rec.city || ' ' ||
            v_rec.zip
        );
    END LOOP;
END list_supervisors;
/
show errors;


-- 3)
CREATE OR REPLACE PROCEDURE list_rentals_by_owner(arg_owner IN INTEGER) AS
    var_ownername VARCHAR2(30);
    CURSOR cur_rentals IS
        SELECT rental_id, street, city, zip
        FROM Property
        WHERE owner_id = arg_owner;
BEGIN
    SELECT name
    INTO var_ownername
    FROM Owner
    WHERE owner_id = arg_owner;

    dbms_output.put_line('Properties owned by ' || var_ownername);

    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(
            v_rec.rental_id || ' ' ||
            v_rec.street || ' ' ||
            v_rec.city || ' ' ||
            v_rec.zip
        );
    END LOOP;
END list_rentals_by_owner;
/
show errors;


-- 4)
CREATE OR REPLACE PROCEDURE list_rentals_by_criteria(
    arg_city IN VARCHAR2,
    arg_rooms IN INTEGER,
    arg_rent_min IN NUMBER,
    arg_rent_max IN NUMBER
) AS
    CURSOR cur_rentals IS
        SELECT rental_id, street, city, zip
        FROM Property
        WHERE status = 'available'
        AND num_rooms = arg_rooms
        AND city = arg_city
        AND monthly_rent >= arg_rent_min
        AND monthly_rent <= arg_rent_max;
BEGIN
    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(
            v_rec.rental_id || ' ' ||
            v_rec.street || ' ' ||
            v_rec.city || ' ' ||
            v_rec.zip
        );
    END LOOP;
END list_rentals_by_criteria;
/
show errors;


-- 5)
CREATE OR REPLACE PROCEDURE list_rentals_per_branch(arg_branch IN INTEGER) AS
    CURSOR cur_rentals IS
        SELECT branch_id, COUNT(*) num
        FROM Property JOIN Employee
        ON Property.supervisor_id = Employee.emp_id
        WHERE Property.status = 'available'
        GROUP BY branch_id;
BEGIN
    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(v_rec.branch_id || ' ' || v_rec.num);
    END LOOP;
END list_rentals_per_branch;
/
show errors;


-- 6)
CREATE OR REPLACE PROCEDURE new_lease(
    arg_lease_id IN INTEGER,
    arg_rental_id IN INTEGER,
    arg_renter_id IN INTEGER,
    arg_friend_name IN VARCHAR2,
    arg_friend_phone IN VARCHAR2,
    arg_date_start IN DATE,
    arg_date_end IN DATE
) AS
    var_renter_name VARCHAR2(30);
    var_phone_work VARCHAR2(12);
    var_phone_home VARCHAR2(12);
    var_status VARCHAR2(9);
    var_deposit NUMBER(6,2);
    var_rent NUMBER(8,2);
BEGIN
    -- make sure that the property is available
    SELECT status
    INTO var_status
    FROM Property
    WHERE rental_id = arg_rental_id;
    
    IF var_status = 'rented' THEN
        RAISE_APPLICATION_ERROR(-20002, 'Invalid argument: property is not available');
    END IF;

    -- fetch renter name and phone numbers
    SELECT name, phone_work, phone_home
    INTO var_renter_name, var_phone_work, var_phone_home
    FROM Renter
    WHERE renter_id = arg_renter_id

    -- fetch monthly rent
    SELECT monthly_rent
    INTO var_deposit
    FROM Property
    WHERE rental_id = arg_rental_id;

    -- calculate total rent
    var_rent := var_deposit * (arg_date_end - arg_date_start) / 30;

    -- create new lease agreement
    INSERT INTO LeaseAgreement VALUES(
        arg_lease_id,
        arg_rental_id,
        arg_renter_id,
        var_renter_name,
        var_phone_work,
        var_phone_home,
        arg_friend_name,
        arg_friend_phone,
        arg_date_start,
        arg_date_end,
        var_rent,
        var_deposit
    );
END new_lease;
/
show errors;


-- 9)
CREATE OR REPLACE PROCEDURE average_rent(arg_city IN VARCHAR2) AS
    var_avgrent_leased NUMBER(8,2);
    var_avgrent_available NUMBER(8,2);
    var_avgrent_all NUMBER(8,2);
BEGIN
    SELECT AVG(rent)
    INTO var_avgrent_leased
    FROM LeaseAgreement
    WHERE rental_id IN (
        SELECT rental_id
        FROM Property
        WHERE status = 'leased'
    );

    SELECT AVG(rent)
    INTO var_avgrent_available
    FROM LeaseAgreement
    WHERE rental_id IN (
        SELECT rental_id
        FROM Property
        WHERE status = 'available'
    );

    var_avgrent_all := (var_avgrent_leased + var_avgrent_available) / 2;

    dbms_output.put_line('Average rent in ' || arg_city);
    dbms_output.put_line('Leased: ' || var_avgrent_leased);
    dbms_output.put_line('Available: ' || var_avgrent_available);
    dbms_output.put_line('All: ' || var_avgrent_all);

END average_rent;
/
show errors;