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

