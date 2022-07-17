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
    1) Достаются из базы игроки, которые находятся в очереди поиска, и матчи которые не заполненны
    2) Далее итерируемся по списку игроков, отпраляем каждого в findMatchUseCase там идет соотношение его ранга, скилла и пинга с матчами
    3) Если найдет матч, добавляем игрока в матч если нет, вернет в поиск, и добавит в массив заполнения новыми матчами (notFoundedMatchPlayers)
- MatchRebalancerSchedule: 
    1) Достаются матчи, которые не собрали нужное колличество игроков для начала матча, каждый игрок их этих матчей попадает обратно в поиск игры,
    но уже с приоритетом (ищется только по рангу)
    2) Далее матчи которые попали под ребалансировку удаляются из базы

Как бы я улучшил:
1) Добавил бы Apache Kafka, туда бы писал игроков, которые ищут игру, разбил бы на три топика (low_skilled_players, middle_skilled_players, hish_skilled_players)
   Эти топики можно разбить на партиции
2) Сделал бы независимые скедулеры, которые читали бы очереди кафки по партициям, что дало бы горизонтально масштабировать скедулеры
   не зависимо от веб слоя
3) Явно требуется улучшить систему поиска, возможно ввести коэффициенты весов например (skill * 0.70, latency * 0.90)
   что дало бы гибкость в настройке поиска, возможность определять приоритетный фактор поиска
4) Необходимо добавить E2E тесты для эффективного тестирования, добавил бы testContainers
5) Что бы улучшить показатель latency, необходимо выделить сервис (под) под каждый регион, например (Европа, Азия, Америка)

Пометки:
1) алгоритм может работать не точно, показана сама идея, которую пытался развить
2) тестами 100 % не успел покрыть, а хотелось бы

