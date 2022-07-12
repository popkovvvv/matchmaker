## Getting Started

### Init database
```
create database matchmaker encoding utf8;
```

#### Set environment variables
```
cp local.env .env
set -a && source .env && set +a
``` 

#### Start the Application

with gradlew (without kafka, postgres)
```
./gradlew bootRun --args='--spring.profiles.active=common' -Duser.language=en -Duser.timezone=UTC
```

with docker-compose
```
docker-compose up
```

... or just run

bash
```
./run-dev.sh
```