GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[pluginRadarInfo]
    @main_context varchar(512) ='',
    @add_context varchar(512) ='',
    @filterinfo xml='',
    @session_context xml ='',
    @element_id varchar(512) ='',    
	@params xml='',
    @data xml output,
    @settings xml output
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;


set    @data=CAST(
'<root>
<data>
<series name="Russia" data1="63.82" data2="17.18" data3="7.77"/>
<series name="Moscow" data1="47.22" data2="19.12" data3="20.21"/>
<series name="Piter" data1="58.77" data2="13.06" data3="15.22"/>
</data>
</root>' as xml)

set @settings='<properties width="800px" height="600px">                                    
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


GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[jstreegrid_addrecord1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),

	@addrecorddata xml,

  @gridaddrecordresult xml output,

	@error_mes varchar(2048) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

--INSERT INTO Debug (context) VALUES (@addrecorddata)


DECLARE @currentRecordId varchar(2048)
SET @currentRecordId=(select @addrecorddata.value('(/addrecorddata/currentRecordId)[1]','varchar(MAX)'))


DECLARE @name varchar(2048)
DECLARE @Id INTEGER
DECLARE @ParentId uniqueidentifier

SET @name = (SELECT Name FROM geo6 where geo6_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo6 where geo6_Id =  @currentRecordId)
	SET @name = @name+'_New'
	INSERT INTO geo6 (Name, Id) VALUES (@name, @Id)
END

SET @name = (SELECT Name FROM geo5 where geo5_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo5 where geo5_Id =  @currentRecordId)+10000
	SET @name = @name+'_New'
	SET @ParentId = (SELECT FJField_9 FROM geo5 where geo5_Id =  @currentRecordId)
	INSERT INTO geo5 (Name, Id, FJField_9) VALUES (@name, @Id, @ParentId)
END

SET @name = (SELECT Name FROM geo3 where geo3_Id =  @currentRecordId)
IF @name IS NOT NULL
BEGIN
	SET @Id = (SELECT Id FROM geo3 where geo3_Id =  @currentRecordId)+10000
	SET @name = @name+'_New'
	SET @ParentId = (SELECT FJField_9 FROM geo3 where geo3_Id =  @currentRecordId)
	INSERT INTO geo3 (Name, Id, FJField_9) VALUES (@name, @Id, @ParentId)
END

--

--geo6_Id
--geo5_Id
--geo3_Id


Declare @gridaddrecordresult_str as varchar(max)
set @gridaddrecordresult_str='
<gridaddrecordresult>
</gridaddrecordresult>'

set    @gridaddrecordresult=CAST(@gridaddrecordresult_str as xml)

--set    @gridaddrecordresult=NULL


	

--	SET @error_mes=N'Ошибка при добавлении записи'
--	RETURN 30


  set @error_mes = N'Record successfully added'
  RETURN 555;


	SET @error_mes=''
	RETURN 0


	
	
END
GO

GO
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[jstreegrid_save1]
	@main_context varchar(512),
	@add_context varchar(512),
	@filterinfo xml,
	@session_context xml,
	@element_Id varchar(512),

	@savedata xml,

  @gridsaveresult xml output,

	@error_mes varchar(2048) output	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

--INSERT INTO Debug (context) VALUES (@savedata)
--INSERT INTO Debug (string) VALUES (@savedata)



Declare @gridsaveresult_str as varchar(max)
set @gridsaveresult_str='
<gridsaveresult>
	<properties refreshAfterSave="true" />
</gridsaveresult>'

set    @gridsaveresult=CAST(@gridsaveresult_str as xml)


--set    @gridsaveresult=NULL


--	SET @error_mes=N'Ошибка при сохранении данных'
--	RETURN 10


  set @error_mes = N'Data saved successfully'
  RETURN 555;


	SET @error_mes=''
	RETURN 0

	
END
GO
