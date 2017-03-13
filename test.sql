execute new_lease(1,2,'Mr. Guy','332-343-2312','982-232-4212','first guy','293-232-1312','6-Mar-2017','6-Dec-2017');
select * from LeaseAgreement;
execute list_rentals_by_branch(2);
execute avaialble_rentals(2);
execute list_supervisors();
execute list_rentals_by_owner(1);
execute list_rentals_by_owner(2);
execute list_rentals_by_criteria('San Jose, CA', 5, 1800,1900);


