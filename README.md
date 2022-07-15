## Getting Started

### Init database
```
docker-compose up -d
```

#### Set environment variables
```
cp local.env .env
set -a && source .env && set +a
``` 

#### Start the Application

With gradlew
```
./gradlew bootRun --args='--spring.profiles.active=common,dev' -Duser.language=en -Duser.timezone=UTC
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

Описание алгоритма поиска матчей

Работа алгоритма состоит из трех задач по расписанию:
- FindMatchSchedule: 
    1) достаются из базы игроки, которые находятся в очереди поиска и матчи, которые не заполненны
    2) далее итерируемся по списку игроков, отпраляем каждого в findMatchUseCase там идет соотношение его ранга и скилла с матчами
    3) если найдет матч, добавляем игрока в матч если нет, вернет в поиск, и отправит через кафку в FillingMatchesSchedule
- FillingMatchesSchedule: 
    1) игроки, попадают в очередь playerInSearch из нее формируются и создаются нужные матчи исходя из колличества игроков в очереди учитывая заданный размер матча
    2) формула ceil(колличество игроков с определенныи рангом / размер матча)
- PlayerLongerSearchSchedule:
    1) достаются из базы игроки, которые находятся в очереди поиска больше заданных секунд, им выставляется приоритет в поиске матча