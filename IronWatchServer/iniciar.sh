echo "[START] Recompilando"
mvn clean compile
mvn clean package
echo "[START] Recompilado!"
echo "[START] Iniciando Servidor..."
mvn exec:java -Dexec.mainClass="com.girellidev.ironwatchserver.IronWatchServer"
