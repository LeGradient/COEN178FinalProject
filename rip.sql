
-- 2)
SELECT
	Property.supervisor_id,
    Employee.name,
    Property.rental_id,
    Property.street,
    Property.city,
    Property.zip
FROM Property JOIN Employee
ON Property.supervisor_id = Employee.emp_id;

-- 3)
SELECT rental_id, street, city, zip
FROM Property
WHERE owner_id = arg_owner;

-- 4)

SELECT rental_id, street, city, zip
FROM Property
WHERE status = 'available'
AND num_rooms = arg_rooms
AND city = arg_city
AND monthly_rent >= arg_rent_min
AND monthly_rent <= arg_rent_max;

-- 5)
SELECT branch_id, COUNT(*) num
FROM Property JOIN Employee
ON Property.supervisor_id = Employee.emp_id
WHERE Property.status = 'available'
GROUP BY branch_id;

-- 6)


-- 7)
SELECT *
FROM LeaseAgreement
WHERE renter_id = arg_renter_id;

-- 8)
SELECT renter_id, name
    FROM Renters
    WHERE renter_id IN (
        SELECT renter_id
        FROM LeaseAgreement
        GROUP BY renter_id
        HAVING COUNT(*) > 1
	);

-- 9)


-- 10)
SELECT lease_id, Property.rental_id, street, city, zip
FROM Property JOIN LeaseAgreement
ON Property.rental_id = LeaseAgreement.rental_id
WHERE MONTHS_BETWEEN(date_end, SYSDATE) <= 2;

