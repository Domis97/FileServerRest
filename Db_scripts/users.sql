create table public.users
(
	id_user integer not null
		constraint user_pk
			primary key,
	username text
);

alter table public.users owner to postgres;

create unique index user_id_user_uindex
	on public.users (id_user);

