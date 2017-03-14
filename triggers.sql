-- a)
CREATE OR REPLACE TRIGGER manager_branch
    BEFORE INSERT ON Employee
    FOR EACH ROW

DECLARE
    CURSOR cur_employees IS 
    SELECT branch_id
    FROM Employee
    WHERE job = 'manager';

BEGIN
    IF :new.job = 'manager' THEN
        FOR v_rec IN cur_employees
        LOOP
            IF v_rec.branch_id = :new.branch_id THEN
                 RAISE_APPLICATION_ERROR(-20000, 'Invalid argument: branch already has a manager');
            END IF;
        END LOOP;
    END IF;
END;
/
show errors;


-- b)
CREATE OR REPLACE TRIGGER check_rentaltime
    BEFORE INSERT ON LeaseAgreement
    FOR EACH ROW
BEGIN 
    IF ((:new.date_end) - :new.date_start)/30 < 6 OR ((:new.date_end) - :new.date_start)/30 > 12 THEN
        raise_application_error(-20001, 'Invalid argument: rental time must be between 6 and 12 months"');
    END IF;
    DBMS_OUTPUT.PUT_LINE(:new.date_end - :new.date_start);
    DMMS_OUTPUT.PUT_LINE((:new.date_end - :new.date_start)/30);
    IF (:new.date_end - :new.date_start)/30 = 6 THEN
        :new.rent := (:new.rent * 1.1);
    END IF;
END;
/
show errors;


-- c)
CREATE OR REPLACE TRIGGER new_lease
    AFTER INSERT ON LeaseAgreement
    FOR EACH ROW
BEGIN
    UPDATE Property
    SET status = 'leased'
    WHERE rental_id = :new.rental_id;
END;
/
show errors;

-- d)
CREATE OR REPLACE TRIGGER remove_rental
    AFTER DElETE ON Property
    FOR EACH ROW
BEGIN
    DELETE FROM LeaseAgreement
    WHERE :old.rental_id = LeaseAgreement.rental_id;
END;
/
show errors;

-- e)
CREATE OR REPLACE TRIGGER rent_increase
    AFTER INSERT ON LeaseAgreement
    FOR EACH ROW
BEGIN
    UPDATE Property
    SET monthly_rent = monthly_rent * 1.1
    WHERE rental_id = :new.rental_id;
END;
/
show errors;