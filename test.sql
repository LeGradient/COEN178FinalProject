-- select * from LeaseAgreement;
-- execute list_rentals_by_branch(2);
-- execute avaialble_rentals(2);
-- execute list_supervisors();
-- execute list_rentals_by_owner(1);
-- execute list_rentals_by_owner(2);
-- execute list_rentals_by_criteria('San Jose, CA', 5, 1800,1900);
select * from property;
execute new_lease(1,2,'Mr. Guy','332-343-2312','982-232-4212','first guy','293-232-1312','6-Mar-2017','6-Apr-2017');
execute new_lease(1,2,'Mr. Guy','332-343-2312','982-232-4212','first guy','293-232-1312','6-Mar-2017','6-Apr-2018');
execute new_lease(1,2,'Mr. Guy','332-343-2312','982-232-4212','first guy','293-232-1312','6-Mar-2017','6-Sep-2017');
select * from LeaseAgreement;
execute new_lease(2,3,'Hey','332-311-2312','982-232-4212','first guy','293-232-1312','6-Mar-2017','6-Apr-2017');
select * from LeaseAgreement;
select * from Property;
execute new_lease(3,3,'Hey','332-344-2312','982-232-4212','first guy','293-232-1312','6-Apr-2017','6-Nov-2017');
select * from LeaseAgreement;
delete from Property where rental_id = 2;
select * from Property;
select * from LeaseAgreement;
