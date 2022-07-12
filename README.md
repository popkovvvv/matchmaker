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

With gradlew (without kafka, postgres)
```
./gradlew bootRun --args='--spring.profiles.active=common' -Duser.language=en -Duser.timezone=UTC
```

With docker-compose
```
docker-compose up
```

Or just run
```
./run-dev.sh
```

Delete postgres data
```
docker-compose down
rm -rf postgres/data/*
docker-compose up -d
```