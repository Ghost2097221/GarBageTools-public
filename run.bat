echo @off
mvn clean package -DskipTests && java.exe  -jar ./target/DropLabTools-0.0.1-SNAPSHOT.jar
pause