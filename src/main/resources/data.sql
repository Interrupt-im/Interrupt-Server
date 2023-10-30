-- job
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


-- skill
insert into skill_group(name, created_at, modified_at)
values ('언어', now(), now());

insert into skill(name, skill_group_id, created_at, modified_at)
values ('Java', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Kotlin', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('JavaScript', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('TypeScript', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Python', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('C', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('C++', 1, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('C#', 1, now(), now());

insert into skill_group(name, created_at, modified_at)
values ('프레임워크', now(), now());

insert into skill(name, skill_group_id, created_at, modified_at)
values ('Spring', 2, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Nest', 2, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Next', 2, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('React', 2, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Vue', 2, now(), now());
insert into skill(name, skill_group_id, created_at, modified_at)
values ('Angular', 2, now(), now());