echo off
cls


set VERSION=1.1
set HOST=hostname
set DESTINATION=\\%HOST%\storagefolder
set DIRECTORY=C:\Documents and Settings
set CLIENTNAME=username
set AUTHOR=Kevin Scroggins


set CURDATE=%DATE%
set DATEFILE=Backup.ini
set LOGFILE=BackupLog.ini
set RESPONSEFILE1=BackupResponse.ini
set RESPONSEFILE2=BackupResponse2.ini


set FIRSTRUN=false
if not exist %DATEFILE% set FIRSTRUN=true
if "%FIRSTRUN%" == "true" goto firstrun
set /p LASTDATE=<%DATEFILE%
set COPYDATE=%LASTDATE:~4,2%-%LASTDATE:~7,2%-%LASTDATE:~10,4%
:firstrun


ping %HOST%>%RESPONSEFILE1%
sort %RESPONSEFILE1% /REVERSE > %RESPONSEFILE2%
del %RESPONSEFILE1%
set /p RESPONSE=<%RESPONSEFILE2%
del %RESPONSEFILE2%
if not "%RESPONSE:~0,10%" == "Reply from" goto fail
cls


echo Auto Backup Version %VERSION%
echo.
echo Beginning file backup...


echo.
echo Backing up Documents and Settings for All Users...
echo.
mkdir "%DESTINATION%\%CLIENTNAME%\"
if "%FIRSTRUN%" == "true" xcopy "%DIRECTORY%" "%DESTINATION%\%CLIENTNAME%" /E /Y
if "%FIRSTRUN%" == "false" xcopy "%DIRECTORY%" "%DESTINATION%\%CLIENTNAME%" /E /Y /D:%COPYDATE%


:success
echo %CURDATE% > %DATEFILE%
echo %CURDATE% - Backup completed successfully at %TIME% >> %LOGFILE%
echo.
echo Backup completed successfully at %TIME%
echo.
goto quit


:fail
echo %CURDATE% - Backup failed - destination source "%HOST%" unavailable at %TIME% >> %LOGFILE%
echo.
echo Backup failed - destination source "%HOST%" unavailable at %TIME%
echo.
goto quit


:quit
pause
cls
