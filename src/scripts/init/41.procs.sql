﻿-- SQL Manager 2011 for SQL Server 3.7.0.2
-- ---------------------------------------
-- Хост         : CASTLE\R2
-- База данных  : showcase
-- Версия       : Microsoft SQL Server  10.50.2500.0


--
-- Definition for stored procedure _206_geomap_labels : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[206_geomap_labels]
CREATE PROCEDURE [dbo].[_206_geomap_labels]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '+
' CASE [Code] WHEN ''RU-AMU'' THEN ''#6BEADA'' ELSE NULL END AS [Color], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><h4>Карта с примерами разнообразной раскраски</h4>
			<ul>
			<li>Тверская область связана со стилем по своему id</li>
			<li>Цвет Амурской области задан в таблице с данными</li>
			<li>Для Республики Алтай стиль <b>подсветки</b> задан через StyleClass</li>
			<li>Для Нижнего Новгорода через StyleClass задан вид точки</li>
			</ul>
			</header>
	
		</labels>
		<exportSettings width="2560" backgroundColor="#FFFFFF" jpegQuality="10" filename="map"/>
		<properties legend="bottom" width="800px" height="600px"/>
		<template> 
	{
		registerSolutionMap: test,
	   
       style: [
{
fid: "l2",
styleFunction: {
                         getStyle: "djeo.util.numeric.getStyle",
                         options: {
                                 numClasses:7, colorSchemeName: "Oranges",
                                 attr: "mainInd",
                                 breaks: "djeo.util.jenks.getBreaks",
                                 calculateStyle: "djeo.util.colorbrewer.calculateStyle"
                                 }
                         },
                     name: "Субъекты РФ",
                     text: {
                                 attr: "name",
                                 fill: "black",
                                 font: {
                                     family: "sans-serif",
                                     weight: "bold",
                                     size:"12px"},
                                 halo: {fill: "white", radius: 1}
                             },
					legend: "djeo._getBreaksAreaLegend",                             
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
},
{
       fid: "71",
       stroke: "red",
       strokeWidth: 3,
       fill: "purple",
       text: {
               attr: "tooltip",
               fill: "blue",
               font:  {family: "cursive", variant: "small-caps", weight: "bold", size:"2em"}
       }               
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure activity_for_test : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[activity_for_test]
	@main_context varchar(512),
	@add_context xml,
	@filterinfo xml,
	@session_context xml,	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	

END
GO

--
-- Definition for stored procedure balancecount : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[balancecount](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT = 0,
   @count INT OUTPUT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
      SET @curValue = '%'+@curValue+'%';
 
      SELECT @count = COUNT(*) FROM [dbo].[Journal_37] WHERE [Journal_37_Name] LIKE @curValue; 
   END
GO

--
-- Definition for stored procedure balancelist : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE  PROCEDURE [dbo].[balancelist](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @firstRecord INT,
   @recordCount INT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
         SET @curValue = '%'+@curValue+'%';
      WITH result AS 	(
         SELECT 
            [Journal_37_Id], 
            [Journal_37_Name],
            ROW_NUMBER() 
            OVER (ORDER BY [Journal_37_Name]) AS rnum 
         FROM [dbo].[Journal_37] WHERE [Journal_37_Name] LIKE @curValue)
      SELECT
         [Journal_37_Id], [Journal_37_Name] FROM result WHERE rnum BETWEEN (@firstRecord + 1) AND (@firstRecord + @recordCount)
         ORDER BY rnum;	
   END
GO

--
-- Definition for stored procedure chart_bal : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_bal]
	@main_context xml ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as "Год",
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per."Квартал" ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= CAST(@main_context as varchar(MAX))

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Регион],' + @params+ ', cast(''<properties>
									<color value="#00FFFF"/>
									<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>
								</properties>'' as xml) as [~~properties]
 FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@add_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC (@Sql)
Declare @chartsettings_str as varchar(max)
set @chartsettings_str='<chartsettings>
		<labels>
			<header><p>'+@add_context+' зерна, тыс. тонн </p></header>
		</labels>
		<properties legend="bottom" selectorColumn="Регион" width="500px" height="500px" flip="false" hintFormat="%x (%labelx): %value"/>
		<template>
{
    plot: {type: "StackedColumns", tension:"S", markers: true, gap: 2},
    axisX: {fixLower: "major", fixUpper: "minor", majorTickStep: 1, minorTicks: false, rotation: -90, includeZero: false},
    axisY: {vertical: true, fixLower: "major", fixUpper: "minor"},
    theme: "course.charting.themes.Showcase",
    action: [
        {type: "dojox.charting.action2d.Shake",
options: {duration: 500, easing: "dojo.fx.easing.bounceOut"}},
        {type: "dojox.charting.action2d.Tooltip"}
    ],
    eventHandler: "eventCallbackChartHandler"
}
		</template>
<action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>hide</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                        
                        		
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_bal_extgridlive : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_bal_extgridlive]
	@main_context xml ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
set @main_context = 'Потери - Всего';    	
	
declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as "Год",
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per."Квартал" ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= CAST(@main_context as varchar(MAX))

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Регион],' + @params+ ', cast(''<properties>
									<color value="#00FFFF"/>
									<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>
								</properties>'' as xml) as [~~properties]
 FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@add_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC (@Sql)
Declare @chartsettings_str as varchar(max)
set @chartsettings_str='<chartsettings>
		<labels>
			<header><p>'+@add_context+' зерна, тыс. тонн </p></header>
		</labels>
		<properties legend="bottom" selectorColumn="Регион" width="500px" height="500px" flip="false" hintFormat="%x (%labelx): %value"/>
		<template>
{
    plot: {type: "StackedColumns", tension:"S", markers: true, gap: 2},
    axisX: {fixLower: "major", fixUpper: "minor", majorTickStep: 1, minorTicks: false, rotation: -90, includeZero: false},
    axisY: {vertical: true, fixLower: "major", fixUpper: "minor"},
    theme: "course.charting.themes.Showcase",
    action: [
        {type: "dojox.charting.action2d.Shake",
options: {duration: 500, easing: "dojo.fx.easing.bounceOut"}},
        {type: "dojox.charting.action2d.Tooltip"}
    ],
    eventHandler: "eventCallbackChartHandler"
}
		</template>
<action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>hide</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>
                        
                        		
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_Bars : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_Bars]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>Все показатели по региону "'+@main_context+'" на графике Bars </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="300px" height="500px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
"legend": {
	"selectable": true
},
	"plot": {
		"type": "Bars", 
		"tension": "S", 
		"gap": 1, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": 45, 
		"minorTicks": false,
		"leftBottom": false
	}, 
	"axisY": {
		"vertical": true,
		"leftBottom": false
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_chart : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_chart]
	@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
 declare @Sql varchar(8000);
set @Sql = 
'SELECT [Регион],' + @params+ ', cast(''<properties>
									<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="16">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>
								</properties>'' as xml) as [~~properties]
 FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@add_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
 EXEC (@Sql) 
Declare @chartsettings_str as varchar(max)
set @chartsettings_str='<chartsettings>
		<labels>
			<header><p>'+@add_context+' зерна, тыс. тонн </p></header>
	
		</labels>
		<properties legend="left" selectorColumn="Регион" width="500px" height="500px" flip="true" hintFormat="%x (%labelx): %value"/>

						<action>
						    <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="16">
	                                <add_context>hide</add_context>
                                </element>                                                             
                            </datapanel>
                        </action>	
			<template>
			
			
{
"legend": {
	"selectable": true
},
"plot": {
		"type": "Pie", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"precision": 3
	}, 	
	"theme": "dojox.charting.themes.PrimaryColors", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}

,

    eventHandler: "eventCallbackChartHandler"
}
		</template>
	
		</chartsettings>' 
		
set	@chartsettings=CAST(@chartsettings_str as xml)

END
GO

--
-- Definition for stored procedure chart_ClusteredColumns : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_ClusteredColumns]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>Все показатели по региону "'+@main_context+'" на графике ClusteredColumns </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="800px" height="200px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
"legend": {
	"selectable": true
},
	"plot": {
		"type": "ClusteredColumns", 
		"tension": "S", 
		"gap": 1, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": 45, 
		"minorTicks": false,
		"leftBottom": false
	}, 
	"axisY": {
		"vertical": true,
		"leftBottom": false
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_Columns : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_Columns]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>Все показатели по региону "'+@main_context+'" на графике Columns </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="200px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
"legend": {
	"selectable": true
},
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": 45, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true,
		"leftBottom": false
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_pas : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_pas]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="200px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.Chris", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_pas_fliped : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_pas_fliped]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="500px" flip="true"/>
		<template>
{
    plot: {name: "default", options: {type: "StackedColumns",
tension:"S", markers: true, gap: 2} },
    axis: [
        {name: "x", options: {fixLower: "major", fixUpper: "minor",
majorTickStep: 1, minorTicks: false, rotation: -90, includeZero:
false}},
        {name: "y", options: {vertical: true, fixLower: "major",
fixUpper: "minor"}}
    ],
    theme: "course.charting.themes.Showcase",
    action: [
        {type: "dojox.charting.action2d.Shake", plot: "default",
options: {duration: 500, easing: "dojo.fx.easing.bounceOut"}},
        {type: "dojox.charting.action2d.Tooltip"}
    ]
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_pas_no_res : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_pas_no_res]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="200px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.Chris", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_pas_wrong_param : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[chart_pas_wrong_param]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="200px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.Chris", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_StackedColumns : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_StackedColumns]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>Все показатели по региону "'+@main_context+'" на графике StackedColumns </h3></header>
		</labels>
		<properties legend="left"  selectorColumn="Статья" width="500px" height="400px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
"legend": {
	"selectable": true
},
	"plot": {
		"type": "StackedColumns", 
		"tension": "S", 
		"gap": 5, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_variables : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_variables]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
<header>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</header>
<footer>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</footer>
			
		</labels>
		<properties legend="left" selectorColumn="Статья" width="500px" height="400px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "StackedColumns", 
		"tension": "S", 
		"gap": 5, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true,
		labelFunc: "function(value) {return 10*value+''тонн'';}"
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure chart_ylabelsfunc : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_ylabelsfunc]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set @main_context = @add_context
set @add_context = ''

declare @filters as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
	[Год],
	[Квартал]
Into #Per
From
	(Select 
		Journal_45_Name as [Год],
		'1' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'2' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'3' as [Квартал]
	From Journal_45
	Union ALL
	Select 
		Journal_45_Name as [Год],
		'4' as [Квартал]
	From Journal_45
	) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
	On #Per.[Год]= Journal_45_Name,
	geo5 left Join geo6
	On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct	
	[Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
	Journal_45_Name,
	 [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name like '%'+@add_context+'%' and
                   Journal_40._Id in (2,3,6,7,8,9,10,12,14)

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
		#Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
		#Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал,
		-1
		From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
		#Tab1.Journal_40_Name,
		#Tab1.год,#Tab1.квартал
     
declare @Sql varchar(8000);
set @Sql = 
'SELECT [Статья],' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Tab1.Journal_40_Name as [Статья],
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
        Where #Reg_year.[Регион]='''+@main_context+''' ) p '+
         
' PIVOT ('+
'	max(t1)'+
'	FOR [Квартал] in('+@params+')'+
' ) AS pvt Order by sort2'
EXEC(@Sql)
Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context='Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>Все показатели по региону "'+@main_context+'" на графике StackedColumns </h3></header>
		</labels>
		<properties legend="left" selectorColumn="Статья" width="500px" height="400px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "StackedColumns", 
		"tension": "S", 
		"gap": 5, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.CubanShirts", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true,
		labelFunc: "function(value) {return 10*value+''тонн'';}"
	}
}
		</template>
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO

--
-- Definition for stored procedure companycount : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[companycount](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @count INT OUTPUT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
      SET @curValue = '%'+@curValue+'%';
 
      SELECT @count = COUNT(*) FROM [dbo].[Journal_47] WHERE [Journal_47_Name] LIKE @curValue; 
   END
GO

--
-- Definition for stored procedure companylist : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE  PROCEDURE [dbo].[companylist](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @firstRecord INT,
   @recordCount INT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
         SET @curValue = '%'+@curValue+'%';
      WITH result AS 	(
         SELECT 
            [Journal_47_Id], 
            [Journal_47_Name],
            ROW_NUMBER() 
            OVER (ORDER BY [Journal_47_Name]) AS rnum 
         FROM [dbo].[Journal_47] WHERE [Journal_47_Name] LIKE @curValue)
      SELECT
         [Journal_47_Id], [Journal_47_Name] FROM result WHERE rnum BETWEEN (@firstRecord + 1) AND (@firstRecord + @recordCount)
         ORDER BY rnum;	
   END
GO

--
-- Definition for stored procedure companylist_and_count : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[companylist_and_count](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @firstRecord INT,
   @recordCount INT,
   @countAllRecords INT OUTPUT
   ) 
   AS
   BEGIN
      SET NOCOUNT ON;
   
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
         SET @curValue = '%'+@curValue+'%';
         
      SELECT @countAllRecords = COUNT(*) FROM [dbo].[Journal_47] WHERE [Journal_47_Name] LIKE @curValue;          
         
      WITH result AS 	(
         SELECT 
            [Journal_47_Id], 
            [Journal_47_Name],
            [CreateRowUser],
            ROW_NUMBER() 
            OVER (ORDER BY [Journal_47_Name]) AS rnum 
         FROM [dbo].[Journal_47] WHERE [Journal_47_Name] LIKE @curValue)
      SELECT
         [Journal_47_Id], [Journal_47_Name], [CreateRowUser]
--         [Journal_47_Name] as Name, [Journal_47_Id] as Id 
         FROM result WHERE rnum BETWEEN (@firstRecord + 1) AND (@firstRecord + @recordCount)
         ORDER BY rnum;	
   END
GO

--
-- Definition for stored procedure dp0903 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[dp0903]
    @main_context varchar(512) ='',
    @session_context xml ='',   
    @datapanel xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

    SET @datapanel = '
<datapanel>	
	<tab id="01" name="navigtor from file">
		<element id="0101" type="webtext" proc="webtext_context_info"/>
	</tab>
	<tab id="03" name="dynamic add_context">
		<element id="0301" type="webtext" proc="webtext_override_add_context"/>
		<element id="0302" type="xforms" template="Showcase_Template_update.xml"
			proc="xforms_proc_override_add_context"/>		
		<element id="d1" type="webtext" proc="webtext_filter_and_add"
			hideOnLoad="true" />	
		<element id="d2" type="webtext" proc="webtext_show_debug_console" transform="xml_in_html.xsl" hideOnLoad="true"/>			
	</tab>
	<tab id="10" name="get tab elements state">
		<element id="61" type="xforms" template="Showcase_Template.xml"
			proc="xforms_proc_dep">
			<proc id="proc1" name="xforms_saveproc1" type="SAVE" />
			<proc id="proc2" name="xforms_submission1" type="SUBMISSION" />
		</element>
		<element id="d01" type="webtext" proc="webtext_filter_and_add"
			hideOnLoad="true" />
		<element id="d03" type="webtext" proc="webtext_dep62" >	
			<related id="d01"/>
			<related id="d02"/>							
		</element>
		<element id ="d02" type="grid" proc="grid_cities_data" hideOnLoad="true">
			<proc id="d201" name="grid_cities_metadata" type="METADATA"/>
		</element>	
	</tab>	
	<tab id="04" name="2 fast grid proc">
		<element id ="0401" type="grid" proc="grid_cities_data">
			<proc id="040101" name="grid_cities_metadata" type="METADATA"/>
		</element>
		<element id="0402" type="grid" proc="grid_cities_one"/>
	</tab>
	<tab id="02" name="SP call: tags in add_context">
		<element id="0200" type="webtext" proc="webtext_buttons_uco"/>
		<element id="0201" type="webtext" proc="webtext_call_sp"/>
		<element id="0202" type="webtext" proc="webtext_show_debug_console" transform="xml_in_html.xsl" hideOnLoad="true"/>	
	</tab>
</datapanel>    
    ';
END
GO

--
-- Definition for stored procedure dp0903dynMain : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[dp0903dynMain]
    @main_context varchar(512) ='',
    @session_context xml ='',   
    @datapanel xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @datapanel_str varchar(MAX)
    SET @datapanel_str = '<datapanel>';
if (@main_context = 'Правильный контекст') 
SET @datapanel_str = @datapanel_str +
	'<tab id="01" name="navigtor from file">
		<element id="0101" type="webtext" proc="webtext_context_info"/>
	</tab>
	<tab id="03" name="dynamic add_context">
		<element id="0301" type="webtext" proc="webtext_override_add_context"/>
		<element id="0302" type="xforms" template="Showcase_Template_update.xml"
			proc="xforms_proc_override_add_context"/>		
		<element id="d1" type="webtext" proc="webtext_filter_and_add"
			hideOnLoad="true" />	
		<element id="d2" type="webtext" proc="webtext_show_debug_console" transform="xml_in_html.xsl" hideOnLoad="true"/>			
	</tab>
	<tab id="10" name="get tab elements state">
		<element id="61" type="xforms" template="Showcase_Template.xml"
			proc="xforms_proc1">
			<proc id="proc1" name="xforms_saveproc1" type="SAVE" />
			<proc id="proc2" name="xforms_submission1" type="SUBMISSION" />
		</element>
		<element id="62" type="webtext" proc="webtext_filter_and_add"
			hideOnLoad="true" />
		<element id="63" type="webtext" proc="webtext_dep62" >
			<related id="62"/>
		</element>
	</tab>	
	<tab id="04" name="2 fast grid proc">
		<element id ="0401" type="grid" proc="grid_cities_data">
			<proc id="040101" name="grid_cities_metadata" type="METADATA"/>
		</element>
		<element id="0402" type="grid" proc="grid_cities_one"/>
	</tab>
	<tab id="02" name="SP call: tags in add_context">
		<element id="0200" type="webtext" proc="webtext_buttons_uco"/>
		<element id="0201" type="webtext" proc="webtext_call_sp"/>
		<element id="0202" type="webtext" proc="webtext_show_debug_console" transform="xml_in_html.xsl" hideOnLoad="true"/>	
	</tab>';
SET @datapanel_str = @datapanel_str + '</datapanel>';
SET @datapanel = @datapanel_str
END
GO

--
-- Definition for stored procedure dp0903dynSession : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[dp0903dynSession]
    @main_context varchar(512) ='',
    @session_context xml ='',   
    @datapanel xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
	DECLARE @userdata VARCHAR(MAX)
   SET @userdata=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   	    
    DECLARE @datapanel_str varchar(MAX)
    SET @datapanel_str = '<datapanel>';
if (@userdata = 'test1') 
SET @datapanel_str = @datapanel_str +
	'
	<tab id="1" name="Ссылки в гриде">
		<element id="11" type="grid" proc="grid_portals" />
	</tab>
	<tab id="2" name="Файлы в XForms">
		<element id="21" type="xforms" template="Showcase_Template21.xml"
			proc="xforms_proc21">
			<proc id="proc1" name="xforms_saveproc1" type="SAVE" />
			<proc id="03" name="xforms_download1" type="DOWNLOAD" />
			<proc id="04" name="xforms_upload1" type="UPLOAD" />
			<proc id="05" name="xforms_upload1" type="UPLOAD" />
		</element>
		<element id="22" type="webtext" proc="webtext_filter_and_add"
			hideOnLoad="true" />		
	</tab>	
	<tab id="6" name="Раскраска по значению">
		<element id="61" type="geomap" proc="geomap_func2"/>
		<element id="06" type="webtext" proc="webtext_grid" hideOnLoad="true" />
	</tab>	
	<tab id="6_1" name="Раскраска стилями">
		<element id="62" type="geomap" proc="geomap_bal"/>
	</tab>				
	<tab id="7" name="SelfRefresh">		
		<element id="77" type="webtext" proc="webtext_self_refresh" />
	</tab>	
	<tab id="8" name="Карточка">		
		<element id="81" type="xforms" template="Showcase_Template2.xml"
			proc="xforms_proc2"/>
		<element id="82" type="webtext" proc="webtext_3buttons" hideOnLoad="true"/>
		<element id="83" type="grid" proc="grid_bal_articles"/>	
		<element id="84" type="xforms" template="Showcase_Template3.xml"
			proc="xforms_proc3" neverShowInPanel="true">
			<proc id="proc1" name="xforms_saveproc3" type="SAVE"/>
		</element>
	</tab>
	';
SET @datapanel_str = @datapanel_str + '</datapanel>';
SET @datapanel = @datapanel_str
END
GO

--
-- Definition for stored procedure exec_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[exec_by_userdata]
	@main_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   		
	if (@session != 'default')
	raiserror ('__user_mes_test1_src__',12,1)	
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure exec_test : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[exec_test]
	@main_context varchar(512),
	@add_context varchar(MAX),
	@filterinfo xml,
	@session_context xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure extlivegrid_bal : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[extlivegrid_bal]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',    
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
set @main_context = 'Потери - Всего';    
    
    
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
            <footer><h3 class="testStyle">Футер. '+@main_context+' зерна, тыс. тонн </h3></footer>            
        </labels>
        <columns>
        <col id="Регион" width="250px"/> <col id="Картинка" width="50px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="85px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="50" autoSelectRecordId="9"  autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure extlivegrid_cities_metadata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_cities_metadata]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @gridsettings_str varchar(max)
DECLARE @cities_count int
SELECT @cities_count = COUNT(*) FROM [dbo].[geo3]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Новый способ загрузки - отдельные процедуры для METADATA и DATA</h3></header>
        </labels>
        <columns>
        <col id="_Id" width="100px"/>                
        <col id="Name" width="400px" precision="2"/>        
        </columns>
        
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties flip="false" pagesize="100" autoSelectRecordId="36"  autoSelectRelativeRecord="false" totalCount="'+
CAST(@cities_count as varchar(max))+'"/></gridsettings>' 
set  @settings=CAST(@gridsettings_str as xml)

END
GO

--
-- Definition for stored procedure extlivegrid_cities_one : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_cities_one]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  '
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties]                       
         FROM [dbo].[geo3] ' + @orderby;
   EXEC(@sql)	    
    
DECLARE @gridsettings_str varchar(max)
DECLARE @cities_count int
SELECT @cities_count = COUNT(*) FROM [dbo].[geo3]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Традиционный способ загрузки - одной процедурой</h3></header>
        </labels>
        <columns>
        <col id="_Id" width="100px"/>                
        <col id="Name" width="400px" precision="2"/>        
        </columns>
        
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties flip="false" pagesize="100" autoSelectRecordId="36"  autoSelectRelativeRecord="false" totalCount="'+
CAST(@cities_count as varchar(max))+'"/></gridsettings>' 
set  @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure extlivegrid_extlivegrid : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[extlivegrid_extlivegrid]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',   
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
set @main_context = 'Потери - Всего';        
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);

declare @a as varchar(255)

if @element_id = '12'  set @a = '7'
if @element_id = '14'  set @a = '11'
if @element_id = '13'  set @a = '15'
if @element_id = '140'  set @a = '110'

set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                       <main_context>current</main_context>
                       <datapanel type="current" tab="current">
                                <element id="' + @a +'">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="100">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                                                               
                       </datapanel>
                        </action>
                    </event>                    
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>

        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="250px" precision="2"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="90px" precision="10"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							

<properties flip="true" pagesize="50"  selectMode = "row" autoSelectRelativeRecord="false" totalCount="10"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)

END
GO

--
-- Definition for stored procedure extlivegrid_extlivegrid1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE  PROCEDURE [dbo].[extlivegrid_extlivegrid1]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
set @main_context = 'Потери - Всего';        
   
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'dfgfdg',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
    Case
     When @sortcols='' then 'Order by sort2'
     Else @sortcols 
    End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>
            <event name="row_single_click">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по строке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event> 
                                <event name="row_double_click">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 2 раза по строке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>   
                          <event name="cell_single_click" column="Регион">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по ячейке Регион</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event> 
                    
                          <event name="cell_double_click" column="Регион">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 2 раза по ячейке Регион</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>   
                            
                    
                                           <event name="cell_single_click" column="нет такой">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по несуществующей ячейке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>  
                    
            
                    
                                           <event name="cell_single_click" column="3кв. 2005г.">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по  ячейке 3кв. 2005г.</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>  
                                                              
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT top 1 [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
   where #Reg_year.[Регион] = '''+@add_context+'''  ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt )p  '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>

        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="250px"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="90px"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
<action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="110">
	                                <add_context>hide</add_context>
                                </element>                                                             
                                               
                            </datapanel>
                        </action>
' 
set @gridsettings_str=@gridsettings_str+'

 <properties flip="false"  selectMode="cell" totalCount="0" autoSelectRecordId = "5" autoSelectRelativeRecord="false"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure footer_proc : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[footer_proc]
	@session_context xml,	
	@framedata varchar(MAX) output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
  
	SET @framedata = '<h1 align="center">Подвал из БД для '+@session+'</h1>'
	RETURN 0
END
GO

--
-- Definition for stored procedure generationtree : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[generationtree]
	@session_context as xml = null,
	@navigator as xml output
As
-- declare @navigator as xml exec [generationtree] @navigator=@navigator OUTPUT select @navigator
--select Journal_40_Name from Journal_40
--declare @navigator as xml
-- 'true' as hideOnLoad,
set @navigator= (Select top 1 '180px' as width, 

(select top 1 '00' as id, 'Фичи' as name, 
	(Select top 1 '07' as id, '7-й этап' as name,
			(select top 1
				'main_context' as [main_context],
					(select top 1
								'07.xml' as [type],
								'firstOrCurrent' as [tab]								
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level1] For xml auto, type),
	
(Select top 1 '08' as id, '8-й этап' as name, 	
	(Select top 1 '0801' as id, '8-й этап, 1-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0801.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0802' as id, '8-й этап, 2-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0802.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),			

	(Select top 1 '0803' as id, '8-й этап, 3-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0803.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
		(Select top 1 '0804' as id, '8-й этап, 4-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0804.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0805' as id, '8-й этап, 5-я неделя' as name,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0805.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type)			
	
						From geo3 [level1] For xml auto, type),
						
(Select top 1 '09' as id, '9-й этап' as name, 	

	(Select top 1 '0901' as id, '9-й этап, 1-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0901.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '0902' as id, '9-й этап, 2-я неделя' as name, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'0902.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),

	(Select top 1 '0903' as id, '9-й этап, 3-5 недели' as name, 'false' as selectOnLoad, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'dp0903' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type),
	
	(Select top 1 '1001' as id, '10-й этап, 1 неделя' as name, 'true' as selectOnLoad, 
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'1001.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level2] For xml auto, type)			
		
From geo3 [level1] For xml auto, type)		
										
from [Websites] as [group] For xml auto, type),

					(Select top 1
					1 as id,
					'Балансы продовольственных ресурсов' as name, 
						(Select top 1
							3 as id,
							'Балансы зерна' as name,
							

							
							(Select 
								Journal_40_id as id,
								Journal_40_Name as [name],
								(select top 1
								Journal_40_Name as [main_context],
								(select top 1
								'a.xml' as [type],
								'firstOrCurrent' as [tab]								
								From geo5 as [datapanel]
								For xml auto, type)
								From geo3 as [action]
								For xml auto, type, elements)

							From Journal_40 as [level2]
							Where [level2]._Id in (2,3,6,7,8,9,10,12,14)
							For xml auto, type)
							
															
						
							
						From geo3 [level1]
						For xml auto, type)
					From geo3 [group]
					For xml auto, type),
					
					(Select top 1
					2 as id,
					'Регионы' as name,
					(Select 
						geo5_Id as id,
						Name as [name],
						(select top 1
							[level1].Name as [main_context],
							(select top 1
								'b.xml' as [type],
								(
									select
									_id as [id],
									(SELECT top 1 [element].Journal_40_Name as [add_context] 
									From Journal_40 as [fake] For xml AUTO, type, elements).query('/fake/add_context[1]')

								From Journal_40 as [element]
								Where [element]._Id in (2,3,6,7,8,9,10,12,14)
								For xml auto, type																
								)
							From geo3 as [datapanel]
							For xml auto, type)
						From geo3 as [action]
						For xml auto, type, elements) 
					From geo5 as [level1]
					Where NAME not like '%---%'
					For xml auto, type)
				From geo3 [group]
				For xml auto, type)
			From geo3 [navigator]
			For xml auto)
	--select @navigator
GO

--
-- Definition for stored procedure generationtree_re : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[generationtree_re]
	@session_context as xml,
	@navigator as xml output
As
	raiserror ('просто raiserror',12,1)
GO

--
-- Definition for stored procedure generationtree2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[generationtree2]
	@session_context as xml,
	@navigator as xml output
As
--select Journal_40_Name from Journal_40
--declare @navigator as xml
-- 'true' as hideOnLoad,
set @navigator= (Select top 1 'true' as hideOnLoad, 

(select top 1 '00' as id, 'Фичи' as name, 

	(Select top 1 '04' as id, 'secret' as name, 'true' as selectOnLoad,
			(select top 1
					'Запасы на конец отчетного периода - Всего' as [main_context],
					(select top 1
								'd.xml' as [type],
								'firstOrCurrent' as [tab]
					from [Websites] as [datapanel] For xml auto, type)
			from [Websites] as [action] For xml auto, type, elements)					
	from [Websites]	as [level1] For xml auto, type)	
from [Websites] as [group] For xml auto, type)
			From geo3 [navigator]
			For xml auto)
	--select @navigator
GO

--
-- Definition for stored procedure generationtree3 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE procedure [dbo].[generationtree3]
	@session_context as xml = null,
	@navigator as xml output
As
--select Journal_40_Name from Journal_40
--declare @navigator as xml
-- 'true' as hideOnLoad,

set @navigator = '
<navigator width="200px">
	<group id="1" name="Балансы зерна">
		<level1 id="a9262a7d-cacb-42db-a4ec-c4010ea16cda" name="Группа элементов №0"><level2 id="f39c1710-0347-4e8f-bd74-cecf9b67c310" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="78517b4e-d599-4298-9b64-471fb2db34d3" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="88204558-b95a-4abe-9215-ab7a0b269430" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ac7a46b-7441-45c1-89d1-246d55a00c3c" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="07adda6f-1155-4c47-bfc2-06d53007189a" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f535746-212a-4ead-91d1-f1caab96979f" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f991c72-fcdf-48dd-9473-50ec6b98c5d9" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bc27384-b806-438e-80b1-8a91737d0457" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c17569d-137b-4731-9cf7-b1fdfba2a1af" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f46502e2-5659-406e-8190-09846882fec4" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b3b3ca5-9a4d-4e1b-a8a8-ddb550175454" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c5a328e-d076-468a-9fea-42196a39e18c" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d8b6cda7-52f1-4b38-bba9-3fd74777cd88" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc95735d-5514-436a-91f6-c13a607e3cb3" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bfb94bad-637f-4269-8343-0f893f83164f" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f74a7247-fecc-4d65-ab56-5604911aefc6" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de50d701-5ac9-4603-977b-6fffdf216121" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9d0ecd42-4ce3-43c0-92fc-f1afcb833010" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b38d3d1-f0d4-4e8d-b3c3-72ddb7e3bcad" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f94df84-c86f-4d23-965a-841ee000636a" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f0e698d-df34-477e-8467-de91bdf8c6c7" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6e4b13ad-954a-43db-b4d5-22ffbb1b77f8" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c74497f9-d89a-494d-8721-76cde804180c" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="32c3575c-15fa-4752-ae48-e2343ac8ffa1" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dd8b9439-4649-4455-9044-b2e4a0b5b14a" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fef6e6ff-f827-4bfe-a1b1-a8e6f4a217d9" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="50ed2586-300d-46c6-b940-c1c938e3810a" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9b23852f-54cb-4ee1-89c4-bf48f899b61f" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cc17a49e-c4a5-43a7-847c-983b8f4b1356" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ffb32136-82c7-4920-a949-6e3e75f95095" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="027d79ba-e430-4919-aad5-4bb9cdedf03d" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7f4c54e3-4a53-46cf-bf06-83ec7c7aedbe" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="07f8e7f3-ab36-4fb0-b9ee-2104c8ae4422" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="683fb6c5-d1c9-4233-adf8-041a425432c3" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="35d10811-388e-4738-bf26-5081b12955b9" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7718a38b-116c-47bf-8b72-718c8d423539" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b40f4025-b375-4a0d-a074-f955cb496c25" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="25d8e737-aec3-4424-935a-386fb73a2e46" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ac942c2-680e-4a68-bf56-4e0c2d3c0728" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ef0f9c8-1432-4b0e-9d58-9aba7e0e66ca" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d0f3c2dd-4025-42e3-a7a7-a5adb6680838" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ccbafaf1-adbd-4ed5-9cfd-c5ab6effb0cc" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7598fb88-d564-4913-ba90-7e0334e153b6" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64541792-29e9-4f7b-adde-3f2f9b131a10" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5241d04f-1467-48b9-ac92-e1abe3a83dce" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57b22b23-cf95-47c4-a2ce-1dc41775facd" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9acd1e98-9a5b-4e08-9d5e-a6716677fc22" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="685476c9-c3e2-4774-a231-36e36744318b" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0fd6cdfe-7487-4e0b-bb9d-35c769f12b38" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="25475c05-bf2a-47fc-a44b-1ed332314664" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9ee12666-2143-4a37-8287-5703f59cf8ce" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="857698f9-69e0-4893-9503-ab0584ae3cec" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0917a635-a516-4d40-ac15-9a3cad00c2d7" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0bda8625-604b-40aa-9bb2-a0eee476ce5f" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4e842ac9-c14e-4de6-84b5-6a3a864a53b4" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a41c8777-640e-459c-9fb7-567a74338511" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3dfc4aa7-cb8c-4e12-a6af-952ff8cc7311" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="42e40f38-ec72-45e6-a7dd-d4e6c18aa607" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44c5ac28-b3c0-4b67-8090-9f474b9a43e9" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57623014-8e1d-4f72-920c-c6cf807c0634" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f6b30251-ba91-4516-ae8e-84a039f6e2e6" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5c2a126c-9eac-4770-951f-51872ba25cb2" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3730056-5066-4819-a68f-bd27b053c54f" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6a2f4f24-094f-41f2-a4c9-1a302c815e7d" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1f049bf1-2136-4929-b05d-c52cd2334f80" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="abe60d36-66c0-444c-9c52-50ab24876e80" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f89ba53d-d74b-4fb3-986a-062aae2957c4" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14ea96d2-7929-4bde-8364-d4cba7952894" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4b7953bd-b67c-4b1a-8a67-29a28c5d5dca" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00008e10-e1ba-4abf-8c98-90ec568c18b0" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a3e88611-05b6-43d5-b72b-621db0bfa8e7" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c37e0a3-e293-46ca-8046-91f0adedcfa7" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d0563be5-5926-48da-8269-af1ad1b76d3f" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3e0a1d46-61df-4b7d-8569-60ff09b5fdd9" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6e27019d-906e-467f-adf9-570490d9ac64" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b3e87736-d802-4322-ba41-73ca8c43ee58" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="61cc6a62-e797-4aa3-b6a6-e53b7dfc68da" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="77df7d01-3dc6-494c-9148-aec6f5031dbb" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7de03535-d5ae-42a5-8320-689646fa835d" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b9642ad2-271b-447c-b949-a42d69d8de85" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="226569ef-8569-4ffe-a641-c27ab1e6f84a" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b33cb4da-567b-4d3e-86dd-73df8b8d2eb1" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ecccf4c2-7e89-40ba-9b75-23d78ec02adf" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4006d714-611c-4960-bdb3-4ab5da81ce48" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f357bd10-be9d-46ff-9a71-296542545686" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="049fce2e-249f-484e-83c3-9d85b007fb94" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c17ffaab-692d-4e47-b220-261d04afc3ba" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="37f33f12-d64b-4243-9cb6-85bcc70a86c7" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9d9dd9e7-45f2-4b19-847a-0741583205fe" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4c2a2d5f-e2cf-4f99-b879-01a8ce5977a3" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53ad2fc0-f5f5-4c2f-a4fe-80542de6d0e7" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2b9b6802-9c97-41fc-a778-a650802d2969" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6cb74b51-3e0f-4ce8-be91-10b325abef27" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9e482fbc-7a5d-47f6-b3f1-d91f6fe7857a" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="59ba90d0-6154-4d26-b529-5c8a55dc9dec" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b32a1bb3-52eb-48a1-a6f3-53b9e7c3f1ff" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="366c8dde-a6ce-4fb1-b4db-a61d29b38a36" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ff35374f-e61d-4dda-96f3-3e7f4a7cf419" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="67562b6e-560e-4247-a681-2e2c8a237685" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8ab14d27-3042-4b8b-b932-8ca5b305b33f" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="47a59edf-31f8-4684-b274-26ad36260cfd" name="Группа элементов №1"><level2 id="e2c29828-47bf-4e66-9638-e9b48384580f" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="204d0fb8-53db-4e1d-bc7f-d0eb82b6c093" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20e73bb5-5d92-4fd4-8d11-9a1fe1b71847" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4ea6c212-68a8-41e2-94c2-4e0e0381c676" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a142685-529b-4ee5-b754-65758ffb7cce" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8ff97e22-7b50-40d7-84df-2858cec6f121" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d52adf7d-95c5-4795-9b6f-efcf053cfa7d" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ebda0c11-4879-4a05-8e58-6db9a81853c3" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99631d62-c159-4631-9c62-87fe74c6d6dc" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dde08cec-c6bb-4edd-8dab-4d2dc22a4535" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c22f7bb2-1ce0-4acf-9feb-652866a1713b" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8741c228-c592-4d89-9607-ed243af51ee1" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2541ed69-7390-4f11-bdb8-925cbffa0810" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ba53a77-57b4-49a4-8723-1bb42b61d6b2" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ee84079c-761f-4cb2-a627-87a653e1f6e3" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="85e699f3-75e0-491f-be62-c937154e5935" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5fbdce3c-b2af-46c5-800e-4382ec8889db" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cd3a3c27-ce69-40e6-83bc-911784f33b46" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed565a49-9065-4603-8cc4-c9d562bc74e5" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8429ecc6-c954-43a0-8fc6-0b483e2153af" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7cb801d2-e7bf-43c9-8762-658a2a1690af" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="565721f3-0730-4356-a1c9-f062d5b438af" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="78bfb6a4-ca43-4394-a5ce-4d3d55a1e5a3" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d252f17-1446-43f3-ac79-a7ab899dbb0a" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="26c6a2d9-88b8-4294-b557-8e0fde1c8e78" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ffd406bb-f117-4d8c-b00f-ce30834d09a2" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df67c847-7fac-4745-ac44-d703d48367b0" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="54a27064-006e-4015-aa67-58a6589009c0" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af920525-5a4b-40d3-b9e9-285442bd41d2" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6950f5cf-1afe-4f5d-af9e-15a286dc5d30" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="88043cc2-e814-4b64-b678-068007666fbe" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01064659-0073-4ea7-83ac-8d9208a48eee" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d3808af9-a24e-4bcd-98ec-efb2f2b7dc95" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e5523de4-fe3c-4e75-aea6-0644440d5637" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e42967b3-75fb-4d0d-9338-1fbcea36212d" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3079a339-3683-4ad9-9715-3d475abd5407" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="38ac227a-ddb9-41a0-b214-5d0cf3cc5066" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="be401674-8660-4199-96de-7d0b3f2edff4" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1cf81cef-bb6a-4e84-baf7-6360dbd65256" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="653255f6-622e-449f-a56c-b2d7d60f8142" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f45af0dd-305c-4cdf-b3da-f44b947ce29f" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6559f85e-69e5-407a-8232-0612821105da" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="92c3fbf1-d400-4e36-bfa0-0f66e457b245" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8bd4743-4ce4-4abf-81c9-fdb6928b9602" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b96a5369-2e60-44d0-85f1-676f4d76d510" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c0b92e7-18bc-4f2a-a0ce-5e63b1d9e542" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="aaf26e7c-42a5-4b97-82b2-37bcc40514a0" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="deaf9f2a-d01d-4678-b19f-8e089b1bd6a3" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6c91689c-f7d5-4483-8bce-1b3dae1e0271" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29541faa-1b45-488b-9199-ad395a096244" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="40c71352-cc1b-4aad-8840-4696b523e154" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1c82721-4fb3-47d0-b647-754c0bccbdd8" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4a28a214-ff21-44b3-9817-ef95815cd637" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1058c1d5-6e4a-4279-9e66-6bc20d3f43f9" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="378943ea-0726-4eb8-9e99-746226f52c96" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ae3d5281-78b6-4fa1-b422-213810032d8e" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6e78208b-f77e-4573-8142-edec279df1c7" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d5d53ac1-4884-497d-b7ce-3bdc90ca4ab3" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="faea69d1-e8b2-4ed8-a61b-fe6f1aafc94e" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7edd18f-78d4-4d92-90d0-c13be023c473" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73f12d67-2817-401a-839f-7dbd0a2beddf" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e27008cb-4e02-4af5-908e-759be73ef064" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49adee3a-a445-4979-b586-9700211ef5ff" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="78f07945-d715-413a-b743-73ff68c5e4f3" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7578277c-b634-4e6e-9679-2a27c6589dec" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fd8cdb81-b815-4965-b69d-13e1f9a22df7" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46a13485-edb1-4ef0-aef5-171f9ad12eac" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="08d7b80e-58a8-46d8-a148-2dcafc0b7775" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="533be97c-7dcb-45e8-9131-4de9d18fc8ea" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="39a860b2-eacf-4d78-b666-befc5dea2bc3" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="688c3def-e9ff-4e32-b2b4-73180f61850b" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1d0767c7-f36e-4c0a-bc34-5168ad53e9a6" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab44b2ab-028e-4a35-9603-d6ad8e01f7ef" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01a76520-76ad-4540-a4e9-906bf182ce45" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d28ef68e-8e49-4282-9685-c44f249a043b" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f6fd378b-d49e-4980-afd5-936c1b1d4b8f" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a2834ef1-a498-4c36-80ae-76ee153e0764" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8223707a-e5ec-4f13-a73d-f359de7fa14b" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="882d04ca-3bb1-48dc-9735-5250d59726de" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="329540fc-e21a-419c-a503-8711f6256000" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="285c4876-b474-4406-a8ee-583bc0772e11" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24ed010c-a625-44a3-85d8-f924e8b2f1d7" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1e1c06ba-4064-4cdb-9c99-95a810c8630c" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8c64428-8bda-41e4-bb50-65d78e30179f" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d68871bf-1429-4a6e-ad30-df55c738c5b2" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="456ce607-f1ec-47a5-8e5d-ed91a60fbf6f" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00b22487-0ad1-4a6c-9ada-2930c475bb10" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eea02782-aad5-42ae-8a78-07ca192479bb" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0f08dbac-759d-47f2-9965-b16dd70c9df6" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5a45b99-cf95-406e-8573-5cc82dceed77" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ba98cf5-4432-4c4b-875b-939b21e11911" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d7d18d98-f9fd-433c-b054-b9c1ce99f369" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d2b3290-ca50-4bd0-9533-a21cdfbcb88f" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ebb2803-730b-45e5-b952-290e9a816f74" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="06036e3c-a331-47d0-8da5-4be1387b8b8a" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8600882b-e7f3-437b-9dea-9b65ec1b7b53" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1c43201a-5674-41e3-9176-4dca768d93d1" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="89da107e-c79c-4960-8569-4ae7b79de064" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6656fc97-9f32-436f-96c5-97ddade52439" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="45674fb8-8cdd-4807-80f1-39a34b0c9f29" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="55a6454c-e852-47fb-aa92-b92dcbabc749" name="Группа элементов №2"><level2 id="7be8adfc-07ee-47f0-8635-be626142d97c" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="550200ce-4386-4efe-9f8d-bc4e9fa9982c" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f61a3db9-0480-4366-b542-d71ba432a0c9" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="59a81b05-c774-4c01-840f-48880a67bd85" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8bf7d95d-6c70-4fc3-ae4d-d88c52f0c94e" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5dfba34a-dac2-44e1-a081-db9934deb878" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c3a27af5-429c-4ef5-a3d4-d728b6508658" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dfbb80bd-0097-4753-a69e-c700568d7cba" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="21e0165d-868c-4bdf-851e-3bacbdf97001" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d60d47d-108d-4458-b56c-9492fee1b004" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="60795c02-430d-4354-b07c-e05db7070aac" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a2c9301-8bc8-457a-bced-f044c72da789" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3e52754b-476b-4e68-b30d-a9ce3dea6eb7" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="16ea7acd-3a2b-40e6-a29a-8142ec1af1e8" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1f832234-50f9-4ce1-8e03-57e323886758" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41501f04-1cc6-4542-ad29-1976f4614d95" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73a48a1d-e90d-4bb7-84a0-43115c7e313a" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6aed1a50-07ea-4588-b335-3b0743f74dc4" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1f66740a-9ca3-454f-8ec2-bd228dff0db4" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="58a3a5f6-2288-4ce1-a370-2b54ae79adaf" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="45c9c87c-9ea9-4e65-b7d2-1fdea838cfa4" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc44ae5a-2f48-4657-8caa-7549bd4a298b" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a11bfc89-9567-4561-9030-d93fb38e6df2" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8f14670a-0233-429d-845d-e8d9b3bd6c7e" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="914773c9-860a-45f5-ab48-ebab28b9e37b" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8b1a6843-31fc-45e0-a9a5-43f2aaa6f38e" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6214f2d8-51ee-4191-95d4-a8709ed8448f" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3765f864-fc5c-4f56-9358-580c48436f3c" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29551569-db93-483f-9b2b-2b71c8fab851" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="52063e41-fa6c-4830-8b24-e7b45a4a1756" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="486d6416-f978-4432-ad15-37b55e969540" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e11e9da3-d22a-4a90-9db4-a4954ebe7ead" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ac640e32-1efc-4c10-ac2f-1f9f342e520b" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="348cc7f0-9208-4360-9268-510af1241c3e" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5319215-4b11-4843-8c6f-4873d800766e" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c9dc2549-984d-4b3f-a819-916dd7f124d2" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="497f0cbd-fea4-419f-b258-a3e3fff6a572" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d7ba54a6-08b2-441e-a3cf-c5c102b10ff3" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f2ae90a5-f88b-43e4-9ce2-e728ffb361ac" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9f88995-e146-4235-bdc1-7327ba68486a" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f63f4781-0dc8-4395-9c14-20cad4a96525" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c5c2dd37-4fc2-4c06-a1b0-3eec18a872cf" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e579a714-6166-4ec2-9430-044423eb7b07" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c3290db3-81b4-4723-9efb-d92e65fca589" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cd66a32c-feef-4f29-a7ba-bd2fe26a25f9" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8839cae4-0fb8-4277-879d-b1c8ad399583" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a81a063c-e8fd-4cc1-a04a-edaeace2c2ea" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f0472f7-41be-4598-ac0b-b7be4a757ee7" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ede20bf0-b1f2-4ad7-be87-b32c8ca51be8" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41a9797e-4718-49f4-925f-03f498f404b7" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="afe40f77-2418-4047-98e8-c1eb49296e36" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b38df14-bccc-4c2b-8cab-381125ff66ca" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="848140c4-90bf-42d3-bff8-bff0ea754332" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="024b6e69-65a9-4b92-8784-470b372c7545" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="86670383-d474-464e-9a65-f4aa84b36bf9" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2be52a77-8ca7-45ca-ac3a-a9785df59dae" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a61544aa-fe1a-4a6f-a695-99412bf7fc48" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4193f3ac-10e2-44ac-a8cb-3ef32b267ab6" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6efd8ae0-6c71-4263-a5ff-71b15b7c5efd" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f588d21a-1073-4077-b529-bb34c36b4a78" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="355b3dcc-affb-4d78-9eb0-24a73ffd3d19" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56a12a38-6b62-431c-9720-8f99e382015a" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6fb349e5-b76d-4422-ae55-73d43d749b77" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8f7f35eb-e718-49c6-a245-85cc1bb3fa92" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5f9594a-956a-4b0d-9318-fcd3eda447a7" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8250bd36-9ce3-4aca-8f3f-2485015bae9e" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dfd10103-ac67-4972-9ee7-480f23b8978d" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bf354b8f-2393-4e1f-bab0-c53a9e924ab4" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d916b1e2-49d0-4ba7-b476-d4272e72d995" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="48f563dc-0d16-4c69-b001-ec3d973217be" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="941a5c28-50da-486d-b45b-0bd279bf96dd" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7ee5dbce-da03-4af4-9397-4863c6f1db69" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f1a7157-0383-4d36-acc7-68102e1aa533" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="272cc62c-71d8-4f0f-a29d-cd0206ffa577" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="51a129a1-cc73-4722-9341-76614ffa4453" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7302bb1-e0b0-4bb7-a2c4-88cd93337020" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8f641fc2-82a1-40f0-8bcd-fdbdbd2f9517" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8ab496d3-0a06-459c-9fd6-f6e97c7ed4e7" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="21aacf67-418e-4364-9adc-4e88c5acc7c0" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="515ce0bc-5ea8-4d85-9afb-f34ff7eefe94" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d5012b49-484e-43e6-9fe6-578c4bf57edc" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab5a849a-d50d-4c7b-bce1-8809ca7d5499" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="495c7e6a-7fef-45c3-a012-769ba1e58af8" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b763eec6-b727-4701-adf5-163bd1b16ed2" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fbc3494b-91a4-4652-95a2-0c11ea64765c" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43c4d2f7-4998-4ec2-8b5c-db172b2fe5f2" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="aebea0d6-71cb-4bd9-ac1c-b04ddaad3167" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="592a1474-ea30-44bf-9d30-0e0e4a17f900" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4ce21dbc-a2de-4949-868d-deee207a0925" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="33cb9746-dbb3-46c0-ac30-56330b9a9c32" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe8ea79b-392f-434c-8011-e69c605caddc" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c3e5fbd-64e4-477d-bbe6-fcef0d1d831f" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6dfd7cd9-ccad-46dd-84ad-aea8ca027194" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c45b0acd-ea6f-4cda-b411-9b7684b4fc37" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b52d668c-055f-4810-8d68-597942badf86" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70c8a6f6-96eb-4846-b31d-60c167bdeaae" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="18bc6212-cefc-4b3e-b442-1e146b6c6793" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6075c872-7d92-4f79-bcb0-832255844bfe" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="122b477b-02bd-44ae-8eed-95bd8fe8f4ca" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b1adbc4b-3f18-4a8b-9b35-5031cadee4f3" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="50515dbe-6980-40aa-b914-45233ea033ad" name="Группа элементов №3"><level2 id="a755ff54-836d-4336-af9e-3f4ab2aaa62f" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2178f1de-98cd-4964-ae54-965211c928db" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="16e7ba4d-1694-49c8-89ea-bb634814c2d9" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="47be5c6e-6068-4196-a112-c60b65e6b718" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="334797ed-6024-4af2-b5c6-fcb3a464549d" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9345097a-769d-4728-84df-94fbe83d5884" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4631397e-c1c1-4990-887c-9881197ac44a" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a691d51d-a34a-4bac-bc54-1b0dd09785d6" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="27658bd5-f49d-42f0-bac4-a57c918e1123" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01af13c6-a939-4997-aba0-6851bdd3fa07" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="40c4855d-6464-488f-9dae-e35dc9ab60cd" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5aa6a042-c36f-4572-bf67-d7d88e2595fe" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c85bcd78-2df9-4990-8e4d-0bfb7cd27af2" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="76a0aa38-54fa-471b-8512-65fc98c3228d" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="db54c399-793e-4aa5-9f7e-897c05853ea3" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1712782b-c45e-4799-8ff3-a532a0f3707d" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="145fb888-a6cb-460d-abfa-ab036b30ab84" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e339e6b0-9b60-4b83-ae7c-e869dd82f38e" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0c76ae03-9508-409c-bdb7-be89794896dd" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7c455e7c-3012-421c-a522-b5eebc4c635f" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ac0d65b-83e3-4409-8297-a6f03f14aae5" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f9c3c8f-e14f-4d9f-8c73-78326e9ac63e" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="82850910-3887-482c-92d6-7535fb158f9b" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d270d16b-cbda-490d-afbe-78876e8ac898" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c0b29c73-6a14-41f4-bf3f-a974faded5c9" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0e0b5a1a-2a07-4c1c-9e7b-e7116b004038" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a9b5553a-9dad-465b-b903-c56b113fe4bf" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c7b48e2-9478-423e-8d10-8bc62ab53d2a" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="62d30889-f6e0-4727-8912-7a03fc77992a" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b09342c8-ec11-4a7c-aaaa-faf7dfd7df53" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b1ea047f-2829-4ceb-98d6-1adc99358d2d" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f14cdfe-02e9-4d47-829f-b22053b2969c" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6c251d66-57f1-4893-a799-91ed479ea688" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6b9db166-85b0-4803-8d16-d5e28525f495" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0cf68acb-5133-443b-8f0c-e5333dcb9276" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d83c5b5-990c-458a-b8b8-723c6d3e8ffe" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="523dba53-e13f-4346-996a-c2a07bfb2936" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="78f52c31-9f4a-4bc9-bdd1-4d5664ddd5d6" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9df4f775-0844-472b-8b6e-37bea4eeb392" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4052a5af-952a-4233-8227-8a68d4f11f20" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="74390ef6-b9e7-4676-afe6-fc0acee89f6a" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d6f0f33b-6ee8-4b34-9577-8da307580156" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="69549174-aa7d-41fb-b6dd-02b5f022615e" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="281c08db-fd44-4e51-a1ca-74c06eee31e3" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2dbce7a7-c34c-41ae-ac53-2786188c49e5" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="733e6fa8-69b4-4340-a916-a68211d0f3b1" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="735dfbd2-975c-48e8-9f6a-a78e51ce9cd7" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="228c0030-edf9-4078-a449-aa5c1b3f361a" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2cb42840-e0b2-49a5-9a5d-1c900f70ac50" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="98e07b3a-8986-43c6-894f-2e768d14a634" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ff6313fb-ff43-46ac-8596-976f1817b161" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ac343d4-db24-4ebc-b743-af9b67f67ed1" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0036032d-e79d-4be7-984f-cb76208dd9a5" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="69b9c4e2-bceb-4150-a9b7-dac16ea76d43" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d50c920d-1597-419d-b35a-8e920d32f9b6" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ece6bc8-ea4c-4bdf-a870-39a4c29844a0" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dbdb4dbd-515e-4aac-889b-595e544c1dfa" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8c63636e-986d-428c-a735-6f8a4eb13bb8" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe423593-e35b-490d-95db-dfbeba2b9989" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="10993205-d17e-4265-b377-adeafc0d73bf" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9bc7322f-f529-478d-9dc2-4129634c485b" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c3987b8d-2b2f-43e0-b44c-922261646fb1" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e758203a-5e9c-4142-a6dd-a690fa627333" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="459a373b-ecd0-40ed-89c4-4b91d5ee8b20" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f6fe3fd5-a587-4af1-9930-5666b3992367" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df6b09fc-70e8-41a0-a2bc-25d672baa682" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fefbb57-0cbe-4acf-9852-79ed5d3037f4" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d95f880e-6744-45a4-b148-121ff50a0483" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b8f9bf83-6913-446c-b403-6f4918cc427f" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f98ab431-0c8e-4158-88c7-d30dc96e66ae" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="27747a4a-2b58-4bc7-b44d-ba55decad2c6" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5bf43cae-ab54-4608-b9e9-8e1629d6591a" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d8ae81e0-1c7a-4133-9902-80c78ab66045" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9aa83b16-a86b-4079-b5f8-2ef13ba18ec2" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3b4c3769-1254-4585-830c-79dbcedf7c05" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a210614a-c356-4abf-930c-d97f03a92188" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fc42e6b3-1200-4bc2-9e01-7484cc2353ac" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a70924c5-3315-4990-9487-c32045f549b7" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d512f58-4f98-4f93-aef1-8f85cd042eca" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c597ee1b-6541-4ede-91ed-70f1b18f78d6" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d7e61032-ed7f-49b1-a032-be68adc8cbe9" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c744a58e-290a-4f6d-80df-d2cc9895ee91" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b0c03621-39cc-45b6-9cc4-36da7410863d" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="83cdf256-ffe9-45c8-ae4e-4a2cd98ba0e1" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="862b0218-a88b-4ccf-a2b8-08f9d1ba2a06" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="473c8852-e6a3-479c-a3b0-8945315c1215" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="87ffabff-d335-4dfc-b27c-29f58103a585" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="356470bb-9abd-4bb3-805f-ab5d5466fa42" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cb782349-b5c9-490f-8b99-9681193fcb8a" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3e345190-6793-4985-bae2-601f9bec9447" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5e5ef71a-f59b-45b8-9164-208317eed6ad" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c460d9c6-bad9-4e58-bbfa-8f67e70aba09" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7cb420c7-b6b6-48da-bd75-13fc0b2b9c29" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9a4f0f7d-3d94-4c3d-b14a-01d6556e4b83" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14bf3f00-6b5c-4d2b-87d2-abf9382b0c9c" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="edbc1c6b-b52b-4ea6-8537-646e7e981ecb" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53d05083-1770-4346-9cdd-70e3d0c87016" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bca70a5f-2de6-4bfd-b8b0-76dcc4cb77d0" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20971d75-9bfb-4e8a-a10f-7e3489eadf2d" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3bdd664f-9230-46ac-9172-5ada31584ab6" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="eb5dd470-2a82-4918-9365-6c1321a5f02f" name="Группа элементов №4"><level2 id="e941bc0d-57a8-4ed6-9d19-678f73de6e04" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c41e7456-6fc9-4cc4-90bb-63ec0b346225" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="765ee917-709f-47ec-9f35-5555883674ea" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01d548ba-8ace-483d-aee9-cc988febe0cf" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="984479d5-817a-45cb-9799-964e13d6c50f" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1fc5b10-5319-40df-bd48-d793e21b2682" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4153d7a0-8140-48c6-871e-640f951d527f" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d3142e3-5c5b-47a7-99a6-8c36a6177307" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70a6ee4a-5069-49d2-9aca-323dd6f18d8a" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3efa8b4d-d9c4-4219-95d4-a04e034868d8" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4ac4315f-592c-4a76-87c7-d457552a0f75" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dedaf09a-18c3-420d-b474-ac36a0be142a" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1db85e07-2ced-4a11-a68e-1bc574c38667" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="580d950d-577c-4576-8c3b-1fb7260d91d8" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24785b16-c228-40f0-a876-fb042dda5b5c" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d5c74c3-32f8-4be3-b69d-fe9a624e794b" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1e28f7b0-0374-4301-91fa-72e172844e8c" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ffd924d-7a17-4216-837d-21229add02ea" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="979dcb7f-9417-452c-97b4-db2a9d117a51" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56bae236-ee2d-4754-ac60-1721fc01dede" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fb8351f8-e02e-4847-a1de-5ea62fb29518" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24ae948a-2b42-40a2-8662-0fa6a59af669" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="101d9d69-e5b3-46a5-a274-f03a33cfa34a" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0cda76c-7fd7-4666-9cf1-65b4b3284235" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="904624e4-16c8-41bb-b98d-7a1ca9038568" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="169ffe03-98be-4734-9f30-e05ca5bc1b17" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a47c1d98-06a3-448c-ac10-543c0cbea2a7" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f440707c-66c3-43f3-8765-ac6f0150e6d3" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c4b5b3aa-f98e-4929-9729-74a2e06c0f81" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8511bd22-887d-49b7-ba7c-7e56003a3e2e" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c36ed87d-1684-4c5e-954c-2f34ec788f3e" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cee7ae6c-b9ad-479f-b999-80cf97c21d59" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6dc62e22-17f6-43ae-adfd-2a0527ba738b" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b037e677-8241-4be4-b6d7-89d1a9836005" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5398bac3-b358-489f-bc5c-2177fab22fcb" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49a90753-563a-4ab3-be10-57201dfbb3c6" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3f9d474a-bb6c-4b6d-b6cf-d9241ddc31dc" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7260f271-63f0-4d0b-a45d-1920067ab9de" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dcc6c438-3d70-4759-a531-89d37345ebd6" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="83d76a10-099f-488e-b55c-c390ace671e0" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="16df1d07-49db-4e2d-9c29-23df4cb849d0" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68cc3233-3ae2-43d2-bebc-b9e0e79f5929" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2ed2835-47eb-486b-9062-f3068a7f8b72" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09d8819d-33f6-4493-9ca0-fba20d8dc054" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1482a061-b5df-4311-a3f1-e3586d0872f8" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8a80a3db-c022-4709-a895-e6c14adc01b9" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70bd997e-d5c8-4d79-87c1-a92ea9670b2f" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ee5e03f-f227-4b6c-92e9-970e190b5238" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3a5c4ae-a250-41af-a6d4-5062ab9dfe0c" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d3e9bea1-7e74-441c-bce7-0536bd2401e9" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f1dbbb96-4989-4195-a50a-0b06d1d31b4d" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b9afae8d-bfdd-4ed5-bc34-d01b2fe5c537" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9b8b0748-ed2f-4f4f-acac-2dd2632aa3eb" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c081abe1-d20a-4c54-8b16-ef48fcf8d93c" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5399c1a-2937-45b3-ad46-82192d82d432" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2edbe182-2702-4860-950a-077c8a952a17" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df842353-497d-467f-8468-e6eece4fa83c" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="688c7d41-16b2-4ad7-9009-fa94ac57da4a" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b52102ce-9642-41b5-bcfe-b5f11c255da8" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b5bf327-2bfe-4c05-a086-eea910cac153" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="97d91433-a6be-4fa6-aef9-b38a37ef43f2" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fd81a76d-d2ef-42c7-8b01-f76c6a7070ad" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a5fcb50-bedd-4efa-b1c5-3873e6a2ec10" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d4d0513f-3cb6-4aab-b1b0-36787d0515ef" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9944ea4c-a617-45a3-8faf-2e36c697180e" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f9af3735-ee88-4ec7-91d1-a3239e9792cd" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f49ee24c-f522-45fb-ab89-9f836d76a6f7" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bbc0403c-b5fb-42be-ab76-36ee52d364d0" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7fe0a952-2e8f-45f3-9d5f-7242085baced" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af49217c-ba36-4630-ad88-3120e4eeeb6d" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="08a416fa-9c2c-4db4-b30c-5986fd5a983e" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="42713b10-67e6-471d-ac69-9f5b1a1d935b" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="099df8dc-27ad-4699-b1d6-8963a353b454" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a319ede2-28cd-4b6f-9964-34372137796b" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc4f7f3d-31ef-4790-8694-1352a71c490c" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7c1e3118-40ad-42c7-921c-21a4c7fc003d" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="86af7e79-d5a6-4759-83b8-c494f5c53814" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75f52d0e-87e3-4f9c-be9c-70d6eacb2ac2" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8b274ea3-6179-43db-bb9c-c7286f562308" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ad6b869-2196-4372-8991-dd044a5c038a" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d6285451-f861-4c6e-8c8b-20e81b39a6bf" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc15b390-daf3-4d02-a688-fad12194594e" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05aba604-bac8-425f-a303-46335a3eb02e" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="77780e59-de4c-49de-ba6e-45c7cc96eee2" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed2ff312-5f09-4b06-a814-8efd70a865a3" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d486bb66-4f09-4f8b-a986-1a2c5782bc3d" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="84f32dac-a1f9-4fc6-9086-eed7953dda92" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a7b67d4f-e3d1-4bdc-8427-610f4ab26d66" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="beee6875-654d-40ce-b231-90e59dd7f404" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6cfe06a5-a6e2-4065-9b67-f0b85b44fee3" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6b93444-92b7-4a9a-b32f-ade2b5cfbba7" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ad794267-eeb3-4ff7-af8d-8315003feeba" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01c5465b-c8d0-4eec-8f72-bfc371221f52" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0eafa4fc-fbd8-4136-8d30-02448d9e5d83" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49160562-922e-4f2a-b15b-794a6c6f6ccd" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4a7fcbb-a8a3-49dd-9e0f-c14c281e2d3d" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="981d7636-f643-4943-be1b-d281994a9151" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f0e668a-177e-4fca-b0a8-342ecffe721b" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="48cd03bf-d621-4930-bff3-a58b2545d678" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="60586428-bfb9-4d81-ada1-cdc1c302482d" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="ea95600d-64b0-408f-9cf3-84ac33bc8547" name="Группа элементов №5"><level2 id="db81347e-4d37-485c-9dc2-64e7a27839a3" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df8e655c-bd1e-426c-82c9-c35718061839" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c9056458-4e47-4d70-815e-36651a2075f2" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="800e7f86-9f01-4d5e-80ce-9a6dc1e2f8cd" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2211d6ee-affd-4a71-b300-51b80dab879e" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0a45c080-6cac-47d3-8b22-1330943f5859" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2000f5cf-d110-43ee-a721-7fc5e80bb250" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="553e6e71-c2c2-40b9-8613-832ab55234b6" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="12c4ba9b-abfd-4c60-aa86-84aefd8cfbed" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1995e7c9-392e-4df5-99dd-d1c723224d8c" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2b3dea5f-3d63-4a2b-93e5-da0a588e5fb4" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9a10f1b2-716b-466e-94b7-93995dee4e28" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0fd49b66-bc9f-4e1b-b184-e9e09692b6ac" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="183124a5-441e-47a1-8736-04f4a26f8083" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce4434a9-fa28-4498-82a0-fd12ece569a4" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f61b5934-d0ed-4988-aaf0-3e3e80560a4f" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6c0d5946-984c-40e1-aafd-4e26d887faf5" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de8be748-f4ee-48bc-8cdb-44b3d252f075" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5c79cb93-57ed-4c1b-afd2-a74d86b03004" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="198f4423-0750-4fe2-a8eb-0fd08af13f56" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fb8c1724-6051-464a-ba57-0f05ab2d650d" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ce99ab3-8a77-48e3-9998-866ba85cf1eb" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a16d44ca-d1f6-4eca-9b92-f72f2df6bf89" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5be55ae-0694-4743-970b-5898b5e87f4a" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="002d3dde-37f1-4d18-87dc-ff6b6a166c2d" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="422b0740-2c58-48b4-8c0a-f5cff90d1121" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0800327e-7473-4755-bcf9-f8633d83d51e" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5d9453aa-ef8e-4e78-9dcc-ca9c8c84bc46" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8788ebba-e106-41cb-ba40-67cd4430696c" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c277ae7d-f530-404a-8fd7-e7d3574f1df6" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="912a2f19-be36-4843-a86f-56734af15700" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b82be744-825f-451a-b1c7-8fe841d45da5" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a43ba13d-6ffb-44d6-97b3-f56979f3c86a" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="be5692f3-e70b-469d-b9ba-5859bb489720" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3fc54530-1d90-4fbe-a3b1-6c4570e32254" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ea4124a-0748-4149-b7a8-9a976bbd0c95" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7e9d17aa-a08d-4d53-9876-a40769e2eb79" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bb949d47-4c5c-4d04-a9f0-f8f28a6e0c56" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e7f0fc01-6edc-435d-addd-70756484d771" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1c57d414-1140-4d85-9802-7454f1d5770e" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ba1ecb8-9752-4e00-be29-b1f5deaae2a6" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="578d76a8-b347-4d66-bbd9-ac19ca5dc5bd" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d04644a0-0c76-4734-8646-39cdb465df76" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cfed8106-d050-4243-b958-7364e862d862" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d98e129-5491-4b34-8af1-177afde48f23" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ddef3fdf-917b-4932-ba45-9386eac26a29" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea328787-77b5-4cb9-8bf8-02057442911b" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="629d3097-076e-420e-a054-5e7310b3b4f8" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cb86b392-ff93-46f0-b079-c6ff75202c54" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="25060698-06e1-4509-b895-b4e7f612dde4" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6123d38e-1979-4a81-97fc-04762f92edd5" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4b93a15-3288-4059-8115-b7f360eb1cc5" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2a3a560-0483-4ce3-b0b4-dbfdbded8ec3" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="35951afe-120f-40ae-a438-c82532278b30" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8dce3899-8ba6-4717-851d-5d63e96afb68" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99d7e4a7-9e6d-4b59-82b9-49f1a5ce5b52" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4c85a04e-715f-41fa-a0d1-bbb6c5518399" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0747376-e3d6-4554-a2e8-a9475500d404" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a75e8a6-2b6e-4819-ab83-28fae0725d4c" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2821212a-6415-4863-8277-5e186cad052c" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="39eda965-a4c8-447d-b705-0ec68b30177a" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8228a87-0f29-4816-8bae-ef59d0a3a208" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="355fd305-1f16-40c2-b977-9e8662d68225" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8856e9a6-4511-4215-9612-fac4981a4ab0" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cce472bb-faae-4e69-a710-8355c3b65681" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="723c7a76-78cd-4c9b-9e63-0116ffccafe9" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="441cbcb7-1220-446f-a83b-e14deba49274" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6fbb5483-3fee-4a52-a64c-66434ad5f92a" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="22f38131-1397-4e05-b679-e743ba917c26" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4c89b5d-7642-497b-ae54-0da8672f9819" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7edc2c70-b9a3-4f7a-b130-ce3e4c44325f" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ff63e54b-68f9-4b7b-b48a-066a2ca5fac4" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="54e7cfe0-d528-445d-a80c-6a3481a37c26" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="173515ea-fbe5-44ac-a4e6-198731735514" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="66d814d6-3ab7-4f5a-bf70-ef1cd05adb42" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="019eb397-1ce0-4713-b57a-a696c0bfe682" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a18aeff-b407-4ea1-89df-e8f06c963bfc" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1f540181-a3fd-4bcf-8755-b873c1545087" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="200fc4d1-c20e-4d79-a206-57cefac1476e" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="742b1611-e2f4-41e0-8908-d75f6bd49609" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7ee75c0e-3edb-46c0-96c6-6a5a199e13c0" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="717eed4f-980a-406c-9bd0-1c55f870fd40" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="adf65619-11a3-4d67-99f9-d73390ede5ef" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="16027a98-1059-4b48-9336-fcbb684a09a3" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="678c7514-893c-4781-89c0-192bbd469bf1" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ec8aa95-7313-4f01-aac0-59c383a4f138" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d049984-a23b-4dde-9c5a-1dd88a4a17d5" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="098ded23-f88b-4063-9fa8-8caf7acd1f30" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8a2f0418-6d5a-44a0-9932-2834827839ea" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ec23b2f-13f1-4575-9f0d-ae6a05ec9598" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eba7df86-1254-40e6-9ef5-2b0a86753d7b" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91e226cb-ea3a-4e9d-8324-eb3deeb5a6e1" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9095361-00dd-446d-af29-17a097824299" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8e014e02-8dc5-4ffb-95c2-35ba628f4335" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="574e0574-4825-4c7b-b675-e9435eb7c11a" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1da99d94-7c99-4fb9-b866-0b2a9899d7cd" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5928fd5b-1c23-4fa9-ba24-0243782c99f2" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2766b269-a570-4115-ae1e-ac775411c24a" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ff2f995-ee75-4ad5-a821-5c63420fd3e4" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="28f84f7d-c1ca-4d5c-b2fb-8431ba4dc13f" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="b8a8c638-0f64-4175-9270-3cf996c35036" name="Группа элементов №6"><level2 id="9a38577b-171b-4b24-b40e-7c95084c4027" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4ba5b11f-cd95-4834-89bd-f5396c776d03" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0860077b-a731-43fa-a51a-6e52346bed15" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="52fe402e-4122-4784-8785-55ebc8c416c6" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c224c06b-8ee2-40d8-8d93-46652b9a2967" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6c8ee371-616f-414c-9998-cf89fcc28085" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="132b833d-9462-4df2-a641-1c964564acf6" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9a0ca577-c1c3-467f-8adf-be75cbc0e2fb" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9503442-5913-4238-8aca-660b99abcafa" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="87a92ed2-3300-4db1-b892-4eaeca8609ed" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="caff479a-0f4c-4129-9ccb-2f0770208d3d" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bdc3f15-95e4-4029-b479-457cbe710e8c" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="350072bf-e4dd-4743-a220-d70dac3bb1ff" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0e862bed-cdbb-4fcd-8666-e36e269bc0a7" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a113d02-e45b-467d-b62d-925af98a68fb" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0edcd33c-57ef-49ef-a3cf-fe8ddcadf5e7" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="15124d3b-6c42-41cf-9478-52f52b3db347" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4b53f6a9-103a-4a30-bb95-9ed471e02e30" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="aa76d5b6-3c90-4db3-b004-07d203864924" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a088c3af-7801-4872-96d5-cda176afce32" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a86f54f8-eb54-4381-bfe2-665be19ae7e8" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c59c8887-dec4-40ff-b0a2-50c47c6e26c9" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93676203-041d-4386-a955-08ee0e81de09" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="109ca507-af56-4566-ab2a-9a1962e344ef" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5047198-db11-40ec-9dfe-8de9e0834fbc" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4141faa-bee3-4461-8554-05eedbfa9781" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0d29921-182c-47d0-9ebe-606381fee10e" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa046a03-7a78-426d-ad23-70bf67897aa2" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="312a3380-af49-4ae0-8cf0-f92487b5a96b" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d0f0f885-78f4-4b08-8519-a4ec7666dbf0" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="887c8759-7e61-4ad0-94d7-a0a84f97645a" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ee521eff-2ec4-4f8d-84d2-9da69c5c4c31" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44104667-85b5-426c-ad66-f1a45ac4a4b5" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2e33d63d-e1a2-4f0a-a13b-f87bfdea1c59" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b4cef459-8a5c-4e83-8ecd-dacca42ffe1e" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a73053a5-a23b-4635-b3b8-882e0f285a95" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5e88e0e3-7f87-4f70-8d34-ee9cbff8900b" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64ff9aee-7f47-4231-899d-ae971343517c" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="099bcb79-c2da-4090-abe7-b931e35f595b" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6b85d105-4e8e-4bb1-bf41-b6bc8b83f100" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a369a143-d63d-451b-9d85-49fcdaac468c" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91881f30-4742-496d-aff6-3c464dae4917" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="514d524b-3d59-481a-afca-73367e4a4c69" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14998a62-47c7-4530-aa85-b1ed3ca8ad23" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="085de547-b259-4ead-8ba4-f7797b46e2f4" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53fd4eae-3889-4632-83f8-8b760fb60c93" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fb7a4854-8f52-4828-8a00-c608d347bd95" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="311bbabe-04ff-4632-9857-e4bf29ca3422" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c26f4470-a453-4909-9cef-0e7c6c186ece" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5d345287-2cfd-4eb2-9665-8aca72dd6e85" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bbec2a39-e428-45c4-9b9e-cc7ef7865643" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="03bd5ee7-ded4-447c-9b71-9ccac4b40925" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="79502d1a-3d28-4b05-a350-1dfd193c55c9" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f880cfdd-d2a8-4037-9de7-d8448bfe945b" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2a0346b8-ec01-471e-bf35-677a4ff3b4ba" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="03c0578d-3e90-4e9c-ba79-4dbdac0e360c" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a7d03abc-dcae-4358-84f4-f24a1e388a42" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0cdfa9cb-c1cc-4ea5-afb8-0e1603c57c2d" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e07f720b-ad0e-40bd-bedf-166127c76c15" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70ceb82a-4924-46f7-b6f7-079924af6419" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c2244fc1-4511-49e4-b30d-d3cd5074c707" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="42c5f072-555f-4751-92d1-234aecb4615e" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc6db7cd-1a4f-41c6-bbcb-76e8f78a5b14" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bb386791-e557-4286-8d4b-47282ca5d421" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="61990493-7df7-4783-87c7-51ecfc4946aa" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ff6c48c-e1b4-49e5-983a-0214570cbcc2" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43e0052a-d89c-4b72-910c-288e8b24e5bc" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56aa1313-abda-4d51-a3fc-69ec18f806dd" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="83763594-29d5-4cc8-8e3d-e10e5014cfe2" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8c8c1486-3b7c-4cad-98e5-3cb5fe15d1e6" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e308cb73-ea61-4970-99de-3397780fd7df" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="533944f3-cd21-4090-a27d-d3aafbc93d72" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9913095c-9edc-4790-84ca-d3f94627f4b6" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7b6eab5b-b7d3-4996-9d3b-b89c4e63e104" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fb720900-a91f-4a6c-b9c8-8f852791af9b" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="314fbbb7-9864-48d8-8a70-8da6019791f6" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1711703-f422-4d70-8ada-e4c93bd5afe7" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="62444552-15a1-4096-bd96-897a05aba913" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c0b1589d-bc69-4a94-b632-bdb3a838e96f" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ffa54db0-dd2a-4a46-8954-5a3ec001c31a" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5affd805-6a8f-45a5-bfb1-5d85fa764497" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0ccf9724-4d18-480d-a49b-3c717af01dd9" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d6a3d700-1655-40c9-a781-99f9b3fb5235" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4449ec00-6a75-421f-9f4e-c7856152c676" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="beace523-4309-452a-b55e-8d5cb686bc34" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8202ed50-e0ab-4fdc-bf57-93eb5b384f0d" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6137d488-8d39-47dc-9faf-bf6e71ae98f5" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3483204d-efac-4956-b9ee-3d58e1f6fa30" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dce2ebb1-4d93-4e7b-ad7c-d46ff6a3b835" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f4813a13-37a5-4141-844f-cf0ac8c23023" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="654c75d9-010f-4286-a3c6-25e2a861e8fa" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cfd202b5-2b36-40cb-9036-3c509a2cf655" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b84d4629-ad19-4268-a582-2e3e5db20af9" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a0684daa-01a0-4d66-931a-681e4778425a" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="69f51a07-4abc-43b9-aa95-72ac7a2ebe35" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="848c6224-10f9-4638-8a24-3baec5461491" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ebf9cb08-5a2a-4e94-be40-e79fc208d3a2" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bde772c1-37fe-4372-8582-2d45201ac968" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4884f51f-c495-4d0d-b3a9-b76a77745cf3" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f43c88a0-fa16-4fee-b4f6-396e3fcd7ca3" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="a08f8963-3680-4c96-a0b6-97d824de9886" name="Группа элементов №7"><level2 id="24beab20-38dc-4027-b60d-e79f96ad925b" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23ffb71f-b73b-47aa-8f5d-b83c7babaab6" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29fbefc6-5ca2-4729-82c3-05dd55220941" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b55aa4a-7c47-482f-af3b-4f2ab522f8c6" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f2dd5f4-b71d-45bc-82de-19fe62a51b29" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4e0f8c3b-354d-45cd-8bd6-5fadd5755fc6" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6042572f-c3f4-47be-a124-db0e947a3741" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e6b928c7-d6ad-423a-8fee-87ec98211e13" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d98617c4-2b78-4135-9e9f-772c48d35f53" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc798c65-f48c-4bdd-906a-9a8d780e1849" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="be15e683-197c-4245-9dc6-e74880033100" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c03e0e4-d704-4857-bfba-e8824f122dbc" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57118fe0-9267-4783-901f-86adeafdc996" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="802fe443-8216-482f-86fd-6434e755ff75" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f1bf946-6280-4740-b4c7-f754fa861b28" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56e75617-8923-414e-9e6c-e05f26dab48a" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7ea98b81-6dd5-4952-8cbf-a0f519edc218" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="469e40ca-bd80-4fe3-9ec9-f42f7a4be684" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5161d32e-b9fe-4b7d-9c1a-467d1511be22" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9b031255-0eeb-4ce4-b668-1689ed026335" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d438911-0991-4359-8cbe-bd4326efcf8c" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2a9ab7a9-f17e-45ea-a042-68002f45757b" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="267c76b4-84c6-40b5-9d99-f44068968474" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a40f475c-dda8-47b5-91e4-a0e94f194713" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b7503f6c-4978-4c99-b9fd-37d3efb90006" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="019cc8f3-f5c8-4722-ae60-24d0fc9d5219" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce1f172e-70d7-42b6-8ff4-8286ff776859" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0c59f424-0073-43cf-bbcf-aa954e3c6b67" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="893d370a-a312-4696-b9e4-3c5a7ac75711" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0f1cdfb4-ad1b-4cc2-be21-dee15a9741cc" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9e8581a3-2a89-4dce-ad28-aec728c1c7f5" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8164e10e-8659-462e-a94f-840bd9e47be1" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4a6a84c4-c19f-4799-9605-bbdf15e5e694" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a09721b7-933b-468a-abe2-7cd4c07f6cd8" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d2475a4-3e8c-4f7c-8816-01de0b224056" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0486b3ad-717e-4461-895c-4204b09acfb6" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="36db7e4a-249e-41af-afe2-8cccac2b3014" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14932c48-b8f0-4699-bb15-7912540175f6" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6b8fa790-2c97-40c4-ae71-5a037f2ddad2" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7f2a2039-b374-4c94-8d7d-657e2771cdf9" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0920d2fe-bccd-43f2-bff9-af5656914ddf" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab0aade4-14b0-4332-a75a-a072e49932e7" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea9ccfa8-fbb0-44d4-a292-03c23871f0f7" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="19a66035-77e1-4eff-a052-1cf24c399776" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b3433d83-27f4-4649-ad09-2bfe21e9bb98" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bb391244-8bab-4596-94fb-a5b38200e755" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab854c80-8648-4456-bfaa-37ad63963d84" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d010288f-1696-48b3-a486-7bd7d3be2968" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3ce9ff2-ba32-4fd9-816b-28fb914c5a20" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3034d5bb-5816-46c6-94f9-eb1ed043f2ef" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="156f3ae0-543b-444a-93a7-a695763f9038" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="85a703e1-a8b1-4083-ad04-466cafafce64" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99669a17-c56d-4f36-b9a0-e7bf0c01ccd9" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f9aa1667-6b0c-48ce-8272-1a967da4c76b" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="18e7da51-4c99-44fd-8cdc-70aea8889f3d" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d0b4b407-58b9-4d11-aa73-ec5a63cd659f" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e5942854-271c-4050-a2d1-3b56d59e00b7" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7043f2e4-acd1-4a44-bd15-f6e5dbe1e262" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7980c0a2-f2e4-4db1-8fb2-bbd979cee795" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dffacd05-cf88-4991-9c01-ab7e28d40975" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6dc922ca-add8-41b1-a9d1-c34496c28c0d" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5f9d4f1d-8e8c-44d9-b424-36c052c9fc8a" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2cd3b07-6d34-432f-a3b4-d3a6b5563e6c" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="659f0455-ba8a-4951-b34e-03a984d8d006" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6566469-ef98-423b-b438-49684d5e5435" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="95318d2e-58fe-43af-9ecc-3ce616e73697" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4245783a-9365-4a6c-a1e5-654467d948c6" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="90e300cb-62e1-4941-a570-ec14c2f69011" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24ea5a5d-6cc1-430f-984d-cfc76cca5d65" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="25ff747d-667b-467c-94ff-041a4e6645b3" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e2386154-8bef-473e-9384-80ff6c0c789a" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d5b92c3e-cf7c-4483-b398-2ae719dac754" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="990ba688-694b-42fe-86d2-b54367db795a" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="59dd5a71-189a-40fd-be46-c7ac2711ee60" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="518f7262-350e-4b50-8f04-d41f3704bc53" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="638f4d1f-43d5-4ad9-9344-895e5e75cfb5" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="403fa670-8727-4def-a0a0-77714fa5569f" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa02b28e-dcbe-4c2c-80e2-143c232199df" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="31b5f597-0aa4-4b30-943b-70fa0aa03ca8" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0a0a59cc-3450-44ac-8b0d-473a3991033f" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ac476eeb-ff51-467b-801e-5e10872770c9" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="21c86051-ee81-4974-866c-306002d5e919" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="070a0920-050d-4110-b1f5-017e2dca5b4f" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="47ccc8a4-04ca-499d-857e-3814f7c5076d" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c7b445cb-4738-4060-a123-22a3352dd439" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4903b21e-f389-4e2e-8aca-26e65c0d221a" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2b2fc95-0593-41c3-a89c-2664d0a67da8" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="534e0044-8a80-48f8-a2fa-a2d5b1ea04d5" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c188e03f-6c4d-446d-b101-b1cc9d95bf88" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8314ec0e-a2d7-4dc6-84d5-39c32635b3e8" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0e4f558f-d221-49fb-b0d1-3febfd271996" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46c2d2de-829e-47c7-94db-e1338f4f4174" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="58dc0822-1afa-4b79-9143-fddca8198f26" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="146a6ee5-7aad-4f39-b23f-f38b997e6526" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="08d7cd6a-1121-41d3-8496-91c261e8ef57" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d97c56b8-eed4-4ce2-b736-7b039a3bd583" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="79c3dd50-3062-4fde-9fad-5c07aedf8e78" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8a8a65b4-80c3-4cf7-885c-06fd4a861eae" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2b54a553-f976-4b43-bba6-a5b8694779be" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ee41404f-6179-4bdf-b494-617deb25f799" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="1898a5c7-0810-402c-b919-deb2865e346a" name="Группа элементов №8"><level2 id="c90f63ef-c345-4330-8aee-374842112013" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e1acdeb1-3d24-4114-9513-372ef6c3a9bf" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64e5bd75-afe8-4266-a551-4f9257f0bcfc" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="928729cb-3fd7-4307-be27-e1c78e5091fe" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ca4201d1-da25-415b-9fe8-f09d05935e19" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5657eff5-5bf4-4a87-b48c-518e8c699cc7" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0439e560-ea96-4294-8f3e-bb8bd3f7d876" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="17c88459-42ba-4126-aa8e-1bacbdae35d0" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3a20b46c-8f4b-4091-95ba-82a6ab799ce6" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1155d82b-1309-49a0-abda-5e5bb5e602cd" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f2fded7e-f524-4192-96c3-640548e1e9ca" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab1034a5-5930-4ecf-a600-fb3eae1c2544" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b87a1243-d1bd-4e25-901a-77b0f4a4b0eb" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="361652b3-f2c6-46a4-ba69-d5d0fc8afa43" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4de23201-f60d-4d80-9899-4035e26a3787" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba094e7e-a71f-4b96-9a0c-65569dfe1a8b" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e89327e4-2e43-41cf-9db6-35f81196fcd9" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43d2b669-3c8b-4ee9-84ee-038fd6e6f011" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="850b60e3-9a9a-476f-962c-f6d39253ebe9" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b7e4c513-4108-47de-9427-0c609ca2ed89" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20b161fe-cc44-4b54-a033-47bb833acd04" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="abb9482d-ee81-4010-ae16-bad66c1c1d4f" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ccb1c4d6-eed6-430a-98f8-77e377a0a0bc" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6fe8e171-05da-4104-9fd3-a5933e6adbeb" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9cc5f115-eab6-4224-a578-b4c6c90c8d30" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75d57c11-26bb-45e1-be7c-d7d9ac9c9139" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b8e1b38d-f9ad-4dd0-9f7e-3dcf62abe0ba" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de5492e1-275c-4423-9421-5d5b81477eb4" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81205135-299a-4499-aefc-b943006054c1" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c7e59046-1ccf-468c-a050-fcb2ea829595" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8249abdc-1343-4128-9f4c-d00a56489c3e" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1628bac3-e765-4d67-9aaf-31ef5d38723c" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2cbab0c-33a1-4d9a-90be-7c10032b47df" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7bf85f07-9546-4770-991b-ba03ab05185d" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="731ed635-f80a-4e5f-9565-7290dc84ea66" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e8b064cf-f3a6-441f-8eaa-e1e25afd897c" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a837294b-4218-4c69-83fe-6bda8c44820f" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bd827527-a31c-4752-9c42-82e7dceb9315" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc31c890-ca46-428b-9cec-34466e1e8c16" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c09d3f0a-4795-4f5d-8d8d-f81a02bde169" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="59f1ab4c-5045-48cd-8699-97d5e000dad2" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a67501d-cb09-4ffc-b418-c9ec85d40a22" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91777b73-d7d6-4920-8f80-c75e5e0a3232" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8052b1f8-d202-4859-bfce-a07e80db065f" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1891faca-23c8-4b8e-ba75-2804a8e8de5b" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c1af369-8e73-434d-af56-1e6be9cc616a" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04b45a92-8d1a-4a13-9ab6-c451cc417b87" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f43413f-8fee-4595-aa50-0eab991dc2f3" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1c9648c5-89ad-4472-8daa-13a15a9fb874" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c247146-9d88-49cf-8266-9dd8d268c098" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d86330db-3135-4074-9228-b33643349154" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="378878fe-645f-4a25-b423-219f17a180ba" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1e74a3e9-fd65-4ee0-b4d8-9f2399b46a03" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c48087f0-388c-4f63-947a-987d0c9b9f7d" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc070a72-cd90-42ec-81c2-f27bb552225b" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fda60f6-8947-4779-9d91-da39914c0e09" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="520da579-4342-43af-acd3-8b101e1f2245" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d181ee69-170d-4fea-873a-53fe32c99d07" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="be9a7500-3cd2-4caf-b2ed-e69df6989bf1" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a5c86143-30fb-4d25-b891-1c07a87c748a" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f331c46-ea31-4f66-bc14-88eb57cdf22a" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a83b31f-4149-487b-bca5-486ea792e4f9" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7edff93-449b-4a38-9e8a-586c78daf788" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="199aed3d-c6cd-46ff-88cc-4d5df74819a4" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="750e0d44-8a4a-4eb3-ba1a-c00853b80695" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d87d0115-2252-4c24-b736-3667463ea207" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="959b3234-ebfb-4a94-a87d-28f24021f7ac" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ac2cc16-8c79-4edf-b58c-5e8b15a4bb90" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f95d96a-9675-48f2-b906-c7107a48455f" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="889a3bfb-8a5a-4bc8-9c6a-9a8d0bebacc2" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="683e01f1-c6e2-4853-ab20-d5893cfad07f" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="98a5c47d-35cf-42ea-9b38-efe20ce12692" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5362093-b4ad-43ea-9579-e1305819b501" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8adf2012-784e-4e87-8b9a-55c3157d7d4e" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cb5e6473-124c-4054-a3e3-c6067aed3013" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da1e8f04-d2c9-4d77-a1c0-99321f838030" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a309620d-df83-4cab-be8e-e7d886a57ff2" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="37195854-850b-4739-8078-54919cf4e6a8" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8b0fb882-8e2a-468e-8ac7-310129a54992" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ffbd1e6d-b43c-4c99-8da7-11f79f7e7aa1" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5264ef9a-a127-40b1-954f-f9a6bb10037d" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b581c230-c127-41ea-b10f-a3c2683cd2c8" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba7a3428-4e1f-4183-8317-7f009589d41f" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f74f9871-5bfa-4ef2-a616-8d83286b37a0" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5f2dac7-247b-41ee-b9e3-2bf835854b81" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bce1f7c4-86a4-4cdb-956b-23794f2896ac" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a0d88d3f-248c-4689-a607-aaaca62987b3" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a40f1e9b-8c6f-4228-b586-9ba8fbc7a808" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3b61fb56-dc3c-4cb8-bea4-58053ee8ef60" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0a5c4f2-2389-438d-8321-eb75e7859202" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b550f9c9-7d43-492e-a0a5-4997a941b537" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b67144a-0c5b-440a-83c5-a3186bbef224" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f14288e1-68d0-4db3-91b0-cfd1b3c791f2" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="202a12ee-5857-4d55-adba-a88ec3e5573e" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dcfc8ad7-20fa-4b56-9744-7267301ef9c4" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a27e3997-d8f8-4f39-a921-fd0373f3746d" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8ab84745-1650-4790-bbc5-0337e89e9932" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a07d697f-efff-42be-a20a-4d65e26011a6" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="22274729-320d-4eff-92c8-ac75b2fca4ec" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="72b0cdac-2102-49b5-a60e-a289e2efdea4" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="6e5f8681-9ebd-47a4-ab19-666e7a000c9f" name="Группа элементов №9"><level2 id="1a14b74d-85d0-4cfe-bd72-cff7c07c7380" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dd4cc15a-befe-42c5-ab45-c8d98316d789" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5010b13b-f5f9-4f16-a7bb-0c393a14d8fe" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b1711b2-5c69-48ca-a667-9be12e58cfd8" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e6538b3b-4c1a-49c3-977c-4ea0e2b1559f" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6424e05c-35ed-49ac-9001-609d2de55b02" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fc8e970f-71ab-4f1a-9b9f-132f888c3082" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5bff13c6-3bec-406d-bb73-6af6a149e247" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="67e5229c-aa22-4d28-94f1-00e7e15780ef" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="733507d6-4ecb-409a-8681-649e92cde0df" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="275f6317-4200-4ece-b030-251ac83c99ca" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="113dbd19-8d26-44e2-bb7c-c12400db83f8" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e4c07325-f2fa-43be-a287-d923dce09215" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3de7ca42-eb0e-4d0d-88ed-79746ff8bebe" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24ed3d02-3bbb-420b-bd0a-0cfbbeb9cb07" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="25122a90-f4f6-48d4-a7a4-14e9ed96950d" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cbb361f2-bbd3-41f6-8527-ac8f2513194d" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1c76870c-f606-4b35-8cef-e6f017e243c1" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="739e0eb0-2396-4708-9030-89d06da3b4e9" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e4714ad6-d5a9-454f-a50f-2db7c116833b" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="872438a3-b87e-4a0c-98cc-ff05ec009b3d" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5392d40e-a178-4324-a103-680ec4520104" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6357ea74-3139-4bcf-ae87-51a4a9285069" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="800e87a2-40e7-439f-bc2c-fa8f30ea66b5" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7849c345-ebfd-4ffc-aeda-0fb717b004cd" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="86673338-a65e-4ac1-86e3-25405fefe207" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4abd452e-6628-4fc1-be8a-c108006f519e" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cdeebdf3-24be-4d21-9eba-c320f9a3577e" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6c36c5fa-8790-4ec6-8a74-2ad47573a53b" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af994de5-47e3-47eb-843e-6351c29768d7" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d01828e-8561-4b0d-9da7-b0406ad8add6" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba180f1a-ea43-4108-bc4d-4f6855fc871e" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99d0dcd3-a700-499d-89a6-4382256f53ad" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c2fc8ceb-a01b-4ee9-bd0e-5fbcee1367e1" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a9daed4-7e02-4bcc-b54d-3691120e5e36" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="48dc884e-0b82-4fd0-b06b-d9dd1e2a6d36" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a1a9e4e6-c9e7-4f31-9814-80ddd0ddf410" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3873ee42-8372-465a-9bc9-03b8044c6918" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ad08954a-9790-4d9f-aaf9-7e85bc06290d" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="656627f1-cb15-4593-928b-741d369c2eba" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e803dc1e-9e8e-4142-8cb0-2d01822f456f" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bf61f006-2fc8-4a53-b045-15f71c25284c" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="16b31516-0bcc-4d17-a824-442c37348453" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ddd01c1-633a-418c-b252-571b15ee7d53" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="39715321-cc1a-4cf6-b739-d818c66a1b56" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4e0122e5-41f7-4840-a7ba-27cde1e9be70" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bf157857-7ab6-48bc-bad4-6b17fd17fa03" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3f5ab95-3d74-4d2f-9e81-55b292e231a1" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a96adac4-8a72-4837-9f85-fd1c50e6ad84" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="384e67db-576a-48da-bbb7-b9f1ef19bc4f" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="592eb0b6-d440-4170-890b-684050b54513" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5d4fa0d4-cadf-4a87-bb17-965024c0d629" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2bf254c3-2bdc-4499-97ab-2981f3dec34d" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8c910082-8f0d-4c64-b14e-b9a471eadb1f" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="029d79f7-e2e1-4a22-9397-73e3f4bc9b70" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05dc8019-ac81-48c6-8181-fda7e55291e4" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ee91bb0-2683-42b2-b488-398b272a7576" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="143b5b47-e2af-49f4-affb-759850e0d0df" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="108ba312-9cd6-492d-91f7-4b5d02e399c8" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da95c18e-92ea-406b-9ed0-b9eb2c2c24c2" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2b882b8c-50e3-4a9d-be6b-d26ba99e2e00" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0dc05cfd-2a12-45da-9eb7-32357ba15caf" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="205d5a36-ca43-4e1e-9378-ac1423afd69b" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="977f72df-a3e2-4cc1-9ef6-5d21576e7683" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d7a794a-fb18-44cb-8e6c-7dba4fb3c1d3" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="883027a8-f95f-4abf-bfb9-e3a8d51150b3" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f391b99d-ccdc-43c3-9bc3-0b5111a1b5ac" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af6b1ab6-bdf4-4896-a409-155ea8f4772e" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1356f153-61ca-4dbf-978e-59f53862d1c3" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="740b36f4-7b75-49be-9ff9-37162e28b32b" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b647b384-bd38-44a5-a6cf-d0962ac11cf8" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d3842426-0fec-4167-982b-518ae14e8b58" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4587fcea-5159-4d5a-a524-bd655db51919" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a83b32c9-bd38-4324-9d43-e4ce67ca88c9" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="017f1912-35d4-4e7f-9cb5-b7fc8d2b7f81" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="98122941-9e17-4e59-a274-44384b3317b8" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f99c2d89-fb8a-4c37-993e-f5efb61f71b0" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e6ca6984-fe40-49e1-a6dc-4f31ba408377" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57f2fde0-9647-44c5-9ca1-6359b76d72fb" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1b768feb-2272-4f4e-aefa-4d4bbb2c86e3" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="971bbb36-b57a-4869-a3cd-6c739ef95c82" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="983a594f-c168-4a04-a19b-6cd2750da67b" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dcde3c38-81be-4fdb-b1f4-fa221cb13fe8" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b1fa1dae-ade1-43b8-9d4a-a03a202c3527" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91f659c5-7118-4fd9-a053-929d711615af" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d6365b8b-48dc-413e-b2c1-02787882c280" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dda2de10-7098-415f-88c9-59e79a46cb13" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91010dca-8de2-48f2-98d0-5986b4111306" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29cb329a-3057-4ef1-a053-651e5d8648f7" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e62fee71-55c7-4e4f-b0ac-6553d0857c85" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9690b92f-cc1f-4a6b-bad9-6c700ff64fc9" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="180c7c70-4ac8-4af2-b316-9edaa26c3635" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a612d06-e6ec-4650-bc26-94a26e6cb3aa" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ca136cc-f6b6-4c80-83fe-07e5cd198434" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81b5574a-02ad-4a79-852d-854ec3d43f30" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ecc7dcf5-6804-45ff-a473-ec6fcfa2a44b" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b18ca150-9a86-4b8e-a496-f5fbdee9ec88" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ac77b0a4-e4a5-4eb9-9e49-ad64de1296c3" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e3c263b4-573b-48f9-8590-a0268294227f" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5c3bcdf-962d-4837-b83c-85fb5a6f5f0d" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1>
	</group>
	<group id="2" name="Балансы зерна 2">
		<level1 id="6462fa87-595e-4a3a-89da-b8b9e1ee7489" name="Группа элементов №0"><level2 id="5533de45-4556-4a81-a20d-15e7f5ee798f" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24df5e79-f8e5-4572-841a-0b8716e7fb6c" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ac76765-2ea3-4366-b77c-d000f284b63d" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f37a7818-4044-4526-a154-4131521dd1ae" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="12b2d62a-7419-4963-8f68-4ebde495712c" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dd051c33-0f2d-4c2d-8bba-d2bf36deec59" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d66c2c6-a140-4867-95f6-59555ebe9c02" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ca0dc082-5aa3-41a6-938f-647078115b45" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5de0122b-8034-423e-93cd-797d6aac3ca4" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="96376c60-1f5a-46b4-a7d0-41d10f7fb35e" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a9c49057-5178-45c3-b9ac-15dddf3a1280" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a182c9d8-c4f5-406a-839d-1afc3b2e65ee" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ecaff01-ba11-45d0-af3d-b9938c1cb887" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f33cb192-b084-4b32-801d-590c08c8bcea" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56891bf6-a1bc-4537-8dc0-46deaca86160" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20414498-b070-4a55-9b55-d1e6bf6af1ab" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0141f952-b73c-4f16-9ce9-3f3a9aec5a2c" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="61da749f-a4b9-4338-b5d8-2bb1589d05d5" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="55a5ab63-ab70-425f-9a03-8c2d675519e6" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9ec14714-69e9-47a2-8fab-7eec59d5d995" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4dfbdc53-09e1-415d-afd3-9b0ea894608f" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="63389c14-b465-4fd1-96fa-2c012f93431c" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a8bc645-510b-44dc-9b61-6e523b5ff15c" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="683413be-9de5-4361-a2bf-6b9c8d940f14" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01ea442c-4a45-44c5-8ce3-effc7fb194b3" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c3c7651-1375-451f-b4a4-b7f180743ee4" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="341a486a-97a5-4173-bfea-f7597c466636" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a1669c33-c8a2-4893-a77b-654f1de7c0f9" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01c914b2-df68-4767-9f08-fcba745c2d1d" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4199469b-20c6-4105-babb-baf09af1e6d9" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ded9cbe-11e8-4758-8e05-01852c3f90e3" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0a11cd96-c8fd-457c-b927-be14918aa8d3" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="428393b8-a579-4a15-85af-2f6335a22dc4" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de1e80f0-79a9-4a8f-bdf3-f4b0d736b886" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c7b17bf8-66d8-4d61-b6cd-70409238ea07" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="413f4e2c-2119-42b3-9816-d94195e0659c" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc576d68-0158-48dd-8bf7-a1750a45acf7" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44f60339-d228-46dd-932a-5f1d80f750f7" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f1af39df-d045-40e8-9d2b-6cc3ed6a2736" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b67d40de-5f51-4e22-a7be-6b8071a1a5d9" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="47868fd6-8b8a-4269-b77f-843bfb5e8714" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8e24a3f7-c82c-44fe-b22f-b8c109d789be" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e2815f9c-3ae5-44e8-9756-fe28dec37517" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7dea0a03-19c9-4f49-b44a-ef4dffc4be5f" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d4a9893e-71a2-44c8-a2d0-3f0aa7ea61bb" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="22418114-a4b5-41d5-9ffb-d1c9241a20d4" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64ae7365-d216-47ce-904c-c56ec03cf90d" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="12771c2b-3459-4bab-85a7-eacf5997975c" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="11832794-fff8-4400-81d4-1b46ce056e4b" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="17db85e4-e323-415f-87d4-bc5934de1fb3" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea6b02e8-e2d9-44f7-b138-bd3ae1c875cf" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="95cf6627-cf6d-4c6c-ae1a-eb68aca83300" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bf026da0-30ef-42bf-bb62-7bf0d9ea6cfc" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="37990b82-2f5d-4a11-bfc3-c35506845828" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dd0f2f74-c9ab-404e-8642-8dd178cc8501" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d6093e74-4b01-4627-8cf6-48cb001d5f12" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c50f5219-b5a4-475b-843d-dbb095949798" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e2bef43d-969f-4faf-b72a-4b821052937a" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75f24d39-f1e5-4924-8e57-af175133d6d9" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3384fd2a-b0e3-4665-9c03-ab897848d72b" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bf185bf0-dddb-49ab-ab96-93e50e1929e4" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7c988d49-6f32-40b6-837b-06090ad01086" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0f3e22d1-c82a-4d40-a558-903c1ce29fa2" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="10d14cf4-3408-4371-b33b-289a1b609a80" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4883f24a-ab9c-4958-afb9-c7d434e3f3fc" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="799267a1-9834-4061-ac13-1d72dcb89f79" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46a344e2-692c-417f-a88e-523d58d1b759" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="afcbd399-64ae-4d88-8499-1d2c7ef96859" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05ef3fb4-7ba0-442d-acbf-46d0042ce517" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eda6587b-be2e-43e6-a002-f4f59c99c221" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="32ac8f46-4177-419f-8f84-94d41622e487" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="28daef0e-4297-4614-8122-ace991c33492" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3683a8fa-da53-4214-a69e-0cb590b45b06" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="13ccfd56-6c9d-4f77-95ba-e073163b626b" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cecbdaa6-f23f-42fc-9b69-c472b0c74fa1" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="97c32e50-7b62-4e49-a88c-e319b06da888" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2017f497-97c1-487e-b4f7-c5cc537cc835" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6705ad8-1a37-4815-8416-f60ca09aa331" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7fddfda-273f-4945-be4a-d86f06b65525" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="302da7a7-b96b-46b4-8298-8f4808b65602" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93adcc84-13ff-4b45-9fe7-f503584f0ab4" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8192d6a5-af0f-491d-ac92-78e3ca6c9092" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e87a48d2-addf-4051-8b8e-be34bcae7808" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ce40f3e-0cde-4b91-bcb3-d813a31e92a2" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="60d985f1-dd3e-4e92-a816-757c4ab78314" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23d97a3a-dc4c-4187-a6bd-175ee999c84b" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f560b50-347b-4c51-b029-ae3d24d3caba" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b07ff15a-6b93-49bf-a63f-24b3ee8e99a9" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8e58bbe-850a-4b20-a5f1-e98b8d829082" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d5533905-0948-449e-a218-22f0fb91d930" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="830592b1-c28e-44cf-8dba-48cd1f1c1553" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d9872af-d22a-4e55-86a2-918ef026f32e" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cbb8d98c-80af-4f2d-8c1e-6f29ba70fa1d" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="28893fe5-afcd-4cc1-a0e8-7d162f06c866" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="47e5def6-2ebc-44ab-a8ff-ef43a17b7537" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b82c9f47-30fb-4753-aa9a-4ae7c343d14f" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3393e44-a6e7-4c57-a7a4-9074117a902b" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ab3fa09-df6a-4257-9a6b-f6134419f67b" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73e25053-0e33-4ae6-b89b-cc4637a3779d" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="96903b04-8c90-4491-aba8-1d4e53a3e75c" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="db05cf48-79f5-4565-8d51-4022f414c85e" name="Группа элементов №1"><level2 id="85a38f06-849d-4bbc-a1df-b9c54eb82df7" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="655217bf-4a8f-42d4-a8c4-ae3b8bf1b2d1" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6fc43f8-f167-4e15-b059-c21430fa82fe" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bb5a575-ee45-4966-8443-0778a459f3e7" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fc32946d-11ab-4078-842d-29624bdffb0b" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75671d43-25d9-4b07-a454-584fca178079" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ffde5ff-7d7e-4b10-87ea-47a776002ba8" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4fc1531e-d583-45dc-b619-fa95ce48aba1" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="124ccf08-1b0f-4161-9e9c-865213fab149" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4762863b-6883-4610-96a3-95e0083e220a" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2fcdfe77-b8c4-45fd-9d66-853b91273787" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d23e6668-c446-4206-871e-162a4fb52ebc" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="79fe07bd-9e60-49cc-9474-effec9d76df0" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c762c661-513a-43b3-94ff-c3b590ee7e09" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="219ee77b-2879-4bef-92b4-f63316891334" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="178ded0f-2829-4223-86f8-0de4dc10e949" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7987768c-db83-4235-9876-b1981e239622" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3df44043-7f29-44ce-a169-a0aa1eb4a6d1" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b6b6ef79-986b-4f50-9498-520a850de93a" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="866b0eba-d0c9-435c-8ac1-4fd6dba1c284" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="60fbea68-dded-46e0-8aa8-ffdcc3ee5975" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41d88e0c-3608-4082-9b73-0a83fad479ac" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="812c2672-a526-4ac3-b6e3-b51b70bb0e2c" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6854838a-5845-43fe-a6e2-ac9100c57d35" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b25d1056-1c1c-4a37-b570-5896e32ecdf8" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6b9e2627-c4a9-4f32-9697-1b552945eb7a" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ffba6666-aee3-40e3-a17d-1ba04f916ad4" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3dff659a-5961-48e4-97d6-4c48b03ed494" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="28abbd53-b7fa-4a51-a27e-20f230487563" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="227f9a21-089d-47f5-bb2b-f1e978cac659" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20628075-97d4-445b-a87d-15609b957679" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7661ce8a-bb04-4c8a-85a7-4adc5a6a0d13" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7b29c18f-5067-4340-8cef-59b9d9eeebf6" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68645638-3a2b-4125-ba52-fe87ddeb115b" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a18382bb-93a2-4ad8-a883-7ebd8063a055" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="956a466c-7c3e-4762-9a0a-4bc7b3655c5e" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="436f4be7-3abe-4e81-ba24-a83323b01bd3" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ae0b8b17-102a-4197-aa09-f4af80bc2c0b" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6575f928-b0f8-49d5-a02a-341b0a2bc8f9" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b7bf610-10b5-48a0-954f-13fbcd8000b9" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f3281399-e072-4cfe-a162-aa51119b449a" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9006790a-c3c7-44a0-bb6b-8cb84fe4fa6f" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="209dbe3f-c24d-4d75-990a-779d6732bb7a" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d40fa0dd-404a-4fa5-b42e-d465bfb417eb" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f95b23df-0f03-4318-8c12-0cf7da78c079" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e09a1023-e0b6-42f1-b01d-97021a2b8fac" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="656182ae-f230-4047-923f-1745f84d4c44" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ea0fef0-00da-456d-ae91-1dcae04d6303" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da0086d7-f58b-47eb-84cf-064751d6dfda" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b7b9a365-c3ef-4c98-94f4-67414febccaa" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9cf0a42-9962-444a-a6ae-5a05c38a7abe" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2eb94d8c-de0e-44e7-bb36-c9dbd290cd9a" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d3b05ade-b1fa-41bf-874c-73c622cc9f9c" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0449ddf1-4fd8-4f67-a017-c162b00fc553" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d799ef7-ff7f-4c63-8621-b8f554c3017f" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70f92fc4-6110-41d3-862d-169e4a677e82" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56efc5c8-d028-4fd6-8129-92ce91b4445e" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7c9a3531-feea-492a-9074-51c9cd50ea63" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="346c677b-6e18-4b76-af0f-74fd309716cd" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0ac5c499-2bc2-488b-9725-e1a69bbd57de" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dae753e9-9317-4a33-be72-2f16cc7bcbf2" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b0cd4a38-f48b-40a8-8a56-79b0c82b3a4f" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b01074c0-cfbf-4d73-b421-412f1e045c52" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73e6d020-2edf-4387-93b4-d2384eacac37" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e4b516a4-adaa-4eb0-a26f-ef90f46c2e21" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="15d09267-1f78-4345-aeb9-d9aae9f9544c" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a28e12a6-0b06-4c90-a49a-18145b928570" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4460fac7-380f-44e8-ab61-cb6fb5d1f8ca" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b6fe00a9-bb14-4fde-a1ff-1094dcc6f539" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="051b95f2-9b7b-4089-8220-f5f5eca9a75d" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3deb5296-a246-4f07-a84f-ff2d671dd434" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c38b8fae-dd9e-4556-9670-69e0cc2e2ea4" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09e13272-d3cf-4726-849d-57639cd1d3a2" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c56fbbfe-7f2c-4c62-9fbe-4583c39cab9f" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="469a1be1-85c0-462a-b607-0d1e4ab6adb5" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="612be580-0bba-4e79-a31f-48e6c33b40ca" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b36f26f8-63b4-48e6-a766-5599792d1890" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="752488b4-278b-468b-b3e9-02304f5e92ca" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49122d47-a217-4826-9bb8-9f54aea95aa0" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a0da49b-f999-475a-b46b-2cb6e5148bfb" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2c1a9550-319e-4eef-9e91-caabe31782d3" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c0f04358-ecc8-4c17-ab3c-0de5e16cad89" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fd5356b-d119-4643-bce7-efb42fb19093" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3fb95275-488e-4444-8a56-74084050d8a3" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea80f4b4-ad17-4bb4-9dcc-b465904c8c5a" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b74365a1-fde2-4ae1-9b6d-da74445cd7e2" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8070c879-599b-4354-b6cb-b509924718cd" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ae5cd9fc-61f5-45a4-a1da-2710dbd4231b" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e5e2757c-91bc-4fc7-b101-9fb269990b3c" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="116d7f9e-3d28-412e-84f9-2dd685d17e6d" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d6d3bae-9943-4fc5-bd78-4f19747a5296" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68f0b46f-0dae-492c-ab0f-13e84df8a1b0" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ef374cd8-0da5-4ea6-9f77-68f178998a1e" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ecb5b443-e125-4ebb-99b6-0ff418c5044f" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="283b0d3f-0bd4-4b12-a36b-e365c724e47b" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="838065fc-de27-4afb-a44e-96f0ab1328e8" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04ceb342-80a7-4410-b173-19437dc08c09" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1d67dfd5-f99b-43da-ad93-699fd32761a8" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e9cce1ed-e7c4-4714-b63e-fc3d187d36a1" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4a916c98-de77-4ae0-8581-1f98615bcefd" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="c1a8ad1a-42b3-48f9-9f35-e64f9832ac9f" name="Группа элементов №2"><level2 id="f6684317-c5a8-4937-b107-12ccc800f202" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="08e3c2df-dc9d-48f1-ba7d-9da7b8e1fd74" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f10cb49f-62ee-4fdb-bfa2-97567f1b1ed4" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68160e75-6b79-4d7f-b0d3-a06557397270" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="204948ee-f4b2-4491-bd5e-a56f20a60972" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fed5223b-2944-4050-8115-1f244ac902ea" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3c14488d-518d-4d20-ad92-8771a5848c9e" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b50f0379-4c08-44dc-a581-b4e19ee5c275" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a7af031-c993-4c71-a517-4c1d9b894b97" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d216dd11-4b02-4491-ba0f-e69e64777bab" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc044a9c-f22f-4800-9a3d-9b7efbbb7bab" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="42f7905f-eed2-4f01-9084-776316fb5aa0" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce9f6ba5-0a1a-41b1-87c5-39a5429efab5" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b68d3ee8-6a23-41f4-853a-a746b0a55957" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="90f7cf01-c8a4-43c3-aafb-3eef68ca06d1" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fc5fbe90-7c5a-4153-b5d6-a89d918283b8" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="152df665-184a-45f6-ab18-93b9ab9c5a17" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6d574544-5f4d-4f9f-bb6b-d3a1ee0f194c" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f612b95f-8921-46a5-9a34-34b506346881" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc181613-5031-4e5b-ab33-7c65372d8c39" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fdced026-b70c-4d43-8e8d-2e1c3a2cebd2" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b155c771-fb91-4ae2-874f-38d7e46514d1" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f119ca22-96ea-4da1-bf43-439701e3aad6" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="321e8bd0-caac-4339-b25b-85f33da1d1f0" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c37973ef-2320-4adc-9ca5-c9b90254630c" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dfeb38ee-6f89-4d7f-8c5b-1d05293d08b8" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2496b142-d7f4-4f4d-b980-164e2fc3c868" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7e6e1ed3-3d71-4417-ba13-4aced02f2f65" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d290558-e110-49cd-b1a9-0b28e80347ec" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce337ea8-b274-45d4-9d68-52feafbc2272" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ab4a900b-0b15-4c26-a5ed-c328edd93a20" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99d29cbc-f9b9-4215-a2d5-2024ba6026ae" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bccc39a2-3a56-4c99-8e57-a38052dead04" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe272a81-bd5f-4ce4-b6ca-27db6e38399a" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5199e0c9-1860-4a5c-aa12-c78e039da51c" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c38cbc32-a826-4eb0-89b7-1517f568049b" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4dfb7c87-5d77-4bac-a001-bd724f47165e" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9030acb7-627b-45b1-ae8b-b05162b6d489" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b17e24cc-1054-4439-b072-396f0f71c6c6" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d074441a-a559-421f-bed4-d594ddfa5e26" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e19882a7-a60d-4314-8d29-ed6ea3941891" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14f49a03-87d0-45a0-aefa-56da35c75c00" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cf57f8ea-0769-4701-b832-5c2a29658cc7" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d73aee6d-abd4-49d4-b89d-f82d26d91fe0" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="182d5c71-df16-4825-87f7-9b91ea80f334" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bca3eca-c715-4317-8cf2-8a37cfe03e78" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fcaaa85f-c861-4a0e-a7f6-360667c64dfa" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ec403c4-bc69-4159-9b80-1db6e8137103" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91948278-f28d-42c5-940b-2b7323af9725" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="effd7c90-962d-40d2-b7f0-50e424d71afa" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70376725-151b-4862-a6f3-0873ee460c7a" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4d7102ed-0a19-40e8-a2cd-3d6c8e3d0f25" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ecccada0-9c2e-462e-8852-f8fb6e9594b4" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93241eca-18c4-4d4b-b871-fef4979a1776" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="45dc3ed4-c05f-47dc-8214-52e3ed140c95" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="15bba4c1-555a-424e-8094-e47239d49dfd" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7217bb14-ef37-4668-be76-0ea7e03e3f78" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e330f82e-b78a-4868-8865-6b7f58928864" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41fc091c-ac92-41f7-bca7-3f273860b56d" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9271ab2d-048f-4541-8ad4-02a78bce7705" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="59a6f734-74d4-401c-bf37-fee155634ad4" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c183a606-4d55-4362-b465-efeb6558256a" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="366c916c-779d-4d72-b3c9-d2eafa06041b" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8c6c099a-9068-415d-b00d-9b3a385881fc" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4a3fa49e-1956-4c8d-bb0d-6e39be8047ad" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="84658f60-1554-4a4c-9e16-c42c9e9c98ec" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09cd7d7a-8076-465a-a2e3-1fa57255b54f" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="643f9b8f-9765-477d-bb84-ba12e2f6f5ea" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d04a99d1-dfdd-4bee-a2a7-0b3239c24a61" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ac424ecf-2423-44b1-aeaf-961c94ef0ccd" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9cf949a2-1d5d-4f37-886a-6959663cfb34" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="feb5690d-dfd1-4596-881b-4be8bf7f467b" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c3eebce5-3621-42ac-910c-c14306bba720" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ef6ac78c-dfc1-4628-8922-21197d990693" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="151592c3-6e3a-4e39-95b4-268af65dbf66" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="616f7647-64e1-46c1-8789-9c88b7d4aecc" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="549ac239-d433-4911-ae93-4d25ac9529e6" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fefef874-e3ec-477f-a795-dc7461412b02" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75b226c1-6908-4164-a3aa-d8ec0d685b3a" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da000152-526c-4a30-95e1-a652d6fb726f" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8226d7a6-553b-4e11-a45a-e78673dd851c" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e719bccd-78ae-432f-ab7f-d2bb974bcb59" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49274e2f-144c-44ad-9ba9-2738a33f4938" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ca32fcc-44e8-4453-abf6-3014ee15523b" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4641711c-597c-42f2-8550-f75a9ea0f010" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed80b196-8d11-40f1-aaf2-69d606edcbd8" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="52b360ff-8ad5-4d4f-87f1-2608201d4c72" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e0b1ccbf-740a-438f-bb72-f4e90d2cc921" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed3d9e70-b1cc-4c05-8994-23cfc9b3a342" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04598b36-cb91-422b-aa9d-6522f910359c" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d992f81a-e141-4271-9525-7c1dba995ee1" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eed8e0b7-ac77-4b9c-8367-ca51f7ae15df" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="71bf21f2-fb84-4317-b5b5-aff401345ec6" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b89e5d63-6ee3-4905-a67e-0887765cda59" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="39455fd3-6331-49f5-968d-5d154cb437fa" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b2cd7e7a-688b-4637-a983-46afd34a68a7" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fd558603-6985-4f84-9afd-6824582cf4d8" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a246559-5b16-4060-9ef9-da32d990ccbd" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c11a2226-33c6-4837-9f0a-db12df403f2c" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a6ff4cd-c6ae-44b2-86ca-b9f2e5703884" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="511eb676-b4e9-48ec-a946-07824f614b49" name="Группа элементов №3"><level2 id="c0e33a64-35ae-4081-8790-4ca17e5715f7" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="184adca6-3ecd-423a-8e47-8e79b016be25" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43de96e0-8795-4e72-bfd1-1acf5676367d" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2e20d765-48dd-40d0-85c1-063b2c489645" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="567555fc-a254-406b-b51a-4f76b7d2330a" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="37341106-b9d8-4dc4-8e00-5dd54287b48b" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56478b2c-3b9c-4692-adfe-4973591e6559" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d5714e9c-7953-45ab-98d7-15922cc3beb0" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bad15b2e-e760-463a-946c-0cf1f1d62e00" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41677777-5c53-4929-8c7b-1e4cd0f368e1" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="456538eb-f9f7-40fb-a8e5-7ce9ccac6ae2" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="71bc7ecf-828d-4833-b124-4f4d99ad06ed" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8291d4bf-e42e-45ee-831b-115dddb03874" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a40d3eae-d0ed-4ff7-815e-7ef73bdbc99c" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bebcb79-c682-4008-b8ea-2537c6070eb7" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="094c470e-f8a9-4415-b540-74f5c650bd1b" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d58a580-8f26-4d62-b418-df782fecb8c3" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8b10ba5e-bc75-4598-9d58-58c8e9e54c3a" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="be8cbae6-718f-409e-93d1-c2da5a4c90c5" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9d82bb6f-520b-4218-8146-7dec497083fe" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ca7970ad-2d11-4572-9507-821721444328" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a211359d-a17d-4690-bfb6-0e2ac62be0e9" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="751a6ba1-8e13-4402-bb45-0d5d33913e2d" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bd6fcdb3-4548-4b80-84cb-8a11011d21df" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3814475a-588e-4661-baf3-d98d35550dfa" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1db4ac63-4297-4758-a795-30386d60bdf4" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5e017af5-27dd-4633-aa3c-716643b6a747" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bb4d8313-a50c-454c-a077-aa870462f993" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2ef57e19-0ea8-4fe7-9f56-4fa0a7622009" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de1f4921-2f1a-4973-8738-05856492ea04" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7b522f59-b380-43ad-9596-9ed5dae1c88c" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6928e48c-b076-483d-8d8c-8609c299e3c0" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e84e412e-21dd-46a0-a3f9-703be1ac0d01" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="34e2a091-542e-4e0c-a810-57f25a1fbe52" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="459cd31e-1f3e-40bb-a9cb-f249e177423c" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="516cde77-35de-4f6b-969d-84e4704a498c" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4603caa4-183a-4b20-ac46-12170712625f" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5651c78-2edd-4037-adab-7b0cdf63b486" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1d2632f7-71ff-4f6f-be62-989a869a8c42" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7724d0e7-c67b-4dd7-b8f4-540239b8e64a" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1eeb79c6-6692-4f10-afb2-bafbd0ae2129" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dba26c0b-2b68-4d07-881f-8cc359ed39aa" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="94dc16b7-2b8a-4753-880d-e1dd967507bc" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="33285db1-2a3b-47ac-924d-21f5946beb96" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a451bc1d-033a-4b6e-a3d3-40b24f29028b" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7345cfa8-8398-4db5-844a-be3adbc18a93" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0fa55011-1fe7-429f-9f55-c9fd107825db" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="139995e9-4314-4268-881f-36a3e77fc18f" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa7f0e1d-39dd-4a8c-9862-80d6b1b37ea3" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73f963aa-0793-4f25-a12b-b44e906143ce" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="795867dd-d802-4c53-917e-75a49402cf6c" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d87ff07c-0f88-4a4a-9ae4-2ad7e7678dbc" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05ecf493-3e88-4b63-9241-c44924266e5b" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="89982c7b-6ca6-439e-a0b2-e0da7fd518b5" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2e67c1cc-940d-4bb0-b8d4-f2272379cf2a" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="176af6cd-71fc-40cc-b6c3-991a4663ff07" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0f35d0ec-0123-4e0c-ba35-c02a69bf62db" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9183ed8-c6de-4cfc-988b-786dd861b82c" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="79505bc2-baca-4259-b03c-7f16394786d3" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05d9c03b-9a38-4b9b-9ce5-24514a857633" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f169e0b-5444-4363-b63f-e863aa61b5df" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da1e7ad7-ffcd-4d5c-8921-e876aa069e36" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2bd22d3a-6652-4362-ac32-748b01929cd6" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eb92e1eb-e0db-4050-affc-6ee1ea365a03" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d91ccb50-0b4a-4056-88af-7ab0151fb777" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b81f31a1-d972-4787-b0ce-4677b3373059" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2477b5db-849f-41ab-8549-3daf53a392b8" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0f700588-3e0b-47ed-827a-6fec14138f7f" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3076d4f8-f2c0-4f8c-89fe-6b52af47698b" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0af748ec-b1d7-4585-9680-d4b700e9599a" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20188137-2d8f-4988-bda4-904efeb11c11" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="90cbe9ac-9830-4204-b5f1-0500a212b158" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d931449-2ce7-4146-8dc9-846291cc69c7" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="85561a40-9e41-4941-be42-e986d3b356e6" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f5717475-e6ba-4d00-8b4d-78f4f9e26e8e" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cb3ae930-1c05-4a2c-bfa6-7e10850121bf" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99effb26-eb43-44b0-ac82-89a67d0ee424" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0a3381a1-c8e6-4bf9-93bb-59a86a85c377" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5fd8f0e5-7b23-43cd-8cc3-c3d723e46a35" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44b12e4e-9e36-4da1-82cf-04304828702e" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d32d98f0-e08b-4689-bcea-dba78e5e48e0" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43ceced5-5327-4299-8b70-d5e0e9f123c4" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="62aca678-2423-456a-a0db-362b6eca354b" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e72515f8-b62f-40f4-a225-4ff2791b25ec" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0b6d0b4e-76c7-44ce-8da4-001bb21c3f1d" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a7481607-856c-437c-9ef6-6feeeecb0e94" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14870e58-8301-4caa-98f0-5690b19e41c3" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="27828f61-d4e1-4c71-82ef-a6eca0d55c91" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="548e4601-0759-4aac-8934-5e0a2c10de41" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d0e68ccc-f0de-40c6-987a-c7da590ad555" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c03b61d-50d7-46fd-8c94-602d06ab3c45" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="19c0d60a-83d8-4cc9-b782-58339bb9efad" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a53b955-bdd3-43cc-92cc-b1959e0c82d5" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4b3e48ca-437c-4ce6-b7fc-05915a28eec3" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3390bf81-c8d1-49f7-94eb-ebc7927765c4" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e42ebb4b-5694-4b11-b57e-d63bfd45d628" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9e39719b-169f-4580-9b18-095608bff03b" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2787b019-46d4-4823-a961-dfa42a2df81b" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eb89bd24-e8bf-418b-bf2b-dd7d5dd9b193" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="79970793-6a0c-4f15-aa1b-fb98f24d5f6d" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="25b825e4-eed1-4631-b9b3-8d6c773c5fd5" name="Группа элементов №4"><level2 id="caaa51e1-20ef-4aa6-a91b-4e10bc5249c4" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a3eb45e2-c83b-441c-bb11-df02297289c7" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a21cdb29-32e1-4106-8a9f-9536233fdf17" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cdea13b9-4647-4a82-b28a-d687ef2cfc2f" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="06c322f7-d91d-4c19-aa04-f45657b4f8c9" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2dcc1491-a9ef-438e-8532-5254bb6bca3e" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c41c3aef-9ca9-4475-812f-62d6bcc60c2a" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2147fc1f-6b27-48ea-a53a-8a2eda65f079" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ee15758-6d4e-48d8-b6fa-4aa4ecc51e88" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64b0eef7-526c-4080-88b8-12d73b26cef0" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6fa5343f-020d-4d97-bec0-533d70f067c0" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cfb5adac-6a2e-418d-8935-61f84ea1786e" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="07560fcb-294b-4530-a66b-e465d7d205dc" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9caafa32-f0ca-4510-8152-71e0f6b81d7f" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="22e03f08-d078-4430-9c07-efa93b98ac6e" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed7a7ff0-2d36-477c-ac56-9a44f389f4cc" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3686fa65-e610-4f74-be13-1f07eb94a879" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f048ac6f-04ef-41dd-b664-ec4a9677ca50" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04211337-942c-4a9d-8cbb-51d9e00337d9" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="77e4e45f-5a74-4f85-8692-9f1f7106055c" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c77d0fe8-dd3f-413e-9a8a-245d2bd77cd2" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3cb932e5-3490-4e84-b8e2-40d545ce2d03" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f91bab31-3fcf-47c4-95df-c562e0554750" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0437fa9e-afb1-4bdb-b13e-c098cb02ffc0" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1be278e5-55a5-4a73-bb51-227b91a486ae" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="825d7dc5-5d3f-43da-bbd8-bfec31a75ec8" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4acf204b-774e-46f7-8da2-10429bbc206d" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="86f1ae8e-af1f-40e4-971f-a2f40685ca0a" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="536f5e02-6043-469f-8534-bacfe117ca92" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b56dd13e-d451-4d3d-9ccb-d49486c7a061" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="255a4c19-7e18-44bd-a834-6ccb4abc04a3" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d347bdee-f050-4327-bd1c-775980b2bcef" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7e9dd05f-3c1d-4c4a-bdfa-f1a872070de4" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44e0c5a2-a8e9-4fd3-b3f3-dab24cfc1042" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c68575e4-c356-44bc-bb02-07822397fd01" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8ee9b94-4201-485b-b307-9a7275d20b85" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2963724-cdbb-4017-a56f-220fcd712fc2" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0cba1669-f5c9-4606-9a4c-2c396d4e34b9" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d0552ff-82f4-4a6f-80e0-e993273677c5" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ca402f31-111a-4b3c-a06a-16e67ec4d41e" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="24671267-5762-41cc-afb5-3b06fb6d7c0e" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="52d483c2-11db-4a29-961e-6d15ce14e241" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ac66d70d-3391-43c5-a8d7-15ac607c38dc" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ad0e9b7-7a93-45b3-a2e0-f376c22eabcd" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e4cf1699-3063-4a65-a36f-e970fca20608" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="83d91945-4a06-42d8-93d3-931af312ff4d" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="40a4e642-9cec-420c-91c0-a2c26e949fa3" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3089bcd6-508e-46d0-9f92-7c861541ac41" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba99e932-c3f2-491f-bc64-4b3b670e7c1d" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cd77573b-7dab-4f0e-93b5-1669658ab854" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6e312865-0579-4685-a302-4bd03a6b4b85" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2e5fba4-2ead-4143-a9f0-b96d0e3ade25" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99ce1355-989f-4c07-950b-99d78c77a907" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c42ddadb-a9cd-4307-8399-62120e4ac9bc" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af009359-b000-4b89-b7ea-7ce6d0017ce2" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa0c2575-a7e4-4852-ad73-ee90ae52e89e" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="818f019b-8be6-4bc2-9485-ba308eb88dd5" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1fbdeffc-ff2b-4ace-bd3d-a9f6a81dd395" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="744b70c5-13e8-41b5-bda2-455604095f5a" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d1f93dcb-2fbb-470f-bcdf-31457c6d314b" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="15f0c365-e78c-4221-b4ac-288bae0ff7a3" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="339be0d9-1e6b-42c9-8971-a9d75b16badd" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ae103482-bfa6-4985-aa22-88266ac4b169" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5d5a6af-54c7-49c1-87f7-6fcf2a84bdd4" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af1dc3b3-c1dd-4237-ac1f-3cb9319ca355" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f8849dae-59e2-4613-a756-3d82911522fa" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="014ce6db-a377-46c3-ae11-d183876c3212" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3273117d-b7c1-48c9-8d0d-8ef40b95e6af" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cf63e55c-773a-4d9d-8799-d49727f8d029" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="837e8f06-bb03-47e8-aabe-7290eacd070f" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b8fadf48-4cf9-40c1-8b6e-d7578a65d6f6" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="72c8d38a-3f50-4292-8161-86e388f7377c" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="71b75eb3-5808-4be2-9e93-f4c9d29a7c68" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e5ed259e-fa82-4dd2-9763-8397235bd026" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="137f8efe-a53e-4f33-8fcb-0ad1ee21496b" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29a6ac85-747f-4ec3-8eec-cd81d4ace3b8" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e3bb7889-99ca-40a2-b38d-22d008941b5c" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fdd7c7a5-5da7-4f34-8a72-dc42f01a18e3" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa979fad-187e-437a-9213-7d9aa2f2ce64" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="42041bbb-4b3c-4d23-925b-b5ba9ce2b138" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81a3414c-5131-4376-bb52-c28bc88dff07" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e5bf1566-d947-473e-8079-1da55f9e9c20" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2e9aa538-b153-4948-8765-1bded231484c" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7dc4b5f3-3aec-4e8d-865d-fd736f7fc45c" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="af35c681-bf73-4740-9fa7-afa09ed95e62" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c423f91-4345-4150-a135-0da9466bd38a" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="97d73261-69fc-4360-9285-3c575b0d19cc" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="54659224-3cc8-4f37-ab82-eccd46288ec6" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20b2a8d4-6012-40ee-a289-6f5eee535046" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0259d9b9-2ba7-4b42-8d01-6ffddcbaee8f" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9f49ded-d695-47e1-b5e6-66db315c9853" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="550affd3-eebb-4ff7-a0a2-ffef695229d0" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e953fb98-2f82-41c5-b373-6a4560b50752" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2047b1a5-bb02-407e-bd53-b082887d5069" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8485ab71-5544-4754-bf66-421613a01aff" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7b11128-df01-496a-99b9-9f24cb172fc1" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f678aa37-b173-4e85-ae73-e0bfd03f3307" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9bb7c737-1c9a-439c-9ee3-3bf5048f617e" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46b07895-c062-4a83-95ac-19450ebc2fb9" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2fdfef94-636c-4406-9bbc-210fc31b70a8" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="3a3f04ea-869c-4508-9641-b3d77062fde7" name="Группа элементов №5"><level2 id="b36cf5e7-3d23-4beb-98cf-4c0d5330a327" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eba4961c-888f-43bf-8c31-e1a4716faf9c" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73954c82-0f66-4490-a848-92b7ca91446d" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9f5e4b5-1dfc-48e3-8d15-1a11013c7c42" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="844feaca-1408-450a-b3e7-bba6df1408f0" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4fb9c130-9b9a-4e00-ace6-b85dc41593a5" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df7a1b0c-31ce-4317-ae1b-2e29c60c45c7" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a869508c-5a11-40ee-bf57-bc16b01a741d" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91c086d4-0b4f-43ce-b773-031e2fd72df0" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1d00eca-d49b-4858-ae10-bebda990545b" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d09cde8-f5ff-48e9-840e-ca8ac0f24540" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4e996d0-1cb5-4f70-92da-e5d2cc26436e" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3f5d0ae0-6c6c-4375-84ec-1a5d736271c7" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0ebc7ae-56fd-4fec-9699-8aa600ea08f7" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc44c944-06db-4fd9-a034-caab60cdcec4" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="10de166b-80d0-4246-aaea-de933ef24578" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8b9b8c7b-2f5e-4db9-a2a3-0555bb0d6740" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6b4589a-288f-47d5-8cf0-c7fe364e3bd2" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1c35f3ca-9e03-43ea-8a33-0851e2ed1d00" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="35e308dc-0171-40b1-ad2c-d7c7855f166b" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="405f45bf-d8eb-4cf6-8f1c-58e89398402a" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b265d07a-4a23-4972-a66d-d9331217c78c" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ca895e84-d993-43d5-af9a-63ad4cdf2567" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed414fc3-7902-4223-91c3-12fa7f59f422" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f1fe9c67-e6c8-4297-8b17-945d1deca79b" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f4c09934-b16b-4c1c-9cf8-68964777165c" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="40f03b8e-1e79-4de8-b830-b075dba196f0" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d7a7c909-016b-4414-845e-08cf497bd622" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c10c68a2-ba07-429b-96e9-3317c1dbc3d9" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a0cae847-fbe4-432b-9f17-6500ccc00ac9" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a8dcc98c-a340-44cd-a85b-fb7ad2ea3299" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93f0ca6c-5e7b-409b-82a3-e01c16f6cafe" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b8faf3d6-ba4e-4fc4-a5cb-4eef00d49ddf" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5f7ff499-cd3b-4f72-9c12-40eb40f392f4" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b157d49f-16ad-4cd4-be4e-b8fe5a631f6e" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a37d77c-7f83-49e6-99f3-473fc1ea3f49" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a3326c3e-d02a-4e6a-ada4-2067febfaaad" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8c141e8f-a418-440f-8c94-ff6814116853" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1ac151b-abd5-4334-9ee2-a411025cb3c8" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de63b6cd-73fe-40a7-b3ea-21055abf0d48" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce12133a-d723-4996-907b-522fcdbd788e" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c366adfa-abfd-4065-8051-b8164f0fb49d" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fa81490-2624-4915-9604-0d1de94ee774" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43d91adb-daa9-40eb-a939-32aa99554845" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="002de547-8755-4365-91a9-b2ab8b5076d6" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3f2529b7-b391-410c-9346-44eb7204f8a4" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c93e25be-a5f6-42b0-94b2-0c71bb865133" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="136be4c3-0a59-4043-8a64-0ea5d82baef3" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b354edc-04f2-402a-9401-084cbcf966da" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="382e549b-0e61-49a7-9057-d891a9ee65e8" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1009df69-dfb0-4592-ae74-757d2536c833" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a4f14f69-bff6-41c4-b45e-b65860db2626" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="408d8764-02f9-41dc-816c-3f77dc41eaec" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="54cfa608-3b2d-4b10-b5cc-97491e308bbe" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a8c288ac-b47a-49f1-84d9-da3671eef701" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="db484e35-804d-4212-bfa9-ebd10f1827d0" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d189d18a-0fcf-427e-b991-5c423fcb6ae7" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8161d7b5-4f52-4ce1-84f4-de2f4440e7dd" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bf469a7-4019-43c4-a3ec-f6f75d0445b9" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="733608cd-b4e2-415b-8b87-05752168bc2e" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a872d8f9-2997-41d2-b70d-1ed57e0923d6" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eb85e017-bba0-4ddc-91e6-b21fe5b9d95e" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e32355af-6dbf-4c8c-a3da-1e78e97d28fb" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b08e3421-c653-457c-afa4-7302cf0e5beb" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c6c32e4e-9e93-44bf-8cb5-6826c0c26958" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1a2820bf-aa94-4b93-8b6a-d98616773566" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="918d3220-01d5-4e12-8472-4438ea285d38" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a0a761e0-9c83-40b2-8fad-c2481ecbc20a" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64be586e-cee7-430a-b469-be33321f7297" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0ddab83e-0a0b-4cfe-891e-576d35edd9a3" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="310a5096-ba2b-409a-9b2c-f8f377257f93" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9a96be28-482a-44e6-9cef-d0b439ca84fd" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="418c96bf-52e8-4fcb-940f-475e13339078" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="66a45959-e02c-44d2-9cb6-96dc7ddbcfa1" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="51cabc72-20ba-4ea4-8b92-663f7f6fc603" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="95bdf822-491d-41b3-bf6c-839bd5c9a635" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="242711bb-9c69-41a1-98d8-28a6f97dfdb7" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc498a75-2934-413d-9982-ef0fe7cd32f0" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ebddf11-f68c-4078-adb8-48b69383d834" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c8a38391-932e-45cb-9d84-8c038a754ae1" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7013a052-27f7-418f-b4fc-20495ce54a42" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cec6987e-87e6-4dbe-8d35-3837d90c704f" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f7d7f9b-d647-42b9-a0e8-f0c22eb46cbf" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="024e341d-9ac9-40b2-b7f8-c9a995ae5915" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f6fa1c5-2e1b-4e08-9f47-34e9640ef097" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b443e6b6-e968-46b6-bf38-46c6286f0821" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="11bdc211-39d1-4358-a3ac-728efd245483" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09354387-714c-4ca8-b8ef-34ad3629dbeb" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="71b75170-4ae1-472e-8e51-9aa3aef88b32" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f0bc6358-a432-46c5-b7e2-dd88d5111d2c" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="172a1553-c483-4fbe-95b0-7e9eb7fc6752" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f4b1aab-e25b-47e7-a2ff-c3c3edf83806" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="85f4c361-80ed-4053-8a6e-efeabb682198" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="44c9af92-9937-4c64-85ea-1bd2b7841bed" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eb938005-72ca-4342-abee-3ced5428e96c" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93d6a022-631d-41b9-9897-a1ef49ea90d8" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e46654ca-8fc7-4043-ae36-e360534c52e7" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b86a71cf-f75b-43bc-ba2f-57bd7874fb85" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba8ccb8c-9da5-48fb-86ac-7669429f92bc" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a1456a2d-01b4-4959-8ddf-24e22a613eca" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="6df6074f-46ee-42d7-8695-ec9bc411898b" name="Группа элементов №6"><level2 id="50d2de42-3a8f-4a91-b8cf-26dac837fc99" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a02ef736-6623-451f-a8be-5b3a872dfa3d" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fa972f10-eb39-4bf4-ac54-c6e9627c604e" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="82350a80-bd4c-4300-ad68-8a76f0010d4e" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0b47c057-3aeb-4867-b64c-831d3a969b82" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8bb68bc6-93e2-4f09-aa46-958838d98a87" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81bbf61b-f95d-4ac5-b808-672dce32339e" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="de52033d-9161-4910-a3cd-517e68459f97" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="12cefdb3-4a50-4749-bb8e-78d638e58c51" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8144122f-89ae-45ae-b48f-aa0d11a7ef99" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d9435522-ec94-4fad-b11f-0f19bba33b0a" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1e48249f-0e46-404c-bbd4-d9996b2b6a71" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="06d02fe2-5b4b-4cae-987f-733323dc734f" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="32c0f145-17ed-4a23-964a-17a7c27dac40" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a8ff60b4-d32f-4457-8255-de38053e7c18" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="62d277c6-42e9-47eb-87a6-0d06ecc85c1d" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="675f7070-b6fd-4958-992b-fb4b733c9ad6" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23db758c-b263-44c5-9daf-8476fab2a54a" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b16f269-69c3-4de7-9176-c78065a02372" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="031f3961-1869-4213-ade1-d117e53bdc53" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57ed32ac-36ff-446d-bab6-c66410c17f59" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="213d217f-4997-427a-89c6-02da8101a107" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a5a57c8-300b-419c-95d0-91068c817b39" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8f2b4351-dc6f-452b-81c5-a4f3c5715316" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2325db6b-0cbe-4721-a9c7-941ecf9046aa" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="459462ea-8e8c-4d70-a9b6-e11226f4b0e6" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ddda3ce6-9a7e-4e5a-8974-48da55a705b6" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="934256dd-16b4-46c1-b110-6bfea93adde7" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f8fff47-cd59-4f89-aaef-a4fad5c2b17a" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b8eb63cb-2b0e-4148-b54b-c0724465b248" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9db0d47c-27b5-495b-8f84-51c4f024320d" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="360e120f-4ce6-4154-8681-a8ea49384c6d" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="abfbb57d-078f-40f7-a85c-1946078e141f" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fc30e71-6e66-4b04-b234-e6b103e1e88c" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7a510fde-2f23-43f4-b0a1-f86d222e905c" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14a91ac3-f618-491f-b131-cb0abfa5d34a" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f01e6f4-7f6a-4b9f-a4cf-82071303e6c7" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e024b879-7f00-427a-96df-13d38bb23bfa" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c96375d5-4278-4d47-9f25-03fb061ef6e5" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cb853b23-34d0-4dd6-87f3-26522379e34d" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cc6fd01a-ff4d-431c-99d2-4a316b3dda24" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f2e45e13-7552-4739-95fb-5686567d9fd7" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fcfe32e1-8e84-4060-896f-0529cd53952c" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5b8dcc0b-b855-4e61-a212-9d5a8715c681" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="55085215-14cb-4174-afbf-b4689b02a453" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4e48a6fa-cd21-4ae1-81cf-30eceff62ff2" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3c360bd4-d737-47a4-bb3a-08c671744530" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="222c0a0e-760d-42d7-8a80-88f482eb8ecf" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dac87cb7-3181-4daa-b10a-d55e8309a89d" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="30f50c2a-4437-454d-aad2-19ec6a0f8c74" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eab50113-9fb2-4c7f-ba35-abbd4f127178" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3ad06e90-f169-4523-8a10-4194011818b2" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56f4301b-6efa-44b2-93b4-c801f0bd0e71" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53266e53-166c-4c6b-980e-e727515e0e94" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d8b45738-9206-4a8a-b972-a25719b218aa" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="73e6eb25-d1f6-4705-a42c-0bf2eda5401a" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c05a8e39-1b5a-4127-9c86-fdc075416b16" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00e60923-dd52-4c39-8a5b-cc1bf802720d" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="96f5323b-c3d0-46de-93fc-18d1c6c99e17" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="76cf2979-78a4-45a1-9b51-ae4cba36dea6" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5d57e286-4ee4-44eb-9df3-ee443b3c501b" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d7cab7f4-e222-4e9b-a289-10aced9d8d39" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ce5f9f7-b76e-4cc4-86fd-41d57bca3348" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="47d9d1dd-e398-417d-8b6e-9ad7e484124b" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ba190d13-1ee8-4e7e-8dcc-3906438b4cb5" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5145d84-353b-4797-8d34-6599a0362fed" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bdff38b1-96e0-4a4f-84e8-7b8cf27660f1" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7ab4ec44-7ec4-444e-8466-cd3230dfae95" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c96de628-1a2d-41b4-9032-6a514a3735ff" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0479b95c-68cd-49be-af51-bb19d869b2c7" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="93a0c7aa-5344-4e33-9349-9956c7c86b22" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0b936cf2-5a9a-466f-8fd5-ec860576d62c" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3a572e95-99d3-4072-a446-0c5d4bf92526" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7bb270e3-281d-4176-99c3-ae9b1cffc147" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="109da0e2-4f32-41a9-a449-0b01811b9d3b" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46695b86-378d-4afe-8299-46e76e5b418e" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="604c19d8-89f6-405f-bd7a-fa072108d2cd" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ce1d1e8f-1f73-4714-96f6-dabf50377b13" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cf78c139-4161-460e-9b10-9dcef60e7293" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e432cdd6-d299-41af-9d88-e784285c77a9" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="13bd67ce-8ca7-4fc9-a3b1-1b7b7722ec27" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c3d1d14-d89e-4589-9ce8-af6ce9965758" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5f309985-bd4c-40d0-8b78-7a80885736a0" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="34d43c9d-9b04-42b1-88fb-b4a7cd185c6a" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d0c1a30-5739-42e0-8047-a2b03af557da" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c54fffa2-41ad-415f-aff1-858ac7222bcb" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e154c71c-c54e-440b-8d84-3d88b5f98ae7" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6d8d6bf9-84e3-496c-aadf-34a14c30b01e" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e9bc3432-1c01-4d92-b34a-4dfda601ed5e" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a43c6e42-22c6-404c-8086-444b4821a40d" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2dc1ca81-4a08-442b-b74f-66cd5e6e10b6" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f8eae06-da7b-4164-a566-58673af252e8" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0af688e9-363b-45a0-94b9-c7d785fc7e96" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9845c929-f95a-4a0e-93d4-32fbad0c7f8c" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81972aca-d702-4ae5-be6b-727f89c4a7a4" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a6a3c5a8-07fc-4692-9525-31cfbdfb020d" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ed4c34eb-4848-48e6-be63-793087700b26" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="679ad2fc-961a-43f6-bf32-0012fa4974a4" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3e61862d-b99d-489c-8701-12c167a1193d" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="01aead1e-5d65-4cc6-8891-ea164c7df904" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="2fbe7fa7-6c27-4cb9-8171-78c3d93c869c" name="Группа элементов №7"><level2 id="a40be266-534a-46c5-bae7-4344e5bbed1f" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ba70e04-4ee7-43fc-832d-db76c2ef3a92" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d00f576-1d01-427d-9037-dc36ef037d4c" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a1c788ea-e770-418f-9aa2-7298ad1b1211" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d96713be-13b7-466a-9520-f53fe5dcea87" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3f2ccc0c-9caf-42ba-8774-461cdae70bfb" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d4039829-b1a8-41b4-abb2-21d7fd5d499d" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d083e59-9e83-4137-9c28-7b09750c8839" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="987b1628-3c8c-414a-b1cc-bfb34dc658c8" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1ce3ed71-6570-4273-90a1-963d80e667dd" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f17ae220-0c11-4e02-b3e2-eb2688c56529" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="98ad6960-9636-4087-bfa6-fd853941ba25" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b1eff32c-8279-401b-9dad-2cc5158cc9c2" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00ac771d-658d-471d-ae36-0ba3e198dfc4" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00e8defb-7a41-48dc-ba1a-2fa485b00faa" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a629b63c-5a23-4b77-a837-4eb0981c6509" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23ed2005-84ae-4225-95b4-0b3b85735f76" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a44ee8f7-4b7d-474c-896c-89c77faf4ead" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d1f62a5e-165c-4595-9e45-e9faaa5be874" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1cd8893b-d038-4a3d-a603-1f622ecea53a" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4d7f7d61-5d4b-4a66-9547-112adf652c8f" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="72f271e4-e0af-4e75-a82f-d6cd2814ded3" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc0bce27-251a-4048-9212-6a6b68822d28" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ec3fe986-cbd9-47e3-badb-267535f3bc9e" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="75d121af-166e-4748-90ea-53985333387e" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8959075e-71e9-40f2-b6da-9a770e3af67a" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="483d30c7-8d50-427b-95f5-bbf229957c41" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f6d9fbfe-4fb6-4201-a057-7c92c920f0dc" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d23b947c-b25c-4a73-8d30-98edc7d8edfb" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bd8a8d4d-73af-4714-99fb-2923c84b6546" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="880fe56b-892b-4b21-8e0e-11cdf81abde5" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="df76de6b-a162-4005-abe3-271e6c503113" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0da7253f-9f98-4267-bc6e-85310596f1c9" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6f0e69d3-b4c2-443f-a434-270ae27c70ba" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f7429e63-486d-4233-97c2-446f9283143a" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="300bc62a-e925-4b31-88bd-11e17909e2f4" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="11321934-03d0-4f1c-a5ab-8431be7e76e4" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f25c01e5-492d-4156-9289-520abd8169f8" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="26e2aafa-8dbf-45f2-97b9-e2330fa869d4" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b52790d8-5d20-44d6-abbe-131997ee8ddd" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="76922544-990f-48cc-ad1b-4666b6529368" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f2cdafa-7af8-4c98-8a5f-1ccc34beaa38" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dd95cbde-c0af-4fb5-932a-db4ac1e83593" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e88a891d-4218-43be-8bd7-b3ac23673531" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="97f51929-4b20-4704-856b-740ce4c644ef" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53205c42-05ea-4354-9c11-d337be6b6ea3" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="49736889-5107-447e-86d5-5d973b401045" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f8c1f5f-5b99-4c5b-8309-e4cf186ebe86" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8f64fbfc-4a6a-4468-989f-6d9ae96a5818" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43f93f19-923f-4d4d-b62a-0e02c7375c90" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="081fa761-4c7e-4058-b0bf-fed1335a4698" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3607b109-cfed-4f4c-bb7c-dc215c8c9b6b" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dad61590-3708-4f1d-ba78-3daa6aceed4f" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c9cced03-ce3a-483b-8cb9-08c582d39fe1" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d14b2825-8db3-42d0-956a-d95d1c55850a" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3955d210-d882-401a-9162-39f510b8257d" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b05a59d8-4662-4afb-b7d9-860f8fff0d50" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0637039e-3001-4a5f-b48a-3ff897733a5b" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="613d391c-361b-4bf5-a00a-26b9c4456734" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe6cef77-b38e-4690-9e3b-4bba412db204" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="84cd424f-3051-4f4d-ae61-c6e2493205a1" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="57a7de73-3ba5-48f1-8ffd-2d24ebb7087e" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04fba185-6732-48cb-aef5-addfce16a487" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="48ee110e-960b-4f3a-80a8-2fefbe705540" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b1ef70bc-6f04-4536-ae73-4bbd47beca21" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f1f5fbeb-7653-4839-a33e-f5af0b57a660" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09aea4b7-c0f0-4f3e-a8f1-e1485c0f1916" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="307bfa24-e053-4b1c-92b3-beab24254d01" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6bc0b04a-6ad2-4079-89ce-c6dcb86d7173" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3a1ee94c-c376-4dd5-8f16-16fe172faabf" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1fa6b8a4-acae-4504-848c-ea58ba26b6b9" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f3c28d4-9dfa-452e-a4d7-9e43657a16aa" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9369c074-8798-438a-aed5-4cf598f509a1" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a8d5c73d-faa5-4488-8973-3755e91b9cd1" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="03820340-10a8-49f7-9f90-f99112532c4d" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3fcc9c92-7633-43e7-a579-d7e8e43b4f66" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="400c6850-d60a-4d48-b3e5-fb6c1eb3d8bc" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="00921b0e-2ac4-4a43-96aa-3972b7f97990" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="adc68b97-088a-4deb-8603-1c47a4bf5150" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9edceb7c-9603-426c-8341-3aad021aff2a" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="aa9fa9bd-6c96-41c0-bdb8-7282262436db" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4a8dff12-c956-415c-9490-914c1466e557" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="418849f9-f029-48f9-9d5c-285780fd8db8" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="da308d8d-4941-4fa9-9761-3109fb94ed78" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5c586685-fdbb-47b6-94b7-2b64b195b078" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d27062a-bdcf-4d73-b6f2-f6ed513428d4" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0344735c-862e-4ccc-884f-9c1527a0b2a4" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70849c80-8895-4ed5-939f-506a3055b8d4" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="89c90d7d-8049-48c3-9413-66731caed6c7" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c83b8af8-2357-4a5d-837d-62b75d7fb0fe" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f42ed541-4180-41b1-8264-c793c26e2dab" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f47c6b6b-9325-49c1-ba51-51df4012d313" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="511ffdc4-4ff3-45a8-9dbc-ea5d9ab1eff6" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a13aebc9-7429-4e49-8641-fc90f10a96ac" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0cebb79d-aeb4-49d7-b0e9-f454ca5e8529" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cbb59ff1-3532-4c0b-af5c-a5e6513ee090" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="64bdc639-06da-47b3-b71f-63efdeab40b2" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8df4e8b6-1343-4d17-8729-bf2aae78496b" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8d2c9024-8332-41ce-bdb8-8f1db1f9c04b" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0856c98f-15b4-4376-9e9f-0f2411fd255d" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="6666fa73-1876-4e4d-83ee-9814f6bfc4a0" name="Группа элементов №8"><level2 id="b8bba931-710b-4a70-a7d0-ab5fbd5c2853" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="51924f02-b7cc-4c5c-a4f6-dcf7de483af9" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f3b9947-4893-41f8-b059-26f878caef84" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="29e3e99a-7db9-4467-930f-20b34d2125ed" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c91c9f5b-e51f-4cc4-804c-7c804c2f0056" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="27bcbb1d-8609-4b57-afe6-b4bf3504f82b" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fcefe197-3840-44e3-ab92-8f51e1230de2" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04fae664-fb46-4e29-a499-b8c6cfe39a95" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9eb400eb-e610-4af1-960a-054c8ab5a7ec" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c363857-ae1d-4a81-9ba3-6c22687e2cf7" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe81dc54-95f3-4b25-af20-6ec646222d8e" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="417bdf63-4548-4f1f-91ca-2294b7ebcc49" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1806a37f-60d3-4014-97a8-f7c9431aa980" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0c4819fd-8263-4f01-89c3-ab444a1c7511" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="88bf2cf6-0630-4103-9013-6f1e70457980" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="243c8aa5-6b70-4e6d-84d6-8bea49b49dbd" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c633bfcf-f073-42ac-949f-85a812cb2835" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b5ad9904-65d9-4992-954d-4ba24e4736b0" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68242f23-59a2-42ae-8635-deca39e50dae" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a0fc9b99-575c-4196-aa7a-314869caffe8" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="11aa8c72-f8f6-4e2b-aa76-9b43ecf0669c" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81b86b0a-29fb-4a34-9299-94a29372e913" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="827022d5-ff76-48a3-94d6-342f826b3db5" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a23474ad-9ffc-4f47-8493-ac40561897ad" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23904c9a-51aa-4377-b277-1a0a1533f54c" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2a7ca43c-3e2b-4c5d-8492-e90684de1ae8" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ceea9f98-d150-45be-955c-d088ca95efa7" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="673428a3-6121-467e-a1bf-115218d71c2e" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1592a11c-3db9-45c4-8e11-d81272d89a83" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6b54f97a-f32a-465c-8d5a-f195373d5e99" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="316d0283-4147-414f-bcf7-e5c2fc915644" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="05a27685-8913-462d-9472-7134a096efd2" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ede122f3-7fcd-41f8-a676-ea6f1ad60470" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bd1afc3c-6a28-4a40-9325-397cbc1c5055" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dc038173-d73e-4370-80ae-fcc8b9e34cbf" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="27491d43-e32e-4ab5-b292-dd611b7b6406" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="66d72c3b-4910-41d5-a9e4-254442df8937" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c9b1d913-ab47-4da5-b1e6-e2e49a92ae8e" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f448565-9f13-4a26-8722-a7164512938f" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a3747653-e515-4f83-a577-b90ac7b3feab" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a9c3b3a8-3456-4c8d-acdf-ed5ea960a09b" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7e29023a-c14d-4f70-beab-fdbe58c7cbb3" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="81004ab9-e058-4c1a-bd2c-485ac637ebec" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="728b08a3-924a-437f-9c69-71724ef9a9b1" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="15e63490-d0c3-4bf1-bf73-0d618331dcf6" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c7a43067-cade-4803-98f3-b5c98c0c5605" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="34478e12-4d28-487d-88b4-b38cde292ce7" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4bbd7a9d-3733-4a85-aa17-c24c787bbb99" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d743dac-3c3f-45ac-9e00-25a79ec67a17" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0bd4a26f-d25c-49af-85e6-41067db75816" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cff8d5f1-76f9-4669-bc57-0b9f31450ecb" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f47100b-67ff-4017-b507-be6e15909272" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="04dfe841-b66e-4117-816c-fef447409919" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b856b7bf-af42-40b3-98db-fd3f76085c41" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d1fe50bb-7827-42c3-a129-552bfea0858e" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6ae60230-7804-4b6a-8a49-3c03f4ac43f4" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="fe13bde3-5aa1-4a87-9f55-83d8409ef101" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="70339fb2-d22a-496f-a1f7-380607210634" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3f10d0af-04a8-4492-afb0-6f6a81033da7" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="febb8288-7538-4a94-b225-70c273422018" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="873b1a92-4a00-4130-a8cf-6ae1e5a8d0bd" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="99b1970a-cc5d-4d48-bdf6-cd4daffe3af3" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b9556c86-5edb-4af4-8e13-5aa37bd9469a" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="89355dba-0c40-4466-a9f6-1e46d2977b1a" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="14483834-342c-446b-9ac9-1e6623bdfca4" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e878a08b-89e9-4b13-ad4f-f910718f49a6" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e2a8c7a3-8de5-4f2b-baed-dadb95429be9" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d2474a8c-ac7c-47ba-97c8-b3a27a12a615" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="86f790b7-6717-4126-a16b-e2ca554824c0" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="880acd84-d325-46d6-bec6-6e89fbe0681a" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="41bad07e-c5f1-4378-a3e0-1c48667e7b77" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5c64be42-020e-4efa-8170-5affb53955a5" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bfd70609-6ae5-4e33-9fa1-a6ecc2c189b3" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7eb7ebd6-4451-4d8f-b641-5188a03df1a5" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="09825f59-31c1-40df-9110-fcf27b61731d" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5cde50bd-4337-47e2-95ed-d0ac4bd02917" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d55a2550-bc44-460b-9706-baaec249dbf6" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9e1ae19f-0359-440c-833a-5a8f4c702f88" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="abc0037b-4555-4067-b64f-b350acd24860" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d915d0e3-780c-439e-82db-dd0c5b28a8b5" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="538be7e1-8695-4149-9c84-3bd30db438da" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="23df00d1-8c62-4b89-9197-13151b449f23" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e97bbdc3-43af-41c5-a214-e48268eb4c5e" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="92872ac8-bd29-43ec-b3b2-e9dc1f2e24b9" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="dbfb2bb9-94d8-44f8-b10c-4044cdb36d57" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f24a882b-a8db-4c58-b500-b36bc6a83a73" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d40b9f3f-4fd2-4c9e-923b-03e3ec09a2c1" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="39c6119f-061c-46ae-9c45-ef046b2121eb" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e3089128-a909-406a-ab2a-a3e78dd523ab" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="048c7ee8-4bdc-4fa9-920d-76d70feeb166" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="61c205b0-e726-43a4-8c8c-dd6dc489bdf6" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="72cd9b62-63f8-4f78-bb08-0d2b8dd696c7" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b13c1a54-6167-44a2-9fa7-ed51e4132a6a" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ad04602-04a5-429f-8bc8-535dab025f71" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="61dea6e3-de71-4662-99b7-597479c70b7d" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6d3d0647-cda9-4aa4-af1f-b3a3455f9c35" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5691d8ed-a4c5-42be-ada2-9c8c706ea5ce" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9c3d5468-00ab-46a1-8f82-6d32e0b19572" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cbc69a65-a590-4de6-b7e9-f7e9400c167c" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0d4b2e7c-f816-4828-866b-69ef95372195" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1><level1 id="8a9df458-f016-4a4e-a645-8420bd1dd218" name="Группа элементов №9"><level2 id="986e6f33-8e84-42c3-935b-d6c38f433b92" name="Элемент №0"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="045863a8-255b-47ea-8c30-e189dca3e7f4" name="Элемент №1"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="049008dd-4bdd-4965-8461-346157c5bb19" name="Элемент №2"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5545e5d5-fd36-447f-9abd-3e924a02ae06" name="Элемент №3"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e9c74308-6bb7-4db4-b1e5-4240cffc1eca" name="Элемент №4"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1740916-9bc0-461c-b737-1ba7831c66d5" name="Элемент №5"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d06b5cd5-a0cb-4bb0-8dc2-5989b44eb7be" name="Элемент №6"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a29bf763-500d-449e-968b-622865f5e7a4" name="Элемент №7"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ae3ce661-7ec2-4443-8a82-1997d7604769" name="Элемент №8"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3558cc9c-9ced-4dda-9d01-d1d81e6987c7" name="Элемент №9"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3c550cf2-911f-442d-b7c3-01e8dcd9546c" name="Элемент №10"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c7818c55-b9b3-484d-9394-ce104ecaf57c" name="Элемент №11"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="840e4100-4d89-4590-8ca7-76a6121f1aa9" name="Элемент №12"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2f3f72d1-1c50-4aa4-b19d-8efa213df400" name="Элемент №13"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="985e70e1-11c6-498f-81e9-e5e16a240581" name="Элемент №14"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1d81fa4f-8979-4079-86c7-6016b42b8c94" name="Элемент №15"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="aa5a5cf9-498c-40a3-92a8-c5dc6392d6ed" name="Элемент №16"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ead25da4-7e93-484c-8bea-9a9e1876484a" name="Элемент №17"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9268f6ae-3989-4900-a08f-3a7a77586bbd" name="Элемент №18"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="2e6853d1-52a7-4f84-a5f9-7f678e0af0e9" name="Элемент №19"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f1282ae0-44df-4936-a0a0-9e61967ca101" name="Элемент №20"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a97d13cd-261f-4266-9b01-8eb8ccccd443" name="Элемент №21"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="20689087-94d4-4ea7-8186-e3873f9e142b" name="Элемент №22"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7e03a2aa-04a9-4452-8db6-b289006c8932" name="Элемент №23"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="68b94e8f-fee8-4243-9bab-ed87419eea99" name="Элемент №24"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4176ca73-9855-45a9-888d-d11c8e86e566" name="Элемент №25"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4691b1d8-7240-40e8-bd35-6bef66fb6322" name="Элемент №26"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="813ec475-28a7-4d30-8341-fc2ca2b6ffc7" name="Элемент №27"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="52c2ddc8-d8f9-4c2a-b87b-890263251ad7" name="Элемент №28"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="94e96954-c6bd-4015-a8ed-f8be3c775eaf" name="Элемент №29"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a193ebd0-435e-424a-924f-471fe41ed576" name="Элемент №30"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7f9476e8-c12b-4312-96be-75947887827e" name="Элемент №31"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5a1e3cc9-9a8e-42ac-89f3-6c5ccf78394f" name="Элемент №32"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1acbbdaf-79a4-4b96-a55a-12be78a7b45c" name="Элемент №33"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="56e23de2-391c-48c8-99bc-40195c2fa8b9" name="Элемент №34"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="193850f1-1ac0-4932-8837-289ab823cc0e" name="Элемент №35"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9fa98331-9948-4a15-9baa-074d9eae0b07" name="Элемент №36"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="0acb2adb-e42b-48c9-bdf2-8e58f4f25001" name="Элемент №37"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9f28d3cc-cbc3-4514-9e1c-0e06ab8b8bf6" name="Элемент №38"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="67cb6698-157e-49b5-beee-0a7c0ac277b7" name="Элемент №39"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="17c9931b-a676-4624-bb3d-f10a2b78cc67" name="Элемент №40"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="740c41a1-f848-48d1-afde-9ad242094ff4" name="Элемент №41"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f091eb8a-b23c-44ab-816e-715bf38d796a" name="Элемент №42"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5ccb139e-f3b4-4767-9bad-a423dd70a5bb" name="Элемент №43"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4f7276d1-10b4-460f-b114-5bbf6f84f135" name="Элемент №44"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c1650fee-9131-4bc5-84d7-e4f320a517e1" name="Элемент №45"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="a843f580-d4d3-4818-96d4-408edd994c8b" name="Элемент №46"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="83f8ce29-2e02-4d70-ac58-9d16ea2d3b38" name="Элемент №47"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3d71d1d9-1993-4222-b3da-4c505245d1e4" name="Элемент №48"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="43a5b2ef-fa29-48dc-b12b-518fde9bd131" name="Элемент №49"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bd437893-000c-4962-80c0-49bfddccb0c5" name="Элемент №50"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="58f2e512-46f8-4926-8adf-c3ebb24a2acd" name="Элемент №51"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="eab0d1fc-b4da-4c92-a288-f6d9778e743f" name="Элемент №52"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="06a2e493-9c23-4441-b706-c2c40b8f3642" name="Элемент №53"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d98e2920-0007-4e51-a15d-de6483ee54e2" name="Элемент №54"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="1f0871b1-ad8f-4400-9dd4-074fc6cf599f" name="Элемент №55"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3a155c5d-d72e-4360-8f29-bbeb6b2773ed" name="Элемент №56"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c681665b-9339-40e1-b073-fb8b49c13d28" name="Элемент №57"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e19a1b80-4639-4a7f-b9da-fdcac847de0b" name="Элемент №58"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="3e75c6f4-e5e2-4ae8-a385-c241a0ad1fa0" name="Элемент №59"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="53680338-7a36-4316-88f3-c36928ae8fce" name="Элемент №60"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9d5b7b30-f212-484c-99d1-b3e86d7058a0" name="Элемент №61"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="12c8a167-7eb0-47dd-9a65-b87c23de830a" name="Элемент №62"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="35ffed67-46f2-47e1-ac75-e1e0806dff67" name="Элемент №63"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="b20d7791-3ec9-4a61-9973-8b49f562e9de" name="Элемент №64"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="daa3c9ee-e604-4199-979f-c5a35acb89f6" name="Элемент №65"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="5762d4c1-8a4f-4f0f-898f-2fa0209c0173" name="Элемент №66"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bad0bb18-f601-4d83-8e1b-301351ff4025" name="Элемент №67"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="37a36763-a0e7-40cd-adc2-aa6983979a78" name="Элемент №68"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="4af5b1e3-0051-40a8-ae61-de4ed46d006e" name="Элемент №69"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="8170e826-5ac0-4b66-a8c6-fa0d7e543689" name="Элемент №70"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6dc6401d-18f4-4cb8-8011-d22dab3dee12" name="Элемент №71"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="7d025b06-aaed-4e7f-ac87-591ae302182d" name="Элемент №72"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c092bab0-36f8-41ff-b883-d43182d27168" name="Элемент №73"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cf13fc25-6e45-4a5e-b2d6-791f7f3d75db" name="Элемент №74"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9b3fe914-604b-4dbb-b9e7-5263d4db3551" name="Элемент №75"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="f356b38b-557e-4acd-9754-958a63df1cb7" name="Элемент №76"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="92e32bf6-a571-4e18-9f30-80d923d500b1" name="Элемент №77"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6e31c279-320b-4834-a4e9-83df3a61d9a8" name="Элемент №78"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="d3da437a-5c2a-40f6-8fd1-9c693cd82186" name="Элемент №79"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="22d27b5c-190c-40d4-b8cf-c2e828cc067d" name="Элемент №80"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="30b5c7be-a013-4e11-9761-15485186a03d" name="Элемент №81"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="9867c3e5-9e97-4e29-a632-b5c54d5c3a51" name="Элемент №82"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c2dfaf15-26e1-4de4-8e5f-5506d29aa2b1" name="Элемент №83"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="858d2f04-b6d3-46cb-99fa-3ee45be2e623" name="Элемент №84"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="535c36a1-581f-4b5a-9419-5372b29e2055" name="Элемент №85"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e603c97f-5aac-4f2c-a74b-f95d1700e4af" name="Элемент №86"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e6ff1ed5-4238-4537-92ca-93fec3037bdc" name="Элемент №87"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="284d9133-6099-4011-ab9f-a338b29db2d8" name="Элемент №88"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="91539f95-ce0a-497c-bd39-7d64cddeb6ad" name="Элемент №89"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="e143fb1f-e4b7-4a37-9736-ae5affb12afe" name="Элемент №90"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea7b5b8c-d4b9-4289-ac7b-1e05a3139875" name="Элемент №91"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="bc2f95bf-6523-4e0c-afa1-ef3d016693e3" name="Элемент №92"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="ea745e4b-f780-485c-a25d-87d9ef8812aa" name="Элемент №93"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="46f3c409-9d23-46f9-8295-e52f25086b5b" name="Элемент №94"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="6124a147-3954-4667-981b-f69f8f1662d1" name="Элемент №95"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="532541a5-3737-4a92-a6c0-58700c755754" name="Элемент №96"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="657dd35d-5d1c-407e-88ee-523028e5fbc0" name="Элемент №97"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="cc472589-351d-4a5a-9803-bd95a0271e82" name="Элемент №98"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2><level2 id="c56a51b6-d22b-4638-b8c9-ab9ffd897508" name="Элемент №99"><action><main_context>Ввоз, включая импорт - Всего</main_context><datapanel type="a.xml" /></action></level2></level1>
	</group>
</navigator>
'
			
	--select @navigator
GO

--
-- Definition for stored procedure geomap_bal : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_bal]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '+
' CASE [Code] WHEN ''RU-AMU'' THEN ''#6BEADA'' ELSE NULL END AS [Color], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><h4>Карта с примерами разнообразной раскраски</h4>
			<ul>
			<li>Тверская область связана со стилем по своему id</li>
			<li>Цвет Амурской области задан в таблице с данными</li>
			<li>Для Республики Алтай стиль <b>подсветки</b> задан через StyleClass</li>
			<li>Для Нижнего Новгорода через StyleClass задан вид точки</li>
			</ul>
			</header>
	
		</labels>
		<exportSettings width="2560" backgroundColor="#FFFFFF" jpegQuality="10" filename="map"/>
		<properties legend="bottom" />
		<template> 
	{
	   registerModules: [["solution", "../../${userdata.dir}/js"]],
	   managerModule: "solution.test",
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
},
{
       fid: "71",
       stroke: "red",
       strokeWidth: 3,
       fill: "purple",
       text: {
               attr: "geometryId",
               fill: "blue",
               font:  {family: "cursive", variant: "small-caps", weight: "bold", size:"2em"}
       }               
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_bal_lite : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_bal_lite]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '+
' CASE [Code] WHEN ''RU-AMU'' THEN ''#6BEADA'' ELSE NULL END AS [Color], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)



Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><h4>Карта с примерами разнообразной раскраски</h4>
			<ul>
			<li>Тверская область связана со стилем по своему id</li>
			<li>Цвет Амурской области задан в таблице с данными</li>
			<li>Для Республики Алтай стиль <b>подсветки</b> задан через StyleClass</li>
			<li>Для Нижнего Новгорода через StyleClass задан вид точки</li>
			</ul>
			</header>
	
		</labels>
		<properties legend="bottom" width="700px" height="400px"/>
		<template> 
	{
	   registerModules: [["solution", "../../${userdata.dir}/js"]],
	   managerModule: "solution.test",
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red"
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
},
{
       fid: "71",
       stroke: "red",
       strokeWidth: 3,
       fill: "purple"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_ec : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_ec]
CREATE PROCEDURE [dbo].[geomap_ec]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;

	DECLARE @mesid int
	DECLARE @add_context_xml xml
	SET @add_context_xml = CAST(@add_context as xml)
	SET @mesid=(select @add_context_xml.value('(/mesid)[1]','int'))
   		
   	
   	if @mesid IS NULL		
		RETURN -1
	ELSE
		RETURN @mesid
	
END
GO

--
-- Definition for stored procedure geomap_func1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- exec [geomap_func1]

CREATE PROCEDURE [dbo].[geomap_func1]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml  = '' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

set @Sql = 'SELECT [ID], 
[Name_Ru] AS [Name], 
''TestStyleClass'' AS [StyleClass], 
''l1'' AS [LayerID], 
[Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip], 
'''+@CityProps+''' AS '+@PROPS_COL+
' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

set @Sql = 'SELECT TOP 5 [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'CASE [Code] WHEN ''RU-AL'' THEN ''Алтай - регион с показателями'' ELSE NULL END AS [Tooltip], '+
'''#FAEC7B'' AS [Color], '+
'[Code] AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Надои молока (тонн)'' AS [Name], ''l2'' AS [LayerID], 0 AS [IsMain], ''#2AAA2E'' AS [Color] UNION ' +
'SELECT ''ind2'' AS [ID], ''Выплавка чугуния (тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#5770D6'' AS [Color] UNION ' +
'SELECT ''ind3'' AS [ID], ''Освоено нанотехнологий (тысяч штук)'' AS [Name], ''l1'' AS [LayerID], 0 AS [IsMain], ''#2AAA2E'' UNION ' +
'SELECT ''ind4'' AS [ID], ''Построено жилья (кв.см)'' AS [Name], ''l1'' AS [LayerID], 1 AS [IsMain], ''#5770D6'' AS [Color] ';
EXEC (@Sql)

set @Sql = 'SELECT ''ind3'' AS [IndicatorID], ''1849'' AS [ObjectID], 10 AS [Value] UNION ' +
'SELECT ''ind4'' AS [IndicatorID], ''1849'' AS [ObjectID], 100 AS [VALUE] UNION ' +
'SELECT ''ind1'' AS [IndicatorID], ''2'' AS [ObjectID], 1000 AS [VALUE] UNION ' +
'SELECT ''ind2'' AS [IndicatorID], ''2'' AS [ObjectID], 10 AS [VALUE] ';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><p>Header к карте (@add_context ='+@add_context+')</p></header>
			<footer><p>Footer к карте (@add_context ='+@add_context+')</p></footer>
		</labels>
		<properties legend="bottom" width="500px" height="500px"/>
		<template> {}
		</template>
		<action>
                            <main_context>current</main_context>		
                            <datapanel type="current" tab="current">
                                <element id="06">
	                                <add_context>hide</add_context>
                                </element>                                                             
                                
                            </datapanel>
                            <navigator element="9EF5F299-0AB3-486B-A810-5818D17047AC"/>
        </action>                		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_func2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_func2]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><p>Карта с раскрашенными по значению показателя регионами</p>
			<h2>И совершенно бесплатно, только сегодня работает масштабирование колесиком мыши</h2>
			</header>
	
		</labels>
		<properties legend="top" />
		<template> 
	{
	   registerModules: [["solution", "../../solutions/default/js"]],
	   managerModule: "solution.test",	
       style: [
	{
		fid: "l2",
		stroke: "black",
		strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Reds",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"
			}
		},
		legend: "djeo._getBreaksAreaLegend",
		name: "имя"
	},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_func2_autosize : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_func2_autosize]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header>
			<h2>Карта без размеров - autosize по свободной области на экране</h2>
			</header>
	
		</labels>
		<properties legend="bottom"/>
		<template> 
	{
	   registerModules: [["solution", "../../${userdata.dir}/js"]],
	   managerModule: "solution.test",
       events: [["onclick", "eventCallbackMapHandler"]],
       action: [{type: "course.geo.control.Highlight", options:{highlight: "orange"}},{type: "course.geo.control.Tooltip"}, {type: "course.geo.control.Navigation"} ],
       style: [
{
       stroke: "yellow",
       strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Greens",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"				
			}
		}       

},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_func2_gm : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_func2_gm]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><p>Карта с раскрашенными по значению показателя регионами</p>
			<h2>И совершенно бесплатно, только сегодня работает масштабирование колесиком мыши</h2>
			</header>
	
		</labels>
		<properties legend="top" />
		<template> 
	{
	   registerSolutionMap: russia_gm,
	   
       style: [
	{
		fid: "l2",
		stroke: "black",
		strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Reds",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"
			}
		},
		legend: "djeo._getBreaksAreaLegend",
		name: "имя"
	},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_func2_ym : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_func2_ym]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
			<header><p>Карта с раскрашенными по значению показателя регионами</p>
			<h2>И совершенно бесплатно, только сегодня работает масштабирование колесиком мыши</h2>
			</header>
	
		</labels>
		<properties legend="top" />
		<template> 
	{
	   registerSolutionMap: russia_ym,	

       style: [
	{
		fid: "l2",
		stroke: "black",
		strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 7,
				colorSchemeName: "Reds",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"
			}
		},
		legend: "djeo._getBreaksAreaLegend",
		name: "имя"
	},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_variables : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_variables]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(1024)
set @CityProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';
declare @AreaProps varchar(1024)
set @AreaProps = '<properties>
						<event name="single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="06">
									<add_context>''+[Name_Ru]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
					    </event>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [Name_Ru] AS [Name], [Code], ''l2'' AS [LayerID], '+
'[Name_Ru]+ '' - производство'' AS [Tooltip], '+
' CASE [ID] WHEN 71 THEN NULL ELSE [Code] END AS [StyleClass], '''+
+@AreaProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @AREA_SELECTOR 
EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], Journal_44.FJField_17 AS [VALUE]
FROM [GeoObjects]
INNER JOIN Journal_47
 ON Journal_47_Name = [GeoObjects].[Name_Ru]  
Inner Join geo5
On geo5.geo5_Id=Journal_47.FJField_12
Inner Join Journal_44
On geo5.geo5_Id=Journal_44.FJField_18
Inner Join Journal_40
On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
   
WHERE ' + @AREA_SELECTOR+ ' and 
 Journal_45.Journal_45_Name=2009 and
 Journal_44.FJField_16 =3 and
 Journal_44.FJField_20=''Факт'' and
 Journal_40_Name= ''Производство (валовый сбор в весе после доработки) - Всего'' and
 Journal_41_Name=''зерно''';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>
<header>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</header>
<footer>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</footer>
	
		</labels>
		<properties legend="bottom" width="800px" height="400px"/>
		<template> 
	{
       registerSolutionMap: test,
       
       events: [["onclick", "eventCallbackMapHandler"]],
       action: [{type: "course.geo.control.Highlight", options:{highlight: "orange"}},{type: "course.geo.control.Tooltip"}, {type: "course.geo.control.Navigation"} ],
       style: [
{
       stroke: "yellow",
       strokeWidth: 1,
		styleFunction: {
			getStyle: "djeo.util.numeric.getStyle",
			options: {
				numClasses: 5,
				colorSchemeName: "Reds",
				attr: "mainInd",
				breaks: "djeo.util.jenks.getBreaks",
				calculateStyle: "djeo.util.colorbrewer.calculateStyle"
			}
		}

},
{
       point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"
       }
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_world : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_world]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" />
		<template> 
	{
	   registerSolutionMap: world_demo,
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		}
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_world_gm : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_world_gm]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" />
		<template> 
	{
	   registerSolutionMap: world_demo_gm,
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_world_small : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_world_small]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" width="600px" height="300px" />
		<template> 
	{
	   registerSolutionMap: world_demo,
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_wrong_connfile : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_wrong_connfile]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" width="600px" height="300px" />
		<template> 
	{
	   registerSolutionMap: fakeFile001,
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_wrong_nums : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_wrong_nums]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" width="600px" height="300px" />
		<template> 
	{
	   registerSolutionMap: fakeFile001,
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1a
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure geomap_wrong_structure : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

--EXEC [dbo].[geomap_bal]
CREATE PROCEDURE [dbo].[geomap_wrong_structure]
	@main_context varchar(512)='',
	@add_context varchar(512)='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',
	@geomapsettings xml='' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
SET NOCOUNT ON;
	    
DECLARE @AREA_CONST varchar(8) = 'POLYGON'
DECLARE @POINT_CONST varchar(8) = 'POINT'
DECLARE @AREA_SELECTOR varchar(32) = ' [Status_ID]=7 '
DECLARE @CITY_SELECTOR varchar(32) = ' [Status_ID]=1 '
DECLARE @PROPS_COL varchar(32) = '[~~properties]'

declare @Sql varchar(8000);
declare @CityProps varchar(512)
set @CityProps = '<properties>
					</properties>';
declare @AreaProps varchar(512)
set @AreaProps = '<properties>
					</properties>';

set @Sql = 'SELECT ''l1'' AS [ID], ''Города'' AS [Name], '''+@POINT_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID) (%Lat - %Lon)'' AS [HintFormat] UNION ' +
'SELECT ''l2'' AS [ID], ''Регионы'' AS [Name], '''+@AREA_CONST+''' AS [ObjectType], ''%LayerName - %ObjectName (%ObjectID)'' AS [HintFormat]';
EXEC (@Sql)

-- города
set @Sql = 'SELECT top 0 [ID], [Name_Ru] AS [Name], ''l1'' AS [LayerID], [Lat], [Lon], 
CASE [ID] WHEN 1849 THEN ''Нижний Новгород - город с показателями'' ELSE NULL END AS [Tooltip],
CASE [ID] WHEN 1849 THEN ''cityStyle'' ELSE NULL END AS StyleClass, '''+@CityProps+''' AS '+@PROPS_COL+' FROM [GeoObjects] WHERE ' + @CITY_SELECTOR + ' AND [Name_Ru] LIKE ''%город''' 
EXEC (@Sql)

-- регионы
set @Sql = 'SELECT [ID], [NAME] AS [Name], [Code], ''l2'' AS [LayerID] FROM [GeoObjectsWorld] '
EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [ID], ''Производство (тыс. тонн)'' AS [Name], ''l2'' AS [LayerID], 1 AS [IsMain], ''#2AAA2E'' AS [Color] '

EXEC (@Sql)

set @Sql = 'SELECT top 0 ''ind1'' AS [IndicatorID], [GeoObjects].[ID] AS [ObjectID], 0 AS [VALUE] FROM [GeoObjects]';
EXEC (@Sql)

Declare @settings_str as varchar(max)
set @settings_str=
'<geomapsettings>
		<labels>	
		</labels>
		<exportSettings width="2560" jpegQuality="10" filename="map"/>
		<properties legend="top" width="600px" height="300px" />
		<template> 
	{
	   registerSolutionMap: fakeFile001
	   
       style: [
{
       stroke: "yellow",
       fill: "green",
       strokeWidth: 1
},
{
       	point: {
			strokeWidth: 2,
			fill: "blue",
			shape: "circle"      
		} 
},
{	  
    styleClass: "cityStyle",
    point: {
		shape:"star",
       strokeWidth: 1,
       stroke: "black",
       fill: "red",
       text: {
               attr: "name",
               fill: "black",
               font: {size:"20px"}
       }          
    }
},
{
	   theme: "highlight",
	   styleClass: "RU-AL",	
       stroke: "black",
       fill: "yellow"
}
]
      
}	
		</template>              		
</geomapsettings>' 
set	@geomapsettings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_bal]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',    
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16"  autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal_articles : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_bal_articles]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
  
DECLARE @Sql varchar(2000)   
  
  if (@add_context != '') BEGIN
  set @Sql = 'DELETE [dbo].[Journal_38] WHERE [Journal_38_Name]='''+@add_context+'''' 
  EXEC(@Sql)
  END  

  
   declare @id varchar(255)
   SET @id=(select @filterinfo.value('(/schema/info/id)[1]','varchar(255)'))
   declare @where_cond varchar(255)
   SET @where_cond = ''   
   IF NOT @id IS NULL
   SET @where_cond = 'WHERE FJField_12=''' + @id +''''
      
  

set @Sql = 
'Select [Journal_38_Name] AS [Название], cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="82" >
									<add_context>''+[Journal_38_Name]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties] FROM [dbo].[Journal_38] '+ @where_cond
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3>Статьи баланса первого уровня</h3></header>
        </labels>

<properties flip="false" pagesize="10" autoSelectRecordId="1" autoSelectRelativeRecord="true" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal_articles_dbl : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_bal_articles_dbl]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
  
DECLARE @Sql varchar(2000)   
  
  if (@add_context != '') BEGIN
  set @Sql = 'DELETE [dbo].[Journal_38] WHERE [Journal_38_Name]='''+@add_context+'''' 
  EXEC(@Sql)
  END  

  
   declare @id varchar(255)
   SET @id=(select @filterinfo.value('(/schema/info/id)[1]','varchar(255)'))
   declare @where_cond varchar(255)
   SET @where_cond = ''   
   IF NOT @id IS NULL
   SET @where_cond = 'WHERE FJField_12=''' + @id +''''
      
  

set @Sql = 
'Select [Journal_38_Name] AS [Название], cast( ''<properties>
                    <event name="row_double_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="82">
									<add_context>''+[Journal_38_Name]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>  
                     
                    <event name="cell_double_click" column="Название">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>
							<modalwindow caption="Редактирование по двойному клику" width="400" height="150"/>                            
                            <datapanel type="current" tab="current">
                                <element id="84">
									<add_context>''+[Journal_38_Name]+''</add_context>                                                                                             
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                                            
            </properties>'' as xml)  as [~~properties] FROM [dbo].[Journal_38] '+ @where_cond
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3>Статьи баланса первого уровня</h3></header>
        </labels>

<properties flip="false" pagesize="10" autoSelectRecordId="1" autoSelectRelativeRecord="true" totalCount="0" 
fireGeneralAndConcreteEvents="true" />
</gridsettings>' 
--profile="sngl_before_dbl.properties"
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal_multiaction : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_bal_multiaction]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',  
    @sortcols varchar(1024) ='',	  
    @gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                
                            </datapanel>
                        </action>
                    </event>                                        
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
                        <action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16" autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal_noevents : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_bal_noevents]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+' FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
<properties flip="false" pagesize="15" autoSelectRecordId="16" autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal_wrong : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_bal_wrong]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',   
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                 
                            </datapanel>
                        </action>
                    </event>                    
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE  PROCEDURE [dbo].[grid_bal1]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    if @add_context = '' 
    set @add_context = 'Краснодарский край'
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
    Case
     When @sortcols='' then 'Order by sort2'
     Else @sortcols 
    End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>                                      
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT top 1 [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
   where #Reg_year.[Регион] = '''+@add_context+'''  ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt )p Where [Регион]<>''Алтайский край'' '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
<properties flip="false" profile="gridbal.test.properties" totalCount="0"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_bal2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_bal2]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="52">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="52">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> 
        <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+
'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="52">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16" autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_by_userdata]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
	
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   			
	if (@session != 'default')
	raiserror ('__user_mes_test1_src__',12,1)	
	
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="15"
totalCount="0" profile="grid.nowidth.properties"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS [Название], [Logo] AS [Логотип], [Url] as [URL], cast( '<properties>                                    
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure grid_cities_data : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_data]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',		
    @firstrecord int = 1,
    @pagesize int = 20   
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  'WITH result AS 	(
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties],           
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[geo3])
      SELECT
         [_Id], [Name], [~~properties] FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstRecord AS varchar(32)) +') AND ('+CAST(@firstRecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)	
END
GO

--
-- Definition for stored procedure grid_cities_data_js : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_data_js]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @firstrecord int = 1,
    @pagesize int = 20  
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  'WITH result AS 	(
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <client>
                                <activity id="cl1" name="showcaseShowMessage"> 
                                <add_context>''+[Name]+''</add_context>  
                                </activity>                                
                            </client>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties],           
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[geo3])
      SELECT
         [_Id], [Name], [~~properties] FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstRecord AS varchar(32)) +') AND ('+CAST(@firstRecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)	
END
GO

--
-- Definition for stored procedure grid_cities_data_rco : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_data_rco]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',		
    @firstrecord int = 1,
    @pagesize int = 20   
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  'WITH result AS 	(
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1" > 
                                <add_context><name>''+[Name]+''</name><table>geo3</table><name_field>name</name_field></add_context>  
                                </element>
                                <element id="d2" >
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3" >
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d4" >
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d5" >
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties],           
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[geo3])
      SELECT
         [_Id], [Name], [~~properties] FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstRecord AS varchar(32)) +') AND ('+CAST(@firstRecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)	
END
GO

--
-- Definition for stored procedure grid_cities_data_sr : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_data_sr]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',		
    @firstrecord int = 1,
    @pagesize int = 20   
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
 
    SET @sql =  'WITH result AS 	(
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_double_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="'+@element_id+'" keep_user_settings="true"> 
                                <add_context>test</add_context>  
                                </element>                                                               
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties],           
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[geo3])
      SELECT
         [_Id], [Name], [~~properties] FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstRecord AS varchar(32)) +') AND ('+CAST(@firstRecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)	
 
END
GO

--
-- Definition for stored procedure grid_cities_metadata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_metadata]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @gridsettings_str varchar(max)
DECLARE @cities_count int
SELECT @cities_count = COUNT(*) FROM [dbo].[geo3]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Новый способ загрузки - отдельные процедуры для METADATA и DATA</h3></header>
        </labels>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="36"  autoSelectRelativeRecord="false" totalCount="'+
CAST(@cities_count as varchar(max))+'"/></gridsettings>' 
set  @settings=CAST(@gridsettings_str as xml)

END
GO

--
-- Definition for stored procedure grid_cities_metadata_ec : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_metadata_ec]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    SET @error_mes = 'Ошибка построения XML c метаданными в процедуре "grid_cities_metadata_ec"'
          
    RETURN 1
END
GO

--
-- Definition for stored procedure grid_cities_one : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_one]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [Name]';
    else
    SET @orderby = @sortcols;
    
    SET @sql =  '
         SELECT 
            [_Id], 
            [Name],cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Name]+''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties]                       
         FROM [dbo].[geo3] ' + @orderby;
   EXEC(@sql)	    
    
DECLARE @gridsettings_str varchar(max)
DECLARE @cities_count int
SELECT @cities_count = COUNT(*) FROM [dbo].[geo3]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Традиционный способ загрузки - одной процедурой</h3></header>
        </labels>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="36"  autoSelectRelativeRecord="false" totalCount="'+
CAST(@cities_count as varchar(max))+'"/></gridsettings>' 
set  @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_cities_one_ec : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_cities_one_ec]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
         SELECT top 0 
            [_Id], 
            [Name],cast( '<properties>                                        
            </properties>' as xml)  as [~~properties]        
          FROM [dbo].[geo3];
          
          SET @error_mes = 'Нет ничего в процедуре "grid_cities_one_ec"'
          
          RETURN 1    
  
END
GO

--
-- Definition for stored procedure grid_col_types_getquery : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_col_types_getquery] 
	@sql varchar(MAX) output 
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    SET @sql =  '
         SELECT 
            [_Id], 
            [Journal_41_Name],
            [UpdateRowTime],
            CAST([UpdateRowTime] as [date]) AS [UpdateRowDate],
            ''<link href="http://''+[Journal_41_Name]+''.рф" openInNewTab="true"/>'' AS [Сайт],
            cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Journal_41_Name]+''</add_context>  
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>                                         
            </properties>'' as xml)  as [~~properties]'
END
GO

--
-- Definition for stored procedure grid_col_types : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_col_types]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    DECLARE @sql varchar(max)    
	EXEC grid_col_types_getquery @sql = @sql OUTPUT
	
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [_Id]';
    else
    SET @orderby = @sortcols;
    
    SET @sql = @sql + 'FROM [dbo].[Journal_41] ' + @orderby	
    
    EXEC(@sql)	    
    
DECLARE @gridsettings_str varchar(max)
DECLARE @rec_count int
SELECT @rec_count = COUNT(*) FROM [dbo].[Journal_41]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Грид с различными типами столбцов</h3></header>
        </labels>
        <columns>
        <col id="Сайт" type="LINK"/>
        </columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties pagesize="20" profile="sngl_before_dbl.properties" totalCount="'+
CAST(@rec_count as varchar(max))+'"/></gridsettings>' 

set  @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_col_types_data : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_col_types_data]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
        @firstrecord int=1,
        @pagesize int=20  
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [_Id]';
    else
    SET @orderby = @sortcols;
    
	EXEC grid_col_types_getquery @sql = @sql OUTPUT    
    
    SET @sql =  '
         WITH result AS 	('+@sql+',                      
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[Journal_41])
      SELECT
         * FROM result 
         WHERE rnum BETWEEN ('+CAST(@firstRecord AS varchar(32)) +') AND ('+CAST(@firstRecord + @pagesize AS varchar(32))+') '
         +'ORDER by rnum';
   EXEC(@sql)	    
    END
GO

--
-- Definition for stored procedure grid_col_types_md : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_col_types_md]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;    
    
DECLARE @gridsettings_str varchar(max)
DECLARE @rec_count int
SELECT @rec_count = COUNT(*) FROM [dbo].[Journal_41]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Грид с различными типами столбцов</h3></header>
        </labels>
        <columns>
        <col id="Сайт" type="LINK"/>
        </columns>        
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties pagesize="2" profile="sngl_before_dbl.properties" totalCount="'+
CAST(@rec_count as varchar(max))+'"/></gridsettings>' 

set  @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_default_profile : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_default_profile]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a
Where a.Год = 2005   

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], getdate() as [Сейчас],  ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> 
        <col id="Картинка" width="40px" type="IMAGE"/>'
        
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_download_load : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_download_load]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" width="100px" /> 
        <col id="Файл1"  width="130px" type="DOWNLOAD" linkId="11"/>         
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="Файл2"  width="100px" type="DOWNLOAD" linkId="12"/>                 
        <col id="URL" width="150px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="22" profile="grid.nowidth.properties" autoSelectRecordId="3" 
 autoSelectRelativeRecord="false" autoSelectColumnId="URL"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT 
       [Name] AS "Название", 
       [File1] AS "Файл1",        
       [Logo] AS "Логотип", 
       [File2] AS "Файл2",               
       [Url] as "URL", 
       Id AS "~~id",  
       cast( '<properties>
			<styleClass name="grid-record-bold"/>
			<styleClass name="grid-record-italic"/>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="d1">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                                <element id="d2">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element>                                
                            </datapanel>
                        </action>
                    </event>  			
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure grid_download_null : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_download_null]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@record_Id varchar(512),
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	SET @file = null
	SET @filename='Test_.xml'	
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure grid_download1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_download1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@record_Id varchar(512),
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @navigator xml
	EXEC [generationtree] '', @navigator OUTPUT
	SET @file = CAST(@navigator AS varbinary(MAX))
--	SET @filename='navigator.xml'
	SET @filename='navigator_'+@record_Id+'.xml'	
	
	SET @error_mes=''
	RETURN 0
	
	
END
GO

--
-- Definition for stored procedure grid_download2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_download2]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@record_Id varchar(512),
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @xml VARCHAR(MAX)
	SET @xml = '<documentset>
	<document>
		<documentset>
			<document>
				<Schema>
					<Temp>
						<Flag />
						<Error />
					</Temp>
					<Stat>
						<Isp />
						<DateIsp />
						<Uch />
						<DateUch />
						<AC />
						<DateAC />
						<Paper />
						<DatePaper />
					</Stat>
					<Info>
						<year>2010</year>
						<FIO />
						<remark />
						<ChangeStr>false</ChangeStr>
						<Executor />
						<Date />
					</Info>
				</Schema>
			</document>
		</documentset>
	</document>
</documentset>'
	
	SET @file = CAST(@xml AS varbinary(MAX))
--	SET @filename='TestTextSampleSmall.xml'
	SET @filename='Test_'+@record_Id+'.xml'	
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure grid_dyn_dp_main : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_dyn_dp_main]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='', 
    @sortcols varchar(1024) ='',	   
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynMain"/>                                                               
                        </action>       
<properties flip="false" pagesize="15"
totalCount="0" profile="grid.nowidth.properties"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS [Название], [Logo] AS [Логотип], [Url] as [URL], cast( '<properties>  
						<event name="row_single_click">
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynMain"/>                                                               
                        </action>
                    </event>                                  
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure grid_dyn_dp_session : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_dyn_dp_session]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynSession"/>                                                               
                        </action>       
<properties flip="false" pagesize="15"
totalCount="0" profile="grid.nowidth.properties"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS [Название], [Logo] AS [Логотип], [Url] as [URL], cast( '<properties>  
						<event name="row_single_click">
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynSession"/>                                                               
                        </action>
                    </event>                                  
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure grid_fewcols : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_fewcols]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a
Where a.Год = 2005   

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16" autoSelectRelativeRecord="false" selectMode="row" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_grid : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_grid]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',   
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);

declare @a as varchar(255)

if @element_id = '12'  set @a = '7'
if @element_id = '14'  set @a = '11'
if @element_id = '13'  set @a = '15'
if @element_id = '140'  set @a = '110'

set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                       <main_context>current</main_context>
                       <datapanel type="current" tab="current">
                                <element id="' + @a +'">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="100">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                                                               
                       </datapanel>
                        </action>
                    </event>                    
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>

        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px" precision="2"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="10"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							

<properties flip="true" pagesize="20"  selectMode = "row" autoSelectRelativeRecord="false" totalCount="10"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)

END
GO

--
-- Definition for stored procedure grid_grid1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE  PROCEDURE [dbo].[grid_grid1]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
   
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'dfgfdg',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
    Case
     When @sortcols='' then 'Order by sort2'
     Else @sortcols 
    End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион],' + @params+',cast( ''<properties>
            <event name="row_single_click">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по строке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event> 
                                <event name="row_double_click">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 2 раза по строке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>   
                          <event name="cell_single_click" column="Регион">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по ячейке Регион</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event> 
                    
                          <event name="cell_double_click" column="Регион">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 2 раза по ячейке Регион</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>   
                            
                    
                                           <event name="cell_single_click" column="нет такой">
                        <action>
                       <main_context>current</main_context>                        
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по несуществующей ячейке</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>  
                    
            
                    
                                           <event name="cell_single_click" column="3кв. 2005г.">
                        <action>
                       <main_context>current</main_context>                                                
                       <datapanel type="current" tab="current">
                                <element id="10">
									<add_context>''+[Регион]+'' 1 раз по  ячейке 3кв. 2005г.</add_context>                                                                                             
                                </element> 
                                                             
                            </datapanel>
                        </action>
                    </event>  
                                                              
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT top 1 [Регион],sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
   where #Reg_year.[Регион] = '''+@add_context+'''  ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt )p  '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>

        <labels>
            <header><h3>'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
<action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="110">
	                                <add_context>hide</add_context>
                                </element>                                                             
                                               
                            </datapanel>
                        </action>
' 
set @gridsettings_str=@gridsettings_str+'

 <properties flip="false"  selectMode="cell" totalCount="0" autoSelectRecordId = "5" autoSelectRelativeRecord="false"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_portals : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_portals]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="15"
totalCount="0" profile="grid.nowidth.properties"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS [Название], [Logo] AS [Логотип], [Url] as [URL], cast( '<properties>                                    
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure grid_portals_id_and_css : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[grid_portals_id_and_css]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="2" profile="grid.nowidth.properties" autoSelectRecordId="3" 
 autoSelectRelativeRecord="false" autoSelectColumnId="URL"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS "Название", [Logo] AS "Логотип", [Url] as "URL", '<id>'+ cast(Id as varchar(max))+'</id>' AS "~~id",  cast( '<properties>
			<styleClass name="grid-record-bold"/>
			<styleClass name="grid-record-italic"/>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="d1">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                                <element id="d2">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element>                                
                            </datapanel>
                        </action>
                    </event>  			
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END

GO

--
-- Definition for stored procedure grid_special_profile : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:        <Author,,Name>
-- Create date: <Create Date,,>
-- Description:    <Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[grid_special_profile]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a
Where a.Год = 2005   

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], getdate() as [Сейчас],  ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> 
        <col id="Картинка" width="40px" type="IMAGE"/>'
        
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" profile="special.properties" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure grid_variables : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_variables]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                
                            </datapanel>
                        </action>
                    </event>                                        
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
<header>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</header>
<footer>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</footer>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> <col id="Картинка" width="20px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
                        <action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>''+[Регион]+''</add_context>  
                                </element>                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="15" autoSelectRecordId="16" autoSelectRelativeRecord="false" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO

--
-- Definition for stored procedure header_proc : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[header_proc]
	@session_context xml,	
	@framedata varchar(MAX) output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SET @framedata = '<h1 align="center">Заголовок из БД</h1>'
	RETURN 0
END
GO

--
-- Definition for stored procedure header_proc_with_error : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[header_proc_with_error]
	@session_context xml,	
	@framedata varchar(MAX) output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
 
	SET @error_mes = 'Ошибка, переданная через @error_mes' 
	RETURN 1
END
GO

--
-- Definition for stored procedure regions_list_and_count : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[regions_list_and_count](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @firstRecord INT,
   @recordCount INT,
   @countAllRecords INT OUTPUT
   ) 
   AS
   BEGIN
      SET NOCOUNT ON;
   
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
         SET @curValue = '%'+@curValue+'%';
         
      SELECT @countAllRecords = COUNT(*) FROM [dbo].[geo5] WHERE [Name] LIKE @curValue;          
         
      WITH result AS 	(
         SELECT 
            [Id], 
            [Name],
            [CreateRowUser],
            ROW_NUMBER() 
            OVER (ORDER BY [Name]) AS rnum 
         FROM [dbo].[geo5] WHERE [Name] LIKE @curValue)
      SELECT
         [Id], [Name], [CreateRowUser]
--         [Journal_47_Name] as Name, [Journal_47_Id] as Id 
         FROM result WHERE rnum BETWEEN (@firstRecord + 1) AND (@firstRecord + @recordCount)
         ORDER BY rnum;	
   END
GO

--
-- Definition for stored procedure regionscount : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[regionscount](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @count INT OUTPUT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
      SET @curValue = '%'+@curValue+'%';
 
      SELECT @count = COUNT(*) FROM [dbo].[geo5] WHERE [Name] LIKE @curValue; 
   END
GO

--
-- Definition for stored procedure regionslist : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE  PROCEDURE [dbo].[regionslist](
   @main_context     varchar(512),
   @add_context      varchar(512),
   @filterinfo       xml,
   @session_context  xml,
   @params VARCHAR(MAX),
   @curValue VARCHAR(MAX),
   @startsWith BIT,
   @firstRecord INT,
   @recordCount INT
   ) 
   AS
   BEGIN
      IF @startsWith = 1
         SET @curValue = @curValue + '%'
      ELSE
         SET @curValue = '%'+@curValue+'%';
      WITH result AS 	(
         SELECT 
            [Id], 
            [Name],
            ROW_NUMBER() 
            OVER (ORDER BY [Name]) AS rnum 
         FROM [dbo].[geo5] WHERE [Name] LIKE @curValue)
      SELECT
         [Id], [Name] FROM result WHERE rnum BETWEEN (@firstRecord + 1) AND (@firstRecord + @recordCount)
         ORDER BY rnum;	
   END
GO

--
-- Definition for stored procedure sc_add_to_debug_console : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sc_add_to_debug_console]
(@data varchar(max),
@label varchar(max) = null)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    INSERT INTO dbo.DebugConsole (label, data) VALUES (@label, @data)
END
GO

--
-- Definition for stored procedure sc_add_to_debug_console_adapter : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sc_add_to_debug_console_adapter]
	@main_context varchar(512),
	@add_context xml,
	@filterinfo xml,
	@session_context xml,	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	

DECLARE @recid VARCHAR(MAX)
SET @recid = (select @session_context.value('(/sessioncontext/related/gridContext/currentRecordId)[1]','varchar(MAX)'))
if (@recid IS NULL)
SET @recid = ''	

	DECLARE @add_context_str VARCHAR(MAX)
	SET @add_context_str = isnull(cast(@add_context as VARCHAR(MAX)),'')
	
	DECLARE @mes varchar(MAX)
	SET @mes = '<time>' + CONVERT(varchar(MAX), GETDATE(), 114) + 
	'</time><add_context>' + @add_context_str +'</add_context><related_recid>related=' + @recid + '</related_recid>'
	EXEC sc_add_to_debug_console @mes
END
GO

--
-- Definition for stored procedure sc_init_debug_console : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sc_init_debug_console]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

delete dbo.DebugConsole

END
GO

--
-- Definition for stored procedure sc_init_debug_console_adapter : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sc_init_debug_console_adapter]
	@main_context varchar(512),
	@add_context varchar(MAX),	
	@filterinfo xml,
	@session_context xml,	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	EXEC sc_init_debug_console
END
GO

--
-- Definition for stored procedure sc_show_debug_console : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[sc_show_debug_console] 

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT * from dbo.DebugConsole
END
GO

--
-- Definition for stored procedure webtext_3buttons : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_3buttons]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

declare @addc varchar(MAX)
if @add_context = ''
set @add_context = NULL
SET @addc = coalesce(@add_context, '')

set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Добавить</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Редактировать</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''3'');">Удалить</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
							<modalwindow caption="Тестовая карточка - новая запись" width="500" height="150" show_close_bottom_button="true"/>
                            <datapanel type="current" tab="current">
                                <element id="84">
	                                <add_context>add</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">							                      
                        <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>                        
                        <modalwindow caption="Тестовая карточка - редактирование" width="400" height="150"/> 
                            <datapanel type="current" tab="current">
                                <element id="84">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                        
                       <event name="single_click" linkId="3">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="83">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>   
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_3buttons_enh : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_3buttons_enh]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Добавить</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Редактировать</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''3'');">Удалить</button>
</div>' as xml)

declare @addc varchar(MAX)
if @add_context = ''
set @add_context = NULL
SET @addc = coalesce(@add_context, '')

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
							<modalwindow caption="Тестовая карточка - новая запись" width="500" height="150" show_close_bottom_button="true"/>
                            <datapanel type="current" tab="current">
                                <element id="card1">
	                                <add_context><name></name><table>geo3</table><name_field>Name</name_field></add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">							                      
                        <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>                        
                        <modalwindow caption="Тестовая карточка - редактирование" width="400" height="150"/> 
                            <datapanel type="current" tab="current">
                                <element id="card1">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
								<element id="card1_d1" >
									<add_context>'+@addc+'</add_context>
								</element>
                            </datapanel>
                        </action>
                       </event>  
                        
                       <event name="single_click" linkId="3">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>   
                            <modalwindow caption="Тестовая карточка - удаление" width="400" height="100"/>                                                  
                            <datapanel type="current" tab="current">
                                <element id="delConfirm1">
	                                <add_context>'+@addc+'</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>   
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_bal : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_bal]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if @add_context=''
set @add_context='Итого по России'
set	@webtextdata=CAST('<div><h1>@add_context='+@add_context+'</h1>
<h2>@filterinfo='+CAST(@filterinfo as varchar(MAX))+'</h2>
<a onclick="gwtWebTextFunc(''5'', ''2'')" class="linkStyle" >Гиперактивная ссылка</a></div>' as xml)
set @webtextsettings='<properties>
									
						<event name="single_click" linkId="2">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="6">
	                                <add_context>'+@add_context+'</add_context>
                                </element>                                    
                                                             
                            </datapanel>
                        </action>
                    </event>
								</properties>'
END
GO

--
-- Definition for stored procedure webtext_buttons_uco : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_buttons_uco]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">add_context = add</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">add_context = context</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
							<datapanel type="current" tab="current">
                                <element id="0201">
	                                <add_context>add</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">							                      
                        <action>
                        <main_context>current</main_context>                        
						   <datapanel type="current" tab="current">
                                <element id="0201">
	                                <add_context>context</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>  
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_call_sp : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_call_sp]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''0201'',''1'');">Очистить отладочную консоль</button>
<button type="button" onclick="gwtWebTextFunc(''0201'',''2'');">Добавить запись в консоль</button>
</div>' as xml)

declare @addc varchar(MAX)
if @add_context = ''
set @add_context = NULL
SET @addc = coalesce(@add_context, '<int_context int_attr="attr_value">
	Вызвана из [webtext_call_sp]
	<test>
	c тэгами
	</test>
</int_context>')

set @webtextsettings=
'<properties>                           
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>                        
						  <datapanel type="current" tab="current">
                                <element id="0202">
	                                <add_context>add</add_context>
                                </element>                                                                                             
                            </datapanel>
                            <server>
								<activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">
                        <action >
                            <main_context>current</main_context>                        
						  <datapanel type="current" tab="current">
                                <element id="0202">
	                                <add_context>add</add_context>
                                </element>                                                                                             
                            </datapanel>
                            <server>
								<activity id="srv02" name="sc_add_to_debug_console_adapter">
								    <add_context>'+@addc+'
								    </add_context>
								</activity>
                            </server>
                        </action>
                       </event>  
                        
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_context_info : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_context_info]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if @add_context=''
set @add_context='Итого по России'
set	@webtextdata=CAST(
'<div>
<h1>@main_context='+@main_context+'</h1>
<h1>@add_context='+@add_context+'</h1>
<h2>@filterinfo='+CAST(@filterinfo as varchar(MAX))+'</h2>
<h2>@session_context='+CAST(@session_context as varchar(MAX))+'</h2>
<a href="?userdata=test1" target="_blank">Переход на test1</a>
<a href="?userdata=test2" target="_blank">Переход на test2</a>
</div>
' 
as xml)

set @webtextsettings='<properties>									
								</properties>'
END
GO

--
-- Definition for stored procedure webtext_dep62 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_dep62]
@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

DECLARE @add_related VARCHAR(MAX)
SET @add_related=(select @session_context.value('(/sessioncontext/related/gridContext/additional)[1]','varchar(MAX)'))
if (@add_related IS NULL)
SET @add_related = ''
DECLARE @filter_related VARCHAR(MAX)
SET @filter_related=(select @session_context.value('(/sessioncontext/related/gridContext/filter)[1]','varchar(MAX)'))
if (@filter_related IS NULL)
SET @filter_related = ''
DECLARE @recid VARCHAR(MAX)
SET @recid = (select @session_context.value('(/sessioncontext/related/gridContext/currentRecordId)[1]','varchar(MAX)'))
if (@recid IS NULL)
SET @recid = 'нет выделения'

set	@webtextdata=CAST('<div>
<a onclick="gwtWebTextFunc('''+@element_id+''', ''1'')" class="linkStyle" >Обнови меня скорей!</a>
<h3>add_context грида равен '+@add_related+'</h3>
<h3>filter_context грида равен '+@filter_related+'</h3>
<h3>в гриде выделена запись с ID  '+@recid+'</h3>
</div>' as xml)
set @webtextsettings='<properties>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="'+@element_id+'">
	                                <add_context>SelfRefresh Context</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>     
					</properties>'
					


END
GO

--
-- Definition for stored procedure webtext_dyn_dp_main : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_dyn_dp_main]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set	@webtextdata='<div/>';
set @webtextsettings='<properties>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynMain"/>                                                               
                        </action>
                    </event>
				</properties>'
END
GO

--
-- Definition for stored procedure webtext_dyn_dp_session : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].webtext_dyn_dp_session
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set	@webtextdata='<div/>';
set @webtextsettings='<properties>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>Правильный контекст</main_context>
                            <datapanel type="dp0903dynSession"/>                                                               
                        </action>
                    </event>
				</properties>'
END
GO

--
-- Definition for stored procedure webtext_filter_and_add : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_filter_and_add]
	@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
DECLARE @filter varchar(512)
SET @filter=CAST(@filterinfo AS varchar(MAX))
if (@filter IS null) 
SET @filter = ''

set	@webtextdata=CAST('<div><h1>Add context='+@add_context+'</h1><h1>Filter='+@filter+'</h1></div>' as xml)
--set @webtextsettings='<properties></properties>'
									
			
								
END
GO

--
-- Definition for stored procedure webtext_filter_and_add1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_filter_and_add1]
	@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
DECLARE @filter varchar(512)
SET @filter=CAST(@filterinfo AS varchar(MAX))
if (@filter IS null) 
SET @filter = ''

set	@webtextdata=CAST('<div>
<button onclick="refreshElementFromBaseFeedbackJSNIFunction(''52'')">Обновить</button>

<h1>Add context='+@add_context+'</h1><h1>Filter='+@filter+'</h1></div>' as xml)
--set @webtextsettings='<properties></properties>'
									
			
								
END
GO

--
-- Definition for stored procedure webtext_filter_and_add2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_filter_and_add2]
	@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
DECLARE @filter varchar(512)
SET @filter=CAST(@filterinfo AS varchar(MAX))
if (@filter IS null) 
SET @filter = ''
DECLARE @t varchar(512)
SET @t =convert(time,getdate(),120)
set	@webtextdata=CAST('<div>

<button onclick="refreshElementFromBaseFeedbackJSNIFunction(''520'')">Обновить</button>


<h1>Обновление данных по таймеру</h1> <h1>Текущее время: '+@t+'</h1></div>' as xml)
--set @webtextsettings='<properties></properties>'
									
			
								
END
GO

--
-- Definition for stored procedure webtext_for_date : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].webtext_for_date
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Редактировать</button>
</div>' as xml)

set @webtextsettings='<properties>    
                              
                          
                       <event name="single_click" linkId="2">							                      
                        <action show_in="MODAL_WINDOW">
                        <main_context>current</main_context>                        
                        <modalwindow caption="Редактирование даты" width="400" height="200"/> 
                            <datapanel type="current" tab="current">
                                <element id="dateCard">
	                                <add_context>edit</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>  
                         
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_grid : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_grid]
	@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;


set	@webtextdata=CAST('<div><h1>'+@add_context+'</h1></div>' as xml)

set @webtextsettings='<properties>
<action>

                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="100">
	                                <add_context>hide</add_context>
                                </element>                                    
                                               
                            </datapanel>
                        </action>
</properties>'
									
			
								
END
GO

--
-- Definition for stored procedure webtext_mesid : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_mesid]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''&lt;mesid&gt;555&lt;/mesid&gt;'');">Установить mesid 555</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''&lt;mesid&gt;556&lt;/mesid&gt;'');">Установить mesid 556</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>                        
							<datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>add</add_context>
                                </element>                                                             
                                <element id="d2">
	                                <add_context>add</add_context>
                                </element> 
                                <element id="d3">
	                                <add_context>add</add_context>
                                </element>                                                                 
                            </datapanel>
                        </action>
                       </event>  
                                                                      
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_navigator : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_navigator]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''31'',''1'');">Обновить навигатор</button>
<br/>
<button type="button" onclick="gwtWebTextFunc(''31'',''2'');">Выделить 7-й этап, не переходя на него</button>
<br/>
<button type="button" onclick="gwtWebTextFunc(''31'',''3'');">Перейти на 7-й этап</button>
<br/>
<button type="button" onclick="gwtWebTextFunc(''31'',''4'');">Заново открыть текущий этап с обновлением навигатора</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
							<navigator refresh="true"/>
                            <datapanel type="current" tab="current">                                                                                            
                            </datapanel>							
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">							                      
                        <action>
                            <main_context>current</main_context>
                            <navigator element="07"/>
                            <datapanel type="current" tab="current">
                               <element id="32">
	                                <add_context>add_context_from_DB</add_context>
                                </element>    
                                                                
                            </datapanel>
                        </action>
                       </event>  
                        
                       <event name="single_click" linkId="3">
                        <action>
							<navigator element="07"/>
                        </action>                                                  
                       </event>   
                       
                       <event name="single_click" linkId="4">
                        <action>
							<navigator refresh="true" element="04"/>
                        </action>                                                  
                       </event>                          
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_override_add_context : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_override_add_context]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Оригинальный контекст</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''additional перекрыт'');">additional override</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''main перекрыт'', ''MAIN_CONTEXT'');">main override</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''d2'', ''ELEMENT_ID'', ''d1'');">elementId override</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'', ''&lt;filter&gt;перекрыт&lt;/filter&gt;'', ''FILTER_CONTEXT'');">filter override</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Очистить отладочную консоль</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
								 <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>я оригинальный</add_context>
                                </element>  
                                <element id="d2">
	                                <add_context>я оригинальный</add_context>
                                </element>                                                                                                                            
                            </datapanel>
                            <server>
								<activity id="sa1" name="sc_add_to_debug_console_adapter">
									<add_context>я оригинальный</add_context>
								</activity>
                            </server>
                            <client>
								<activity id="ca1" name="showcaseShowAllContexts">
									<add_context>я оригинальный</add_context>
								</activity>
                            </client>                            
                        </action>
                       </event>  
                                 
                      <event name="single_click" linkId="2">
                        <action >
                            <main_context>current</main_context>    
								 <datapanel type="current" tab="current">
                                <element id="d2">
	                                <add_context>я оригинальный</add_context>
                                </element>                                                                                                                            
                            </datapanel>                                                
                            <server>
								<activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                       </event>                                                 
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_pas : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_pas]
	@main_context varchar(512) ='Алтайский край',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	Declare @name as varchar(256)
	

select 	@name='<root><name>'+NAME+'</name><count>'+cast(geo5._Id*1000000 as varchar(256))+'</count></root>' From geo5 where NAME=@main_context
set	@webtextdata=CAST(@name as xml)
set @webtextsettings=null
END
GO

--
-- Definition for stored procedure webtext_pas_tranform : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_pas_tranform]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>
    <xsl:template match="/">
        <div>
            <h2 align="center">Паспорт региона</h2>
            <h3 align="center">Общие сведения</h3>
            <div>
                <span>
                    <xsl:value-of select="concat(''Название региона - '', /root/name)"/> 
                </span>
            </div>
            <div><span style="position:relative;vertical-align:40px;"> Флаг - &#160;&#160;&#160;&#160;</span>                
                <img src="solutions/default/resources/webtext/Flag_of_Russia.png" alt="" style="border: 1px solid black;"/>
            </div>
            
            <div>
                <span style="position:relative;vertical-align:50px;"> Герб - 
                    &#160;&#160;&#160;&#160;&#160;</span>
                <img src="solutions/default/resources/webtext/Gerb_of_Russia.png" alt="" style=""/>
            </div>
            <div>
                <span> <xsl:value-of select="concat(''Площадь - '', /root/count)"/> <sup>2</sup> </span>
            </div>
            <div>
                <span> Население - 111 тыс чел</span>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
' 

set  @settings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure webtext_pas_with_events : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_pas_with_events]
	@main_context varchar(512) ='Алтайский край',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	Declare @name as varchar(256)
	

select 	@name='<root><name>'+NAME+'</name><count>'+cast(geo5._Id*1000000 as varchar(256))+'</count></root>' From geo5 where NAME=@main_context
set	@webtextdata=CAST(@name as xml)
set @webtextsettings='<properties>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
	                                <add_context>current</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
<event name="single_click" linkId="0">
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
	                                <add_context>current</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
</event></properties>'
END
GO

--
-- Definition for stored procedure webtext_sa : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_sa]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Очистить отладочную консоль</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''3'');">Добавить запись в консоль</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'', ''bal1.xsl'');">Вызвать Jython скрипт (вариант bal1)</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'', ''bal2.xsl'');">Вызвать Jython скрипт (вариант bal2)</button>
</div>' as xml)


set @webtextsettings=
'<properties>                           
                      <event name="single_click" linkId="1">
                        <action >
                            <main_context>current</main_context>                        
						  <datapanel type="current" tab="current">
                                <element id="d2">
	                                <add_context>add</add_context>
                                </element>                                                                                             
                            </datapanel>
                            <server>
								<activity id="srv01" name="sc_init_debug_console_adapter"/>
                            </server>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">
                        <action >
                            <main_context>current</main_context>                        
						    <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>fake</add_context>
                                </element>                                                                                             
                            </datapanel>
                            <server>
								<activity id="srv02" name="FilesReplaceJythonProc.py">
								    <add_context>
								    bal1.xsl
									</add_context>
								</activity>
                            </server>
                        </action>
                       </event>  
                       <event name="single_click" linkId="3">
                        <action >
                            <main_context>current</main_context>                        
						  <datapanel type="current" tab="current">
                                <element id="d2">
	                                <add_context>add</add_context>
                                </element>                                                                                             
                            </datapanel>
                            <server>
								<activity id="srv02" name="sc_add_to_debug_console_adapter">
								    <add_context>
										<int_context int_attr="attr_value">
											контекст
											<test>
											 + 1 тэг внутри
											</test>
										</int_context>
								    </add_context>
								</activity>
                            </server>
                        </action>
                       </event>                              
                                                                                          
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_sa_error : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_sa_error]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''1'');">Ошибка через UserMessage для ServerActivivty</button>
<button type="button" onclick="gwtWebTextFunc(''${elementId}'',''2'');">Ошибка через UserMessage для WebText</button>
</div>' as xml)


set @webtextsettings=
'<properties>                                                     
                       <event name="single_click" linkId="1">
                        <action >
                            <main_context>плохой</main_context>                        
                            <server>
								<activity id="srv02" name="NoValidateJythonProc.py">
								    <add_context>
									</add_context>
								</activity>
                            </server>
                        </action>
                       </event>                              
                       <event name="single_click" linkId="2">
                        <action >
                            <main_context>плохой</main_context>                        
						    <datapanel type="current" tab="current">						    
                                <element id="d1">
	                                <add_context>fake</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>                                                                                            
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_self_refresh : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_self_refresh]
@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if (@add_context ='')
SET @add_context='Здесь мог бы быть...'

set	@webtextdata=CAST('<div><h1>'+@add_context+ '</h1>
<a onclick="gwtWebTextFunc(''77'', ''1'')" class="linkStyle" >Обнови меня скорей!</a></div>' as xml)
set @webtextsettings='<properties>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="77">
	                                <add_context>SelfRefresh Context</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>     
					</properties>'
END
GO

--
-- Definition for stored procedure webtext_show_debug_console : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_show_debug_console]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

DECLARE @data varchar(MAX)
DECLARE @data_text varchar(MAX)
set @data_text = ''
/*Объявляем курсор*/
DECLARE @CURSOR CURSOR
/*Заполняем курсор*/
SET @CURSOR  = CURSOR SCROLL
FOR
SELECT data FROM DebugConsole
/*Открываем курсор*/
OPEN @CURSOR
/*Выбираем первую строку*/
FETCH NEXT FROM @CURSOR INTO @data
/*Выполняем в цикле перебор строк*/
WHILE @@FETCH_STATUS = 0
BEGIN
set @data_text = @data_text + ISNULL(@data,'')

FETCH NEXT FROM @CURSOR INTO @data
END
CLOSE @CURSOR

set    @webtextdata=CAST(
'<div>'
+@data_text+
'</div>' as xml)

set @webtextsettings='<properties>                                                                      
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_user_mes : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_user_mes]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

if (@add_context = 'test1') 
INSERT INTO [dbo].[UserMessagesTest]
           ([test1]
           ,[test2]
           ,[test3])
     VALUES
           (
           'test1',
           'test22',
           'test33')
           
if (@add_context = 'test2') 
INSERT INTO [dbo].[UserMessagesTest]
           ([test1]
           ,[test2]
           ,[test3])
     VALUES
           (
           'test11',
           'test2',
           'test33')  
           
if (@add_context = 'test3')
raiserror ('__user_mes_test3_src__',12,1)
return  
                   

END
GO

--
-- Definition for stored procedure webtext_user_mes_activator : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[webtext_user_mes_activator]
@main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
    @webtextdata xml output,
    @webtextsettings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @webtextdata=CAST(
'<div><button type="button" onclick="gwtWebTextFunc(''0301'',''1'');">Ошибка</button>
<button type="button" onclick="gwtWebTextFunc(''0301'',''2'');">Предупреждение</button>
<button type="button" onclick="gwtWebTextFunc(''0301'',''3'');">Информация</button>
</div>' as xml)

set @webtextsettings='<properties>    
                                
                       <event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
								 <datapanel type="current" tab="current">
                                <element id="0302">
	                                <add_context>test1</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>  
                          
                       <event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>                        
								 <datapanel type="current" tab="current">
                                <element id="0302">
	                                <add_context>test2</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>  
                        
                       <event name="single_click" linkId="3">
                        <action>
                            <main_context>current</main_context>                        
								 <datapanel type="current" tab="current">
                                <element id="0302">
	                                <add_context>test3</add_context>
                                </element>                                                                                             
                            </datapanel>
                        </action>
                       </event>    
                                                                  
                    </properties>'

END
GO

--
-- Definition for stored procedure webtext_variables : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_variables]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set	@webtextdata=CAST(
'<div>
<h1>userdata.dir=${userdata.dir}</h1>
<h1>images.in.grid.dir=${images.in.grid.dir}</h1>
<h2>elementId=${elementId}</h2>
<img src="${images.in.grid.dir}/header.jpg"/>
</div>
' 
as xml)

set @webtextsettings='<properties>									
								</properties>'
END
GO

--
-- Definition for stored procedure webtext_wrong : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_wrong]
@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set	@webtextdata=NULL
set @webtextsettings='<properties1>									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="77">
	                                <add_context>SelfRefresh Context</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                       </event>     
					</properties1>'
END
GO

--
-- Definition for stored procedure webtext_wrong_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_wrong_userdata]
	@main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
	@add_context varchar(512) ='Итого по России',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

set	@webtextdata=CAST(
'<div>
<a href="?userdata=test3" target="_blank">Переход на test3</a>
</div>
' 
as xml)

set @webtextsettings='<properties>									
								</properties>'
END
GO

--
-- Definition for stored procedure webtext_wt : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[webtext_wt]
@main_context varchar(512) ='',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@webtextdata xml output,
	@webtextsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;



set	@webtextdata=CAST('<div>
<a onclick="gwtWebTextFunc(''7'', ''1'')" class="linkStyle" >'+@add_context+'</a></div>' as xml)
set @webtextsettings='<properties>
                       <action>
                            <main_context>current</main_context>                                               
                            <datapanel type="current" tab="current">
                                <element id="8">
	                                <add_context>hide</add_context>
                                </element>   
                            </datapanel>
                        </action>
									
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                                                
                            <datapanel type="current" tab="current">
                                <element id="8">
	                                <add_context>'+ @add_context+ '</add_context>
                                </element>   
                            </datapanel>
                        </action>
                       </event>
                       
                         
								</properties>'


END
GO

--
-- Definition for stored procedure wsHandle : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
-- EXEC [wsHandle] @request = '<command type="select" table="GeoObjects" id="1" column="Code" />'
CREATE PROCEDURE [dbo].[wsHandle] 
	@request varchar(MAX) = '',
	@response varchar(MAX) = '' output,
	@error_mes varchar(512) = '' output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @request_xml xml
	SET @request_xml = CAST(@request as xml)
	DECLARE @type VARCHAR(MAX)	
    SET @type=(select @request_xml.value('(/command/@type)[1]','varchar(MAX)'))
	if @type <> 'select'
		raiserror ('команда не поддерживается',12,1)	
	DECLARE @table VARCHAR(MAX)	
    SET @table=(select @request_xml.value('(/command/@table)[1]','varchar(MAX)'))		
	DECLARE @id VARCHAR(MAX)	
    SET @id=(select @request_xml.value('(/command/@id)[1]','varchar(MAX)'))	
	DECLARE @column VARCHAR(MAX)	
    SET @column=(select @request_xml.value('(/command/@column)[1]','varchar(MAX)'))	        
    DECLARE @sql varchar(MAX)
    create table #tmp_res (result varchar(max))
    SET @sql = 'INSERT INTO [#tmp_res] SELECT ' + @column + ' FROM ' + @table + ' WHERE ID=' + @id
    EXEC(@sql)
	SET @response=(SELECT top 1 * FROM [#tmp_res])
END
GO

--
-- Definition for stored procedure xforms_date : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_date]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata='<schema xmlns=""><date>'+(select top 1 convert(varchar(MAX), [date], 126) from XFormsTest)+'</date></schema>';
   
	SET @xformssettings=
	'<properties>

						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="6">                                                            
                                
                            </datapanel>
                        </action>
                    </event>                   
</properties>';
END
GO

--
-- Definition for stored procedure xforms_date_save : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_date_save]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @date date;
    SET @date=(select @xformsdata.value('(/schema/date)[1]','date'))

	update [dbo].[XFormsTest] set [date]=@date
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_delConfirm1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_delConfirm1]
	@main_context varchar(MAX),
	@add_context varchar(MAX),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if (@add_context = 'add') 
SET @add_context=''
   
   DECLARE @add_context_xml xml
   SET @add_context_xml=CAST(@add_context as xml)   
   
   SET @xformsdata='<schema xmlns="">
                    <info>
                        <name>'+(select @add_context_xml.value('(/name)[1]','varchar(MAX)'))+'</name>
                        <table>'+(select @add_context_xml.value('(/table)[1]','varchar(MAX)'))+'</table>
                        <name_field>'+(select @add_context_xml.value('(/name_field)[1]','varchar(MAX)'))+'</name_field> 
                    </info>
                </schema>';

             
      
   SET @xformssettings=
	'<properties>
						<event name="single_click" linkId="1">
                        <action keep_user_settings="true">
                            <main_context>current</main_context>
                            <datapanel type="current" tab="8">
                            </datapanel>
                        </action>
                        </event>
                        
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="table1" keep_user_settings="true">
	                                <add_context>some_fake_name</add_context>
                                </element>  
                                                            
                            </datapanel>
                        </action>
                        </event>                                                                                           
</properties>';
END
GO

--
-- Definition for stored procedure xforms_download_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_download_by_userdata]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   	
	DECLARE @navigator xml
	EXEC [generationtree] '', @navigator OUTPUT
	SET @file = CAST(@navigator AS varbinary(MAX))
	SET @filename=@session+'.xml'
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_download1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_download1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @navigator xml
	EXEC [generationtree] '', @navigator OUTPUT
	SET @file = CAST(@navigator AS varbinary(MAX))
	SET @filename='navigator.xml'
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_download2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_download2]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @xml VARCHAR(MAX)
	SET @xml = '<documentset>
	<document>
		<documentset>
			<document>
				<Schema>
					<Temp>
						<Flag />
						<Error />
					</Temp>
					<Stat>
						<Isp />
						<DateIsp />
						<Uch />
						<DateUch />
						<AC />
						<DateAC />
						<Paper />
						<DatePaper />
					</Stat>
					<Info>
						<year>2010</year>
						<FIO />
						<remark />
						<ChangeStr>false</ChangeStr>
						<Executor />
						<Date />
					</Info>
				</Schema>
			</document>
		</documentset>
	</document>
</documentset>'
	
	SET @file = CAST(@xml AS varbinary(MAX))
	SET @filename='TestTextSampleSmall.xml'
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_download3_wrong : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_download3_wrong]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@filename varchar(64) output,
	@file varbinary(MAX) output,
	@error_mes varchar(512) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @xml VARCHAR(MAX)
	SET @xml = '<documentset>
	<document>
		<documentset>
			<document>
				<Schema1>
					<Temp>
						<Flag />
						<Error />
					</Temp>
					<Stat>
						<Isp />
						<DateIsp />
						<Uch />
						<DateUch />
						<AC />
						<DateAC />
						<Paper />
						<DatePaper />
					</Stat>
					<Info>
						<year>2010</year>
						<FIO />
						<remark />
						<ChangeStr>false</ChangeStr>
						<Executor />
						<Date />
					</Info>
				</Schema1>
			</document>
		</documentset>
	</document>
</documentset>'
	
	SET @file = CAST(@xml AS varbinary(MAX))
	SET @filename='TestTextSampleSmall.xml'
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_proc_all : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc_all]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>     
						<event name="single_click" linkId="3">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>update child only</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>                                   
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc_dep : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc_dep]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d01">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                <element id="d02">
	                                <add_context>xforms default action</add_context>
                                </element>                                   
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d01">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                <element id="d02">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                 
                            </datapanel>
                        </action>
                    </event>
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d01">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                                             
                                <element id="d02">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                
                            </datapanel>
                        </action>
                    </event>                    
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc_no_data : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc_no_data]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=null;
   
	SET @xformssettings=
	'<properties>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>                    
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc_override_add_context : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc_override_add_context]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                       <event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                        
								 <datapanel type="current" tab="current">
                                <element id="d1">
	                                <add_context>я оригинальный</add_context>
                                </element>  
                                <element id="d2">
	                                <add_context>я оригинальный</add_context>
                                </element>                                                                                                                            
                            </datapanel>
                            <server>
								<activity id="sa1" name="sc_add_to_debug_console_adapter">
									<add_context>я оригинальный</add_context>
								</activity>
                            </server>
                            <client>
								<activity id="ca1" name="showcaseShowAddContext">
									<add_context>я оригинальный</add_context>
								</activity>
                            </client>                            
                        </action>
                       </event>               
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc_wrong_1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_proc_wrong_1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if (@add_context = 'add') 
SET @add_context=''

   DECLARE @add_context_xml xml
   SET @add_context_xml=CAST(@add_context as xml)   
   
   SET @xformsdata=				'<schema xmlns="">
                    <info>
                        <name>'+(select @add_context_xml.value('(/name)[1]','varchar(MAX)'))+'</name>
                        <old_name>'+(select @add_context_xml.value('(/name)[1]','varchar(MAX)'))+'</old_name>
						<table>'+(select @add_context_xml.value('(/table)[1]','varchar(MAX)'))+'</table>                        
						<name_field>'+(select @add_context_xml.value('(/name_field)[1]','varchar(MAX)'))+'</name_field> 
                    </info>
                </schema>';

             
      
   SET @xformssettings=
	'<properties>
						<event name="single_click" linkId="1">
                        <action keep_user_settings="true">
                            <main_context>current</main_context>
                            <datapanel type="current" tab="8">
                            </datapanel>
                        </action>
                        </event>
						<event name="single_click" linkId="1.1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="table1" keep_user_settings="true">
	                                <add_context>some_fake_name</add_context>
                                </element>  
                                                            
                            </datapanel>
                        </action>
                        </event>                        
						<event name="single_click" linkId="2">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="card1" refresh_context_only="true">
	                                <add_context>add</add_context>
                                </element>                              
                            
                            </datapanel>
                        </action>
                        </event>                                                
                                           
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>
						<event name="single_click" linkId="2">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="62">
	                                <add_context>filter click on xforms</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>                    
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc2 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_proc2]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=null;
      
   SET @xformssettings=
	'<properties>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="82">
	                                <add_context>fake</add_context>
                                </element>                           
                                <element id="83">
	                                <add_context>fake</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                        </event>
                                           
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc21 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_proc21]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="22">
	                                <add_context>xforms default action</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
						<event name="single_click" linkId="1">
                        <action>
                            <main_context>current</main_context>                                                
                            <datapanel type="current" tab="current">
                                <element id="22">
	                                <add_context>save click on xforms (with filtering)</add_context>
                                </element>                                                             
                                
                            </datapanel>
                        </action>
                    </event>                  
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc3 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_proc3]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if (@add_context = 'add') 
SET @add_context=''
   
   SET @xformsdata=				'<schema xmlns="">
                    <info>
                        <name>'+@add_context+'</name>
                        <old_name>'+@add_context+'</old_name>
                    </info>
                </schema>';

             
      
   SET @xformssettings=
	'<properties>
						<event name="single_click" linkId="1">
                        <action keep_user_settings="true">
                            <main_context>current</main_context>
                            <datapanel type="current" tab="8">
                            </datapanel>
                        </action>
                        </event>
						<event name="single_click" linkId="1.1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="83" keep_user_settings="true">
	                                <add_context>some_fake_name</add_context>
                                </element>  
                                                            
                            </datapanel>
                        </action>
                        </event>                        
						<event name="single_click" linkId="2">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="84">
	                                <add_context>add</add_context>
                                </element>                              
                            
                            </datapanel>
                        </action>
                        </event>                                                
                                           
</properties>';
END
GO

--
-- Definition for stored procedure xforms_proc3_enh : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_proc3_enh]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

if (@add_context = 'add') 
SET @add_context=''

   DECLARE @add_context_xml xml
   SET @add_context_xml=CAST(@add_context as xml)   
   
   SET @xformsdata=				'<schema xmlns="">
                    <info>
                        <name>'+(select @add_context_xml.value('(/name)[1]','varchar(MAX)'))+'</name>
                        <old_name>'+(select @add_context_xml.value('(/name)[1]','varchar(MAX)'))+'</old_name>
						<table>'+(select @add_context_xml.value('(/table)[1]','varchar(MAX)'))+'</table>                        
						<name_field>'+(select @add_context_xml.value('(/name_field)[1]','varchar(MAX)'))+'</name_field> 
                    </info>
                </schema>';

             
      
   SET @xformssettings=
	'<properties>
						<event name="single_click" linkId="1">
                        <action keep_user_settings="true">
                            <main_context>current</main_context>
                            <datapanel type="current" tab="8">
                            </datapanel>
                        </action>
                        </event>
						<event name="single_click" linkId="1.1">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="table1" keep_user_settings="true">
	                                <add_context>some_fake_name</add_context>
                                </element>  
                                                            
                            </datapanel>
                        </action>
                        </event>                        
						<event name="single_click" linkId="2">
                        <action show_in="MODAL_WINDOW">
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="card1">
	                                <add_context>add</add_context>
                                </element>                              
                            
                            </datapanel>
                        </action>
                        </event>                                                
                                           
</properties>';
END
GO

--
-- Definition for stored procedure xforms_save_error_proc1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_save_error_proc1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SET @error_mes='Неуловимая ошибка из БД, связанная с триггерами и блокировками'
	RETURN 1
END
GO

--
-- Definition for stored procedure xforms_saveproc_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_saveproc_by_userdata]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   			
	if (@session != 'default')
	raiserror ('__user_mes_test1_src__',12,1)	
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_saveproc_delConfirm1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].xforms_saveproc_delConfirm1
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
   declare @name varchar(255)
   SET @name=(select @xformsdata.value('(/schema/info/name)[1]','varchar(255)'))		
   declare @table varchar(255)
   SET @table=(select @xformsdata.value('(/schema/info/table)[1]','varchar(255)'))	
   declare @name_field varchar(255)
   SET @name_field=(select @xformsdata.value('(/schema/info/name_field)[1]','varchar(255)'))      

   declare @sql varchar(MAX) = 'DELETE '+@table+' WHERE '+@name_field+' = ''' + @name + ''''
   EXEC(@sql)

   SET @error_mes=''
   RETURN 0
END
GO

--
-- Definition for stored procedure xforms_saveproc_enh : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_saveproc_enh]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
   declare @name varchar(255)
   SET @name=(select @xformsdata.value('(/schema/info/name)[1]','varchar(255)'))	
   declare @old_name varchar(255)
   SET @old_name=(select @xformsdata.value('(/schema/info/old_name)[1]','varchar(255)'))	
   declare @table varchar(255)
   SET @table=(select @xformsdata.value('(/schema/info/table)[1]','varchar(255)'))	
   declare @name_field varchar(255)
   SET @name_field=(select @xformsdata.value('(/schema/info/name_field)[1]','varchar(255)'))      

   declare @sql varchar(MAX) = '
MERGE INTO [dbo].['+@table+'] AS Target
USING (VALUES ('''+@name+''', '''+@old_name+'''))
       AS Source (NewName, OldName)
ON Target.'+@name_field+' = Source.OldName
WHEN MATCHED THEN
	UPDATE SET '+@name_field+' = Source.NewName
WHEN NOT MATCHED BY TARGET THEN
	INSERT ('+@name_field+') VALUES (NewName);';
	EXEC(@sql)
exec sc_add_to_debug_console @sql
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_saveproc1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_saveproc1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	update [dbo].[XFormsTest] set [data]=@xformsdata
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_saveproc3 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_saveproc3]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
   declare @name varchar(255)
   SET @name=(select @xformsdata.value('(/schema/info/name)[1]','varchar(255)'))	
   declare @old_name varchar(255)
   SET @old_name=(select @xformsdata.value('(/schema/info/old_name)[1]','varchar(255)'))	
	--UPDATE [dbo].[Journal_38] set [Journal_38_Name] =@name
	
MERGE INTO [dbo].[Journal_38] AS Target
USING (VALUES (@name, @old_name))
       AS Source (NewName, OldName)
ON Target.Journal_38_Name = Source.OldName
WHEN MATCHED THEN
	UPDATE SET Journal_38_Name = Source.NewName
WHEN NOT MATCHED BY TARGET THEN
	INSERT (Journal_38_Name) VALUES (NewName);
	
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_submission_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_submission_by_userdata] 
	@inputdata xml,
	@outputdata xml output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
	EXEC sc_init_debug_console

    SET @session=(select @inputdata.value('(/schema/context/session/sessioncontext/userdata)[1]','varchar(MAX)'))

	if (@session is null)
	raiserror ('__user_mes_test1_src__',12,1)	
	if (@session != 'default')
	raiserror ('__user_mes_test2_src__',12,1)	
		
	SET @outputdata=@inputdata
END
GO

--
-- Definition for stored procedure xforms_submission_ec : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_submission_ec] 
	@inputdata xml,
	@outputdata xml output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @mesid int
	SET @mesid=(select @inputdata.value('(/mesid)[1]','int'))
   	
	SET @error_mes='Ошибка в SP'   	
   	if @mesid IS NULL		
		RETURN -1
	ELSE
		RETURN @mesid
END
GO

--
-- Definition for stored procedure xforms_submission_re : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_submission_re] 
	@inputdata xml,
	@outputdata xml output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	raiserror ('просто raiserror',12,1)
END
GO

--
-- Definition for stored procedure xforms_submission_um : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_submission_um] 
	@inputdata xml,
	@outputdata xml output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	raiserror ('__user_mes_test1_src__',12,1)
END
GO

--
-- Definition for stored procedure xforms_submission1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[xforms_submission1] 
	@inputdata xml,
	@outputdata xml output,
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SET @outputdata=@inputdata
END
GO

--
-- Definition for stored procedure xforms_template_uploaders_simple : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_template_uploaders_simple]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
<?xml-stylesheet href="xsltforms/xsltforms.xsl" type="text/xsl"?>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events"
	xmlns:xsd="http://www.w3.org/2001/XMLschema" xmlns:fs="http://www.curs.ru/ns/FormServer"
	xmlns:xf="http://www.w3.org/2002/xforms">
	<head>
		<style type="text/css">
			/*
			-----Заголовок
			таблицы-----
			*/
			div.ScrollPanel {
			margin: 0px;
			padding: 0px;
			width: 949px;
			height: 270px;
			overflow-y: auto;
			overflow-x: hidden;
			}
			th.TableHeader {
			background-color:
			#5A8BC3;
			color:
			white;
			padding: 2px;
			text-align: center;
			}
			/*
			-----Заголовок
			таблицы-----
			*/
			.xforms-value {
			border: 1px
			solid
			#B3B3B3;
			padding:
			0.3em;
			margin: 0px
			1px
			0px 0px;
			font-size: 11px;
			width: 96px;
			}
			th.NameHeader {
			width:
			900px;
			}
			/*
			-----№
			п/п-----
			*/
			th.SobNumberHeader {
			width:
			26px;
			}
			th.SobNameHeader {
			width:
			310px;
			}
			th.SobResponsibleHeader {
			width:
			310px;
			}
			th.SobConsiderHeader {
			width:
			20px;
			text-align: center;
			}
			th.SobResponsibleHeader1 {
			width:
			184px;
			}
			th.SobDateHeader1 {
			width:
			86px;
			}
			th.SobPeriodHeader1 {
			}
			/*
			-----Общая
			информация-----
			*/
			.InfoTable {
			text-align: left;
			background-color:
			white;
			color: black;
			}
			.WarningInput
			.xforms-value {
			color: red;
			border: 0px;
			font-weight: bold;
			width: 800px;
			font-size: 120%;
			}
			.HintInput
			.xforms-value {
			color: green;
			border: 0px;
			font-weight: bold;
			width: 800px;
			font-size: 100%;
			}
			.RoleInput
			.xforms-value {
			width: 806px;
			}
			.StatusInput
			.xforms-value {
			width: 785px;
			}
			.PeriodInput
			.xforms-value {
			width: 120px;
			}
			.ExecutorElemInput
			.xforms-value {
			width: 816px;
			}
			.ExecutorInput
			.xforms-value {
			width:
			806px;
			}
			.ProjectInput
			.xforms-value {
			width: 806px;
			}
			.TransitionInput
			.xforms-value {
			width: 660px;
			}
			.DateInput
			.xforms-value {
			width: 60px;
			}
			.FIOInput
			.xforms-value {
			width: 806px;
			}
			.EmailInput
			.xforms-value {
			width: 200px;
			}
			.TelInput
			.xforms-value {
			width: 200px;
			}
			.RemarkInput
			.xforms-value {
			width: 806px;
			height: 450px;
			}
			.CommentInput
			.xforms-value {
			width: 806px;
			height: 100px;
			}
			.TaskInput
			.xforms-value {
			width: 675px;
			background-color: #bbbbbb;
			}
			.NaprInput
			.xforms-value {
			width: 675px;
			}
			.ResponsibleInput
			.xforms-value {
			width: 675px;
			}
			.ConsiderInput
			.xforms-value {
			width: 22px;
			}
			.CodeInput
			.xforms-value {
			width: 50px;
			}
			.NaznInput
			.xforms-value {
			width: 806px;
			text-align: right;
			border: 0px;
			}
			.NameInput
			.xforms-value {

			width:
			870px;

			}
			/* -----Первая
			вкладка----- */
			.TaskInput1
			.xforms-value {
			width: 940px;
			text-align:
			center;
			}
			.NumberInput1
			.xforms-value {
			width: 70%;
			margin-top: 1px;
			border: 0px;
			}
			.NameInput1
			.xforms-value {
			width:
			162px;
			border:
			0px;
			}
			.ResponsibleInput1
			.xforms-value {
			width:
			191px;
			border:
			0px;
			}
			.NumberInput
			.xforms-value {
			width: 17px;
			border:
			0px;
			}
			.ActionInput
			.xforms-value {
			width:
			301px;
			border:
			0px;
			}
			.ConsiderInput
			.xforms-value {
			width:
			10px;
			border:
			0px;
			}
			.Pok1Input1
			.xforms-value {
			width:
			80px;
			border:
			0px;
			}
			.DateInput1
			.xforms-value {
			width:
			64%;
			border: 0px;
			}
			.CauseInput1
			.xforms-value {
			width: 96.8%;
			border: 0px;
			}
			th.SobNumberHeader1,
			th.SobNumberHeader,
			th.SobNameHeader,th.SobAntiKrHeader,th.SobResponsibleHeader,
			th.SobPeriodHeader1, th.SobConsiderHeader {
			vertical-align:
			middle;
			font-size:
			10px;
			background-color:
			#5A8BC3;
			color:
			white;
			}
			.xforms-invalid
			.xforms-value {
			border: 1px
			solid red;
			}
			div.Hide {
			display:
			none;
			}
			div.Show {
			display:
			inline;
			}</style>	
		<xf:model id="mainModel">
			<xf:instance id="mainInstance">
				<schema xmlns="">
					<info>
						<name />
						<growth />
						<eyescolour />
						<music />
						<comment />
					</info>
				</schema>
			</xf:instance>
			
            <xf:instance id="myInstance">
                <schema xmlns="">
				  <files1>                
					<file>Файл11</file>                
					<file>Файл12</file>
				  </files1>
				  <files2>                
					<file>Файл21</file>                
					<file>Файл22</file>
					<file>Файл23</file>					
				  </files2>
                </schema>
           </xf:instance>

			<xf:bind>
			</xf:bind>
		</xf:model>
	</head>
	
	<body>
	
		<div>
			Быстрая загрузка файла - 1-й (задан submitLabel, запрет мультивыбора)					
		</div>
		<xf:upload id="05"  submit="true" submitLabel="Загрузить файлы" singleFile="true"  />
			
		<div>
			Быстрая загрузка файла - 2-й (submitLabel по умолчанию)			
		</div>
		<xf:upload id="051"  submit="true"  />
	
		<hr />
	
		<br />	
	
		<div>
			Обычная загрузка файла - 1-й (задан filenamesMapping с предварительной очисткой списка файлов)					
		</div>
		<xf:upload id="04" filenamesMapping="XPath(instance(quot(myInstance))/files1)" needClearFilenames ="true"  />
		<div>
			Список файлов:	
        <xf:select1 ref="/schema/info/name">
            <xf:item>
                <xf:label>-</xf:label>
                <xf:value>-</xf:value>
            </xf:item>
            <xf:itemset nodeset="instance(''myInstance'')/files1/file">
                <xf:label ref="."/>
                <xf:value ref="."/>
            </xf:itemset>
        </xf:select1>
        </div>
        
		<br />		
		
		<div>
			Обычная загрузка файла - 2-й (задан filenamesMapping без предварительной очистки списка файлов)			
		</div>
		<xf:upload id="041" filenamesMapping="XPath(instance(quot(myInstance))/files2)" />
		<div>
			Список файлов:	
        <xf:select1 ref="/schema/info/growth">
            <xf:item>
                <xf:label>-</xf:label>
                <xf:value>-</xf:value>
            </xf:item>
            <xf:itemset nodeset="instance(''myInstance'')/files2/file">
                <xf:label ref="."/>
                <xf:value ref="."/>
            </xf:itemset>
        </xf:select1>
        </div>
        
		<br />        
		
		<hr />		
		
		<div>
			<xf:trigger>
				<xf:label>Запустить upload (через gwtXFormSave)
				</xf:label>
				<xf:action ev:event="DOMActivate">
					<xf:load
						resource="javascript:gwtXFormSave(''xformId'', ''1'', Writer.toString(xforms.defaultModel.getInstanceDocument(''mainInstance'')))" />
				</xf:action>
			</xf:trigger>
		</div>
		

	</body>
</html>' 

set  @settings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure xforms_transform_test : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE  PROCEDURE [dbo].[xforms_transform_test]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:template match="/">
      <schema xmlns="">
      <info>
        <name>Отработка сервлета XFormsTransformationServlet</name>
        <growth/><eyescolour>Зеленый</eyescolour>
        <music>Инструментальная Эстрадная</music>
        <comment>dddddddddd</comment>
      </info></schema>		
    </xsl:template>
</xsl:stylesheet>
' 

set  @settings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure xforms_upload_by_userdata : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_upload_by_userdata]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml='',
	@filename varchar(64),
	@file varbinary(MAX)=null,	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   		
	if (@session != 'default')
	raiserror ('__user_mes_test1_src__',12,1)	
	
	INSERT INTO [XFormsFilesTest] ([filename], [filedata], [data])
	values (@filename, @file, @xformsdata)
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_upload_by_userdata_err : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_upload_by_userdata_err]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml='',
	@filename varchar(64),
	@file varbinary(MAX)=null,	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @session VARCHAR(MAX)
   SET @session=(select @session_context.value('(/sessioncontext/userdata)[1]','varchar(MAX)'))
   		
	if (@session != 'default')
	raiserror ('__user_mes_test1_src__',12,1)	
	
	INSERT INTO [XFormsFilesTest] ([filename], [filedata], [data])
	values (@filename, @file, @xformsdata)
	
	SET @error_mes='Это сообщение об ошибке в хранимой процедуре'
	RETURN 1
END
GO

--
-- Definition for stored procedure xforms_upload1 : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_upload1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml,
	@filename varchar(64),
	@file varbinary(MAX),	
	@error_mes varchar(512) output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	INSERT INTO [XFormsFilesTest] ([filename], [filedata], [data])
	values (@filename, @file, @xformsdata)
	
	SET @error_mes=''
	RETURN 0
END
GO

--
-- Definition for stored procedure xforms_variables : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xforms_variables]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),
	@xformsdata xml output,
	@xformssettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   SET @xformsdata=(SELECT TOP 1 [data] FROM [dbo].[XFormsTest]);
   
	SET @xformssettings=
	'<properties>
                  
	</properties>';
END
GO

--
-- Definition for stored procedure xformSchemaTestGood : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xformSchemaTestGood]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
  <xs:element name="documentset">
    <xs:complexType>
      <xs:sequence>
        <xs:element minOccurs="0" maxOccurs="unbounded" ref="document"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="document">
    <xs:complexType>
      <xs:sequence>
        <xs:element minOccurs="0" ref="documentset"/>
        <xs:choice>
          <xs:element ref="PriorDirs"/>
          <xs:element ref="Schema"/>
        </xs:choice>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="PriorDirs">
    <xs:complexType>
      <xs:sequence>
        <xs:element maxOccurs="unbounded" ref="PriorDir"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Schema">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="Temp"/>
        <xs:element ref="Stat"/>
        <xs:element ref="Info"/>
        <xs:choice>
          <xs:element ref="Objective"/>
          <xs:element maxOccurs="unbounded" ref="Task"/>
          <xs:element maxOccurs="unbounded" ref="Action"/>
        </xs:choice>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Temp">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="Flag"/>
        <xs:element ref="Error"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Flag" type="xs:string"/>
  <xs:element name="Error">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Stat">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="Isp"/>
        <xs:element ref="DateIsp"/>
        <xs:element ref="Uch"/>
        <xs:element ref="DateUch"/>
        <xs:element ref="AC"/>
        <xs:element ref="DateAC"/>
        <xs:element ref="Paper"/>
        <xs:element ref="DatePaper"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Isp">
    <xs:complexType/>
  </xs:element>
  <xs:element name="DateIsp">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Uch">
    <xs:complexType/>
  </xs:element>
  <xs:element name="DateUch">
    <xs:complexType/>
  </xs:element>
  <xs:element name="AC">
    <xs:complexType/>
  </xs:element>
  <xs:element name="DateAC">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Paper">
    <xs:complexType/>
  </xs:element>
  <xs:element name="DatePaper">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Info">
    <xs:complexType>
      <xs:choice maxOccurs="unbounded">
        <xs:element ref="Objective"/>
        <xs:element ref="PriorDir"/>
        <xs:element ref="Task"/>
        <xs:element ref="ActionSuperviser"/>
        <xs:element ref="ChangeStr"/>
        <xs:element ref="Date"/>
        <xs:element ref="Executor"/>
        <xs:element ref="FIO"/>
        <xs:element ref="FIOisp"/>
        <xs:element ref="FIOuch"/>
        <xs:element ref="History"/>
        <xs:element ref="Nazn"/>
        <xs:element ref="Num_table"/>
        <xs:element ref="Period"/>
        <xs:element ref="ProjectName"/>
        <xs:element ref="ProjectNumber"/>
        <xs:element ref="Responsible"/>
        <xs:element ref="UserName"/>
        <xs:element ref="Userisp"/>
        <xs:element ref="Usertype"/>
        <xs:element ref="datecheck"/>
        <xs:element ref="datefill"/>
        <xs:element ref="inyearperiod"/>
        <xs:element ref="remark"/>
        <xs:element ref="repcampaign"/>
        <xs:element ref="roleacmanager"/>
        <xs:element ref="roleoimanager"/>
        <xs:element ref="status"/>
        <xs:element ref="transition"/>
        <xs:element ref="year"/>
      </xs:choice>
    </xs:complexType>
  </xs:element>
  <xs:element name="ActionSuperviser" type="xs:string"/>
  <xs:element name="ChangeStr" type="xs:boolean"/>
  <xs:element name="Date">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Executor">
    <xs:complexType/>
  </xs:element>
  <xs:element name="FIO">
    <xs:complexType/>
  </xs:element>
  <xs:element name="FIOisp">
    <xs:complexType/>
  </xs:element>
  <xs:element name="FIOuch">
    <xs:complexType/>
  </xs:element>
  <xs:element name="History">
    <xs:complexType>
      <xs:sequence>
        <xs:element ref="Rec"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Rec">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Nazn" type="xs:string"/>
  <xs:element name="Num_table" type="xs:decimal"/>
  <xs:element name="Period" type="xs:string"/>
  <xs:element name="ProjectName" type="xs:string"/>
  <xs:element name="ProjectNumber" type="xs:integer"/>
  <xs:element name="Responsible" type="xs:string"/>
  <xs:element name="UserName" type="xs:string"/>
  <xs:element name="Userisp">
    <xs:complexType/>
  </xs:element>
  <xs:element name="Usertype" type="xs:string"/>
  <xs:element name="datecheck" type="xs:string"/>
  <xs:element name="datefill" type="xs:string"/>
  <xs:element name="inyearperiod" type="xs:NCName"/>
  <xs:element name="remark">
    <xs:complexType/>
  </xs:element>
  <xs:element name="repcampaign" type="xs:string"/>
  <xs:element name="roleacmanager" type="xs:string"/>
  <xs:element name="roleoimanager" type="xs:string"/>
  <xs:element name="status" type="xs:string"/>
  <xs:element name="transition" type="xs:string"/>
  <xs:element name="year" type="xs:integer"/>
  <xs:element name="PriorDir" type="xs:string"/>
  <xs:element name="Task">
    <xs:complexType mixed="true">
      <xs:choice minOccurs="0" maxOccurs="unbounded">
        <xs:element ref="Action"/>
        <xs:element ref="Indicator"/>
      </xs:choice>
      <xs:attribute name="Code" type="xs:integer"/>
      <xs:attribute name="Consider"/>
      <xs:attribute name="GUID"/>
      <xs:attribute name="Name"/>
      <xs:attribute name="Napr" type="xs:NCName"/>
      <xs:attribute name="resp"/>
    </xs:complexType>
  </xs:element>
  <xs:element name="Objective">
    <xs:complexType mixed="true">
      <xs:sequence>
        <xs:element minOccurs="0" maxOccurs="unbounded" ref="Indicator"/>
      </xs:sequence>
    </xs:complexType>
  </xs:element>
  <xs:element name="Action">
    <xs:complexType>
      <xs:sequence>
        <xs:element minOccurs="0" maxOccurs="unbounded" ref="Keypoint"/>
        <xs:element minOccurs="0" maxOccurs="unbounded" ref="Fin"/>
      </xs:sequence>
      <xs:attribute name="AntiKr"/>
      <xs:attribute name="Cause1"/>
      <xs:attribute name="Cause2"/>
      <xs:attribute name="Code" use="required"/>
      <xs:attribute name="Consider" use="required" type="xs:boolean"/>
      <xs:attribute name="GUID" type="xs:integer"/>
      <xs:attribute name="Name" use="required"/>
      <xs:attribute name="Number" use="required" type="xs:integer"/>
      <xs:attribute name="Pok1" use="required"/>
      <xs:attribute name="Pok10" use="required"/>
      <xs:attribute name="Pok11" use="required"/>
      <xs:attribute name="Pok12" use="required"/>
      <xs:attribute name="Pok13" use="required"/>
      <xs:attribute name="Pok14" use="required"/>
      <xs:attribute name="Pok15" use="required"/>
      <xs:attribute name="Pok2" use="required"/>
      <xs:attribute name="Pok3" use="required"/>
      <xs:attribute name="Pok4" use="required"/>
      <xs:attribute name="Pok5" use="required"/>
      <xs:attribute name="Pok6" use="required"/>
      <xs:attribute name="Pok7" use="required"/>
      <xs:attribute name="Pok8" use="required"/>
      <xs:attribute name="Pok9" use="required"/>
      <xs:attribute name="resp" use="required"/>
    </xs:complexType>
  </xs:element>
  <xs:element name="Keypoint">
    <xs:complexType>
      <xs:attribute name="Cause1"/>
      <xs:attribute name="Cause2"/>
      <xs:attribute name="Code" use="required"/>
      <xs:attribute name="Consider"/>
      <xs:attribute name="Name" use="required"/>
      <xs:attribute name="Number" use="required" type="xs:integer"/>
      <xs:attribute name="Pok1" use="required"/>
      <xs:attribute name="Pok2" use="required"/>
      <xs:attribute name="Pok3" use="required"/>
      <xs:attribute name="Pok4" use="required"/>
      <xs:attribute name="Pok5" use="required"/>
      <xs:attribute name="Pok6" use="required"/>
      <xs:attribute name="Pok7" use="required"/>
    </xs:complexType>
  </xs:element>
  <xs:element name="Fin">
    <xs:complexType>
      <xs:attribute name="KBK" type="xs:integer"/>
      <xs:attribute name="KBK_GRBS" type="xs:integer"/>
      <xs:attribute name="KBK_podrazdel"/>
      <xs:attribute name="KBK_rashod"/>
      <xs:attribute name="KBK_razdel"/>
      <xs:attribute name="KBK_statya"/>
      <xs:attribute name="Number" use="required"/>
      <xs:attribute name="Pok1" use="required"/>
      <xs:attribute name="Pok2" use="required"/>
      <xs:attribute name="Pok3" use="required"/>
      <xs:attribute name="Pok4" use="required"/>
      <xs:attribute name="Pok5"/>
      <xs:attribute name="Pok6"/>
      <xs:attribute name="Pok7"/>
      <xs:attribute name="Rem"/>
      <xs:attribute name="Rem1"/>
      <xs:attribute name="Rem2"/>
    </xs:complexType>
  </xs:element>
  <xs:element name="Indicator">
    <xs:complexType>
      <xs:attribute name="Code" use="required"/>
      <xs:attribute name="Consider" type="xs:boolean"/>
      <xs:attribute name="GUID" type="xs:decimal"/>
      <xs:attribute name="Name" use="required"/>
      <xs:attribute name="Number" use="required" type="xs:integer"/>
      <xs:attribute name="Pok8" use="required"/>
      <xs:attribute name="Pok9" use="required"/>
      <xs:attribute name="Rem" use="required"/>
    </xs:complexType>
  </xs:element>
</xs:schema>
' 

set  @settings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure xformSchemaTestGoodSmall : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xformSchemaTestGoodSmall]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
	elementFormDefault="qualified">
	<xs:element name="documentset">
		<xs:complexType>
			<xs:sequence>
				<xs:element minOccurs="0" maxOccurs="unbounded" ref="document" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="document">
		<xs:complexType>
			<xs:sequence>
				<xs:element minOccurs="0" ref="documentset"/>											
				<xs:element minOccurs="0"  ref="Schema" />					
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Schema">
		<xs:complexType>
			<xs:sequence>
				<xs:element ref="Temp" />
				<xs:element ref="Stat" />
				<xs:element ref="Info" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Temp">
		<xs:complexType>
			<xs:sequence>
				<xs:element ref="Flag" />
				<xs:element ref="Error" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Flag" type="xs:string" />
	<xs:element name="Error">
		<xs:complexType />
	</xs:element>
	<xs:element name="Stat">
		<xs:complexType>
			<xs:sequence>
				<xs:element ref="Isp" />
				<xs:element ref="DateIsp" />
				<xs:element ref="Uch" />
				<xs:element ref="DateUch" />
				<xs:element ref="AC" />
				<xs:element ref="DateAC" />
				<xs:element ref="Paper" />
				<xs:element ref="DatePaper" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Isp">
		<xs:complexType />
	</xs:element>
	<xs:element name="DateIsp">
		<xs:complexType />
	</xs:element>
	<xs:element name="Uch">
		<xs:complexType />
	</xs:element>
	<xs:element name="DateUch">
		<xs:complexType />
	</xs:element>
	<xs:element name="AC">
		<xs:complexType />
	</xs:element>
	<xs:element name="DateAC">
		<xs:complexType />
	</xs:element>
	<xs:element name="Paper">
		<xs:complexType />
	</xs:element>
	<xs:element name="DatePaper">
		<xs:complexType />
	</xs:element>
	<xs:element name="Info">
		<xs:complexType>
			<xs:choice maxOccurs="unbounded">
				<xs:element ref="Objective" />
				<xs:element ref="PriorDir" />
				<xs:element ref="Task" />
				<xs:element ref="ActionSuperviser" />
				<xs:element ref="ChangeStr" />
				<xs:element ref="Date" />
				<xs:element ref="Executor" />
				<xs:element ref="FIO" />
				<xs:element ref="FIOisp" />
				<xs:element ref="FIOuch" />
				<xs:element ref="History" />
				<xs:element ref="Nazn" />
				<xs:element ref="Num_table" />
				<xs:element ref="Period" />
				<xs:element ref="ProjectName" />
				<xs:element ref="ProjectNumber" />
				<xs:element ref="Responsible" />
				<xs:element ref="UserName" />
				<xs:element ref="Userisp" />
				<xs:element ref="Usertype" />
				<xs:element ref="datecheck" />
				<xs:element ref="datefill" />
				<xs:element ref="inyearperiod" />
				<xs:element ref="remark" />
				<xs:element ref="repcampaign" />
				<xs:element ref="roleacmanager" />
				<xs:element ref="roleoimanager" />
				<xs:element ref="status" />
				<xs:element ref="transition" />
				<xs:element ref="year" />
			</xs:choice>
		</xs:complexType>
	</xs:element>
	<xs:element name="ActionSuperviser" type="xs:string" />
	<xs:element name="ChangeStr" type="xs:boolean" />
	<xs:element name="Date">
		<xs:complexType />
	</xs:element>
	<xs:element name="Executor">
		<xs:complexType />
	</xs:element>
	<xs:element name="FIO">
		<xs:complexType />
	</xs:element>
	<xs:element name="FIOisp">
		<xs:complexType />
	</xs:element>
	<xs:element name="FIOuch">
		<xs:complexType />
	</xs:element>
	<xs:element name="History">
		<xs:complexType>
			<xs:sequence>
				<xs:element ref="Rec" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Rec">
		<xs:complexType />
	</xs:element>
	<xs:element name="Nazn" type="xs:string" />
	<xs:element name="Num_table" type="xs:decimal" />
	<xs:element name="Period" type="xs:string" />
	<xs:element name="ProjectName" type="xs:string" />
	<xs:element name="ProjectNumber" type="xs:integer" />
	<xs:element name="Responsible" type="xs:string" />
	<xs:element name="UserName" type="xs:string" />
	<xs:element name="Userisp">
		<xs:complexType />
	</xs:element>
	<xs:element name="Usertype" type="xs:string" />
	<xs:element name="datecheck" type="xs:string" />
	<xs:element name="datefill" type="xs:string" />
	<xs:element name="inyearperiod" type="xs:NCName" />
	<xs:element name="remark">
		<xs:complexType />
	</xs:element>
	<xs:element name="repcampaign" type="xs:string" />
	<xs:element name="roleacmanager" type="xs:string" />
	<xs:element name="roleoimanager" type="xs:string" />
	<xs:element name="status" type="xs:string" />
	<xs:element name="transition" type="xs:string" />
	<xs:element name="year" type="xs:integer" />
	<xs:element name="PriorDir" type="xs:string" />

	<xs:element name="Task">
		<xs:complexType mixed="true">
			<xs:choice minOccurs="0" maxOccurs="unbounded">
				<xs:element ref="Action" />
				<xs:element ref="Indicator" />
			</xs:choice>
			<xs:attribute name="Code" type="xs:integer" />
			<xs:attribute name="Consider" />
			<xs:attribute name="GUID" />
			<xs:attribute name="Name" />
			<xs:attribute name="Napr" type="xs:NCName" />
			<xs:attribute name="resp" />
		</xs:complexType>
	</xs:element>
	<xs:element name="Objective">
		<xs:complexType mixed="true">
			<xs:sequence>
				<xs:element minOccurs="0" maxOccurs="unbounded" ref="Indicator" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	<xs:element name="Action">
		<xs:complexType>
			<xs:sequence>
				<xs:element minOccurs="0" maxOccurs="unbounded" ref="Keypoint" />
				<xs:element minOccurs="0" maxOccurs="unbounded" ref="Fin" />
			</xs:sequence>
			<xs:attribute name="AntiKr" />
			<xs:attribute name="Cause1" />
			<xs:attribute name="Cause2" />
			<xs:attribute name="Code" use="required" />
			<xs:attribute name="Consider" use="required" type="xs:boolean" />
			<xs:attribute name="GUID" type="xs:integer" />
			<xs:attribute name="Name" use="required" />
			<xs:attribute name="Number" use="required" type="xs:integer" />
			<xs:attribute name="Pok1" use="required" />
			<xs:attribute name="Pok10" use="required" />
			<xs:attribute name="Pok11" use="required" />
			<xs:attribute name="Pok12" use="required" />
			<xs:attribute name="Pok13" use="required" />
			<xs:attribute name="Pok14" use="required" />
			<xs:attribute name="Pok15" use="required" />
			<xs:attribute name="Pok2" use="required" />
			<xs:attribute name="Pok3" use="required" />
			<xs:attribute name="Pok4" use="required" />
			<xs:attribute name="Pok5" use="required" />
			<xs:attribute name="Pok6" use="required" />
			<xs:attribute name="Pok7" use="required" />
			<xs:attribute name="Pok8" use="required" />
			<xs:attribute name="Pok9" use="required" />
			<xs:attribute name="resp" use="required" />
		</xs:complexType>
	</xs:element>
	<xs:element name="Keypoint">
		<xs:complexType>
			<xs:attribute name="Cause1" />
			<xs:attribute name="Cause2" />
			<xs:attribute name="Code" use="required" />
			<xs:attribute name="Consider" />
			<xs:attribute name="Name" use="required" />
			<xs:attribute name="Number" use="required" type="xs:integer" />
			<xs:attribute name="Pok1" use="required" />
			<xs:attribute name="Pok2" use="required" />
			<xs:attribute name="Pok3" use="required" />
			<xs:attribute name="Pok4" use="required" />
			<xs:attribute name="Pok5" use="required" />
			<xs:attribute name="Pok6" use="required" />
			<xs:attribute name="Pok7" use="required" />
		</xs:complexType>
	</xs:element>
	<xs:element name="Fin">
		<xs:complexType>
			<xs:attribute name="KBK" type="xs:integer" />
			<xs:attribute name="KBK_GRBS" type="xs:integer" />
			<xs:attribute name="KBK_podrazdel" />
			<xs:attribute name="KBK_rashod" />
			<xs:attribute name="KBK_razdel" />
			<xs:attribute name="KBK_statya" />
			<xs:attribute name="Number" use="required" />
			<xs:attribute name="Pok1" use="required" />
			<xs:attribute name="Pok2" use="required" />
			<xs:attribute name="Pok3" use="required" />
			<xs:attribute name="Pok4" use="required" />
			<xs:attribute name="Pok5" />
			<xs:attribute name="Pok6" />
			<xs:attribute name="Pok7" />
			<xs:attribute name="Rem" />
			<xs:attribute name="Rem1" />
			<xs:attribute name="Rem2" />
		</xs:complexType>
	</xs:element>
	<xs:element name="Indicator">
		<xs:complexType>
			<xs:attribute name="Code" use="required" />
			<xs:attribute name="Consider" type="xs:boolean" />
			<xs:attribute name="GUID" type="xs:decimal" />
			<xs:attribute name="Name" use="required" />
			<xs:attribute name="Number" use="required" type="xs:integer" />
			<xs:attribute name="Pok8" use="required" />
			<xs:attribute name="Pok9" use="required" />
			<xs:attribute name="Rem" use="required" />
		</xs:complexType>
	</xs:element>
</xs:schema>
' 

set  @settings=CAST(@settings_str as xml)
END
GO

--
-- Definition for stored procedure xformTransformTestGood : 
--
GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[xformTransformTestGood]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
	@settings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
DECLARE @settings_str varchar(max)
set @settings_str='
            <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
                
                <xsl:template match="/">
                    <html>
                        <head>
                            <meta lang="RU"/>
                        </head>
                        <body>
                            <table cellpadding="3" cellspacing="1" border="0"
                                style="width: 950px; height: 20px;">
                                <tr>
                                    <td rowspan="1" style="background-color: #FFFFFF; text-align: right;"
                                        >Таблица 2</td>
                                </tr>
                                <tr>
                                    <td rowspan="2" style="background-color: #FFFFFF; text-align: center;"
                                        >Сведения о достижении значений целевых индикаторов проекта за текущий
                                        период</td>
                                </tr>
                            </table>
                            <br/>
                            <table cellpadding="3" cellspacing="1" border="0"
                                style="width: 950px; height: 20px;">
                                <tr>
                                    <td rowspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">№
                                        п/п</td>
                                    <td rowspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >Наименование индикатора, ед. измерения</td>
                                    <td colspan="2"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >Значения индикаторов на текущий год</td>
                                </tr>
                                <tr>
                                    <td colspan="1"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >план на текущий год</td>
                                    <td colspan="1"
                                        style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;"
                                        >ожидаемое значение <br/> на конец текущего года</td>
                                </tr>
                                <tr>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center; ">
                                        1 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        2 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        3 </td>
                                    <td style="background-color: #5A8BC3; color: #FFFFFF; text-align: center;">
                                        4 </td>
                                </tr>
                                <tr>
                                    <td
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                        >&#160;</td>
                                    <td colspan="3"
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE"
                                        >I. Индикаторы проекта</td>
                                </tr>
                                <xsl:for-each
                                    select="documentset/document/documentset/document/Schema[Info/Num_table=''2.1'']">
                                    <tr>
                                        <td
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                            >&#160;</td>
                                        <td colspan="3"
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                            <xsl:value-of select="concat(Info/Objective/text(),''&#160;'')"/>
                                            [<a href="{concat(''?showform='', ../@docid )}" target="_blank" style="color: #2C5BA1;">редактировать</a>]
                                        </td>
                                    </tr>
                                    <xsl:for-each select="Objective/Indicator">
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE">
                                                <xsl:value-of
                                                    select="concat(''1'',''.'',count(../../../preceding-sibling::document/Schema/Objective/Indicator)+count(../../preceding-sibling::Schema/Objective/Indicator)+position())"/>
                                                
                                            </td>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Name=''''">
                                                        <xsl:value-of select="''&#160;''"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Name"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Pok8=''''">
                                                        <xsl:value-of select="''&#160;''"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Pok8"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Pok9=''''">
                                                        <xsl:value-of select="''&#160;''"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Pok9"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                                >&#160;</td>
                                            <td colspan="3"
                                                style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:choose>
                                                    <xsl:when test="./@Rem=''''">
                                                        <xsl:value-of select="''&#160;''"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:value-of select="./@Rem"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </xsl:for-each>
                                <tr>
                                    <td
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                        >&#160;</td>
                                    <td colspan="3"
                                        style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE"
                                        >II. Индикаторы задач проекта</td>
                                </tr>
                                <xsl:for-each
                                    select="documentset/document/PriorDirs/PriorDir[not(preceding-sibling::PriorDir/text()=text())]">
                                    <tr>
                                        <td
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                            >&#160;</td>
                                        <td colspan="3"
                                            style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                            <xsl:value-of select="text()"/>
                                        </td>
                                    </tr>
                                    <xsl:variable name="Direction" select="text()"/>
                                    <xsl:for-each
                                        select="/documentset/document/documentset/document/Schema[Info/PriorDir=$Direction]">
                                        <tr>
                                            <td
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-right 1px solid #CCDDEE"
                                                >&#160;</td>
                                            <td colspan="3"
                                                style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                <xsl:value-of select="Info/Task"/>
                                                [<a href="{concat(''?showForm='', ../@docid )}" target="_blank" style="color: #2C5BA1;">редактировать</a>]
                                            </td>
                                        </tr>
                                        <xsl:for-each select="Task/Indicator">
                                            <tr>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE">
                                                    <xsl:value-of
                                                        select="concat(''2'',''.'',count(../../../preceding-sibling::document/Schema/Task/Indicator)+count(../../preceding-sibling::Schema/Task/Indicator)+position())"/>
                                                    
                                                </td>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Name=''''">
                                                            <xsl:value-of select="''&#160;''"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Name"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Pok8=''''">
                                                            <xsl:value-of select="''&#160;''"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Pok8"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Pok9=''''">
                                                            <xsl:value-of select="''&#160;''"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Pok9"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td
                                                    style="text-align: center; background-color: white; color: black; border-bottom: 1px solid #CCDDEE"
                                                    >&#160;</td>
                                                <td colspan="3"
                                                    style="text-align: left; background-color: white; color: black; border-bottom: 1px solid #CCDDEE; border-left: 1px solid #CCDDEE">
                                                    <xsl:choose>
                                                        <xsl:when test="./@Rem=''''">
                                                            <xsl:value-of select="''&#160;''"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:value-of select="./@Rem"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                            </tr>
                                            
                                        </xsl:for-each>
                                    </xsl:for-each>
                                </xsl:for-each>
                            </table>
                        </body>
                    </html>
                </xsl:template>
            </xsl:stylesheet>
' 

set  @settings=CAST(@settings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_portals]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="15" rowHeight = "50"
totalCount="0" profile="grid.nowidth.properties"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS [Название], [Logo] AS [Логотип], [Url] as [URL], cast( '<properties>                                    
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_portals_id_and_css]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" /> 
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="150px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="20" rowHeight = "50" profile="grid.nowidth.properties" autoSelectRecordId="3" 
 autoSelectRelativeRecord="false" autoSelectColumnId="URL"/>
</gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
SELECT [Name] AS "Название", [Logo] AS "Логотип", [Url] as "URL", Id AS "~~id",  cast( '<properties>
			<styleClass name="extlivegrid-record-bold"/>
			<styleClass name="extlivegrid-record-italic"/>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="d1">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                                <element id="d2">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element>                                
                            </datapanel>
                        </action>
                    </event>  			
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
END
GO

--
-- Definition for stored procedure chart_pas_xmlds : 
--

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_pas_xmlds]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
Set @main_context='Белгородская обл.'	

Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context=@main_context+', Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Статья" width="500px" height="300px" flip="false" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.Chris", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		
		
		
		
<records>
  <rec>
  <Статья>Запасы на конец отчетного периода - Всего</Статья>
  <_x0033_кв._x0020_2005г.>1691.20</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>1260.80</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>798.00</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>243.58</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>1302.40</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>959.80</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>658.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>246.70</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>1441.30</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>1106.60</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>590.00</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>326.50</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>2578.40</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>2128.50</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>1405.10</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>751.70</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>2032.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>1593.10</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>1023.30</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>559.60</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>1302.40</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>959.80</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>590.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>243.58</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#0000FF"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Личное потребление (фонд потребления) без продуктов переработки - Всего</Статья>
  <_x0033_кв._x0020_2005г.>104.80</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>78.20</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>63.31</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>107.70</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>91.40</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>78.70</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>78.20</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>105.00</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>99.20</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>102.80</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>83.67</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>116.00</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>99.80</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>105.90</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>80.16</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>0.20</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>0.10</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>0.20</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.10</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>0.30</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>0.13</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>0.13</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.13</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>0.13</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#8A2BE2"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Межрегиональный обмен - Всего</Статья>
  <_x0033_кв._x0020_2005г.>82.10</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>146.20</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>-23.71</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>-2.30</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>222.12</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>190.20</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>-8.08</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>-8.84</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>74.00</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>91.70</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>70.52</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>257.40</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>76.90</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>210.30</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>70.49</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>164.90</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>53.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>132.30</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>29.73</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>87.20</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>605.83</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>396.10</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>291.69</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>388.55</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#A52A2A"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Переработано на муку, крупу, комбикорма и др.цели - Переработано в сельхозорганизациях и у населения</Статья>
  <_x0033_кв._x0020_2005г.>271.90</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>598.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>137.89</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>102.71</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>295.37</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>500.90</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>49.40</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>65.63</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>227.37</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>362.30</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>199.08</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>138.90</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>328.10</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>535.80</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>371.63</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>402.80</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>519.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>435.00</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>239.75</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>119.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>458.28</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>518.63</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>371.63</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>397.40</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#5F9EA0"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Переработано на муку, крупу, комбикорма и др.цели - Переработано промышленными организациями</Статья>
  <_x0033_кв._x0020_2005г.>144.40</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>135.10</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>180.30</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>190.36</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>160.43</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>176.30</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>159.00</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>200.89</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>201.93</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>215.20</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>323.78</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>231.70</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>266.50</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>376.70</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>342.93</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>258.50</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>286.70</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>289.90</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>305.78</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>267.40</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>278.54</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>271.09</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>222.26</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>200.89</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#7FFF00"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Потери - Всего</Статья>
  <_x0033_кв._x0020_2005г.>16.20</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>26.80</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>2.54</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>0.93</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>15.59</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>17.50</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>2.40</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>0.30</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>15.80</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>17.50</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>2.82</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>1.20</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>12.10</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>20.70</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>5.55</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>4.70</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>11.60</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>21.70</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>3.32</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>4.10</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>13.50</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>18.33</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>5.55</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>4.00</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#D2691E"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Производственное потребление в сельхозорганизациях и у населения - На корм скоту</Статья>
  <_x0033_кв._x0020_2005г.>50.40</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>24.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>42.30</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>42.91</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>55.59</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>41.80</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>35.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>44.40</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>59.90</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>49.90</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>40.00</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>40.80</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>60.20</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>53.60</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>52.50</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>31.00</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>42.30</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>49.80</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>49.60</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>34.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>42.33</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>49.84</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>61.92</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>42.44</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#6495ED"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Производственное потребление в сельхозорганизациях и у населения - На семена</Статья>
  <_x0033_кв._x0020_2005г.>71.90</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>3.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>0.00</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>140.25</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>78.35</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>0.00</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>37.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>82.90</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>72.00</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>8.00</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>21.20</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>108.10</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>81.20</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>12.90</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>20.90</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>121.10</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>66.60</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>5.70</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.00</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>126.10</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>66.70</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>5.71</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>117.55</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#556B2F"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
<rec>
  <Статья>Производство (валовый сбор в весе после доработки) - Всего</Статья>
  <_x0033_кв._x0020_2005г.>1818.50</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>212.40</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>110.70</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>150.70</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>1442.20</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>203.70</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>114.30</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>148.80</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>1697.60</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>226.60</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>131.10</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>154.90</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>2923.30</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>339.60</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>160.80</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>0.00</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>2153.80</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>230.90</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.00</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>0.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>996.45</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>125.03</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>27.45</_x0032_кв._x0020_2011г.>
  <properties>
						<color value="#00FFFF"/>
						<event name="series_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="4">
									<add_context>''+[Балансы]+''</add_context>                                                                                             
                                </element> 
                            </datapanel>
                        </action>
                    </event>
  </properties>
</rec>
</records>		
		
		
		
		
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[grid_portals_id_and_css_xmlds]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
	SET NOCOUNT ON;
	
	
--        DECLARE @err VARCHAR(MAX)
--        set @err = CAST(@session_context as varchar(5120))
--		  raiserror (@err,12,1)	
	

/*
SELECT [Name] AS "Название", 'imagesingrid/test.jpg' AS [Картинка], [File1] AS "Файл1", [File2] AS "Файл2", [Logo] AS "Логотип", [Url] as "URL", Id AS "~~id",  cast( '<properties>
			<styleClass name="grid-record-bold"/>
			<styleClass name="grid-record-italic"/>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="d1">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element> 
                                <element id="d2">
									<add_context>''+[Name]+''</add_context>                                                                                             
                                </element>                                
                            </datapanel>
                        </action>
                    </event>  			
            </properties>' as xml)  as [~~properties] FROM [dbo].[Websites]
WHERE [IsPortal]=1
*/


Declare @gridsettings_str as varchar(max)
set @gridsettings_str=
'<gridsettings>
<labels>
<header>
<h3>Порталы</h3>
</header>
</labels>
        <columns>
        <col id="Название" width="100px" /> 
        <col id="Картинка" width="20px" type="IMAGE"/>        
        <col id="Файл1"  width="130px" type="DOWNLOAD" linkId="11"/>                 
        <col id="Файл2"  width="100px" type="DOWNLOAD" linkId="12"/>                         
        <col id="Логотип" width="250px" type="LINK"/>
        <col id="URL" width="100px" type="LINK"/>
        </columns>
<properties flip="false" pagesize="2" profile="grid.nowidth.properties" autoSelectRecordId="3" 
 autoSelectRelativeRecord="false" autoSelectColumnId="URL"/>
 
 
<records> 
<rec>
  <Название>Советский<br>Яндекс</br>ведущий<br>поисковик</br>Рунета</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1><div style="text-align:center">Файл c навигатором в имени которого содержится GUID записи</div></Файл1>
  <Файл2></Файл2>
  <Логотип><link href="http://yandex.ru" image="${images.in.grid.dir}/imagesingrid/yandex.png" text="Яндекс" openInNewTab="true"/></Логотип>
  <URL> <link href="http://yandex.ru/yandsearch?text=КУРС-ИТ" text="Яндекс" openInNewTab="true"/></URL>
  <_x007e__x007e_id>7451DF70-ACC3-48CC-8CC0-3092F8A237BE</_x007e__x007e_id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context><br>Яндекс</br> ведущий <br/>поисковик <br/>Рунета</add_context>
            </element>
            <element id="d2">
              <add_context><br>Яндекс</br> ведущий <br/>поисковик <br/>Рунета</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>РБК</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл12</Файл1>
  <Файл2>Файл22</Файл2>
  <Логотип><link href="http://rbc.ru" image="${images.in.grid.dir}/imagesingrid/rbc.gif" text="rbc.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://rbc.ru"  openInNewTab="true"/></URL>
  <_x007e__x007e_id>8BC3D54A-AE03-4728-AFCD-54DC092B0823</_x007e__x007e_id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>РБК</add_context>
            </element>
            <element id="d2">
              <add_context>РБК</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>Рамблер</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл13</Файл1>
  <Файл2>Файл23</Файл2>
  <Логотип><link href="http://rambler.ru" image="${images.in.grid.dir}/imagesingrid/rambler.gif" text="rambler.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://rambler.ru" text="rambler.ru" openInNewTab="true"/></URL>
  <_x007e__x007e_id>77F60A7C-42EB-4E32-B23D-F179E58FB138</_x007e__x007e_id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Рамблер</add_context>
            </element>
            <element id="d2">
              <add_context>Рамблер</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
<rec>
  <Название>Mail.ru</Название>
  <Картинка>imagesingrid/test.jpg</Картинка>
  <Файл1>Файл14</Файл1>
  <Файл2>Файл24</Файл2>
  <Логотип><link href="http://mail.ru" image="${images.in.grid.dir}/imagesingrid/mailru.gif" text="mail.ru" openInNewTab="true"/></Логотип>
  <URL><link href="http://mail.ru" text="mail.ru" openInNewTab="true"/></URL>
  <_x007e__x007e_id>856ACCF2-53AB-4AF0-A956-F6E85601D0B4</_x007e__x007e_id>
    <properties>
      <styleClass name="grid-record-bold" />
      <styleClass name="grid-record-italic" />
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Mail.ru</add_context>
            </element>
            <element id="d2">
              <add_context>Mail.ru</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
</rec>
</records>		 
 
</gridsettings>' 

set    @gridsettings=CAST(@gridsettings_str as xml)

END

GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[grid_col_types_xmlds]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
	@gridsettings xml output,
    @error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;

/*    
    DECLARE @sql varchar(max)
    DECLARE @orderby varchar(max)
    if (@sortcols = '')
    SET @orderby = 'ORDER BY [_Id]';
    else
    SET @orderby = @sortcols;
    
	EXEC grid_col_types_getquery @sql = @sql OUTPUT    
    
    SET @sql =  '
         WITH result AS 	('+@sql+',                      
            ROW_NUMBER() 
            OVER ('+@orderby+') AS rnum 
         FROM [dbo].[Journal_41])
      SELECT
         * FROM result 
          ORDER by rnum';
   EXEC(@sql)	
*/   
   
       
DECLARE @gridsettings_str varchar(max)
DECLARE @rec_count int
SELECT @rec_count = COUNT(*) FROM [dbo].[Journal_41]
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">Грид с различными типами столбцов</h3></header>
        </labels>
        <columns>
        <col id="_Id" type="INT"/>        
        <col id="UpdateRowTime" type="DATETIME"/>        
        <col id="UpdateRowDate" type="DATE"/>                        
        <col id="Сайт" type="LINK"/>
        <col id="rnum" type="INT"/>              
          
        </columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="d1"> 
                                <add_context>add</add_context>  
                                </element>
                                <element id="d2">
                                <add_context>add</add_context>  
                                </element>                                                              
                                <element id="d3">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d4">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d5">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d6">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d7">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d8">
                                <add_context>add</add_context>  
                                </element>                                 
                                <element id="d9">
                                <add_context>add</add_context>  
                                </element>                                                                
                                <element id="d10"> 
                                <add_context>add</add_context>  
                                </element>                                                                                                   
                            </datapanel>
                        </action>
<properties pagesize="20" profile="sngl_before_dbl.properties" totalCount="'+
CAST(@rec_count as varchar(max))+'"/>

<records>
<rec>
  <_Id>1</_Id>
  <Journal_41_Name>Зерно</Journal_41_Name>
  <UpdateRowTime>2009-11-13T12:50:11.250</UpdateRowTime>
  <UpdateRowDate>2009-11-13</UpdateRowDate>
  <Сайт><link href="http://Зерно.рф" openInNewTab="true"/></Сайт>
      <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Зерно</add_context>
            </element>
            <element id="d2">
              <add_context>Зерно</add_context>
            </element>
            <element id="d3">
              <add_context>Зерно</add_context>
            </element>
            <element id="d4">
              <add_context>Зерно</add_context>
            </element>
            <element id="d5">
              <add_context>Зерно</add_context>
            </element>
            <element id="d6">
              <add_context>Зерно</add_context>
            </element>
            <element id="d7">
              <add_context>Зерно</add_context>
            </element>
            <element id="d8">
              <add_context>Зерно</add_context>
            </element>
            <element id="d9">
              <add_context>Зерно</add_context>
            </element>
            <element id="d10">
              <add_context>Зерно</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>1</rnum>
</rec>
<rec>
  <_Id>2</_Id>
  <Journal_41_Name>Пшеница</Journal_41_Name>
  <UpdateRowTime>2009-11-23T14:07:09.063</UpdateRowTime>
  <UpdateRowDate>2009-11-23</UpdateRowDate>
  <Сайт><link href="http://Пшеница.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d2">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d3">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d4">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d5">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d6">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d7">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d8">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d9">
              <add_context>Пшеница</add_context>
            </element>
            <element id="d10">
              <add_context>Пшеница</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>2</rnum>
</rec>
<rec>
  <_Id>3</_Id>
  <Journal_41_Name>Мясо</Journal_41_Name>
  <UpdateRowTime>2010-04-26T12:42:46.850</UpdateRowTime>
  <UpdateRowDate>2010-04-26</UpdateRowDate>
  <Сайт><link href="http://Мясо.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Мясо</add_context>
            </element>
            <element id="d2">
              <add_context>Мясо</add_context>
            </element>
            <element id="d3">
              <add_context>Мясо</add_context>
            </element>
            <element id="d4">
              <add_context>Мясо</add_context>
            </element>
            <element id="d5">
              <add_context>Мясо</add_context>
            </element>
            <element id="d6">
              <add_context>Мясо</add_context>
            </element>
            <element id="d7">
              <add_context>Мясо</add_context>
            </element>
            <element id="d8">
              <add_context>Мясо</add_context>
            </element>
            <element id="d9">
              <add_context>Мясо</add_context>
            </element>
            <element id="d10">
              <add_context>Мясо</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>3</rnum>
</rec>
<rec>
  <_Id>4</_Id>
  <Journal_41_Name>Молоко</Journal_41_Name>
  <UpdateRowTime>2010-04-26T12:42:59.413</UpdateRowTime>
  <UpdateRowDate>2010-04-26</UpdateRowDate>
  <Сайт><link href="http://Молоко.рф" openInNewTab="true"/></Сайт>
    <properties>
      <event name="row_single_click">
        <action>
          <main_context>current</main_context>
          <datapanel type="current" tab="current">
            <element id="d1">
              <add_context>Молоко</add_context>
            </element>
            <element id="d2">
              <add_context>Молоко</add_context>
            </element>
            <element id="d3">
              <add_context>Молоко</add_context>
            </element>
            <element id="d4">
              <add_context>Молоко</add_context>
            </element>
            <element id="d5">
              <add_context>Молоко</add_context>
            </element>
            <element id="d6">
              <add_context>Молоко</add_context>
            </element>
            <element id="d7">
              <add_context>Молоко</add_context>
            </element>
            <element id="d8">
              <add_context>Молоко</add_context>
            </element>
            <element id="d9">
              <add_context>Молоко</add_context>
            </element>
            <element id="d10">
              <add_context>Молоко</add_context>
            </element>
          </datapanel>
        </action>
      </event>
    </properties>
  <rnum>4</rnum>
</rec>
</records>


</gridsettings>' 

set  @gridsettings=CAST(@gridsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[chart_pas_xmlds_fliped]
	@main_context varchar(512) ='Итого по России',
	@add_context varchar(512) ='',
	@filterinfo xml='',
	@session_context xml ='',
	@element_id varchar(512) ='',	
	@chartsettings xml output
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
Set @main_context='Белгородская обл.'	

Declare @chartsettings_str as varchar(max)
if @add_context='' set @add_context=@main_context+', Балансы'
set @chartsettings_str='<chartsettings>
		<labels>
			<header><h3>'+@add_context+' зерна, тыс. тонн </h3></header>
		</labels>
		<properties legend="bottom" selectorColumn="Периоды" width="500px" height="300px" flip="true" hintFormat="%x (%labely): %value"/>
		<labelsY>

<labelY value="196.9" text="аываыва"/>

<labelY value="30" text="тридцать"/>
</labelsY>
		<template>
{
	"plot": {
		"type": "Columns", 
		"tension": "S", 
		"gap": 3, 
		"markers": true, 
		"areas": false
	}, 
	"theme": "dojox.charting.themes.Chris", 
	"action": [
		{
			"type": "dojox.charting.action2d.Shake", 
			"options": {
				"duration": 500
			}
		}, 
		{
			"type": "dojox.charting.action2d.Tooltip"
		}
	], 
	"axisX": {
		"fixLower": "major", 
		"fixUpper": "major", 
		"minorLabels": false, 
		"microTicks": false, 
		"rotation": -90, 
		"minorTicks": false
	}, 
	"axisY": {
		"vertical": true
	}
}
		</template>
		
		
		
		
<records>
  <rec>
  <Периоды>Период 1</Периоды>
  <_x0033_кв._x0020_2005г.>1691.20</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>1260.80</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>798.00</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>243.58</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>1302.40</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>959.80</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>658.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>246.70</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>1441.30</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>1106.60</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>590.00</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>326.50</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>2578.40</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>2128.50</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>1405.10</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>751.70</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>2032.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>1593.10</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>1023.30</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>559.60</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>1302.40</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>959.80</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>590.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>243.58</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 2</Периоды>
  <_x0033_кв._x0020_2005г.>104.80</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>78.20</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>63.31</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>107.70</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>91.40</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>78.70</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>78.20</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>105.00</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>99.20</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>102.80</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>83.67</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>116.00</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>99.80</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>105.90</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>80.16</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>0.20</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>0.10</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>0.20</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.10</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>0.30</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>0.13</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>0.13</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.13</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>0.13</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 3</Периоды>
  <_x0033_кв._x0020_2005г.>82.10</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>146.20</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>-23.71</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>-2.30</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>222.12</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>190.20</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>-8.08</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>-8.84</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>74.00</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>91.70</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>70.52</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>257.40</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>76.90</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>210.30</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>70.49</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>164.90</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>53.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>132.30</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>29.73</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>87.20</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>605.83</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>396.10</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>291.69</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>388.55</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 4</Периоды>
  <_x0033_кв._x0020_2005г.>271.90</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>598.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>137.89</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>102.71</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>295.37</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>500.90</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>49.40</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>65.63</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>227.37</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>362.30</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>199.08</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>138.90</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>328.10</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>535.80</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>371.63</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>402.80</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>519.20</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>435.00</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>239.75</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>119.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>458.28</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>518.63</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>371.63</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>397.40</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 5</Периоды>
  <_x0033_кв._x0020_2005г.>144.40</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>135.10</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>180.30</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>190.36</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>160.43</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>176.30</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>159.00</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>200.89</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>201.93</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>215.20</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>323.78</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>231.70</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>266.50</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>376.70</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>342.93</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>258.50</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>286.70</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>289.90</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>305.78</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>267.40</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>278.54</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>271.09</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>222.26</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>200.89</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 6</Периоды>
  <_x0033_кв._x0020_2005г.>16.20</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>26.80</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>2.54</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>0.93</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>15.59</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>17.50</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>2.40</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>0.30</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>15.80</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>17.50</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>2.82</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>1.20</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>12.10</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>20.70</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>5.55</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>4.70</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>11.60</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>21.70</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>3.32</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>4.10</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>13.50</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>18.33</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>5.55</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>4.00</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 7</Периоды>
  <_x0033_кв._x0020_2005г.>50.40</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>24.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>42.30</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>42.91</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>55.59</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>41.80</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>35.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>44.40</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>59.90</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>49.90</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>40.00</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>40.80</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>60.20</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>53.60</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>52.50</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>31.00</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>42.30</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>49.80</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>49.60</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>34.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>42.33</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>49.84</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>61.92</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>42.44</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 8</Периоды>
  <_x0033_кв._x0020_2005г.>71.90</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>3.70</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>0.00</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>140.25</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>78.35</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>0.00</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>37.80</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>82.90</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>72.00</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>8.00</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>21.20</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>108.10</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>81.20</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>12.90</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>20.90</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>121.10</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>66.60</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>5.70</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.00</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>126.10</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>66.70</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>5.71</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>117.55</_x0032_кв._x0020_2011г.>
</rec>
<rec>
  <Периоды>Период 9</Периоды>
  <_x0033_кв._x0020_2005г.>1818.50</_x0033_кв._x0020_2005г.>
  <_x0034_кв._x0020_2005г.>212.40</_x0034_кв._x0020_2005г.>
  <_x0031_кв._x0020_2006г.>110.70</_x0031_кв._x0020_2006г.>
  <_x0032_кв._x0020_2006г.>150.70</_x0032_кв._x0020_2006г.>
  <_x0033_кв._x0020_2006г.>1442.20</_x0033_кв._x0020_2006г.>
  <_x0034_кв._x0020_2006г.>203.70</_x0034_кв._x0020_2006г.>
  <_x0031_кв._x0020_2007г.>114.30</_x0031_кв._x0020_2007г.>
  <_x0032_кв._x0020_2007г.>148.80</_x0032_кв._x0020_2007г.>
  <_x0033_кв._x0020_2007г.>1697.60</_x0033_кв._x0020_2007г.>
  <_x0034_кв._x0020_2007г.>226.60</_x0034_кв._x0020_2007г.>
  <_x0031_кв._x0020_2008г.>131.10</_x0031_кв._x0020_2008г.>
  <_x0032_кв._x0020_2008г.>154.90</_x0032_кв._x0020_2008г.>
  <_x0033_кв._x0020_2008г.>2923.30</_x0033_кв._x0020_2008г.>
  <_x0034_кв._x0020_2008г.>339.60</_x0034_кв._x0020_2008г.>
  <_x0031_кв._x0020_2009г.>160.80</_x0031_кв._x0020_2009г.>
  <_x0032_кв._x0020_2009г.>0.00</_x0032_кв._x0020_2009г.>
  <_x0033_кв._x0020_2009г.>2153.80</_x0033_кв._x0020_2009г.>
  <_x0034_кв._x0020_2009г.>230.90</_x0034_кв._x0020_2009г.>
  <_x0031_кв._x0020_2010г.>0.00</_x0031_кв._x0020_2010г.>
  <_x0032_кв._x0020_2010г.>0.00</_x0032_кв._x0020_2010г.>
  <_x0033_кв._x0020_2010г.>996.45</_x0033_кв._x0020_2010г.>
  <_x0034_кв._x0020_2010г.>125.03</_x0034_кв._x0020_2010г.>
  <_x0031_кв._x0020_2011г.>0.00</_x0031_кв._x0020_2011г.>
  <_x0032_кв._x0020_2011г.>27.45</_x0032_кв._x0020_2011г.>
</rec>
</records>		
		
		</chartsettings>' 
set	@chartsettings=CAST(@chartsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_RowHeight1]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',    
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/yandex.png'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн. Настройки GridHeight, RowHeight по умолчанию</h3></header>
            <footer><h3 class="testStyle">Футер. '+@main_context+' зерна, тыс. тонн </h3></footer>            
        </labels>
        <columns>
        <col id="Регион" width="250px"/> <col id="Картинка" width="200px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="85px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties pagesize="50" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_RowHeight2]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',    
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
    @error_mes varchar(512) output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
   
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], ''imagesingrid/yandex.png'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн. Подобранные настройки GridHeight, RowHeight</h3></header>
            <footer><h3 class="testStyle">Футер. '+@main_context+' зерна, тыс. тонн </h3></footer>            
        </labels>
        <columns>
        <col id="Регион" width="250px"/> <col id="Картинка" width="200px" type="IMAGE"/>'
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="85px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties pagesize="50" gridHeight="700" rowHeight="120" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_default_profile]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
  
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a
Where a.Год = 2005   

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], getdate() as [Сейчас],  ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> 
        <col id="Картинка" width="40px" type="IMAGE"/>'
        
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="50" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO


SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[extlivegrid_special_profile]
    @main_context varchar(512) ='Производственное потребление в сельхозорганизациях и у населения - На семена',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
	@element_id varchar(512) ='',    
    @sortcols varchar(1024) ='',	
    @gridsettings xml output,
	@error_mes varchar(512) output    
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    
declare @filters as varchar(1024)
declare @ordering as varchar(1024)

IF OBJECT_ID('tempdb.dbo.#Per') is not null 
drop table #Per
IF OBJECT_ID('tempdb.dbo.#Reg_year') is not null 
drop table #Reg_year
IF OBJECT_ID('tempdb.dbo.#Columns_Name') is not null 
drop table #Columns_Name
IF OBJECT_ID('tempdb.dbo.#Tab1') is not null 
drop table #Tab1

declare @year as varchar(50)
declare @quater as varchar(50)
declare @params as varchar(8000)
Set @year='2010'
Set @quater='3'
Set @params=''
Select
    [Год],
    [Квартал]
Into #Per
From
    (Select 
        Journal_45_Name as [Год],
        '1' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '2' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '3' as [Квартал]
    From Journal_45
    Union ALL
    Select 
        Journal_45_Name as [Год],
        '4' as [Квартал]
    From Journal_45
    ) a
Where a.Год = 2005   

Select
    newid() as id__,
    geo5_id,
    geo5.NAME as [Регион],
    Journal_45_Name,
    Journal_45_Id,
    geo6.Name as [Федеральный округ],
    #Per.[Квартал] ,
      Case
            When geo6.Name='Южный ФО' then 3
            When geo6.Name='Уральский ФО' then 5
            When geo6.Name='Сибирский ФО' then 6
            When geo6.Name='Приволжский ФО' then 4
            When geo6.Name='Дальневосточный ФО' then 7
            When geo6.Name='Центральный ФО' then 1
            When geo6.Name='Северо-Западный ФО' then 2
            Else 0
         End as sort     
Into #Reg_year
From Journal_45 Inner Join #Per
    On #Per.[Год]= Journal_45_Name,
    geo5 left Join geo6
    On geo5.FJField_9=geo6_Id
Where geo5.FJField_16=1   and
 cast( Journal_45_Name as float)
   + cast(#Per.[Квартал] as float)/10 >= cast(cast(@year as int)-5  as float)+cast(@quater as float)/10
 and
 cast( Journal_45_Name as float)
     + cast(#Per.[Квартал] as float)/10 < cast(cast(@year as int)+1  as float)+cast(@quater as float)/10
Select distinct    
    [Квартал]  +'кв. ' +Journal_45_Name+ 'г.' as Col_Id,
    Journal_45_Name,
     [Квартал]
Into #Columns_Name
From #Reg_year
Order by Journal_45_Name, [Квартал]

select Journal_44.FJField_17 as [~Всего, тыс тонн],
       Journal_44.FJField_20,
       Journal_44.FJField_18,
        geo5.Name as [регион],
        Journal_41.Journal_41_Name,
        Journal_40_Name,
        Journal_45.Journal_45_Name as [год],
       Journal_44.FJField_16 as [квартал],
       Case
            When geo6.Name in ('Южный ФО','Уральский ФО','Сибирский ФО',
                'Приволжский ФО','Дальневосточный ФО','Центральный ФО','Северо-Западный ФО','Российская Федерация') then 0
            Else 1
         End as sort2,
         geo6.Name
Into #Tab1
From Journal_44
    Inner Join geo5
    On geo5.geo5_Id=Journal_44.FJField_18
    Inner Join Journal_40
    On Journal_40_Id=Journal_44.FJField_14
    Inner Join Journal_41
    On Journal_41_Id=Journal_44.FJField_12
    left Join geo6
    On geo5.FJField_9=geo6_Id
    Inner Join Journal_45
    On Journal_45_Id=Journal_44.FJField_21
    Where geo5.FJField_16=1    and
                  Journal_40_Name= @main_context

and
(((cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)>=(cast(cast(@year as int)  as float)+ cast(@quater as float)/10 )

               and (cast( Journal_45.Journal_45_Name as float)
               + cast(Journal_44.FJField_16 as float)/10)<(cast((cast(@year as int)+1 )  as float)+cast(@quater as float)/10)
                and Journal_44.FJField_20='Прогноз')
        or
                (( cast( Journal_45.Journal_45_Name as float)
                   + cast(Journal_44.FJField_16 as float)/10 )< (cast(cast(@year as int)  as float)+cast(@quater as float)/10 )
                   and Journal_44.FJField_20='Факт'))
                   
                   
select @params = @params + ', [' + RTRIM(#Columns_Name.Col_Id)+']' FROM #Columns_Name
set @params = substring(@params, 3, len(@params) - 1);



Insert into #Tab1 (#Tab1.[~Всего, тыс тонн],#Tab1.FJField_20,#Tab1.[регион],
        #Tab1.Journal_41_Name,#Tab1.Journal_40_Name,#Tab1.год,#Tab1.квартал,#Tab1.sort2)
select sum([~Всего, тыс тонн]),
       #Tab1.FJField_20,
       'Итого по россии',
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал,
        -1
        From #Tab1
   Group by
   #Tab1.FJField_20,
        #Tab1.Journal_41_Name,
        #Tab1.Journal_40_Name,
        #Tab1.год,#Tab1.квартал

Set @ordering=(Select
				Case
					When @sortcols='' then 'Order by sort2'
					Else @sortcols 
				End)     
declare @Sql varchar(8000);
set @Sql = 
'Select [Регион], getdate() as [Сейчас],  ''imagesingrid/test.jpg'' AS [Картинка],' + @params+',cast( ''<properties>
                    <event name="row_single_click">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>    
                    <event name="row_selection">
                        <action>
                            <main_context>current</main_context>                        
                            <datapanel type="current" tab="current">
                                <element id="3">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                <element id="5">
									<add_context>''+[Регион]+''</add_context>                                                                                             
                                </element> 
                                
                            </datapanel>
                        </action>
                    </event>                                       
            </properties>'' as xml)  as [~~properties] FROM('+
'SELECT [Регион], sort2,' + @params+
' FROM' +
' (Select
     cast (#Tab1.[~Всего, тыс тонн] as numeric(10,2))as t1,
     #Reg_year.[Регион] ,
     #Tab1.квартал+''кв. ''+#Reg_year.[Journal_45_Name]+''г.'' as [Квартал],     
     #Reg_year.[Федеральный округ],
      #Tab1.sort2,
     ''Зерно''  as [~Продовольственный ресурс]
      
FROM #Reg_year
    Left join #Tab1
        On #Reg_year.[Регион]=#Tab1.[регион] and
         #Reg_year.Journal_45_Name=#Tab1.год and 
         #Reg_year.Квартал =#Tab1.квартал
         ) p '+
         
' PIVOT ('+
'    max(t1)'+
'    FOR [Квартал] in('+@params+')'+
' ) AS pvt Where [3кв. 2005г.] is Not NULL )p '+@ordering
EXEC(@Sql)
Declare @gridsettings_str as varchar(max)
set @gridsettings_str='<gridsettings>
        <labels>
            <header><h3 class="testStyle">'+@main_context+' зерна, тыс. тонн </h3></header>
        </labels>
        <columns>
        <col id="Регион" width="100px"/> 
        <col id="Картинка" width="40px" type="IMAGE"/>'
        
        
select     @gridsettings_str=@gridsettings_str+'<col id="'+#Columns_Name.Col_Id+'" width="60px" precision="2"/>' From #Columns_Name
set @gridsettings_str=@gridsettings_str+'</columns>
							<action>
							<main_context>current</main_context>							
                            <datapanel type="current" tab="current">
                                <element id="3">
	                                <add_context>current</add_context>
                                </element>   
                                <element id="5">
	                                <add_context>current</add_context>
                                </element>                                                                   
                                
                            </datapanel>
                        </action>
<properties flip="false" pagesize="50" profile="special.properties" totalCount="0"/></gridsettings>' 
set    @gridsettings=CAST(@gridsettings_str as xml)
END
GO
