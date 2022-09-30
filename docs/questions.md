# A kérdések, amik felmerülnek ide lesznek lejegyzetelve.

- Az authnál hogyan lesz az megoldva, hogy maga az auth (login register) egy külön microservice, viszont azt a részt is annak kéne csinálnia, hogy ellenőrzi a tokenek validságát és ad refresh tokent a kéréseknél, szóval kb az egész appban ott van valamennyire, nem tudom hogyan kellene különválasztani?
  - Megbeszéltük

- RabbitMQ-t/Apache Kafkát érdemes lenne-e használnom microservicek közti kommunikációhoz HTTP (REST Templates kommunikáció) helyett? -> lehetne akkor aszinkron módon kommunikálniuk (ha jól értem), az is gyorsítaná az alkalmazást
  - Megfontolandó, de nem feltétlen kell nekem

- Ezeknél a messaging szolgáltatásoknál minden serviceben kell lennie egy producer/consumernek?
  - Igen

- Ez így most a képernyőképek alapján teljesíthető feladatnak tűnik, vagy túlvállaltam magam?
  - Igen

- Az endpointok a mostani állapotukban elegek lesznek/jókat írtam össze? Mit hagytam ki, mi az amin változtassak?
  - Igen

---

- Hova lenne érdemes deployolni?
  - erről azt beszéltük hogy majd visszatérünk ide, mert ráér, viszont utána kell nézni hogy melyik szolgáltatás mit kínál és ennek fényében majd valamit választok

