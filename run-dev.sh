cp local.env .env
set -a && source .env && set +a

docker-compose up -d

./gradlew ktlintCheck && ./gradlew test && ./gradlew bootRun --args='--spring.profiles.active=common,dev' -Duser.language=en -Duser.timezone=UTC
