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

- **UserService**: Regisztráció, Profil szerkesztése
- **AuthenticationService**: Authentikáció kezelése

Ezt a kettőt amiatt választottam szét, mert egy nagyobb alkalmazásnál lehet több féle bejelentkezési metódus is, ráadásul az authentikációnak semmi köze a felhasználó személyes adataihoz. Ugyan így fordítva is igaz, hogy a felhasználót kezelő servicenek nincsen köze a bejelentkezés folyamatához.
Ezt ennek a threadnek az elolvasása után döntöttem el: [stackoverflow link](https://stackoverflow.com/questions/44886715/should-the-auth-server-be-combined-with-the-user-service-in-a-microservices-arch)

- **PostService**: Itt lehet hozzáadni, törölni, szerkeszteni, lekérni a posztokat. Ebből kell majd a legtöbb instance-nek futnia, mivel ez lesz a leginkább leterhelve
- **InteractionService**: Itt lehet kommenteket és reakciókat létrehozni, törölni.
- **FriendService**: Barát jelöléseket, törléseket és jelölések elfogadását teszi lehetővé.
- **NotificationService**: Értesítéseket küld a mobilok felé bizonyos történésekről.
- **NamingServer**: Terhelés elosztása és naming szerver
- **APIGatewayServer**: API Gateway szerver

### Service kommunikáció
A servicek feign client tervezési mintával fognak ehymással kommunikálni HTTP kérések formájában, kivéve a Notification Service-szel, ami előtt lesz egy message queue szolgáltatás.

### Load balancing
A terhelés szétosztását egy Eureka szerver fogja megvalósítani

### Adatbázisok
Egy adatbázist fogok használni az összes microservicehez, mivel a serviceim a legnagyobb terhelést a képfeltöltésre helyezik, amit terveim szerint egy CDN szolgáltatással fogok megoldani. A többi service viszont egy jól skálázó SQL adatbázist fog írni, ami mivel önmagában megoldja a skálázódást, nem követeli meg hogy külön db-ket használjak minden microservicenél.

### Authorizáció , Authentikáció és az API gateway kapcsolata
A megoldásomban az API gateway végzi el az authentikációt, az auth-service pedig az authorizációt. Az API gateway authentikálja a beérkező kéréseket és invalid vagy nem létező token esetében csak az auth-service loginjéhez, vagy a user-service register endpointjához enged tovább felhasználókat, ha viszont csatoltak a kéréshez tokent és az valid is, akkor viszont tovább engedi azokat bárhova. Ezt azért csináltam így, mert az én megértésemben az API gateway egyfajta jegy ellenőr, aki megnézi hogy érvényes-e, vagy hogy egyáltalán van-e jegye a beérkező felhasználónak és ha nincs, akkor csak a jegypénztárhoz, azaz az én esetemben a register/login-hoz engedi tovább. Nem tartom a felelősségének még azt is, hogy JWT tokeneket provízionáljon a felhasználóknak meg kikeresse az adatbázisból hogy jók-e a bejelentkezési adatok, ezt a feladatot szerintem egy külön service-nek kell ellátnia, így szétoszlik a terhelés is és kontrasztosabbak a felelősségek is.
Ez a megoldás viszont előhozza azt a problémát, hogy mivel külön szerver generálja a kódot és egy másik validálja, más secretekkel is fognak dolgozni, ami miatt nem fogják elfogadni a megadott tokeneket mert nem bíznak meg bennük.
Erre azt a megoldást találtam, hogy egy aszinkron RSA kulccsal generálok egy privát és egy publikus kulcspárt és ezekkel a kulcsokkal titkosítom és dekódolom a tokeneket, ezek a kulcsok pedig minden szerveren ott vannak így garantáltan ugyan azt használják.
Ennek előnye, hogy a program működik, hátránya pedig, hogy nem generálódik magától újra minden szerver idításkor és mivel a fájlrendszerben elérhető, feltehetően kevésbé is biztonságos ez, mintha biztonságos módon a szerver memóriájában tárolná.

### Képek letöltése
A képek letöltésével kapcsolatban először egy olyan megoldást alkalmaztam, ami az API hívás alkalmával letölti a kért képet a szerveren keresztül az AWS S3 bucketből, viszont ez két problémát okozott.
Az egyik az, hogy mobil oldalon nem akarom a felhasználókat rengeteg fájl letöltésére kényszeríteni. Pontosabban de, de nem olyan módon, mint ahogy ez ebben a formában történne, hogy a user letölti a képet a belső tárhelyre és utána azt megjeleníti, hanem valamilyen caching segítségével. Mivel ez az előbbi meldás nem is elterjedt, nem találtam amúgy sem megoldást erre mobil oldalon. A másik ok pedig az, hogy az összes képnek keresztzül kellene mennie a szervereimen, ami nagyon nagy sávszélesség terhelést okozna, jobb lenne egyből az AWS S3 szervereire küldeni a kérést kliens oldalról is. Ezzel annyi a probléma, hogy a képeim elérése titkosítva van, így ha csak megnyitom az URL-jüket, egy Access Denied oldalra kerülök. Azt a megoldást találtam erre, hogy készítek olyan előre, a szerver által aláírt URL-eket, amik egy ideig érvényesek csak, utána lejárnak. Erre a megoldást nagyon jól mutatja be ez az oldal: ```https://blog.ascendingdc.com/quick-tutorial-to-integrate-amazon-s3-presigned-url```. Így most a szerverre csak egy kérés fog menni, amire az generál egy url-t és egy stringet ad vissza egy több megabájtos kép helyett.
