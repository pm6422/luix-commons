export GPG_TTY=$(tty)

./mvnw -s /Users/louislau/Workspace/luix-rpc/.mvn/settings.xml -DskipTests clean javadoc:jar deploy -P release