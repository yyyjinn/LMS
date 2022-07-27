delete from department;
delete from student where s_id=22001001;
delete from personal where p_id='p2001001';
delete from ssubject where sub_name ='범죄와인권';
delete from post;
delete from application;
delete from application where s_id=22001001 and sub_code='S0036';

select * from application;
select * from ssubject;
select * from department;
select * from student;
select * from personal;
select * from post;
select * from notice;
select * from exam1;
select * from exam2;
select * from answer;
select s_name, s_id from student where s_regNumber1=200101 and s_regNumber2=1111111;
select * from student where s_id=22001001;
select * from application;
select * from lecture;

select * from ssubject where sub_name in(select sub_name from application where s_id=22001001);
select count(*) from application where sub_code='S0011' and s_id=22001001;

update student set s_max=0 where s_id=22001001;
update ssubject set sub_max=0 where sub_name='범죄와인권';
update ssubject set sub_max=30 where sub_name='범죄와인권';
update ssubject set sub_classtime='11,12,13' where sub_name='범죄와인권';
