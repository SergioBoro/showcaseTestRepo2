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

/**
 {"scale": 4}
 */
f4 real,

f5 real not null default 5.5,
f6 text not null default 'abc',
f7 varchar(8),
f8 datetime default '20130401',
f9 datetime not null default getdate()
--f10 blob default 0xFFAAFFAAFF,
--f11 blob not null default 0xFF
);

create table street (
	name varchar(40) NOT NULL,
    rnum int,	
	id varchar(17) NOT NULL PRIMARY KEY,	
	socr varchar(10) NOT NULL,
	--index varchar(6) NOT NULL,
	gninmb varchar(4) NOT NULL,
	uno varchar(4) NOT NULL,
	ocatd varchar(11) NOT NULL
);

create index ix_street on street (name, id);





create table street4(
/**
 {"width": 270, "caption": "Название"}
 */
	name varchar(40) NOT NULL,
	
    rnum int,	
	id varchar(17) NOT NULL PRIMARY KEY,	
	socr varchar(10) NOT NULL,
	--index varchar(6) NOT NULL,
	gninmb varchar(4) NOT NULL,
	uno varchar(4) NOT NULL,
	ocatd varchar(11) NOT NULL
);

create index ix_street4 on street4 (name, id);



create table test2 (
id int identity not null primary key,
name text not null default 'abc'
);



create table websites (
id int identity not null primary key,
Name varchar(64) NOT NULL,

/**
 {"subtype": "IMAGE"}
 */
Picture varchar(255)  NULL,

/**
 {"subtype": "DOWNLOAD", "linkId": "11"}
 */
File1 varchar(255) NULL,

/**
 {"subtype": "LINK", "width": 150}
 */
Logo varchar(255)  NULL,

/**
 {"subtype": "DOWNLOAD", "linkId": "12"}
 */
File2 varchar(255) NULL,

/**
 {"subtype": "LINK"}
 */
Url varchar(255)  NULL
);
