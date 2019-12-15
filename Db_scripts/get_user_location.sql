create or replace function public.get_user_location(inputusername text) returns text
	language plpgsql
as $$
declare
    elo text;
begin

    select sessions.place as elko into elo
    from sessions
             join users on sessions.fk_id_user = users.id_user
    where users.userName = inputUserName;
    return elo;
end ;
$$;

alter function public.get_user_location(text) owner to postgres;

