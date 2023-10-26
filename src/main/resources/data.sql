insert into job_group(name, created_at, modified_at)
values ('개발', now(), now());

insert into job(name, job_group_id, created_at, modified_at)
values ('프론트엔드 개발자', 1, now(), now());
insert into job(name, job_group_id, created_at, modified_at)
values ('백엔드 개발자', 1, now(), now());

insert into job_group(name, created_at, modified_at)
values ('게임 개발', now(), now());

insert into job(name, job_group_id, created_at, modified_at)
values ('게임 서버 개발자', 2, now(), now());
insert into job(name, job_group_id, created_at, modified_at)
values ('게임 클라이언트 개발자', 2, now(), now());
insert into job(name, job_group_id, created_at, modified_at)
values ('모바일 게임 개발자', 2, now(), now());
