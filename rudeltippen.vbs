Set WshShell = CreateObject("WScript.Shell")
If Wscript.Arguments(0) = "/start" Then
	WshShell.Run chr(34) & "startup.bat" & Chr(34), 0
ElseIf Wscript.Arguments(0) = "/stop" Then
	WshShell.Run chr(34) & "shutdown.bat" & Chr(34), 0
End If
Set WshShell = Nothing