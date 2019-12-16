create or replace procedure public.add_log(inputusername text, inputlog text)
	language plpgsql
as $$
declare
begin
    insert into logs
    values ((select nextval('seq_logs')), inputLog,
            (select id_user from users where users.username = inputUserName));
end ;
$$;

alter procedure public.add_log(text, text) owner to postgres;

