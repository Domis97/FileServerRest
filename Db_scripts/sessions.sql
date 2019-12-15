create table public.sessions
(
	id_session integer not null
		constraint sessions_pk
			primary key,
	place text not null,
	fk_id_user integer
		constraint sessions_fk_id_user_fkey
			references public.users
);

alter table public.sessions owner to postgres;

create unique index sessions_sessionid_uindex
	on public.sessions (id_session);

create unique index unikalnosc
	on public.sessions (place, fk_id_user);

