-- 1)
CREATE OR REPLACE PROCEDURE available_rentals(arg_branch IN INTEGER) AS
DECLARE
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
            v_rec.rental_id || " " ||
            v_rec.street || " " ||
            v_rec.city || " " ||
            v_rec.zip
        );
    END LOOP;
END available_rentals;
/
show errors;


-- 2)
CREATE OR REPLACE PROCEDURE list_supervisors AS
DECLARE
    CURSOR cur_supervisors IS
        SELECT
            Property.supervisor_id,
            Employee.name,
            Property.street,
            Property.city,
            Property.zip
        FROM Property JOIN Employee
        ON Property.supervisor_id = Employee.emp_id;
BEGIN
    FOR v_rec IN cur_supervisors
    LOOP
        dbms_output.put_line(
            v_rec.supervisor_id || " " ||
            v_rec.name || " " ||
            v_rec.street || " " ||
            v_rec.city || " " ||
            v_rec.zip
        );
    END LOOP;
END list_supervisors;
/
show errors;

-- 3)
CREATE OR REPLACE PROCEDURE list_rentals_by_owner(arg_owner IN INTEGER) AS
DECLARE
    var_ownername VARCHAR2(30);
    CURSOR cur_rentals IS
        SELECT rental_id, street, city, zip
        FROM Property
        WHERE owner_id = arg_owner;
BEGIN
    SELECT name
    INTO var_ownername
    FROM Owners
    WHERE owner_id = arg_owner;

    dbms_output.put_line('Properties owned by ' || var_ownername);

    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(
            v_rec.rental_id || " " ||
            v_rec.street || " " ||
            v_rec.city || " " ||
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
DECLARE
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
            v_rec.rental_id || " " ||
            v_rec.street || " " ||
            v_rec.city || " " ||
            v_rec.zip
        );
    END LOOP;
END list_rentals_by_criteria;
/
show errors;

-- 5)
CREATE OR REPLACE PROCEDURE list_rentals_by_branch(arg_branch IN INTEGER) AS
DECLARE
    CURSOR cur_rentals IS
        SELECT branch_id, COUNT(*) num
        FROM Property JOIN Employee
        ON Property.supervisor_id = Employee.emp_id
        GROUP BY branch_id;
BEGIN
    FOR v_rec IN cur_rentals
    LOOP
        dbms_output.put_line(v_rec.branch_id || " " || v_rec.num);
    END LOOP;
END list_rentals_by_branch;