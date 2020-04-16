root_dir=..
java -server -Dspring.profiles.active=prod -Dfile.encoding=UTF-8 -XX:+HeapDumpOnOutOfMemoryError -Xmx1024m -Xms512m -XX:HeapDumpPath=$root_dir/heapdump.hprof -jar $root_dir/${spring.application.name}.jar
