export GPG_TTY=$(tty)

# For Mac env
./mvnw -s /Users/louislau/Workspace/tech-docs/maven/settings.xml -DskipTests clean javadoc:jar deploy -P release

# For Ubuntu env
#./mvnw -s /home/louis/Workspace/tech-docs/maven/settings.xml -DskipTests clean javadoc:jar deploy -P release