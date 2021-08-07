jpackage ^
--type msi ^
--name "LS-ModManager" ^
--description "LS-ModManager" ^
--copyright "Kaktushose, 2021" ^
--license-file ".\LICENSE" ^
--vendor "Kaktushose" ^
--app-version 1.0.0 ^
--input .\target ^
--icon .\src\main\resources\img\LogoT.ico ^
--main-class com.github.kaktushose.lsmodmanager.Bootstrapper ^
--main-jar LS-ModManager-1.0.0.jar ^
--module-path "C:\Program Files\Java\javafx-jmods-11.0.2" ^
--module-path "C:\Program Files\Java\jdk-14.0.1\jmods" ^
--add-modules javafx.controls,javafx.fxml,java.naming,java.sql ^
--win-shortcut --win-menu --win-dir-chooser --win-console
pause