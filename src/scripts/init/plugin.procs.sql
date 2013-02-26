SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[extJsTree_getData]
   @main_context nvarchar(512)='',  
   @add_context nvarchar(512)='',  
   @filterinfo xml='',  
   @session_context xml='',
   @params xml='',
   @data xml output
AS
BEGIN
declare
	@parentId nvarchar(20)='',
	@curValue nvarchar(20)=''
	
set @parentId = @params.value('(/params/id)[1]','nvarchar(20)');
if @parentId is null begin 
	set @parentId = 'Root'
end
set @curValue = @params.value('(/params/curValue)[1]','nvarchar(20)');
if @curValue is null 
	begin 
		set @curValue = ''
	end else 
	begin
		set @curValue = '['+@curValue+']'
	end

set @data='
	<items>
		<item id="'+@parentId+'.1" name="Lazy loaded item '+@parentId+'.1'+@curValue+'" leaf="false"/>
		<item id="'+@parentId+'.2" name="Lazy loaded item '+@parentId+'.2'+@curValue+'" leaf="false"/>
    </items>
	';

END
GO
