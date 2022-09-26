# Tervezési megfontolások

``` Itt fogom összegyűjteni a döntéseimhez kapcsolódó okokat/gondolatmeneteket, hogy a dolgozat írásakor már ne okozzon különösebb fejtörést ha valamire már nem emlékszem hogy miért csináltam. ```

### Az Egyed-Kapcsolat diagramomat az alábbiak alapján terveztem meg:
Az alkalmazásom mivel kezelni fogja a felhasználókat és perzisztensen el lesznek valahol tárolva, ezért kell hogy legyen egy User egyedem.
Ezeknek a usereknek lesznek adataik a profiljukon, ezért ezeket tulajdonságokként felvettem az entitásra.
Az alaklamazás lényegi része az az, hogy lehet posztokat készíteni, amiknek szintén vannak tulajdonságaik, így létrehoztam egy Post entitást is és felvettem rajtuk a megfelelő tulajdonságokat.
Mivel az alkalmazás úgy működik, hogy a Userek posztolhatnak minden nap egy képet, egy kép viszont mindig egy felhasználóhoz tartozik, így közéjük felvettem egy 1-N kapcsolatot, aminek van egy plusz tulajdonsága, az, hogy időben posztolt-e a felhasználó, vagy sem.
Azeket az adatbázisban is majd jelezni fogom, lesz egy tábla, aminek az oszlopai így fognak kinézni: USER_ID, POST_ID, IS_ON_TIME. Az első két oszlop külő kulcsokként fognak szerepelni a táblában.
A Posztokra lehet kommentelni és reagálni, minden poszthoz tartozhat több komment és több reakció is, viszont ezekhez egyértelműen megadható a felhasználó, aki kommentelt/reagált, emiatt köztük és a felhasználó entitás között is egy 1-N kapcsolatot vettem fel.
Mivel lehet barátokat bejelölni, létrehoztam a User entitáson saját magával egy kapcsolatot, amin jeleztem, hogy lehet N-N kapcsolat a felhasználók között, azaz egy felhasználónak lehet több ismerőse is, ahogy az ismerősének is lehetnek más ismerősei.
Ennél a kapcsolatnál jeleztem, hogy hozzá tartozik egy státusz, azaz hogy milyen fázisban van a barátságuk az alkalmazáson (pending, active, stb, akár lehet bővíteni blocked-dal is később) és egy since, ami azt jelzi, hogy mióta aktív a barátságuk (mikor lfogadta el a másik fél a barát jelölést).
Ez az adatbázisban majd így fog megjelenni: USER1_ID, USER2_ID, STATUS, SINCE.
Ezt a status tulajdonságot akarom majd felhasználni arra, hogy a bejövő barátkéréseket megjelenítsük, méghozzá egy szűréssel, aminél ha a user2id a felhasználóé és a status pending, akkor az egy bejövő barátkérést jelent.

### RabbitMQ / Kafka
Szeretnék egy Notification szervert is csinálni, ami kafkán vagy rabbiten kapja a jelet, hogy küldenie kell értesítést, ami jól fogja demonstrálni ezeknek a technológiáknak az előnyeit.
Azért előnyös ilyen technológiákat alkalmazni, mert ha lehal a Notification szerver, de az értesítések mégis érkeznek, akkor még ha csinálok is circuit breakert mögé, akkor is elvesznek az értesítések és a szerver újraindulása után nem fogják megkapni az értesítéseiket a felhasználók, ami nagy gondokat okozhat.
Ha viszont berakok a többi service és a notification szervice közé egy message queue-t, akkor az az elérhetősége miatt (ezek a szolgáltatások nagyon ritkán halnak le) nem kell amiatt aggódnom, hogy az üzenetek egy darabig elvesznek.
Azért is akarom ennél a servicenél alkalmazni ezt, mert ott a legfontosabb, hogy bármilyen hiba esetén utólag visszaálljon a rend.
Ha nem tudnak kommentelni a felhasználók egy darabig, az nem olyan probléma, minthogy lemaradnak fontos eseményekről, ezért ennél a részénél az alkalmazásnak fontosnak tartom hogy használjam vagy a kafka, vagy a rabbitmq által nyújtott előnyöket.

### Mikroszolgáltatások szétválasztása
A microservicek szétválasztásánál a szempont a felelősségi körük és a feltehető terhelésük mértéke volt a szempont.

- **UserService**: Profil szerkesztése
- **AuthenticationService**: Regisztráció, Authentikáció kezelése

Ezt a kettőt amiatt választottam szét, mert egy nagyobb alkalmazásnál lehet több féle bejelentkezési metódus is, ráadásul az authentikációnak semmi köze a felhasználó személyes adataihoz. Ugyan így fordítva is igaz, hogy a felhasználót kezelő servicenek nincsen köze a bejelentkezés folyamatához.
Ezt ennek a threadnek az elolvasása után döntöttem el: [stackoverflow link](https://stackoverflow.com/questions/44886715/should-the-auth-server-be-combined-with-the-user-service-in-a-microservices-arch)

- **PostService**: Itt lehet hozzáadni, törölni, szerkeszteni, lekérni a posztokat. Ebből kell majd a legtöbb instance-nek futnia, mivel ez lesz a leginkább leterhelve
- **InteractionService**: Itt lehet kommenteket és reakciókat létrehozni, törölni.
- **FriendService**: Barát jelöléseket, törléseket és jelölések elfogadását teszi lehetővé.
- **NotificationService**: Értesítéseket küld a mobilok felé bizonyos történésekről.
- **NamingServer**: Terhelés elosztása és naming szerver
- **APIGatewayServier**: API Gateway szerver

### Service kommunikáció
A servicek feign client tervezési mintával fognak ehymással kommunikálni HTTP kérések formájában, kivéve a Notification Service-szel, ami előtt lesz egy message queue szolgáltatás.

### Load balancing
A terhelés szétosztását egy Eureka szerver fogja megvalósítani
