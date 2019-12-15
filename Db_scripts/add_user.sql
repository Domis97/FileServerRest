create or replace procedure public.add_user(inputusername text)
	language plpgsql
as $$
declare
begin
    insert into users values ((select nextval('seq_userid')), inputUserName);
    insert into sessions
    values ((select nextval('seq_sessions')), '\',
            (select id_user from users where users.username = inputUserName));
end ;
$$;

alter procedure public.add_user(text) owner to postgres;

