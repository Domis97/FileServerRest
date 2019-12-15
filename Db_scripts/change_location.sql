create or replace procedure public.change_location(inputusername text, inputdirectory text)
	language plpgsql
as $$
begin

    if (select place
        from sessions
        where sessions.fk_id_user = (select users.id_user from users where users.username = inputUserName)) is null then
        insert into sessions
        values ((select nextval('seq_sessions')), inputDirectory,
                (select users.id_user from users where username = inputUserName));
    else
        update sessions
        set place = inputDirectory
        where sessions.fk_id_user = (select users.id_user from users where users.username = inputUserName);
    end if;

end ;
$$;

alter procedure public.change_location(text, text) owner to postgres;

