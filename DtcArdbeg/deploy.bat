@echo off
set currentDir=%~dp0
"c:\Program Files\iPuTTY\pscp" -pw phnw2search -r %currentDir%war search@xe3wtest1:/home/search/gwt/testbed/es133/
