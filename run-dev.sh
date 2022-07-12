cp local.env .env
set -a && source .env && set +a

./gradlew ktlintCheck && ./gradlew test && ./gradlew bootRun --args='--spring.profiles.active=common' -Duser.language=en -Duser.timezone=UTC
