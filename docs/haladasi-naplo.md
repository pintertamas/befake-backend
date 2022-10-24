# Haladási napló

### 1. hét
Jelentkeztem a témára aminek még nincsen véglegesítve a neve

### 2. hét
Létrehoztam a repositorykat és a specifikációt is megírtam
A repok url-jei:
- https://github.com/pintertamas/befake-backend
- https://github.com/pintertamas/befake-mobile

### 3. hét
Adobe XD segítségével elkészítettem a képernyőképeket. Mivel a BeReal nevű alkalmazást fogom lemásolni, nem volt nehéz dolgom, csak ugyan azt meg kellett valósítnom mint az app, egy néhány számomra felesleges funkciót lecsípve.
A képernyőképeket ebben a mappában lehet részletesen megnézni (egyelőre nem csináltam regisztrációs és bejelentkezős felületet, de az is lesz majd):

```https://github.com/pintertamas/befake-backend/tree/main/docs/designs/artboards```

Az egészről pedig a terv itt látható:
![](https://github.com/pintertamas/befake-backend/blob/main/docs/designs/Screenshot%202022-09-18%20at%200.44.35.png)

Elkészítettem az EK diagramot is, jelenlegi formájában így néz ki:
![](https://github.com/pintertamas/befake-backend/blob/main/docs/ER%20diagrams/BeFake%20ER%20diagram_2.png)

Az alkalmazás ezeket az endpointokat fogja tudni kiszolgálni:

```
GET:
- /user/{id} -> visszaadja egy felhasználó adatait
- /post/{postId} -> visszaadja a kért posztot
- /post/todaysPost/{userId} -> visszaadja a felhasználó aznapi posztját
- /storage/lastPosts/{userId} -> visszaadja a felhasználó utolsó X napjának posztjait - itt az X egy paraméter lesz, amit ha nem adunk meg akkor az alapértelmezett érték 14 lesz
- /storage/{userId} -> visszaadjaa felhasználó addigi összes posztját

POST:
- /auth/register -> regisztrál egy felhasználót
- /auth/login -> beléptet egy felhasználót
- /friendlist/add/{userId} -> hozzáadja a kért barátot pending státusszal
- /friendlist/confirm/{userId} -> elfogadja a barát kérést és a pending státuszt aktívra változtatja
- /post/create -> készít egy posztot
- /comment/create -> kommentel egyet
- /reaction/create -> reagál egyet

PATCH:
- /user/{id} -> szerkeszt egy felhasználót
- /post/{id} -> szerkeszti a kért posztot

DELETE:
- /user/{id} -> töröl egy felhasználót
- /friendlist/{userId} -> töröl egy barátot
- /post/{postId} -> töröl egy posztot
- /comment/{commentId} -> töröl egy kommentet
- /reaction/{reactionId} -> töröl egy reakciót
```

Csináltam egy új fájlt, amiben a kérdéseimet fogom követni. Ez itt érhető el:

```https://github.com/pintertamas/befake-backend/blob/main/docs/questions.md```

Ezek mellett csinálok egy Udemy kurzust is ami a Spring Bootos microservicek fejlesztéséről szól és van benne szó a Dockerizálásról és a Kubernetesről is.
A kurzus itt érhető el: ```https://www.udemy.com/course/microservices-with-spring-boot-and-spring-cloud/```

Egyelőre annyit csináltam, hogy van két microservicem, az egyik egy adatbázisból szed pénzváltós adatokat, a másik meg feign client segítségével kommunikál ezzel a service-szel és kiszámolja hogy a visszaadott értékek alapján mennyit ér x pénz egy másik valutában.
A két service mellett van egy Netflix Eureka naming server is, ami kezeli a loadot és balance-olja a két futó instance között a kéréseket, amit közben a böngészőből tudok monitorozni. A szakdolgozatomban majd annyival szeretném ezt a részt bővíteni, hogy ahol csak lehet, Kafkát, vagy RabbitMQ-t használok a kommunikációra, hogy megbízható, hibatűrőbb és skálázhatóbb legyen az alkalmazás.
Ezek mellé csináltam egy API gateway-t is, aminek köszönhetően olyat tudok csinálni, hogy a gateway portján hívom a keresett endpointokat, a gateway pedig ezeket a kéréseket továbbítja az eureka szervernek, ami load balance-olva tovább adja adott microserviceknek, amik valami más porton futnak, de ezt nem kell ismernie a felhasználónak, mivel be vannak regisztrálva már az eureka szerveren.

Ez így néz ki a gyakorlatban:

![](https://i0.wp.com/kishoretechblog.com/wp-content/uploads/2020/04/API-Gateway.png?w=791&ssl=1)

### 4. hét
Véglegesítettem a téma magyar és angol címét:
- Skálázhatóság vizsgálata mikroszolgáltatásokra épülő konténer-alapú képmegosztó alkalmazáson
- Scalability analysis of a microservice-based containerized image sharing application

Csináltam egy fájlt a tervezési megfontolások követéséhez:

```https://github.com/pintertamas/befake-backend/blob/main/docs/tervezesi-megfontolasok.md```

Folytattam az Udemy-s kurzust, de nem sokat haladtam vele a gyakorlatban, mivel el voltam akadva egy elég bosszantó hibánál: nem lehetett Docker image-et csinálni a microservice-eimből. Elment jó sok órám debugolással, aztán mivel nem volt rá megoldás a neten, megpróbáltam magamtól megoldani.
Mint kiderült, az volt a hiba, hogy nekem Amazon Corretto-s JDK-m volt, a buildpack meg BellSoft-ossal működik csak, valami megmagyarázhatatlan indok miatt.
Most hogy sikerült ezt megoldani, már vannak konténerizált mikroszolgáltatásaim, amiket futtatni tudok egy docker-compose.yaml fájl segítségével.
A tervem az még erre a hétre, hogy a Kubernetessel is elkezdek ismerkedni. Ha ez is meglesz, akkor már fogok úgy állni a szakdogával is, hogy a kurzuson tanultakat már alkalmazni tudjam rajta.

### 5. hét
Megcsináltam az api-gateway-t, a naming-server-t és megírtam a user-service-t. Az api-gatewaybe raktam authentikációt és az authorizációhoz létrehoztam az auth-service-t. Az api-gateway minden kérést csak token kíséretében küld tovább, az /auth/login és /user/register kéréseket viszont továbbítja token nélkül is. Sajnos elég sok időm ment el azza, hogy az auth-service 403-as válaszokat adott vissza, de aztán rájöttem, hogy a csrf-et kellett kikapcsolni ahhoz, hogy működjön újra.
A másik kettő bosszantó probléma amibe belfutottam az az, hogy a Docker néha megbolondul és nem hajlandó semmit se csinálni amit előtte simán tudtam, amíg újra nem indítom, helyette csak 500-as hibákat dob. A másik meg az, hogy ha egy service Dockerben fut csak, akkor az api gateway "Failed to resolve 'de7e17ad10f5' after 5 queries" hibaüzenetet ad és nem éri el. Erre még nem találtam megoldást.

### 6. hét
Ezen a héten gyakorlatilag semmit sem haladtam és okosabb se lettem, mivel minden amit megpróbáltam csak hibákat szült és semmit se sikerült megoldanom. Először a Dockerrel volt probléma, ami még a múlt hétről maradt meg, de miközben próbáltam javítani, rájöttem, hogy az auth service nem valid jwt tokent ad vissza és validáltatni se lehet az api gateway-jel, mert 500-as hibákat kapok. Jelenleg azt próbálom megoldani, hogy az auth servicem működjön és tudjam végre elkezdeni a lényegi részét a projektnek.
Arra jutottam, hogy a múltheti állapotában voltam a legközelebb a megoldáshoz, az 500-as hibák azért jöttek, mert a token elejéről nem szedtem ki a "Bearer " substringet. Miután ezt megcsináltam, egy másik problémába ütköztem, mivel az api-gateway és az auth-server két különböző szerver, így a JWT-k aláírása is más lesz. Egyelőre az a tervem, hogy a secretet egy közös, config serverről fogják kiszedni, így minden microservice ugyan azzal a kulccsal fog dolgozni, így pedig nem lesznek aláírás problémák. Még ne tudom hogy ez így jó megoldás lehet-e, de egyelőre ez a következő lépés terve.

### 7. hét
Ezen a héten az előzőhöz képest nagyon jól haladtam. Megoldottam az auth problémát, erről a tervezési megfontolásos doksiban írtam is Authentikáció/Authorizáció cím alatt. Ezek után nekiláttam az AWS S3 setupolásához, amivel sajnos eléggé meg kellett küzdeni, mert viszonylag értelmetlenek a hibaüzenetek és félre is vezettek, de egy supportossal való beszélgetésnek köszönhetően egy nap alatt megoldódott a probléma.
Ennek segítségével megcsináltam a posztokat kezelő microservice-t, aztán megcsináltam a barát jelölős meg jelölést elutasítós, stb friend-service-t is.
A [roadmap.md](https://github.com/pintertamas/befake-backend/blob/main/docs/roadmap.md) nevű doksiban összeírtam egy listát az endpointokról és hogy még miket kell lefejlesztenem, ezek mellett pedig elkezdtem jegyzetelni hogy milyen további teendőim vannak a fejlesztéssel kapcsolatban, mert az a tapasztalatom hogy ezeket általában elfelejtem és így lesz honnan elindulnom legközelebb.
Megírtam az interaction-service-t is, már csak néhány edit useres teendő maradt hátra, hogy az alapok meglegyenek.
Amit viszont rosszul csináltam eddig az az, hogy kicsit bénán kezeltem az adatbázist és a servicek közti kommunikációt a legtöbb esetben kikerültem.
Most azt csinálom meg, hogy a servicek külön adatbázisokban és csak egyetlen, a feladatukhoz kötődő táblát tartalmaznak és ha valami plusz infót akarnak megtudni, akkor azt egy másik service-től lekérik, de nem túrkálnak bele egymás tábláiba, mint eddig. Emellé még azt is megcsinálom, hogy a JWT már a userId-t is fogja tartalmazni az egyszerűség kedvéért.
Megcsináltam az adatbázisok szétosztását, most ElephantSQL-en fut 4 egymástól független adatbázis és ezekre kapcsolódnak rá a servicek. Befejeztem a BeFake time generáló service-t is és az endpointok nagyrészének az implementálását is, már csak a user-service delete user funkciója van hátra, amihez majd kell az összes serviceben endpointokat létrehozni, amik a userhez tartozó összes adatukat letörlik. Ezeken kívül meg már csak a notification service van hátra, amihez majd Kafkát fogok használni. Olyan dolgok is vannak még hátra, mint a userek posztolásának korlátozása (csak akkor lehessen, ha már volt aznap BeFake time, vagy ha az az előtti napit még nem posztolta ki), vagy hogy ne lehessen többször reagálni egy posztra, hanem felahsználónként csak egyszer.

### 8.hét
Végigteszteltem az endpointokat, majd néhány helyen korrigáltam a response-ok formátumát és fixáltam néhány kisebb edge-case bugot is. Visszaraktam az adatbázisokat lokálisra, hogy ne legyen probléma a tárhellyel és a kapcsolatok maximális számával.
Ezek után nekiálltam megcsinálni a notification service-t ami az emailek kiküldéséért és a telefonos értesítések küldéséért felelős.
A terv az, hogy amint ez kész van, elkezdem a mobilos appot készíteni.
A Kafkát egy Docker image segítségével futtatom lokálban, aminek a konfigurációja a docker-compose.yml fileban található meg.
Ehhez segítséget itt találtam: ```https://docs.spring.io/spring-kafka/reference/html/```
Létrehoztam egy teszt enpointot amivel a user service-ből lehet üzenetet küldeni a Kafkának, a notification service pedig az ide érkező, registration topicra küldött üzeneteket feldolgozza.