## Using Docker to simplify development (optional)

You can use Docker to improve your development experience. A number of docker-compose configuration are available in
the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mysql database in a docker container, run:

```
docker-compose -f luix-uid-generator-usage-demo/src/main/docker/mysql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f luix-uid-generator-usage-demo/src/main/docker/mysql.yml down
```