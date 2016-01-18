create grain testgrain version '1.0';

create table test (
id int identity not null primary key,
attrVarchar varchar(2),
/**
 {"caption": "integer field"}
 */
attrInt int default 3,
f1 bit not null,
f2 bit default 'true',
f4 real,
f5 real not null default 5.5,
f6 text not null default 'abc',
f7 varchar(8),
f8 datetime default '20130401',
f9 datetime not null default getdate()
--f10 blob default 0xFFAAFFAAFF,
--f11 blob not null default 0xFF
);
