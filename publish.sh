export GPG_TTY=$(tty)

./mvnw -s /Users/louislau/Workspace/tech-docs/maven/settings.xml -DskipTests clean javadoc:jar deploy -P release