create grain dirU version '1.0';

create table dirTypes (
id int not null primary key,  
name nvarchar(30) not null
);

create table viewTypes (
id int not null primary key,  
name nvarchar(30) not null
);

create table folders (
id int not null primary key,  
name nvarchar(30) not null,
parentId int foreign key references dirU.folders(id)
);

create table directions (
id int not null primary key, 
grain nvarchar(20) not null,
name nvarchar(255) not null,
prefix nvarchar(100),
tableName nvarchar(100) not null,
dirTypeId int foreign key references dirU.dirTypes(id),
folderId int foreign key references dirU.folders(id),
viewTypeId int foreign key references dirU.viewTypes(id)--,
--viewDescription image 
);

create table fieldsTypes (
id int not null primary key,  
name nvarchar(30),
celestaType nvarchar(30),
defLength int,
defPrecision int,
useInKey bit,
useInSelector bit,
useInFilter bit
);

create table fields (
id int not null primary key,
dirId int foreign key references dirU.directions(id),
name nvarchar(100),
prefix nvarchar(100),
dbFieldName nvarchar(100) not null,
fieldTypeId int foreign key references dirU.fieldsTypes(id),
length int,
precision int,
fieldOrderInGrid int,
isRequired bit,
visualLength int,
refKeyId int, --foreign key references dirU.keys(id),
--selectList image,
minValue nvarchar(100),
maxValue nvarchar(100),
fieldOrderInSort int,
sortOrder bit
);

create table refFields (
id int not null primary key,
fieldId int foreign key references dirU.fields(id),
visualName nvarchar(100),
refFieldId int foreign key references dirU.fields(id)
);

create table keys (
id int not null primary key,
dirId int foreign key references dirU.directions(id),
name nvarchar(100),
type nvarchar(10),
useInImport bit
);

create table keyFields (
id int not null primary key,
keyId int foreign key references dirU.keys(id),
fieldId int foreign key references dirU.fields(id),
fieldOrder int,
sortOrder bit
);

create table filtersConditions (
id int not null primary key,
name nvarchar(100),
prefix nvarchar(100),
pythonCond nvarchar(100),
visualCond nvarchar(100)
);

create table filtersForTypes (
id int not null primary key,
fieldTypeId int foreign key references dirU.fieldsTypes(id),
filterCondition int foreign key references dirU.filtersConditions(id)
);

create table filters (
id int not null primary key,
employeeId int,
name nvarchar(100),
dirId int foreign key references dirU.directions(id),
pythonCond nvarchar(4000),
visualCond nvarchar(4000)
);