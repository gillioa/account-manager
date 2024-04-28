insert into ACCOUNT select * from (
    select '12345678', 1000000 union
    select '88888888', 1000000
) x where not exists(select * from ACCOUNT);