create or replace function public.select_user(inputusername text) returns boolean
	language plpgsql
as $$
begin

    if(select users.id_user from users where username = inputUserName) is null then
        return false;
        else
        return true;
    end if;
end ;
$$;

alter function public.select_user(text) owner to postgres;

