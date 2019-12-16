create table public.logs
(
	id_logs integer not null
		constraint logs_pk
			primary key,
	log text not null,
	fk_id_user integer
		constraint sessions_fk_id_user_fkey
			references public.users
);

alter table public.logs owner to postgres;

create unique index logs_logsionid_uindex
	on public.logs (id_logs);

